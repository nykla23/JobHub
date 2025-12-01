package com.cyd.xs.controller.Group;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.Group.*;
import com.cyd.xs.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/group")
//@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }
    /**
     * 获取小组列表
     * 文档路径：GET /api/v1/group/list
     */
    @GetMapping("/list")
    public ResponseEntity<Result<?>> getGroupList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "member") String sort,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            GroupDTO result = groupService.getGroupList(keyword, tag, sort, pageNum, pageSize);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    /**
     * 创建小组
     * 文档路径：POST /api/v1/group
     */
    @PostMapping
    public ResponseEntity<Result<?>> createGroup(
            @RequestBody GroupCreateDTO request,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            GroupCreateResultDTO result = groupService.createGroup(request, userId);
            return ResponseEntity.ok(Result.success("小组创建成功，待审核", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("创建失败"));
        }
    }

    /**
     * 获取小组详情
     * 文档路径：GET /api/v1/group/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<?>> getGroupDetail(
            @PathVariable String id,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            GroupDetailDTO result = groupService.getGroupDetail(id, userId);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    /**
     * 加入/退出小组
     * 文档路径：PUT /api/v1/group/{id}/join-or-quit
     */
    @PutMapping("/{id}/join-or-quit")
    public ResponseEntity<Result<?>> joinOrQuitGroup(
            @PathVariable String id,
            @RequestParam String action,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            GroupJoinDTO result = groupService.joinOrQuitGroup(id, userId, action);
            String message = "join".equals(action) ? "加入小组成功" : "退出小组成功";
            return ResponseEntity.ok(Result.success(message, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("操作失败"));
        }
    }

    /**
     * 发布小组动态
     * 文档路径：POST /api/v1/group/{id}/dynamic
     */
    @PostMapping("/{id}/dynamic")
    public ResponseEntity<Result<?>> publishDynamic(
            @PathVariable String id,
            @RequestBody GroupDynamicDTO request,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            GroupDynamicResultDTO result = groupService.publishDynamic(id, request, userId);
            return ResponseEntity.ok(Result.success("动态发布成功，待审核", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("发布失败"));
        }
    }

    /**
     * 上传小组资源
     * 文档路径：POST /api/v1/group/{id}/resource
     */
    @PostMapping("/{id}/resource")
    public ResponseEntity<Result<?>> uploadResource(
            @PathVariable String id,
            @RequestBody GroupResourceDTO request,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            GroupResourceResultDTO result = groupService.uploadResource(id, request, userId);
            return ResponseEntity.ok(Result.success("资源上传成功，待审核", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("上传失败"));
        }
    }

    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("用户未认证");
    }
}