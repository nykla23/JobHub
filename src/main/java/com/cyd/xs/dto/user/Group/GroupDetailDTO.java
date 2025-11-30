package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupDetailDTO {
    private GroupInfo groupInfo;
    private GroupDynamic groupDynamic;
    private GroupResource groupResource;
    private GroupNotice groupNotice;

    @Data
    public static class GroupInfo {
        private String id;
        private String name;
        private List<String> tags;
        private Integer memberCount;
        private String activityType;
        private String intro;
        private String avatar;
        private String creator;
        private LocalDateTime createTime;
        private Boolean isJoined;
        private Boolean isManager;

        public void setJoined(boolean b) {
            isJoined = b;
        }

        public void setManager(boolean b) {
            isManager = b;
        }
    }

    @Data
    public static class GroupDynamic {
        private Long total;
        private Integer pageNum;
        private Integer pageSize;
        private List<DynamicItem> list;
    }

    @Data
    public static class DynamicItem {
        private String id;
        private String userId;
        private String nickname;
        private String avatar;
        private String title;
        private String content;
        private LocalDateTime publishTime;
        private Integer likeCount;
        private Integer commentCount;
        private List<String> imageUrls;
    }

    @Data
    public static class GroupResource {
        private Long total;
        private Integer pageNum;
        private Integer pageSize;
        private List<ResourceItem> list;
    }

    @Data
    public static class ResourceItem {
        private String id;
        private String title;
        private String type;
        private String uploader;
        private LocalDateTime uploadTime;
        private Integer downloadCount;
        private String size;
        private String link;
    }

    @Data
    public static class GroupNotice {
        private Long total;
        private List<NoticeItem> list;
    }

    @Data
    public static class NoticeItem {
        private String id;
        private String title;
        private String content;
        private LocalDateTime publishTime;
    }
}