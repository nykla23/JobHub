package com.cyd.xs.entity.User;

import lombok.Data;

// 公开统计（public_stats_json）
@Data
public class UserPublicStats {
    private Integer likesReceived = 0;     // 获赞数
    private Integer postsCount = 0;        // 发布内容数
    private Integer followersCount = 0;    // 粉丝数

    public Integer getLikesReceived() {
        return likesReceived;
    }

    public void setLikesReceived(Integer likesReceived) {
        this.likesReceived = likesReceived;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }
}