package com.cyd.xs.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.cyd.xs.config.JwtConfig;
import com.cyd.xs.dto.user.LoginResponseDTO;
import com.cyd.xs.dto.user.UserLoginDTO;
import com.cyd.xs.dto.user.UserRegisterDTO;
import com.cyd.xs.entity.User.UserPrivacy;
import com.cyd.xs.entity.User.UserProfile;
import com.cyd.xs.entity.User.UserPublicStats;
import com.cyd.xs.entity.User.UserSensitive;
import com.cyd.xs.exception.BusinessException;
import com.cyd.xs.mapper.UserMapper;
import com.cyd.xs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.cyd.xs.entity.User.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 注册核心逻辑：参数校验 → 密码加密 → 组装用户信息 → 保存数据库
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterDTO registerDTO) {
        // 1. 校验用户名是否已存在
        if (checkUsernameExists(registerDTO.getUsername())) {
            throw new BusinessException("用户名已被占用");
        }

        // 2. 组装User实体（基础字段）
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        // 密码加密（BCrypt不可逆加密）
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        // 昵称默认等于用户名（如果未传）
        user.setDisplayName(Convert.toStr(registerDTO.getDisplayName(), registerDTO.getUsername()));
        user.setAvatarUrl(registerDTO.getAvatarUrl());
        user.setRole("USER"); // 默认角色：普通用户
        user.setStatus("ACTIVE"); // 默认状态：激活
        user.setCreditScore(100); // 初始信用分：100

        // 3. 组装JSON字段（通过Hutool JSONUtil转换为字符串）
        // 3.1 扩展信息（profile_json）
        UserProfile profile = new UserProfile();
        profile.setBio(registerDTO.getBio());
        profile.setCareerStage(registerDTO.getCareerStage());
        profile.setFields(registerDTO.getFields());
        profile.setLocation(registerDTO.getLocation());
        profile.setEducation(registerDTO.getEducation());
        user.setProfileJson(JSONUtil.toJsonStr(profile));

        // 3.2 隐私设置（privacy_json，默认值）
        UserPrivacy privacy = new UserPrivacy();
        user.setPrivacyJson(JSONUtil.toJsonStr(privacy));

        // 3.3 公开统计（public_stats_json，初始值）
        UserPublicStats publicStats = new UserPublicStats();
        user.setPublicStats(JSONUtil.toJsonStr(publicStats));

        // 3.4 敏感信息（sensitive_json，初始为空，后续用户自行完善）
        user.setSensitiveJson(JSONUtil.toJsonStr(new UserSensitive()));

        // 4. 保存用户到数据库
        userMapper.insert(user);
        return user.getId();
    }

    /**
     * 登录核心逻辑：查询用户 → 密码校验 → 生成JWT令牌
     */
    @Override
    public LoginResponseDTO login(UserLoginDTO loginDTO) {
        // 1. 根据用户名查询用户（未删除+状态正常）
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 2. 校验密码（BCrypt匹配明文与加密后的密码）
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 生成JWT令牌
        String token = jwtConfig.generateToken(user.getId().toString(), user.getRole());
        LocalDateTime expireTime = jwtConfig.getExpireTime();

        // 4. 组装响应DTO
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setUserId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setDisplayName(user.getDisplayName());
        responseDTO.setAvatarUrl(user.getAvatarUrl());
        responseDTO.setRole(user.getRole());
        responseDTO.setToken(token);
        responseDTO.setExpireTime(expireTime);

        return responseDTO;
    }

    /**
     * 校验用户名是否已存在
     */
    @Override
    public boolean checkUsernameExists(String username) {
        Integer count = userMapper.countByUsername(username);
        return count != null && count > 0;
    }
}