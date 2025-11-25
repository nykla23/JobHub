package com.cyd.xs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cyd.xs.entity.User.Circle.CircleMember;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CircleMemberMapper extends BaseMapper<CircleMember> {

    @Select("SELECT COUNT(*) FROM circle_members WHERE user_id = #{userId} AND circle_id = #{circleId}")
    boolean checkUserInCircle(String userId, String circleId);

    @Delete("DELETE FROM circle_members WHERE user_id = #{userId} AND circle_id = #{circleId}")
    int deleteByUserAndCircle(String userId, String circleId);
}
