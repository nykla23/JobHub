package com.cyd.xs.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cyd.xs.dto.expert.VO.ExpertContentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 专家内容管理 Mapper
 * 数据来源：professional_resources
 */
@Mapper
public interface ExpertContentMapper {

    /**
     * 分页查询专家发布的专业资源
     *
     * @param page 分页参数（MyBatis-Plus）
     * @param expertUserId experts.id
     * @return 分页结果（ExpertContentVO）
     */
    @Select("""
        SELECT
            r.id                     AS id,
            r.title                  AS title,
            'resource'               AS type,
            r.score                  AS score,
            r.view_count             AS viewCount,
            r.collect_count          AS collectCount,
            r.created_at             AS publishTime
        FROM professional_resources r
        WHERE r.expert_id = #{expertUserId}
        ORDER BY r.created_at DESC
    """)
    IPage<ExpertContentVO> selectExpertContentPage(
            Page<ExpertContentVO> page,
            @Param("expertUserId") Long expertUserId
    );
}
