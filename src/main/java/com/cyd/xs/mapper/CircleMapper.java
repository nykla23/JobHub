package com.cyd.xs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.User.Circle.Circle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CircleMapper extends BaseMapper<Circle> {

    @Select("SELECT * FROM circles WHERE group_name LIKE CONCAT('%', #{keyword}, '%') OR JSON_CONTAINS(tags, JSON_QUOTE(#{keyword})) LIMIT #{pageSize} OFFSET #{offset}")
    List<Circle> searchCircles(String keyword, int offset, int pageSize);

    @Select("SELECT COUNT(*) FROM circles WHERE group_name LIKE CONCAT('%', #{keyword}, '%') OR JSON_CONTAINS(tags, JSON_QUOTE(#{keyword}))")
    int countSearchCircles(String keyword);

    @Select("SELECT * FROM circles ORDER BY member_count DESC LIMIT #{limit}")
    List<Circle> findRecommendedCircles(int limit);
}
