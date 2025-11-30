package com.cyd.xs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyd.xs.entity.User.Home.HomeContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HomeContentMapper extends BaseMapper<HomeContent> {

    @Select("SELECT * FROM home_contents ORDER BY created_at DESC LIMIT #{limit}")
    List<HomeContent> findRecentContents(@Param("limit") int limit);

    @Select("SELECT * FROM home_contents WHERE status = 'published' ORDER BY hot_value DESC, created_at DESC LIMIT #{limit}")
    List<HomeContent> findRecommendedContents(@Param("limit") int limit);

    @Select("SELECT * FROM home_contents WHERE status = 'published' ORDER BY hot_value DESC, created_at DESC LIMIT #{offset}, #{pageSize}")
    List<HomeContent> findRecommendedContentsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM home_contents WHERE status = 'published'")
    Long countPublishedContents();

    @Select("SELECT * FROM home_contents WHERE (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) AND status = 'published' ORDER BY ${sort} DESC LIMIT #{offset}, #{pageSize}")
    List<HomeContent> searchContents(@Param("keyword") String keyword, @Param("sort") String sort, @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT COUNT(*) FROM home_contents WHERE (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) AND status = 'published'")
    Long countSearchContents(@Param("keyword") String keyword);
}