package com.cyd.xs.entity.User.Topic;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "topics")
public class Topic {
    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    private String tag; // professionalShare/firstIntern/graduateJob/newbiePitfall/offerChoice
    private Integer participantCount = 0;
    private Integer interactiveCount = 0;
    private LocalDateTime latestReplyTime;
    private String guideText;
    private LocalDateTime createdAt;

    public Topic() {
    }

    public Topic(String id, String title, String tag, Integer participantCount, Integer interactiveCount, LocalDateTime latestReplyTime, String guideText, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.tag = tag;
        this.participantCount = participantCount;
        this.interactiveCount = interactiveCount;
        this.latestReplyTime = latestReplyTime;
        this.guideText = guideText;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    public Integer getInteractiveCount() {
        return interactiveCount;
    }

    public void setInteractiveCount(Integer interactiveCount) {
        this.interactiveCount = interactiveCount;
    }

    public LocalDateTime getLatestReplyTime() {
        return latestReplyTime;
    }

    public void setLatestReplyTime(LocalDateTime latestReplyTime) {
        this.latestReplyTime = latestReplyTime;
    }

    public String getGuideText() {
        return guideText;
    }

    public void setGuideText(String guideText) {
        this.guideText = guideText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
