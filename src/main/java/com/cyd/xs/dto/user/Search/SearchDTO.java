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

    // 新增字段 - 修复报错
    private List<TopicResult> topic;           // 话题搜索结果
    private List<ContentResult> content;       // 内容搜索结果
    private List<String> hotKeywords;          // 热门关键词
    private List<String> historyKeywords;      // 历史搜索关键词

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

        // 新增字段 - 修复报错
        private String topicId;  // 用于 setTopicId 方法

        public Long getTopicId() {
            return topicId != null ? Long.valueOf(topicId) : null;
        }

        public void setTopicId(Long topicId) {
            this.topicId = topicId != null ? topicId.toString() : null;
        }
    }

    // 内容搜索结果
    @Data
    public static class ContentResult {
        private String id;
        private String title;
        private String author;
        private String link;

        // 新增字段 - 修复报错
        private String contentId;  // 用于 setContentId 方法

        public Long getContentId() {
            return contentId != null ? Long.valueOf(contentId) : null;
        }

        public void setContentId(Long contentId) {
            this.contentId = contentId != null ? contentId.toString() : null;
        }
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

    // 原有的 getter 和 setter 保持不变
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }

    public List<String> getSearchHistory() {
        return searchHistory;
    }

    public void setSearchHistory(List<String> searchHistory) {
        this.searchHistory = searchHistory;
    }

    public List<String> getHotSearch() {
        return hotSearch;
    }

    public void setHotSearch(List<String> hotSearch) {
        this.hotSearch = hotSearch;
    }

    // 新增的 getter 和 setter
    public List<TopicResult> getTopic() {
        return topic;
    }

    public void setTopic(List<TopicResult> topic) {
        this.topic = topic;
    }

    public List<ContentResult> getContent() {
        return content;
    }

    public void setContent(List<ContentResult> content) {
        this.content = content;
    }

    public List<String> getHotKeywords() {
        return hotKeywords;
    }

    public void setHotKeywords(List<String> hotKeywords) {
        this.hotKeywords = hotKeywords;
    }

    public List<String> getHistoryKeywords() {
        return historyKeywords;
    }

    public void setHistoryKeywords(List<String> historyKeywords) {
        this.historyKeywords = historyKeywords;
    }
}