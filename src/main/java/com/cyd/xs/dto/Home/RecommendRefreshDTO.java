package com.cyd.xs.dto.Home;

import lombok.Data;
import java.util.List;

@Data
public class RecommendRefreshDTO {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<ContentItem> list;

    @Data
    public static class ContentItem {
        private String id;
        private String title;
        private String type;
        private String author;
        private Integer likeCount;
        private Integer collectCount;
        private String publishTime;
        private String link;
    }
}