package com.cyd.xs.entity.User.Group;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_dynamics")
public class GroupDynamic {
    @Id
    private String id;

    private String groupId;
    private String userId;
    private String nickname;
    private String avatar;
    private String title;
    private String content;
    private String imageUrls; // 存储为JSON字符串
    private String tags; // 存储为JSON字符串
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime publishTime;
    private String status; // pending/approved/rejected
}