package com.cyd.xs.service;

import com.cyd.xs.entity.User.User;
import com.cyd.xs.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户ID查询用户（与JWT中存入的subject对应）
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 根据用户ID查询数据库
        User dbUser = userMapper.selectById(Long.parseLong(userId));
        if (dbUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 组装Spring Security需要的UserDetails（角色需以"ROLE_"开头）
        return org.springframework.security.core.userdetails.User.withUsername(dbUser.getUsername())
                .password(dbUser.getPassword())
                .roles(dbUser.getRole()) // 角色（如"USER" → 自动转为"ROLE_USER"）
                .build();
    }
}