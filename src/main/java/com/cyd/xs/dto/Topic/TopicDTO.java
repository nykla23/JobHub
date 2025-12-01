package com.cyd.xs.dto.Topic;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TopicDTO {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<TopicItem> list;

    @Data
    public static class TopicItem {
        private String id;
        private String title;
        private String level; // A/S等级
        private List<String> tags;
        private Integer participantCount;
        private Integer interactionCount; // 互动数（评论+点赞+收藏）
        private LocalDateTime latestReplyTime;
        private String link;
    }
}