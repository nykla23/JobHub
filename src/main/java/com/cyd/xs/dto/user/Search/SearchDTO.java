package com.cyd.xs.dto.user.Search;

import lombok.Data;
import java.util.List;

@Data
public class SearchDTO {
    private List<TopicResult> topic;
    private List<ContentResult> content;
    private List<String> hotKeywords;
    private List<String> historyKeywords;

    @Data
    public static class TopicResult {
        private String topicId;
        private String title;
        private Integer participantCount;
    }

    @Data
    public static class ContentResult {
        private String contentId;
        private String title;
        private String author;
    }

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
