package com.cyd.xs.dto.Topic.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TopicPostVO {
    private Long id;
    private String content;
    private String nickname;
    private LocalDateTime publishTime;
    private Integer likeCount;
}
