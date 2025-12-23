package com.cyd.xs.dto.expert.VO;

import lombok.Data;

import java.util.List;

/**
 * 专家内容管理分页结果VO
 */
@Data
public class ExpertContentPageVO {

    private Long total;         // 总记录数
    private Integer pageNum;    // 当前页码
    private Integer pageSize;   // 每页条数

    // ⭐ 关键修改点：ItemVO → VO
    private List<ExpertContentVO> list;
}
