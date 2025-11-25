package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.user.Topic.TopicDTO;
import com.cyd.xs.entity.User.Topic.Topic;
import com.cyd.xs.entity.User.Topic.TopicPost;
import com.cyd.xs.mapper.TopicMapper;
import com.cyd.xs.mapper.TopicPostMapper;
import com.cyd.xs.service.TopicService;
import com.cyd.xs.util.IDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicMapper topicMapper;
    private final TopicPostMapper topicPostMapper;
    private final ObjectMapper objectMapper;

    @Override
    public TopicDTO getTopicList(String tab, String sortType, Integer page, Integer pageSize) {
        log.info("获取话题列表: tab={}, sortType={}, page={}, pageSize={}", tab, sortType, page, pageSize);

        try {
            if (page == null) page = 1;
            if (pageSize == null) pageSize = 20;
            int offset = (page - 1) * pageSize;

            List<Topic> topics = topicMapper.findTopicsByCondition(tab, sortType, offset, pageSize);

            TopicDTO topicDTO = new TopicDTO();
            topicDTO.setData(topics.stream().map(topic -> {
                TopicDTO.TopicItem item = new TopicDTO.TopicItem();
                item.setTopicId(topic.getId());
                item.setTitle(topic.getTitle());
                item.setTag(topic.getTag());
                item.setParticipantCount(topic.getParticipantCount());
                item.setInteractiveCount(topic.getInteractiveCount());
                item.setLatestReplyTime(topic.getLatestReplyTime());
                return item;
            }).collect(Collectors.toList()));

            topicDTO.setTotal(tab != null ? topicMapper.countByTag(tab) : topicMapper.selectCount(null));

            // 设置标签页
            topicDTO.setTabs(Arrays.asList(
                    createTab("professionalShare", "专业分享"),
                    createTab("firstIntern", "第一份实习"),
                    createTab("graduateJob", "研究生求职"),
                    createTab("newbiePitfall", "职场新人避坑"),
                    createTab("offerChoice", "offer选择")
            ));

            return topicDTO;
        } catch (Exception e) {
            log.error("获取话题列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取话题列表失败");
        }
    }

    @Override
    public Map<String, Object> getTopicDetail(String topicId, Integer page, Integer pageSize) {
        log.info("获取话题详情: topicId={}, page={}, pageSize={}", topicId, page, pageSize);

        try {
            Map<String, Object> result = new HashMap<>();

            // 获取话题基础信息
            Topic topic = topicMapper.selectById(topicId);
            if (topic == null) {
                throw new RuntimeException("话题不存在");
            }

            Map<String, Object> topicInfo = new HashMap<>();
            topicInfo.put("topicId", topic.getId());
            topicInfo.put("title", topic.getTitle());
            topicInfo.put("tag", topic.getTag());
            topicInfo.put("participantCount", topic.getParticipantCount());
            topicInfo.put("interactiveCount", topic.getInteractiveCount());
            topicInfo.put("latestReplyTime", topic.getLatestReplyTime());
            topicInfo.put("guideText", topic.getGuideText());

            result.put("topicInfo", topicInfo);

            // 获取帖子列表
            if (page == null) page = 1;
            if (pageSize == null) pageSize = 20;
            int offset = (page - 1) * pageSize;

            List<TopicPost> posts = topicPostMapper.findPostsByTopicId(topicId, offset, pageSize);
            result.put("postList", posts);

            // 相似话题推荐
            List<Map<String, String>> similarTopics = Arrays.asList(
                    createSimilarTopic("t2004", "职场新人如何拒绝不合理要求"),
                    createSimilarTopic("t2005", "应届生薪资谈判技巧")
            );
            result.put("similarTopics", similarTopics);

            return result;
        } catch (Exception e) {
            log.error("获取话题详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取话题详情失败");
        }
    }

    @Override
    @Transactional
    public String publishPost(String userId, String topicId, String content, List<String> images, List<String> tags) {
        log.info("用户 {} 在话题 {} 发布帖子", userId, topicId);

        try {
            TopicPost post = new TopicPost();
            post.setId(IDGenerator.generateId());
            post.setTopicId(topicId);
            post.setUserId(userId);
            post.setContent(content);

            // 处理图片列表为JSON
            if (images != null && !images.isEmpty()) {
                post.setImages(objectMapper.writeValueAsString(images));
            }

            // 处理标签列表为JSON
            if (tags != null && !tags.isEmpty()) {
                post.setTags(objectMapper.writeValueAsString(tags));
            }

            post.setCreatedAt(LocalDateTime.now());

            int result = topicPostMapper.insert(post);
            if (result > 0) {
                log.info("帖子发布成功: {}", post.getId());
                return post.getId();
            } else {
                throw new RuntimeException("帖子发布失败");
            }
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            throw new RuntimeException("帖子发布失败");
        } catch (Exception e) {
            log.error("发布帖子失败: {}", e.getMessage(), e);
            throw new RuntimeException("发布帖子失败");
        }
    }

    @Override
    @Transactional
    public Map<String, Integer> interactPost(String userId, String postId, String interactType,
                                             String commentContent, String quotePostId, Boolean isCancel) {
        log.info("用户 {} 对帖子 {} 进行互动: type={}, isCancel={}", userId, postId, interactType, isCancel);

        try {
            // 这里简化处理，实际应该根据interactType进行不同的操作
            // 比如点赞、评论、收藏等

            TopicPost post = topicPostMapper.selectById(postId);
            if (post == null) {
                throw new RuntimeException("帖子不存在");
            }

            Map<String, Integer> result = new HashMap<>();

            // 根据互动类型更新计数
            switch (interactType) {
                case "like":
                    int newLikeCount = isCancel != null && isCancel ?
                            Math.max(0, post.getLikeCount() - 1) : post.getLikeCount() + 1;
                    post.setLikeCount(newLikeCount);
                    result.put("likeCount", newLikeCount);
                    break;
                case "comment":
                    // 这里应该创建评论记录
                    int newCommentCount = post.getCommentCount() + 1;
                    post.setCommentCount(newCommentCount);
                    result.put("commentCount", newCommentCount);
                    break;
                case "collect":
                    int newCollectCount = isCancel != null && isCancel ?
                            Math.max(0, post.getCollectCount() - 1) : post.getCollectCount() + 1;
                    post.setCollectCount(newCollectCount);
                    result.put("collectCount", newCollectCount);
                    break;
                default:
                    throw new RuntimeException("不支持的互动类型");
            }

            // 更新帖子
            topicPostMapper.updateById(post);

            return result;
        } catch (Exception e) {
            log.error("帖子互动失败: {}", e.getMessage(), e);
            throw new RuntimeException("帖子互动失败");
        }
    }

    private TopicDTO.Tab createTab(String key, String name) {
        TopicDTO.Tab tab = new TopicDTO.Tab();
        tab.setKey(key);
        tab.setName(name);
        return tab;
    }

    private Map<String, String> createSimilarTopic(String topicId, String title) {
        Map<String, String> similarTopic = new HashMap<>();
        similarTopic.put("topicId", topicId);
        similarTopic.put("title", title);
        return similarTopic;
    }
}
