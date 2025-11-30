package com.cyd.xs.entity.User.Group;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "group_members")
public class GroupMember {
    @Id
    private String id;

    private String groupId;
    private String userId;
    private String role; // member/manager/creator
    private LocalDateTime joinTime;
}