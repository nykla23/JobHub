package com.cyd.xs.Utils;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

public class ExpertSqlProvider {

    /**
     * 专家分页列表 SQL
     */
    public String listExpertByPage(Map<String, Object> params) {

        String keyword = (String) params.get("keyword");
        String tag = (String) params.get("tag");
        String sort = (String) params.get("sort");

        Integer pageSize = (Integer) params.get("pageSize");
        Integer offset = (Integer) params.get("offset");

        SQL sql = new SQL()
                .SELECT("""
                    e.id,
                    e.user_id AS userId,
                    e.certification,
                    e.expertise,
                    e.score,
                    e.consult_count AS consultCount,
                    e.intro,
                    u.display_name AS name,
                    u.avatar_url AS avatar
                """)
                .FROM("experts e")
                .LEFT_OUTER_JOIN("users u ON e.user_id = u.id")
                .WHERE("e.status = 'ACTIVE'");

        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            sql.WHERE("""
                (
                  u.display_name LIKE CONCAT('%', #{keyword}, '%')
                  OR e.certification LIKE CONCAT('%', #{keyword}, '%')
                )
            """);
        }

        // 标签（单值枚举，等值匹配）
        if (tag != null && !tag.isEmpty()) {
            sql.WHERE("e.expertise = #{tag}");
        }

        // 排序
        if ("consult".equals(sort)) {
            sql.ORDER_BY("e.consult_count DESC");
        } else {
            sql.ORDER_BY("e.score DESC");
        }

        // MyBatis SQL Builder 不支持 LIMIT，需要手拼
        return sql.toString() + " LIMIT " + pageSize + " OFFSET " + offset;
    }
}
