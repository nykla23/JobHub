package com.cyd.xs.dto.user.ChatRoom;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatRoomMessageDTO {
    private String messageId;
    private LocalDateTime sendTime;
}