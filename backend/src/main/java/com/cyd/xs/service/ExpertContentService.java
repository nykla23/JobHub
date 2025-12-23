package com.cyd.xs.service;

import com.cyd.xs.dto.expert.DTO.ExpertContentQueryDTO;
import com.cyd.xs.dto.expert.VO.ExpertContentPageVO;

public interface ExpertContentService {

    /**
     * 获取专家内容管理列表
     * @param expertUserId 专家ID（experts.id）
     * @param queryDTO 筛选 + 分页参数
     */
    ExpertContentPageVO getExpertContentList(
            Long expertUserId,
            ExpertContentQueryDTO queryDTO
    );
}
