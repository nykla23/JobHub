package com.cyd.xs.entity.User.Topic.ChatRoom;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;

    private String title;
    private String theme;
    private String status; // preview/ongoing/ended
    private Integer onlineCount;
    private String host;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}