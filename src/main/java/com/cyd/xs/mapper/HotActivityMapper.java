package com.cyd.xs.mapper;


import com.cyd.xs.entity.User.HotActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HotActivityMapper {

    @Select("SELECT * FROM hot_activities WHERE status = 'active' ORDER BY participant_count DESC, created_at DESC LIMIT #{limit}")
    List<HotActivity> findHotActivities(int limit);
}