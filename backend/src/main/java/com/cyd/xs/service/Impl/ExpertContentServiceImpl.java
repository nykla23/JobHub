package com.cyd.xs.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyd.xs.dto.expert.DTO.ExpertContentQueryDTO;
import com.cyd.xs.dto.expert.VO.ExpertContentPageVO;
import com.cyd.xs.dto.expert.VO.ExpertContentVO;
import com.cyd.xs.mapper.ExpertContentMapper;
import com.cyd.xs.service.ExpertContentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ExpertContentServiceImpl implements ExpertContentService {

    @Resource
    private ExpertContentMapper contentMapper;

    @Override
    public ExpertContentPageVO getExpertContentList(
            Long expertUserId,
            ExpertContentQueryDTO queryDTO
    ) {
        // 1️⃣ 参数校验
        queryDTO.validate();

        // 2️⃣ 分页对象（泛型必须和 Mapper / VO 一致）
        Page<ExpertContentVO> page =
                new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 3️⃣ 查询 professional_resources
        IPage<ExpertContentVO> resultPage =
                contentMapper.selectExpertContentPage(page, expertUserId);

        // 4️⃣ 组装 PageVO
        ExpertContentPageVO pageVO = new ExpertContentPageVO();
        pageVO.setTotal(resultPage.getTotal());
        pageVO.setPageNum(queryDTO.getPageNum());
        pageVO.setPageSize(queryDTO.getPageSize());
        pageVO.setList(resultPage.getRecords());

        return pageVO;
    }
}
