package com.cyd.xs.dto.user.Search;

import lombok.Data;
import java.util.List;

@Data
public class SearchDTO {
    private String keyword;      // 搜索关键词
    private String type;         // 结果类型
    private Long total;          // 总记录数
    private Integer pageNum;     // 当前页
    private Integer pageSize;    // 每页条数
    private List<Object> list;   // 搜索结果列表

    // 搜索历史和热门搜索字段
    private List<String> searchHistory;  // 个人搜索历史
    private List<String> hotSearch;      // 热门搜索

    // 话题搜索结果
    @Data
    public static class TopicResult {
        private String id;
        private String title;
        private String desc;
        private Integer hotValue;
        private List<String> tags;
        private Integer participantCount;
        private String link;
    }

    // 内容搜索结果
    @Data
    public static class ContentResult {
        private String id;
        private String title;
        private String author;
        private String link;
    }

    // 圈子搜索结果
    @Data
    public static class GroupResult {
        private String id;
        private String name;
        private List<String> tags;
        private Integer memberCount;
        private String intro;
        private String avatar;
        private Boolean isJoined;
        private String link;
    }

    // 用户搜索结果
    @Data
    public static class UserResult {
        private String id;
        private String nickname;
        private String identity;
        private String avatar;
        private String intro;
        private String link;
    }

    // 专家搜索结果
    @Data
    public static class ExpertResult {
        private String id;
        private String name;
        private String avatar;
        private String certification;
        private String expertise;
        private Double score;
        private Integer consultCount;
        private String intro;
        private String link;
    }


}