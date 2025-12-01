package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GroupJoinDTO {
    private String groupId;
    private LocalDateTime joinTime;
    private Integer memberCount;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }
}