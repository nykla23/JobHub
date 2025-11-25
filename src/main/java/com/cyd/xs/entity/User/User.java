package com.cyd.xs.entity.User;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID
    private String username;        // 登录名（唯一）
    private String password;        // 加密后的密码（BCrypt）
    private String displayName;     // 显示昵称
    private String avatarUrl;       // 头像地址
    private String profileJson;     // 扩展信息（JSON字符串）
    private String privacyJson;     // 隐私设置（JSON字符串）
    private String publicStats; // 公开统计（JSON字符串）
    private String sensitiveJson;   // 敏感信息（加密存储）
    private String role;            // 角色（USER/ADMIN/EXPERT）
    private String status;          // 状态（ACTIVE/BANNED/PENDING）
    private Integer creditScore;    // 信用分（初始100）
    private String tier;            // 用户分层标签
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public User() {
    }

    public User(Long id, String username, String displayName, String avatarUrl, String profileJson, String privacyJson, String publicStatsJson, String sensitiveJson, String role, String status, Integer creditScore, String tier, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.profileJson = profileJson;
        this.privacyJson = privacyJson;
        this.publicStats = publicStatsJson;
        this.sensitiveJson = sensitiveJson;
        this.role = role;
        this.status = status;
        this.creditScore = creditScore;
        this.tier = tier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getProfileJson() {
        return profileJson;
    }

    public void setProfileJson(String profileJson) {
        this.profileJson = profileJson;
    }

    public String getPrivacyJson() {
        return privacyJson;
    }

    public void setPrivacyJson(String privacyJson) {
        this.privacyJson = privacyJson;
    }

    public String getPublicStats() {
        return publicStats;
    }

    public void setPublicStats(String publicStats) {
        this.publicStats = publicStats;
    }

    public String getSensitiveJson() {
        return sensitiveJson;
    }

    public void setSensitiveJson(String sensitiveJson) {
        this.sensitiveJson = sensitiveJson;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}