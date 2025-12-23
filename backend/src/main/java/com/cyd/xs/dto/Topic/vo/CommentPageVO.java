package com.cyd.xs.dto.Topic.vo;

import lombok.Data;
import java.util.List;

@Data
public class CommentPageVO {

    /** 评论列表 */
    private List<TopicPostVO> list;

    private Integer pageNum;
    private Integer pageSize;
    private Long total;
}
