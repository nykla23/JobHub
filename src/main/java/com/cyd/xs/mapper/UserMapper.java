package com.cyd.xs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.cyd.xs.entity.User.User;

@Mapper
public interface UserMapper extends BaseMapper<com.cyd.xs.entity.User.User> {
    // 根据用户名查询用户（登录/注册校验用）
    com.cyd.xs.entity.User.User selectByUsername(@Param("username") String username);

    // 校验用户名是否已存在
    Integer countByUsername(@Param("username") String username);
}