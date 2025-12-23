package com.cyd.xs.dto.Home;

import lombok.Data;

import java.util.List;

@Data
public class HomeDTO {
    // 首页数据字段
    private String userIdentity;
    private List<Carousel> carousel;
    private List<HotActivity> hotActivities;
    private RecommendedContent recommendedContent;

    // 身份选择返回的字段（保持兼容）
    private List<RecommendContent> recommendContent;
    private List<CommunityTopic> communityTopics;
    private List<Activity> activities;

    private List<TopicRecommendItem> topicRecommend;
    // 首页数据结构
    @Data
    public static class Carousel {
        private Long id;
        private String title;
        private String imageUrl;
        private String desc;
        private String link;
    }

    @Data
    public static class HotActivity {
        private Long id;
        private String title;
        private String time;
        private Integer participantCount;
        private String link;
    }

    @Data
    public static class RecommendedContent {
        private Long total;
        private Integer pageNum;
        private Integer pageSize;
        private List<ContentItem> list;
    }

    @Data
    public static class ContentItem {
        private Long id;
        private String title;
        private String type;           // article/topic/post
        private String author;
        private String avatarUrl;      // 新增：作者头像
        private Integer likeCount;
        private Integer collectCount;
        private Integer commentCount;  // 新增：评论数
        private String summary;        // 新增：内容摘要
        private String coverImage;     // 新增：封面图
        private String publishTime;
        private String link;
    }

    // 身份选择数据结构（保持兼容）
    @Data
    public static class RecommendContent {
        private Long contentId;
        private String title;
        private String type;
        private String author;
        private Integer viewCount;
        private Integer interactiveCount;
    }

    @Data
    public static class CommunityTopic {
        private Long topicId;
        private String title;
        private Integer participantCount;
    }

    @Data
    public static class Activity {
        private Long activityId;
        private String title;
        private String time;
    }
    @Data
    public static class TopicRecommendItem {
        private Long id;
        private String title;
        private String tag;               // 原始 tag 字符串
        private String level;             // A / S
        private Integer participantCount;
        private Integer interactiveCount;
    }
}