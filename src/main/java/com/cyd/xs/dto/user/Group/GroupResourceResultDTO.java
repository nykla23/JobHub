package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupResourceResultDTO {
    private String resourceId;
    private String status;
    private LocalDateTime submitTime;
}