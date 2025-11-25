package com.cyd.xs.dto.user.Topic;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TopicDetailDTO {
    private List<TopicInfo> topicInfo;
    private List<Postlist> postlist;
    private List<SimilarTopics> similarTopics;


    @Data
    public static class TopicInfo {
        private String topicId;
        private String title;
        private String tag;
        private Integer participantCount;
        private Integer interactiveCount;
        private LocalDateTime latestReplyTime;
        private String guideText;
    }

    @Data
    public static class Postlist {
        private Integer postId;
        private Integer userId;
        private String userName;
        private String content;
        private LocalDateTime createdTime;
        private Integer commentCount;
    }

    @Data
    public static class SimilarTopics {
        private String topicId;
        private String title;
    }

}
