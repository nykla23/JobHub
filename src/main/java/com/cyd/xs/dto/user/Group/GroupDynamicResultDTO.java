package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupDynamicResultDTO {
    private String dynamicId;
    private String status;
    private LocalDateTime submitTime;
}