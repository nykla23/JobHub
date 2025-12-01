package com.cyd.xs.dto.Topic;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TopicDetailDTO {
    private TopicInfo topicInfo;
    private ChatRoomPreview chatRoom; // 聊天室预告
    private CommentList comments;     // 话题互动内容

    @Data
    public static class TopicInfo {
        private String id;
        private String title;
        private String level;
        private List<String> tags;
        private Integer participantCount;
        private Integer interactionCount;
        private LocalDateTime latestReplyTime;
        private String intro;      // 话题简介
        private String host;       // 话题主持人
        private LocalDateTime createTime;
    }

    @Data
    public static class ChatRoomPreview {
        private String title;
        private String time;
        private String host;
        private String scope;   // 全员可见
        private String status;  // 预告中/进行中/已结束
    }

    @Data
    public static class CommentList {
        private Long total;
        private Integer pageNum;
        private Integer pageSize;
        private List<CommentItem> list;
    }

    @Data
    public static class CommentItem {
        private String id;
        private String userId;
        private String nickname;
        private String avatar;
        private String content;
        private LocalDateTime publishTime;
        private Integer likeCount;
        private Integer collectCount;
        private Integer replyCount;
    }
}