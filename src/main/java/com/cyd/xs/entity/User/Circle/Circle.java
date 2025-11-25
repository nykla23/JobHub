package com.cyd.xs.entity.User.Circle;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "circles")
public class Circle {
    @Id
    private String id;

    @Column(nullable = false)
    private String groupName;

    @Column(columnDefinition = "JSON")
    private String tags;

    private Integer memberCount = 0;
    private String latestActivity;
    private String intro;
    private String joinType; // free/audit
    private String createdBy;
    private LocalDateTime createdAt;

    public Circle() {
    }

    public Circle(String id, String groupName, String tags, Integer memberCount, String latestActivity, String intro, String joinType, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.groupName = groupName;
        this.tags = tags;
        this.memberCount = memberCount;
        this.latestActivity = latestActivity;
        this.intro = intro;
        this.joinType = joinType;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getLatestActivity() {
        return latestActivity;
    }

    public void setLatestActivity(String latestActivity) {
        this.latestActivity = latestActivity;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}