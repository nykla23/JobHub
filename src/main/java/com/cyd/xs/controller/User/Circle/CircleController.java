package com.cyd.xs.controller.User.Circle;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.user.Circle.CircleDTO;
import com.cyd.xs.entity.User.Circle.Circle;
import com.cyd.xs.service.CircleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/circle")
@RequiredArgsConstructor
public class CircleController {

    private final CircleService circleService;

    //创建小组
    @PostMapping("/create-group")
    public ResponseEntity<Result<?>> createCircle(@RequestBody CircleDTO circleDTO) {
        try {
            Circle circle = circleService.createCircle(circleDTO);
            return ResponseEntity.ok(Result.success("创建成功", circle));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("创建失败"));
        }
    }
    //加入/退出小组
    //修改小组信息
    //删除小组









    //获取圈子分类标签与工具入口
    @GetMapping("/category-tools")
    public ResponseEntity<Result<?>> getCategoryTools() {
        try {
            CircleDTO circleDTO = circleService.getCategoryTools();
            return ResponseEntity.ok(Result.success("获取成功", circleDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    //搜索圈子（小组）
    @GetMapping("/search")
    public ResponseEntity<Result<?>> searchCircles(@RequestParam String keyword,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer pageSize) {
        try {
            List<Circle> circles = (List<Circle>) circleService.searchCircles(keyword, page, pageSize);
            return ResponseEntity.ok(Result.success("搜索成功", circles));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("搜索失败"));
        }
    }

    //获取推荐小组列表
    @GetMapping("/recommend-groups")
    public ResponseEntity<Result<?>> getRecommendGroups(@RequestParam String userId,
                                                        @RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer pageSize) {
        try {
            List<Circle> circles = circleService.getRecommendedCircles(userId, page, pageSize);
            return ResponseEntity.ok(Result.success("获取成功", circles));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }
    //获取小组详情
    @GetMapping("/group-detail")
    public ResponseEntity<Result<?>> getCircleDetail(@RequestParam String groupId) {
        try {
        } catch (Exception e) {

        }
    }


}
