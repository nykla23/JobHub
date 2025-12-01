package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupResourceResultDTO {
    private String resourceId;
    private String status;
    private LocalDateTime submitTime;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }
}