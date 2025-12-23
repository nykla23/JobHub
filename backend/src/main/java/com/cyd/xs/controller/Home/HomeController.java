package com.cyd.xs.controller.Home;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.Home.HomeDTO;
import com.cyd.xs.dto.Home.RecommendRefreshDTO;
import com.cyd.xs.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cyd.xs.service.UserService;
import com.cyd.xs.Utils.ResultVO;
import jakarta.annotation.Resource;
@Slf4j
@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {
    @Resource
    private UserService userService;
    private final HomeService homeService;


    /**
     * 获取首页数据（含热门活动、推荐内容）
     * 文档路径：GET /api/v1/home
     */
    @GetMapping
    public ResponseEntity<Result<?>> getHomeData(
            @RequestParam(defaultValue = "1") Integer pageNum,
            Authentication authentication) {

        Long userId = getUserIdFromAuth(authentication);

        HomeDTO homeDTO = homeService.getHomeData(userId);

        return ResponseEntity.ok(Result.success("获取成功", homeDTO));
    }




    /**
     * 推荐内容换一批
     * 文档路径：GET /api/v1/home/recommend/refresh
     */
    @GetMapping("/recommend/refresh")
    public ResponseEntity<Result<RecommendRefreshDTO>> refreshRecommend(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            Authentication authentication) {
        try {

            // 获取用户ID，允许未登录用户访问
            Long userId = getUserIdFromAuth(authentication);

//            String userId = getUserIdFromAuthentication(authentication);
            RecommendRefreshDTO result = homeService.refreshRecommend(userId, pageNum, pageSize);
            return ResponseEntity.ok(Result.success("推荐内容刷新成功", result));
        } catch (Exception e) {
            System.err.println("推荐内容刷新失败: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Result.error("推荐内容刷新失败"));        }
    }

    /**
     * 获取用户ID，支持未认证用户
     */
    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            System.out.println("认证用户: " + username);
            // 这里需要根据实际情况从用户名获取用户ID
            // 假设用户名就是用户ID，或者可以从数据库查询
            return username;
        } else {
            System.out.println("未认证用户，使用默认用户ID");
            // 未认证用户返回默认ID，或者返回null
            // 根据业务需求，可以返回一个默认的匿名用户ID
            return "anonymous"; // 或者返回 "guest"，根据业务调整
        }
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName(); // 123@163.com
        return userService.getUserIdByUsername(username);
    }

}
