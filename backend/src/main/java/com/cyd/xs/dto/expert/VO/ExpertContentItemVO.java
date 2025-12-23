package com.cyd.xs.dto.expert.VO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExpertContentItemVO {

    private Long id;
    private String title;
    private String type;        // 固定 resource
    private Double score;
    private Long viewCount;
    private Integer collectCount;
    private LocalDateTime publishTime;
}
