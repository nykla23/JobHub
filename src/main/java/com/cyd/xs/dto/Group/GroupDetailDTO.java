package com.cyd.xs.dto.Group;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public Integer getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(Integer memberCount) {
            this.memberCount = memberCount;
        }

        public String getActivityType() {
            return activityType;
        }

        public void setActivityType(String activityType) {
            this.activityType = activityType;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }

        public Boolean getJoined() {
            return isJoined;
        }

        public void setJoined(Boolean joined) {
            isJoined = joined;
        }

        public Boolean getManager() {
            return isManager;
        }

        public void setManager(Boolean manager) {
            isManager = manager;
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