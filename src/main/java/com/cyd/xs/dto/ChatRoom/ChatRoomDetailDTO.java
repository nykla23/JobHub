package com.cyd.xs.dto.ChatRoom;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatRoomDetailDTO {
    private ChatRoomInfo chatroomInfo;
    private MessageList messages;
    private PinnedMessage pinnedMessage;

    @Data
    public static class ChatRoomInfo {
        private String id;
        private String title;
        private String theme;
        private String status;
        private Integer onlineCount;
        private String host;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String notice;
    }

    @Data
    public static class MessageList {
        private List<MessageItem> list;
    }

    @Data
    public static class MessageItem {
        private String id;
        private String userId;
        private String nickname;
        private String avatar;
        private String content;
        private LocalDateTime sendTime;
        private Boolean isHost;
    }

    @Data
    public static class PinnedMessage {
        private String id;
        private String content;
        private LocalDateTime sendTime;
    }
}