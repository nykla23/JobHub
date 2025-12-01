package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.ChatRoom.ChatRoomDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomDetailDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomMessageDTO;
import com.cyd.xs.dto.Topic.*;
import com.cyd.xs.entity.User.Topic.Topic;
import com.cyd.xs.entity.User.Topic.TopicPost;
import com.cyd.xs.mapper.Topic.TopicMapper;
import com.cyd.xs.mapper.Topic.TopicPostMapper;
import com.cyd.xs.service.TopicService;
import com.cyd.xs.util.IDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicMapper topicMapper;
    private final TopicPostMapper topicPostMapper;

    @Override
    public TopicDTO getTopicList(String tag, String level, String sort, Integer pageNum, Integer pageSize) {
        log.info("获取话题列表: tag={}, level={}, sort={}, pageNum={}, pageSize={}",
                tag, level, sort, pageNum, pageSize);

        try {
            int offset = (pageNum - 1) * pageSize;
            List<Topic> topics = topicMapper.findTopicsByCondition(tag, sort, offset, pageSize);

            TopicDTO result = new TopicDTO();
            result.setTotal(topicMapper.countByTag(tag));
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);

            List<TopicDTO.TopicItem> topicList = topics.stream().map(topic -> {
                TopicDTO.TopicItem item = new TopicDTO.TopicItem();
                item.setId(topic.getId());
                item.setTitle(topic.getTitle());
                item.setLevel("A"); // 默认等级，实际应从数据库获取
                item.setTags(Arrays.asList(topic.getTag())); // 假设tag字段存储单个标签
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
    public TopicDetailDTO getTopicDetail(String topicId, Integer pageNum, Integer pageSize, String userId) {
        log.info("获取话题详情: topicId={}, pageNum={}, pageSize={}, userId={}",
                topicId, pageNum, pageSize, userId);

        try {
            Topic topic = topicMapper.selectById(topicId);
            if (topic == null) {
                throw new RuntimeException("话题不存在");
            }

            TopicDetailDTO result = new TopicDetailDTO();

            // 设置话题信息
            TopicDetailDTO.TopicInfo topicInfo = new TopicDetailDTO.TopicInfo();
            topicInfo.setId(topic.getId());
            topicInfo.setTitle(topic.getTitle());
            topicInfo.setLevel("A"); // 默认等级
            topicInfo.setTags(Arrays.asList(topic.getTag()));
            topicInfo.setParticipantCount(topic.getParticipantCount());
            topicInfo.setInteractionCount(topic.getInteractiveCount());
            topicInfo.setLatestReplyTime(topic.getLatestReplyTime());
            topicInfo.setIntro(topic.getGuideText());
            topicInfo.setHost("求职导师B"); // 默认主持人
            topicInfo.setCreateTime(topic.getCreatedAt());
            result.setTopicInfo(topicInfo);

            // 设置评论列表
            int offset = (pageNum - 1) * pageSize;
            List<TopicPost> posts = topicPostMapper.findPostsByTopicId(topicId, offset, pageSize);

            TopicDetailDTO.CommentList commentList = new TopicDetailDTO.CommentList();
            commentList.setTotal((long) posts.size()); // 简化处理，实际应该查询总数
            commentList.setPageNum(pageNum);
            commentList.setPageSize(pageSize);

            List<TopicDetailDTO.CommentItem> commentItems = posts.stream().map(post -> {
                TopicDetailDTO.CommentItem item = new TopicDetailDTO.CommentItem();
                item.setId(post.getId());
                item.setUserId(post.getUserId());
                item.setNickname(post.getUserName());
                item.setAvatar("https://jobhub.com/avatar/default.png"); // 默认头像
                item.setContent(post.getContent());
                item.setPublishTime(post.getCreatedAt());
                item.setLikeCount(post.getLikeCount());
                item.setCollectCount(post.getCollectCount());
                item.setReplyCount(post.getCommentCount());
                return item;
            }).collect(Collectors.toList());

            commentList.setList(commentItems);
            result.setComments(commentList);

            return result;
        } catch (Exception e) {
            log.error("获取话题详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取话题详情失败");
        }
    }

    @Override
    @Transactional
    public TopicCommentDTO publishTopicComment(String topicId, String userId, TopicCommentRequest request) {
        log.info("用户 {} 在话题 {} 发布评论", userId, topicId);

        try {
            TopicPost post = new TopicPost();
            post.setId(IDGenerator.generateId());
            post.setTopicId(topicId);
            post.setUserId(userId);
            post.setContent(request.getContent());
            post.setCreatedAt(LocalDateTime.now());

            // 处理图片
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                // 这里需要将List转换为JSON字符串存储
                // post.setImages(objectMapper.writeValueAsString(request.getImageUrls()));
            }

            int result = topicPostMapper.insert(post);
            if (result > 0) {
                TopicCommentDTO response = new TopicCommentDTO();
                response.setCommentId(post.getId());
                response.setStatus("pending"); // 默认待审核
                response.setSubmitTime(LocalDateTime.now());
                return response;
            } else {
                throw new RuntimeException("评论发布失败");
            }
        } catch (Exception e) {
            log.error("发布评论失败: {}", e.getMessage(), e);
            throw new RuntimeException("发布评论失败");
        }
    }

    @Override
    @Transactional
    public TopicCommentLikeDTO likeTopicComment(String commentId, String userId, Boolean isLike) {
        log.info("用户 {} {}评论 {}", userId, isLike ? "点赞" : "取消点赞", commentId);

        try {
            // 这里应该查询评论并更新点赞数
            TopicPost comment = topicPostMapper.selectById(commentId);
            if (comment == null) {
                throw new RuntimeException("评论不存在");
            }

            int newLikeCount = isLike ?
                    comment.getLikeCount() + 1 : Math.max(0, comment.getLikeCount() - 1);
            comment.setLikeCount(newLikeCount);
            topicPostMapper.updateById(comment);

            TopicCommentLikeDTO result = new TopicCommentLikeDTO();
            result.setCommentId(commentId);
            result.setLikeCount(newLikeCount);
            result.setIsLike(isLike);

            return result;
        } catch (Exception e) {
            log.error("点赞操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("点赞操作失败");
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
                    createChatRoomItem("21001", "秋招求职聊天室", "秋招求职交流", "ongoing",
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
    public ChatRoomDetailDTO getChatRoomDetail(String chatRoomId, String userId) {
        log.info("获取聊天室详情: chatRoomId={}, userId={}", chatRoomId, userId);

        try {
            ChatRoomDetailDTO result = new ChatRoomDetailDTO();

            // 聊天室基本信息
            ChatRoomDetailDTO.ChatRoomInfo chatRoomInfo = new ChatRoomDetailDTO.ChatRoomInfo();
            chatRoomInfo.setId(chatRoomId);
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
                    createMessageItem("22001", "16001", "HR Jane",
                            "https://jobhub.com/avatar/jane.jpg",
                            "大家好，欢迎来到秋招求职聊天室，有问题可以直接提问~",
                            LocalDateTime.of(2025, 5, 20, 19, 0), true),
                    createMessageItem("22002", userId, "叶同学",
                            "https://jobhub.com/avatar/Y.png",
                            "请问HR，简历上的项目经历需要写多少个合适？",
                            LocalDateTime.of(2025, 5, 20, 19, 5), false)
            );
            messageList.setList(messages);
            result.setMessages(messageList);

            // 置顶消息
            ChatRoomDetailDTO.PinnedMessage pinnedMessage = new ChatRoomDetailDTO.PinnedMessage();
            pinnedMessage.setId("22003");
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
    @Transactional
    public ChatRoomMessageDTO sendChatRoomMessage(String chatRoomId, String userId, String content) {
        log.info("用户 {} 在聊天室 {} 发送消息: {}", userId, chatRoomId, content);

        try {
            // 这里应该保存消息到数据库
            ChatRoomMessageDTO result = new ChatRoomMessageDTO();
            result.setMessageId(IDGenerator.generateId());
            result.setSendTime(LocalDateTime.now());

            return result;
        } catch (Exception e) {
            log.error("发送聊天室消息失败: {}", e.getMessage(), e);
            throw new RuntimeException("发送聊天室消息失败");
        }
    }

    @Override
    @Transactional
    public EssenceNoteDTO generateEssenceNote(String chatRoomId, String userId) {
        log.info("用户 {} 为聊天室 {} 生成精华笔记", userId, chatRoomId);

        try {
            // 这里应该检查用户权限（主持人/管理员）
            // 然后生成精华笔记

            EssenceNoteDTO result = new EssenceNoteDTO();
            result.setNoteId(IDGenerator.generateId());
            result.setNoteUrl("https://jobhub.com/chatroom/essence/" + result.getNoteId() + ".html");
            result.setGenerateTime(LocalDateTime.now());

            return result;
        } catch (Exception e) {
            log.error("生成精华笔记失败: {}", e.getMessage(), e);
            throw new RuntimeException("生成精华笔记失败");
        }
    }

    // 辅助方法
    private ChatRoomDTO.ChatRoomItem createChatRoomItem(String id, String title, String theme,
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

    private ChatRoomDetailDTO.MessageItem createMessageItem(String id, String userId, String nickname,
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


}