package com.cyd.xs.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 64, message = "用户名长度为4-64位")
    private String username;        // 登录名（唯一）

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度为6-32位")
    private String password;        // 密码（明文，后端加密存储）

    @Size(max = 64, message = "昵称长度不超过64位")
    private String displayName;     // 显示昵称（可为空，默认等于用户名）

    private String avatarUrl;       // 头像地址（可为空）

    // 扩展信息（注册时可选填）
    private String bio;
    private String careerStage;
    private List<String> fields;
    private String location;
    private String education;

    public @NotBlank(message = "用户名不能为空") @Size(min = 4, max = 64, message = "用户名长度为4-64位") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "用户名不能为空") @Size(min = 4, max = 64, message = "用户名长度为4-64位") String username) {
        this.username = username;
    }

    public @NotBlank(message = "密码不能为空") @Size(min = 6, max = 32, message = "密码长度为6-32位") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "密码不能为空") @Size(min = 6, max = 32, message = "密码长度为6-32位") String password) {
        this.password = password;
    }

    public @Size(max = 64, message = "昵称长度不超过64位") String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Size(max = 64, message = "昵称长度不超过64位") String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCareerStage() {
        return careerStage;
    }

    public void setCareerStage(String careerStage) {
        this.careerStage = careerStage;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}