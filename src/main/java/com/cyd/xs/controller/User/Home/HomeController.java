package com.cyd.xs.controller.User.Home;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.user.Home.HomeDTO;
import com.cyd.xs.dto.user.Search.SearchDTO;
import com.cyd.xs.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    //首次进入 APP - 身份标签选择
    @PostMapping("/select-identity")
    public ResponseEntity<Result<?>> selectIdentity(@RequestParam String identityType,
                                                    @RequestParam String userId) {
        try {
            HomeDTO homeDTO = homeService.selectIdentity(identityType, userId);
            return ResponseEntity.ok(Result.success("身份标签选择成功", homeDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("身份标签选择失败"));
        }
    }

    //全域搜索
    @GetMapping("/search")
    public ResponseEntity<Result<?>> search(@RequestParam String keyword,
                                              @RequestParam(required = false) String tabType,
                                              @RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer pageSize,
                                              @RequestParam String userId) {
        try {
            SearchDTO searchDTO = homeService.search(keyword, tabType, page, pageSize, userId);
            return ResponseEntity.ok(Result.success("搜索成功", searchDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("搜索失败"));
        }
    }

    //清除搜索历史
    @PostMapping("/clear-search-history")
    public ResponseEntity<Result<?>> clearSearchHistory(@RequestParam String userId) {
        try {
            homeService.clearSearchHistory(userId);
            return ResponseEntity.ok(Result.success("搜索历史清除成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("清除失败"));
        }
    }

    //首页推荐内容刷新
    @GetMapping("/refresh-recommend")
    public ResponseEntity<Result<?>> refreshRecommend(@RequestParam String userId,
                                                      @RequestParam(required = false) Integer pageSize) {
        try {
            HomeDTO homeDTO = (HomeDTO) homeService.refreshRecommend(userId, pageSize);
            return ResponseEntity.ok(Result.success("推荐内容刷新成功", homeDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("推荐内容刷新失败"));
        }
    }


}
