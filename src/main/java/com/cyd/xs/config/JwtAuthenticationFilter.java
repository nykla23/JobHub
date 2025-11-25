package com.cyd.xs.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // 1. 获取请求头中的JWT令牌
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        token = token.substring(7); // 去掉 "Bearer " 前缀

        // 2. 解析令牌，获取用户ID
        Claims claims = jwtConfig.parseToken(token);
        String userId = claims.getSubject();

        // 3. 如果用户未认证，自动完成认证
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 这里简化：UserDetailsService可自定义，从数据库查询用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
            // 创建认证令牌，存入SecurityContext
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 4. 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }
}