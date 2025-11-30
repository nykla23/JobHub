package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupCreateResultDTO {
    private String groupId;
    private String status;
    private LocalDateTime submitTime;
}