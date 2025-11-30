package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.util.List;

@Data
public class GroupDTO {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<GroupItem> list;

    @Data
    public static class GroupItem {
        private String id;
        private String name;
        private List<String> tags;
        private Integer memberCount;
        private String activityType;
        private String intro;
        private String avatar;
        private Boolean isJoined;

        public void setJoined(boolean b) {
            isJoined = b;
        }

    }
}