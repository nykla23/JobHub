package com.cyd.xs.mapper;
import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.dto.expert.DTO.ExpertInfoDTO;
import com.cyd.xs.dto.expert.VO.ExpertVO;
import com.cyd.xs.entity.Expert.Expert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface ExpertMapper extends BaseMapper<Expert> {

    /**
     * 专家分页列表（最终稳定版）
     */
    @Select("""
        SELECT
          e.id,
          e.user_id AS userId,
          e.certification,
          e.expertise,
          e.score,
          e.consult_count AS consultCount,
          e.intro,
          u.display_name AS name,
          u.avatar_url AS avatar
        FROM experts e
        LEFT JOIN users u ON e.user_id = u.id
        WHERE e.status = 'ACTIVE'
          AND (
            #{keyword} IS NULL OR #{keyword} = ''
            OR u.display_name LIKE CONCAT('%', #{keyword}, '%')
            OR e.certification LIKE CONCAT('%', #{keyword}, '%')
          )
          AND (
            #{tag} IS NULL OR #{tag} = ''
            OR e.expertise = #{tag}
          )
        ORDER BY e.score DESC
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<ExpertVO> listExpertByPage(
            @Param("keyword") String keyword,
            @Param("tag") String tag,
            @Param("sort") String sort,   // 保留参数，不用也没问题
            @Param("pageSize") Integer pageSize,
            @Param("offset") Integer offset
    );

    /**
     * 统计专家总数
     */
    @Select("""
        SELECT COUNT(e.id)
        FROM experts e
        LEFT JOIN users u ON e.user_id = u.id
        WHERE e.status = 'ACTIVE'
          AND (
            #{keyword} IS NULL OR #{keyword} = ''
            OR u.display_name LIKE CONCAT('%', #{keyword}, '%')
            OR e.certification LIKE CONCAT('%', #{keyword}, '%')
          )
          AND (
            #{tag} IS NULL OR #{tag} = ''
            OR e.expertise = #{tag}
          )
    """)
    Integer countExpertTotal(
            @Param("keyword") String keyword,
            @Param("tag") String tag
    );

    /**
     * 专家详情
     */
    @Select("""
        SELECT 
            e.id,
            e.user_id AS userId,
            e.certification,
            e.expertise,
            e.score,
            e.consult_count AS consultCount,
            e.intro,
            u.display_name AS name,
            u.avatar_url AS avatar
        FROM experts e
        LEFT JOIN users u ON e.user_id = u.id
        WHERE e.id = #{expertId}
          AND e.status = 'ACTIVE'
    """)
    ExpertInfoDTO selectExpertInfoById(@Param("expertId") Long expertId);
}
