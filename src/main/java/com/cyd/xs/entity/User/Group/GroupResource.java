package com.cyd.xs.entity.User.Group;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_resources")
public class GroupResource {
    @Id
    private String id;

    private String groupId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileName;
    private String fileSize;
    private String tag;
    private String uploader;
    private LocalDateTime uploadTime;
    private Integer downloadCount;
    private String status; // pending/approved/rejected
}