package com.cyd.xs.dto.user.Home;

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

    // 首页数据结构
    @Data
    public static class Carousel {
        private String id;
        private String title;
        private String imageUrl;
        private String desc;
        private String link;
    }

    @Data
    public static class HotActivity {
        private String id;
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
        private String id;
        private String title;
        private String type;
        private String author;
        private Integer likeCount;
        private Integer collectCount;
        private String publishTime;
        private String link;
    }

    // 身份选择数据结构（保持兼容）
    @Data
    public static class RecommendContent {
        private String contentId;
        private String title;
        private String type;
        private String author;
        private Integer viewCount;
        private Integer interactiveCount;
    }

    @Data
    public static class CommunityTopic {
        private String topicId;
        private String title;
        private Integer participantCount;
    }

    @Data
    public static class Activity {
        private String activityId;
        private String title;
        private String time;
    }
}