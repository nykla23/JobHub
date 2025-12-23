package com.cyd.xs.dto.Topic.vo;

import lombok.Data;
import java.time.LocalDateTime;
import com.cyd.xs.entity.Topic.Topic;

@Data
public class TopicInfoVO {

    private Long id;
    private String title;
    private String level;
    private Integer participantCount;
    private Integer interactionCount;
    private LocalDateTime latestReplyTime;

    public static TopicInfoVO from(Topic topic) {
        TopicInfoVO vo = new TopicInfoVO();
        vo.setId(topic.getId());
        vo.setTitle(topic.getTitle());
        vo.setLevel(topic.getLevel());
        vo.setParticipantCount(topic.getParticipantCount());
        vo.setInteractionCount(topic.getInteractiveCount());
        vo.setLatestReplyTime(topic.getLatestReplyTime());
        return vo;
    }
}
