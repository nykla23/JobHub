package com.cyd.xs.dto.user.Home;

import lombok.Data;
import java.util.List;

@Data
public class HomeDTO {
    private List<RecommendContent> recommendContent;
    private List<CommunityTopic> communityTopics;
    private List<Activity> activities;

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

    public List<RecommendContent> getRecommendContent() {
        return recommendContent;
    }

    public void setRecommendContent(List<RecommendContent> recommendContent) {
        this.recommendContent = recommendContent;
    }

    public List<CommunityTopic> getCommunityTopics() {
        return communityTopics;
    }

    public void setCommunityTopics(List<CommunityTopic> communityTopics) {
        this.communityTopics = communityTopics;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
