package com.cyd.xs.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtConfig {
    @Value("${jwt.secret}") // 配置在application.yml中
    private String secret;

    @Value("${jwt.expire-hours:24}") // 默认过期时间：24小时
    private int expireHours;

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param role 角色
     * @return 令牌字符串
     */
    public String generateToken(String userId, String role) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes()); // 密钥必须足够长（至少256位）
        Date expireDate = Date.from(
                LocalDateTime.now().plusHours(expireHours)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        return Jwts.builder()
                .setSubject(userId) // 存入用户ID
                .claim("role", role) // 存入角色
                .setExpiration(expireDate) // 过期时间
                .signWith(key) // 签名
                .compact();
    }

    /**
     * 解析令牌，获取Claims（包含用户ID、角色等信息）
     */
    public Claims parseToken(String token) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取令牌过期时间
     */
    public LocalDateTime getExpireTime() {
        return LocalDateTime.now().plusHours(expireHours);
    }
}