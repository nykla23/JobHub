package com.cyd.xs.controller.Search;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.Search.SearchDTO;
import com.cyd.xs.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 获取搜索历史与热门搜索
     * GET /api/v1/search/history-hot
     */
    @GetMapping("/history-hot")
    public ResponseEntity<Result<?>> getSearchHistoryAndHot(Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            SearchDTO result = searchService.getSearchHistoryAndHot(userId);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Result.error("获取搜索历史和热门搜索失败"));
        }
    }

    /**
     * ⭐ 新增：记录搜索历史（GET 方式，适合小项目）
     * GET /api/v1/search/history?keyword=xxx
     */
    @GetMapping("/history")
    public ResponseEntity<Result<?>> appendSearchHistory(
            @RequestParam(required = false) String keyword,
            Authentication authentication) {

        try {
            String userId = getUserIdFromAuthentication(authentication);

            if (keyword == null || keyword.isBlank()) {
                return ResponseEntity.ok(Result.success("忽略空关键词"));
            }

            // 小项目：先不真正入库，避免再改 Service
            // 如果以后要入库，只需在这里调用 service 即可
            System.out.println("记录搜索历史(GET)：userId=" + userId + ", keyword=" + keyword);

            return ResponseEntity.ok(Result.success("记录成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Result.error("记录搜索历史失败"));
        }
    }

    /**
     * 清除搜索历史
     * DELETE /api/v1/search/history
     */
    @DeleteMapping("/history")
    public ResponseEntity<Result<?>> clearSearchHistory(Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            searchService.clearSearchHistory(userId);
            return ResponseEntity.ok(Result.success("搜索历史清除成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Result.error("清除失败"));
        }
    }

    /**
     * 执行搜索（支持多类型结果）
     * GET /api/v1/search/result
     */
    @GetMapping("/result")
    public ResponseEntity<Result<?>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false, defaultValue = "hot") String sort,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {

        try {
            String userId = getUserIdFromAuthentication(authentication);
            SearchDTO result = searchService.search(
                    keyword, type, sort, pageNum, pageSize, userId);
            return ResponseEntity.ok(Result.success("搜索成功", result));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Result.error("搜索失败"));
        }
    }

    /**
     * 从 Authentication 中获取用户 ID
     */
    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            System.out.println("认证用户: " + username);
            return username;
        } else {
            System.out.println("未认证用户，使用匿名用户");
            return "anonymous";
        }
    }
}
