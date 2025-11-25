package com.cyd.xs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.User.HomeContent.HomeContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HomeContentMapper extends BaseMapper<HomeContent> {

    @Select("SELECT * FROM home_contents ORDER BY created_at DESC LIMIT #{limit}")
    List<HomeContent> findRecentContents(int limit);

}
