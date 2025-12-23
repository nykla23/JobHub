package com.cyd.xs.entity.Topic;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("topics")
public class Topic {

    @TableId
    private Long id;

    private String title;
    private String level;
    private String tag;

    private Integer participantCount;
    private Integer interactiveCount;

    private LocalDateTime latestReplyTime;
    private String guideText;
    private String host;
    private LocalDateTime createdAt;
}
