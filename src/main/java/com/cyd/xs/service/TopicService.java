package com.cyd.xs.service;

import com.cyd.xs.dto.user.Topic.TopicDTO;
import com.cyd.xs.dto.user.Topic.TopicPostRequest;
import com.cyd.xs.entity.User.Topic.TopicPost;

import java.util.List;
import java.util.Map;

public interface TopicService {

    /**
     * 获取话题列表
     */
    TopicDTO getTopicList(String tab, String sortType, Integer page, Integer pageSize);

    /**
     * 获取话题详情
     */
    Map<String, Object> getTopicDetail(String topicId, Integer page, Integer pageSize);

    /**
     * 发布话题帖子
     */
    String publishPost(String userId, String topicId, String content, List<String> images, List<String> tags);

    /**
     * 话题帖子互动（点赞 / 评论 / 收藏）
     */
    Map<String, Integer> interactPost(String userId, String postId, String interactType,
                                      String commentContent, String quotePostId, Boolean isCancel);

}