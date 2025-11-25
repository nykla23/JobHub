package com.cyd.xs.dto.user.Circle;

import lombok.Data;
import java.util.List;

@Data
public class CircleDTO {
    private List<Category> categories;
    private Tools tools;

    @Data
    public static class Category {
        private String key;
        private String name;
    }

    @Data
    public static class Tools {
        private String searchUrl;
        private String createGroupUrl;
    }
}
