package com.cyd.xs.entity.User.Topic.ChatRoom;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_room_messages")
public class ChatRoomMessage {
    @Id
    private String id;

    private String chatRoomId;
    private String userId;
    private String nickname;
    private String avatar;
    private String content;
    private LocalDateTime sendTime;
    private Boolean isHost;
    private Boolean isPinned;
}