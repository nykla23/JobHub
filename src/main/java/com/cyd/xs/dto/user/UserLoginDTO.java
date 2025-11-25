package com.cyd.xs.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;    // 登录名
    @NotBlank(message = "密码不能为空")
    private String password;    // 密码（明文）

    public @NotBlank(message = "用户名不能为空") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "用户名不能为空") String username) {
        this.username = username;
    }

    public @NotBlank(message = "密码不能为空") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "密码不能为空") String password) {
        this.password = password;
    }
}