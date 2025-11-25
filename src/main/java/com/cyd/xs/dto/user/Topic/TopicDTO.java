package com.cyd.xs.dto.user.Topic;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TopicDTO {
    private List<TopicItem> data;
    private Integer total;
    private List<Tab> tabs;

    @Data
    public static class TopicItem {
        private String topicId;
        private String title;
        private String tag;
        private Integer participantCount;
        private Integer interactiveCount;
        private LocalDateTime latestReplyTime;
    }

    @Data
    public static class Tab {
        private String key;
        private String name;
    }

    public List<TopicItem> getData() {
        return data;
    }

    public void setData(List<TopicItem> data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tab> tabs) {
        this.tabs = tabs;
    }
}
