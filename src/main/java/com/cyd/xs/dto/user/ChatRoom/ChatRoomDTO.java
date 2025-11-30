package com.cyd.xs.dto.user.ChatRoom;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatRoomDTO {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<ChatRoomItem> list;

    @Data
    public static class ChatRoomItem {
        private String id;
        private String title;
        private String theme;
        private String status; // preview/ongoing/ended
        private Integer onlineCount;
        private String host;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String link;
    }
}