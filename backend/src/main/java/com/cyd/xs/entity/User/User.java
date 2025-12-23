package com.cyd.xs.entity.User;

/*
 * 用户实体类
 */

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
    private String identityTag;
    public User() {
    }

    public User(String username, String password, String displayName, String avatarUrl, String profileJson, String privacyJson, String publicStats, String sensitiveJson, String role, String status, Integer creditScore, String tier, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.username = username;

        this.password = password;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.profileJson = profileJson;
        this.privacyJson = privacyJson;
        this.publicStats = publicStats;
        this.sensitiveJson = sensitiveJson;
        this.role = role;
        this.status = status;
        this.creditScore = creditScore;
        this.tier = tier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}