package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.ChatRoom.ChatRoomDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomDetailDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomMessageDTO;
import com.cyd.xs.dto.ChatRoom.EssenceNoteDTO;
import com.cyd.xs.dto.Topic.TopicDTO;
import com.cyd.xs.dto.Topic.TopicCommentDTO;
import com.cyd.xs.dto.Topic.TopicCommentLikeDTO;
import com.cyd.xs.dto.Topic.TopicCommentRequest;
import com.cyd.xs.dto.Topic.vo.TopicDetailVO;
import com.cyd.xs.dto.Topic.vo.TopicInfoVO;
import com.cyd.xs.dto.Topic.vo.CommentPageVO;
import com.cyd.xs.dto.Topic.vo.TopicPostVO;

import com.cyd.xs.entity.Topic.ChatRoom.ChatRoom;
import com.cyd.xs.entity.Topic.ChatRoom.ChatRoomMessage;
import com.cyd.xs.entity.Topic.ChatRoom.EssenceNote;
import com.cyd.xs.entity.Topic.Topic;
import com.cyd.xs.entity.Topic.TopicPost;
import com.cyd.xs.entity.Topic.TopicPostLike;
import com.cyd.xs.entity.User.User;
import com.cyd.xs.mapper.ChatRoom.ChatRoomMapper;
import com.cyd.xs.mapper.ChatRoom.ChatRoomMessageMapper;
import com.cyd.xs.mapper.ChatRoom.EssenceNoteMapper;
import com.cyd.xs.mapper.Topic.TopicMapper;
import com.cyd.xs.mapper.Topic.TopicPostMapper;
import com.cyd.xs.mapper.UserMapper;
import com.cyd.xs.service.TopicService;
import com.cyd.xs.util.IDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.cyd.xs.dto.Topic.vo.TopicDetailVO;
import com.cyd.xs.dto.Topic.vo.TopicInfoVO;
import com.cyd.xs.dto.Topic.vo.CommentPageVO;
import com.cyd.xs.dto.Topic.vo.TopicPostVO;
@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicMapper topicMapper;
    private final TopicPostMapper topicPostMapper;
    private final ChatRoomMapper chatRoomMapper;
    private final ChatRoomMessageMapper chatRoomMessageMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final EssenceNoteMapper essenceNoteMapper;


    @Override
    public TopicDTO getTopicList(String tag, String level, String sort, Integer pageNum, Integer pageSize) {
        log.info("获取话题列表: tag={}, level={}, sort={}, pageNum={}, pageSize={}",
                tag, level, sort, pageNum, pageSize);

        try {
            int offset = (pageNum - 1) * pageSize;
            List<Topic> topics = topicMapper.findTopicsByCondition(tag, level, sort, offset, pageSize);

            TopicDTO result = new TopicDTO();
            result.setTotal(topicMapper.countByCondition(tag, level));
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);

            List<TopicDTO.TopicItem> topicList = topics.stream().map(topic -> {
                TopicDTO.TopicItem item = new TopicDTO.TopicItem();
                item.setId(topic.getId());
                item.setTitle(topic.getTitle());
                item.setLevel(topic.getLevel());
                item.setTags(Collections.singletonList(topic.getTag())); // 假设tag字段存储单个标签
                item.setParticipantCount(topic.getParticipantCount());
                item.setInteractionCount(topic.getInteractiveCount());
                item.setLatestReplyTime(topic.getLatestReplyTime());
                item.setLink("/api/v1/topic/" + topic.getId());
                return item;
            }).collect(Collectors.toList());

            result.setList(topicList);
            return result;
        } catch (Exception e) {
            log.error("获取话题列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取话题列表失败");
        }
    }

    @Override
    @Transactional
    public TopicCommentDTO publishTopicComment(Long topicId, Long userId, TopicCommentRequest request) {

        // 1. 校验用户
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 构建评论实体（⚠️ 不要手动 setId）
        TopicPost post = new TopicPost();
        post.setTopicId(topicId);
        post.setUserId(userId);
        post.setUserName(
                user.getDisplayName() != null && !user.getDisplayName().isBlank()
                        ? user.getDisplayName()
                        : user.getUsername()
        );
        post.setContent(request.getContent());
        post.setCreatedAt(LocalDateTime.now());

        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setCollectCount(0);

        // 3. 插入
        topicPostMapper.insert(post);

        // 4. 更新话题统计
        topicMapper.incrementInteractiveCount(topicId);
        topicMapper.updateLatestReplyTime(topicId, LocalDateTime.now());

        // 5. 返回
        TopicCommentDTO dto = new TopicCommentDTO();
        dto.setCommentId(post.getId()); // ✅ MyBatis 会回填
        dto.setStatus("pending");
        dto.setSubmitTime(LocalDateTime.now());
        return dto;
    }



    @Override
    @Transactional
    public TopicCommentLikeDTO likeTopicComment(Long commentId, Long userId, Boolean isLike) {
        log.info("用户 {} {}评论 {}", userId, isLike ? "点赞" : "取消点赞", commentId);

        try {
            // 检查评论是否存在
            TopicPost comment = topicPostMapper.selectById(commentId);
            if (comment == null) {
                throw new RuntimeException("评论不存在");
            }

            // 检查点赞记录
            boolean hasLiked = topicPostMapper.exists(commentId, userId)> 0;

            if (isLike) {
                // 点赞
                if (hasLiked) {
                    throw new RuntimeException("已点过赞");
                }

                // 插入点赞记录
                TopicPostLike like = new TopicPostLike();
                like.setPostId(commentId);
                like.setUserId(userId);
                like.setCreatedAt(LocalDateTime.now());
                topicPostMapper.insertLike(like);

                // 更新评论点赞数
                comment.setLikeCount(comment.getLikeCount() + 1);
            } else {
                // 取消点赞
                if (!hasLiked) {
                    throw new RuntimeException("未点赞，无法取消");
                }

                // 删除点赞记录
                topicPostMapper.deleteByPostAndUser(commentId, userId);

                // 更新评论点赞数
                comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
            }

            // 更新评论
            topicPostMapper.updateById(comment);

            TopicCommentLikeDTO result = new TopicCommentLikeDTO();
            result.setCommentId(commentId);
            result.setLikeCount(comment.getLikeCount());
            result.setIsLike(isLike);

            return result;
        } catch (Exception e) {
            log.error("点赞操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("点赞操作失败: " + e.getMessage());
        }
    }

    @Override
    public ChatRoomDTO getChatRoomList(String status, String keyword, Integer pageNum, Integer pageSize) {
        log.info("获取聊天室列表: status={}, keyword={}, pageNum={}, pageSize={}",
                status, keyword, pageNum, pageSize);

        try {
            // 模拟数据 - 实际应该从数据库查询
            ChatRoomDTO result = new ChatRoomDTO();
            result.setTotal(15L);
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);

            List<ChatRoomDTO.ChatRoomItem> chatRoomList = Arrays.asList(
                    createChatRoomItem(21001L, "秋招求职聊天室", "秋招求职交流", "ongoing",
                            1285, "HR Jane",
                            LocalDateTime.of(2025, 5, 20, 19, 0),
                            LocalDateTime.of(2025, 5, 20, 21, 0),
                            "/api/v1/chatroom/21001")
            );

            result.setList(chatRoomList);
            return result;
        } catch (Exception e) {
            log.error("获取聊天室列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取聊天室列表失败");
        }
    }

    @Override
    public ChatRoomDetailDTO getChatRoomDetail(Long chatRoomId, Long userId) {
        log.info("获取聊天室详情: chatRoomId={}, userId={}", chatRoomId, userId);

        try {
            ChatRoomDetailDTO result = new ChatRoomDetailDTO();

            // 聊天室基本信息
            ChatRoomDetailDTO.ChatRoomInfo chatRoomInfo = new ChatRoomDetailDTO.ChatRoomInfo();
            chatRoomInfo.setId(Long.valueOf(chatRoomId));
            chatRoomInfo.setTitle("秋招求职聊天室");
            chatRoomInfo.setTheme("秋招求职交流");
            chatRoomInfo.setStatus("ongoing");
            chatRoomInfo.setOnlineCount(1285);
            chatRoomInfo.setHost("HR Jane");
            chatRoomInfo.setStartTime(LocalDateTime.of(2025, 5, 20, 19, 0));
            chatRoomInfo.setEndTime(LocalDateTime.of(2025, 5, 20, 21, 0));
            chatRoomInfo.setNotice("本次聊天室将解答秋招简历、面试相关问题，欢迎提问");
            result.setChatroomInfo(chatRoomInfo);

            // 聊天消息
            ChatRoomDetailDTO.MessageList messageList = new ChatRoomDetailDTO.MessageList();
            List<ChatRoomDetailDTO.MessageItem> messages = Arrays.asList(
                    createMessageItem(22001L, 1600L, "HR Jane",
                            "https://jobhub.com/avatar/jane.jpg",
                            "大家好，欢迎来到秋招求职聊天室，有问题可以直接提问~",
                            LocalDateTime.of(2025, 5, 20, 19, 0), true),
                    createMessageItem(22002L, Long.valueOf(userId), "叶同学",
                            "https://jobhub.com/avatar/Y.png",
                            "请问HR，简历上的项目经历需要写多少个合适？",
                            LocalDateTime.of(2025, 5, 20, 19, 5), false)
            );
            messageList.setList(messages);
            result.setMessages(messageList);

            // 置顶消息
            ChatRoomDetailDTO.PinnedMessage pinnedMessage = new ChatRoomDetailDTO.PinnedMessage();
            pinnedMessage.setId(Long.valueOf("22003"));
            pinnedMessage.setContent("本次聊天室精华笔记将在结束后1小时内生成，大家可关注消息通知");
            pinnedMessage.setSendTime(LocalDateTime.of(2025, 5, 20, 19, 10));
            result.setPinnedMessage(pinnedMessage);

            return result;
        } catch (Exception e) {
            log.error("获取聊天室详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取聊天室详情失败");
        }
    }

    @Override
    public ChatRoomMessageDTO sendChatRoomMessage(Long chatRoomId, Long userId, String content) {


        try {
            // 1. 验证聊天室是否存在

            ChatRoom chatRoom = chatRoomMapper.findById(chatRoomId);
            if (chatRoom == null) {
                throw new RuntimeException("聊天室不存在");
            }


            // 2. 获取用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }


            // 3. 创建消息实体
            ChatRoomMessage message = new ChatRoomMessage();
            message.setId(Long.valueOf(String.valueOf(IDGenerator.generateId())));
            message.setChatRoomId(chatRoomId);
            message.setUserId(userId);
            message.setContent(content);
            message.setSendTime(LocalDateTime.now());

            // 设置用户信息
            message.setNeckName(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
            message.setAvatar(user.getAvatarUrl());

            // 5. 保存消息
            int result = chatRoomMessageMapper.insert(message);
            if (result == 0) {
                throw new RuntimeException("消息保存失败");
            }

//            // 4. 保存消息
//            chatRoomMessageMapper.insert(message);

            // 5. 返回DTO
            ChatRoomMessageDTO dto = new ChatRoomMessageDTO();
            dto.setMessageId(message.getId());
            dto.setSendTime(message.getSendTime());
            return dto;
        }catch (Exception e) {
            log.error("发送聊天室消息失败: chatRoomId={}, userId={}, content={}",
                    chatRoomId, userId, content, e);
            throw new RuntimeException("发送消息失败: " + e.getMessage());
        }

    }

    @Override
    @Transactional
    public EssenceNoteDTO generateEssenceNote(Long chatRoomId, Long userId) {
        log.info("用户 {} 为聊天室 {} 生成精华笔记", userId, chatRoomId);

        try {
            // 1. 获取用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            // 2. 获取聊天室最近的消息
            List<ChatRoomMessage> recentMessages = chatRoomMessageMapper.findRecentMessages(chatRoomId);

            if (recentMessages == null || recentMessages.isEmpty()) {
                throw new RuntimeException("该聊天室没有消息，无法生成精华笔记");
            }

            // 限制消息数量，避免内容过长
            int maxMessages = Math.min(recentMessages.size(), 50);
            List<ChatRoomMessage> selectedMessages = recentMessages.subList(0, maxMessages);

            // 3. 生成简洁的内容
            String title = String.format("%s - 精华笔记", getChatRoomTitle(chatRoomId));
            String summary = String.format("包含 %d 条消息，由 %s 生成",
                    selectedMessages.size(),
                    user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());

            // 4. 生成纯文本内容（避免 HTML 太长的问题）
            StringBuilder content = new StringBuilder();
            content.append("=== ").append(title).append(" ===\n\n");
            content.append("生成时间: ").append(LocalDateTime.now()).append("\n");
            content.append("生成者: ").append(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername()).append("\n");
            content.append("消息数量: ").append(selectedMessages.size()).append(" 条\n\n");
            content.append("------------------------\n\n");

            // 按时间顺序排序
            selectedMessages.sort(Comparator.comparing(ChatRoomMessage::getSendTime));

            for (ChatRoomMessage message : selectedMessages) {
                content.append("[")
                        .append(message.getSendTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .append("] ")
                        .append(message.getNeckName())
                        .append(": ")
                        .append(message.getContent())
                        .append("\n");
            }

            content.append("\n------------------------\n");
            content.append("笔记生成完成\n");

            // 5. 保存精华笔记
            EssenceNote essenceNote = new EssenceNote();
            Long noteId = IDGenerator.generateId();
            essenceNote.setId(noteId);
            essenceNote.setChatRoomId(chatRoomId);
            essenceNote.setUserId(userId);
            essenceNote.setTitle(title);
            essenceNote.setSummary(summary);
            essenceNote.setContent(content.toString());
            essenceNote.setMessageCount(selectedMessages.size());
            essenceNote.setGenerateTime(LocalDateTime.now());
            essenceNote.setCreatedAt(LocalDateTime.now());
            essenceNote.setUpdatedAt(LocalDateTime.now());

            // 保存到数据库
            int result = essenceNoteMapper.insert(essenceNote);
            if (result == 0) {
                throw new RuntimeException("保存精华笔记失败");
            }

            log.info("精华笔记生成成功，ID: {}, 包含消息: {} 条", noteId, selectedMessages.size());

            // 6. 返回DTO
            EssenceNoteDTO dto = new EssenceNoteDTO();
            dto.setNoteId(noteId);
            dto.setNoteUrl(String.format("/api/v1/essence-notes/%d", noteId));
            dto.setGenerateTime(essenceNote.getGenerateTime());

            return dto;

        } catch (Exception e) {
            log.error("生成精华笔记失败: chatRoomId={}, userId={}, error={}",
                    chatRoomId, userId, e.getMessage(), e);
            throw new RuntimeException("生成精华笔记失败: " + e.getMessage());
        }
    }

    // 辅助方法
    private ChatRoomDTO.ChatRoomItem createChatRoomItem(Long id, String title, String theme,
                                                        String status, Integer onlineCount, String host,
                                                        LocalDateTime startTime, LocalDateTime endTime, String link) {
        ChatRoomDTO.ChatRoomItem item = new ChatRoomDTO.ChatRoomItem();
        item.setId(id);
        item.setTitle(title);
        item.setTheme(theme);
        item.setStatus(status);
        item.setOnlineCount(onlineCount);
        item.setHost(host);
        item.setStartTime(startTime);
        item.setEndTime(endTime);
        item.setLink(link);
        return item;
    }

    private ChatRoomDetailDTO.MessageItem createMessageItem(Long id, Long userId, String nickname,
                                                            String avatar, String content,
                                                            LocalDateTime sendTime, Boolean isHost) {
        ChatRoomDetailDTO.MessageItem item = new ChatRoomDetailDTO.MessageItem();
        item.setId(id);
        item.setUserId(userId);
        item.setNickname(nickname);
        item.setAvatar(avatar);
        item.setContent(content);
        item.setSendTime(sendTime);
        item.setIsHost(isHost);
        return item;
    }

    // 辅助方法：获取最近的消息
    private List<ChatRoomMessage> getRecentChatMessages(Long chatRoomId, LocalDateTime startTime) {
        // 这里可以使用自定义的Mapper方法，如果没有可以添加
        // 假设有方法：findMessagesByTimeRange
        return chatRoomMessageMapper.findMessagesByTimeRange(chatRoomId, startTime);
    }

    // 辅助方法：验证权限
    private boolean hasEssenceNotePermission(Long chatRoomId, Long userId) {
        // 方案1：检查用户是否是主持人
        ChatRoom chatRoom = chatRoomMapper.findById(chatRoomId);
        if (chatRoom != null && chatRoom.getHostId() != null && chatRoom.getHostId().equals(userId)) {
            return true;
        }

        // 方案2：检查用户是否有特殊角色（从chatroom_roles表查询）
        try {
            // 假设有 ChatRoomRolesMapper
            // ChatRoomRoles role = chatRoomRolesMapper.findByChatRoomAndUser(chatRoomId, userId);
            // return role != null && ("HOST".equals(role.getRole()) || "ADMIN".equals(role.getRole()));
        } catch (Exception e) {
            log.warn("检查用户权限失败: {}", e.getMessage());
        }

        // 方案3：检查用户是否是管理员（从users表查询）
        User user = userMapper.findById(userId);
        if (user != null && "ADMIN".equals(user.getRole())) {
            return true;
        }

        return false;
    }

    // 辅助方法：获取聊天室标题
    private String getChatRoomTitle(Long chatRoomId) {
        try {
            ChatRoom chatRoom = chatRoomMapper.findById(chatRoomId);
            if (chatRoom != null && chatRoom.getTitle() != null) {
                return chatRoom.getTitle();
            }
        } catch (Exception e) {
            log.warn("获取聊天室标题失败: {}", e.getMessage());
        }
        return "聊天室 " + chatRoomId;
    }

    // 辅助方法：生成笔记摘要
    private String generateNoteSummary(List<ChatRoomMessage> messages) {
        if (messages.isEmpty()) {
            return "暂无内容摘要";
        }

        // 提取前3条有代表性的消息作为摘要
        StringBuilder summary = new StringBuilder();
        int count = Math.min(3, messages.size());

        for (int i = 0; i < count; i++) {
            ChatRoomMessage msg = messages.get(i);
            String truncatedContent = msg.getContent().length() > 50 ?
                    msg.getContent().substring(0, 50) + "..." : msg.getContent();
            summary.append(String.format("%s: %s\\n", msg.getNeckName(), truncatedContent));
        }

        return summary.toString();
    }

    // 辅助方法：生成笔记内容
    private String generateNoteContent(List<ChatRoomMessage> messages, ChatRoom chatRoom) {
        StringBuilder html = new StringBuilder();

        // HTML头部
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"zh-CN\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append(String.format("<title>%s - 精华笔记</title>", chatRoom.getTitle()));
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; }");
        html.append(".note-header { background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin-bottom: 20px; }");
        html.append(".message { margin-bottom: 15px; padding: 10px; border-left: 4px solid #007bff; background-color: #f8f9fa; }");
        html.append(".message-header { display: flex; align-items: center; margin-bottom: 5px; }");
        html.append(".user-name { font-weight: bold; color: #333; margin-right: 10px; }");
        html.append(".message-time { color: #6c757d; font-size: 0.9em; }");
        html.append(".message-content { margin-top: 5px; }");
        html.append(".host-message { border-left-color: #28a745; background-color: #e8f5e9; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        // 笔记头部信息
        html.append("<div class=\"note-header\">");
        html.append(String.format("<h1>%s</h1>", chatRoom.getTitle()));
        html.append(String.format("<p><strong>主题:</strong> %s</p>", chatRoom.getTheme()));
        html.append(String.format("<p><strong>主持人:</strong> %s</p>", chatRoom.getHostId())); // 实际应该是主持人名称
        html.append(String.format("<p><strong>生成时间:</strong> %s</p>", LocalDateTime.now()));
        html.append(String.format("<p><strong>包含消息:</strong> %d 条</p>", messages.size()));
        html.append("</div>");

        // 消息列表
        html.append("<div class=\"messages\">");
        for (ChatRoomMessage message : messages) {
            String messageClass = message.getUserId().equals(chatRoom.getHostId()) ?
                    "message host-message" : "message";

            html.append("<div class=\"").append(messageClass).append("\">");
            html.append("<div class=\"message-header\">");
            html.append(String.format("<span class=\"user-name\">%s</span>", message.getNeckName()));
            html.append(String.format("<span class=\"message-time\">%s</span>", message.getSendTime()));
            html.append("</div>");
            html.append(String.format("<div class=\"message-content\">%s</div>", message.getContent()));
            html.append("</div>");
        }
        html.append("</div>");

        // 尾部
        html.append("<div style=\"margin-top: 30px; padding-top: 20px; border-top: 1px solid #dee2e6; color: #6c757d; font-size: 0.9em;\">");
        html.append("<p>本笔记由系统自动生成，内容来自聊天室实时对话记录。</p>");
        html.append("</div>");

        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    // 辅助方法：生成笔记URL
    private String generateNoteUrl(Long noteId) {
        // 可以是静态HTML文件路径，或者API接口路径
        return String.format("/api/v1/essence-note/%s", noteId);
    }
    @Override
    public TopicDetailVO getTopicDetail(Long topicId, Integer pageNum, Integer pageSize) {

        // 1. 查询话题
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }

        // 2. 查询评论
        int offset = (pageNum - 1) * pageSize;
        List<TopicPost> posts =
                topicPostMapper.findPostsByTopicId(topicId, offset, pageSize);

        // 3. Entity → VO（对齐前端字段）
        List<TopicPostVO> postVOs = posts.stream().map(p -> {
            TopicPostVO vo = new TopicPostVO();
            vo.setId(p.getId());
            vo.setContent(p.getContent());
            vo.setNickname(p.getUserName());
            vo.setPublishTime(p.getCreatedAt());
            vo.setLikeCount(p.getLikeCount() == null ? 0 : p.getLikeCount());
            return vo;
        }).toList();

        // 4. 组装分页
        CommentPageVO commentPage = new CommentPageVO();
        commentPage.setList(postVOs);
        commentPage.setPageNum(pageNum);
        commentPage.setPageSize(pageSize);
        commentPage.setTotal((long) postVOs.size());

        // 5. 组装最终 VO
        TopicDetailVO detail = new TopicDetailVO();
        detail.setTopicInfo(TopicInfoVO.from(topic));
        detail.setComments(commentPage);

        return detail;
    }
}