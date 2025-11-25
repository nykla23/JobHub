package com.cyd.xs.controller.User;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.user.LoginResponseDTO;
import com.cyd.xs.dto.user.UserLoginDTO;
import com.cyd.xs.dto.user.UserRegisterDTO;
import com.cyd.xs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/user")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public Result<Long> register(@Validated @RequestBody UserRegisterDTO registerDTO) {
        Long userId = userService.register(registerDTO);
        return Result.success("注册成功", userId);
    }

    /**
     * 用户登录接口
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<LoginResponseDTO> login(@Validated @RequestBody UserLoginDTO loginDTO) {
        LoginResponseDTO loginResponse = userService.login(loginDTO);
        return Result.success("登录成功", loginResponse);
    }
}