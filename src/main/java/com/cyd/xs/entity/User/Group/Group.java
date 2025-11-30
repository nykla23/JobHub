package com.cyd.xs.entity.User.Group;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "groups")
public class Group {
    @Id
    private String id;

    private String name;
    private String tags; // 存储为JSON字符串
    private Integer memberCount;
    private String activityType;
    private String intro;
    private String avatar;
    private String creator;
    private LocalDateTime createTime;
    private String status; // pending/approved/rejected


}