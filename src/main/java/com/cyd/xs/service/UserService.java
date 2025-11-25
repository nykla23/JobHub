package com.cyd.xs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyd.xs.dto.user.LoginResponseDTO;
import com.cyd.xs.dto.user.UserLoginDTO;
import com.cyd.xs.dto.user.UserRegisterDTO;
import com.cyd.xs.entity.User.User;


public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param registerDTO 注册参数
     * @return 注册成功的用户ID
     */
    Long register(UserRegisterDTO registerDTO);

    /**
     * 用户登录（生成JWT令牌）
     * @param loginDTO 登录参数
     * @return 登录响应（含令牌）
     */
    LoginResponseDTO login(UserLoginDTO loginDTO);

    /**
     * 校验用户名是否已存在
     * @param username 用户名
     * @return true=已存在，false=不存在
     */
    boolean checkUsernameExists(String username);
}