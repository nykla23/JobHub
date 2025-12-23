package com.cyd.xs.mapper.Search;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;



import java.util.List;
import java.util.Map;

@Mapper
public interface SearchHistoryMapper {

    @Insert("""
        INSERT INTO user_search_history (user_id, keyword)
        VALUES (#{userId}, #{keyword})
    """)
    int saveSearchHistory(@Param("userId") Long userId,
                          @Param("keyword") String keyword);

    @Select("""
        SELECT keyword
        FROM user_search_history
        WHERE user_id = #{userId}
        ORDER BY id DESC
        LIMIT #{limit}
    """)
    List<String> findRecentSearchHistory(@Param("userId") Long userId,
                                         @Param("limit") int limit);

    @Delete("""
        DELETE FROM user_search_history
        WHERE user_id = #{userId}
    """)
    int deleteByUserId(@Param("userId") Long userId);

    @Select("""
        SELECT keyword
        FROM (
            SELECT keyword, COUNT(*) cnt
            FROM user_search_history
            GROUP BY keyword
            ORDER BY cnt DESC
            LIMIT #{limit}
        ) t
    """)
    List<String> findHotKeywords(@Param("limit") int limit);
}
