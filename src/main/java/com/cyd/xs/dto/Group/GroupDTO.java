package com.cyd.xs.dto.Group;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
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

        public Boolean getJoined() {
            return isJoined;
        }

        public void setJoined(Boolean joined) {
            isJoined = joined;
        }

        private String activityType;
        private String intro;
        private String avatar;
        private Boolean isJoined;

        public void setJoined(boolean b) {
            isJoined = b;
        }

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

    public List<GroupItem> getList() {
        return list;
    }

    public void setList(List<GroupItem> list) {
        this.list = list;
    }
}