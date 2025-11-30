package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupJoinDTO {
    private String groupId;
    private LocalDateTime joinTime;
    private Integer memberCount;
}