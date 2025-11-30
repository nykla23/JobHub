package com.cyd.xs.entity.User.Group;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_notices")
public class GroupNotice {
    @Id
    private String id;

    private String groupId;
    private String title;
    private String content;
    private String publisher;
    private LocalDateTime publishTime;
}