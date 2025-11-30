package com.cyd.xs.entity.User;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hot_activities")
public class HotActivity {
    @Id
    private String id;

    private String title;
    private String time;
    private Integer participantCount;
    private String link;
    private String status; // active/inactive
    private LocalDateTime createdAt;
}