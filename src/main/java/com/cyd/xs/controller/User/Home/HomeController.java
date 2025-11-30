package com.cyd.xs.controller.User.Home;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.user.Home.HomeDTO;
import com.cyd.xs.dto.user.Search.SearchDTO;
import com.cyd.xs.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 获取首页数据（含热门活动、推荐内容）
     * 文档路径：GET /api/v1/home
     */
    @GetMapping
    public ResponseEntity<Result<?>> getHomeData(Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            HomeDTO homeDTO = homeService.getHomeData(userId);
            return ResponseEntity.ok(Result.success("获取成功", homeDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取首页数据失败"));
        }
    }

    /**
     * 推荐内容换一批
     * 文档路径：GET /api/v1/home/recommend/refresh
     */
    @GetMapping("/recommend/refresh")
    public ResponseEntity<Result<?>> refreshRecommend(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            RecommendRefreshDTO result = homeService.refreshRecommend(userId, pageNum, pageSize);
            return ResponseEntity.ok(Result.success("推荐内容刷新成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("推荐内容刷新失败"));
        }
    }

    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            return ((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()).getUsername();
        }
        throw new RuntimeException("用户未认证");
    }



//    //首次进入 APP - 身份标签选择
//    @PostMapping("/select-identity")
//    public ResponseEntity<Result<?>> selectIdentity(@RequestParam String identityType,
//                                                    @RequestParam String userId) {
//        try {
//            HomeDTO homeDTO = homeService.selectIdentity(identityType, userId);
//            return ResponseEntity.ok(Result.success("身份标签选择成功", homeDTO));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Result.error("身份标签选择失败"));
//        }
//    }
//
//    //全域搜索
//    @GetMapping("/search")
//    public ResponseEntity<Result<?>> search(@RequestParam String keyword,
//                                              @RequestParam(required = false) String tabType,
//                                              @RequestParam(required = false) Integer page,
//                                              @RequestParam(required = false) Integer pageSize,
//                                              @RequestParam String userId) {
//        try {
//            SearchDTO searchDTO = homeService.search(keyword, tabType, page, pageSize, userId);
//            return ResponseEntity.ok(Result.success("搜索成功", searchDTO));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Result.error("搜索失败"));
//        }
//    }
//
//    //清除搜索历史
//    @PostMapping("/clear-search-history")
//    public ResponseEntity<Result<?>> clearSearchHistory(@RequestParam String userId) {
//        try {
//            homeService.clearSearchHistory(userId);
//            return ResponseEntity.ok(Result.success("搜索历史清除成功"));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Result.error("清除失败"));
//        }
//    }
//
//    //首页推荐内容刷新
//    @GetMapping("/refresh-recommend")
//    public ResponseEntity<Result<?>> refreshRecommend(@RequestParam String userId,
//                                                      @RequestParam(required = false) Integer pageSize) {
//        try {
//            HomeDTO homeDTO = (HomeDTO) homeService.refreshRecommend(userId, pageSize);
//            return ResponseEntity.ok(Result.success("推荐内容刷新成功", homeDTO));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Result.error("推荐内容刷新失败"));
//        }
//    }


}
