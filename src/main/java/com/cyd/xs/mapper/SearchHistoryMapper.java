package com.cyd.xs.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SearchHistoryMapper {

    @Insert("INSERT INTO search_histories (id, user_id, keyword, created_at) VALUES (#{id}, #{userId}, #{keyword}, NOW())")
    void saveSearchHistory(String id, String userId, String keyword);

    @Insert("INSERT INTO search_histories (user_id, keyword, created_at) VALUES (#{userId}, #{keyword}, NOW())")
    void saveSearchHistory(String userId, String keyword);

    @Select("SELECT keyword FROM search_histories WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<String> findRecentSearchHistory(String userId, int limit);

    @Select("SELECT keyword FROM search_histories GROUP BY keyword ORDER BY COUNT(*) DESC, MAX(created_at) DESC LIMIT #{limit}")
    List<String> findHotKeywords(int limit);

    @Delete("DELETE FROM search_histories WHERE user_id = #{userId}")
    int deleteByUserId(String userId);
}