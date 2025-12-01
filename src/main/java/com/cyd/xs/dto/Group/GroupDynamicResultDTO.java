package com.cyd.xs.dto.Group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupDynamicResultDTO {
    private String dynamicId;
    private String status;
    private LocalDateTime submitTime;

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
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