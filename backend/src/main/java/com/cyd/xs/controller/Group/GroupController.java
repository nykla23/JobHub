package com.cyd.xs.controller.Group;

import com.cyd.xs.Response.Result;

import com.cyd.xs.Utils.SecurityUtils;
import com.cyd.xs.config.CustomUserPrincipal;
import com.cyd.xs.dto.Group.*;
import com.cyd.xs.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    /**
     * 获取小组列表
     */
    @GetMapping("/list")
    public ResponseEntity<Result<?>> getGroupList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false, defaultValue = "member") String sort,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        try {
            // 获取用户ID，允许未登录用户访问
            Long userId = getUserIdFromAuth(authentication);

            GroupDTO result = groupService.getGroupList(keyword, tag, sort, pageNum, pageSize, userId);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            log.error("获取小组列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Result.error("获取失败: " + e.getMessage()));
        }
    }

    /**
     * 创建小组
     */
    @PostMapping
    public ResponseEntity<Result<?>> createGroup(
            @RequestBody GroupCreateDTO request,
            Authentication authentication) {
        try {
            // 方法1：使用 SecurityUtils.getUserId()
            Long userId = SecurityUtils.getUserId();

            // 方法2：或者直接从 Authentication 中获取（更直接）
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserPrincipal) {
                    userId = ((CustomUserPrincipal) principal).getUserId();
                    log.debug("从CustomUserPrincipal获取userId: {}", userId);
                } else {
                    log.error("Principal类型错误: {}", principal.getClass().getName());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Result.error("用户信息异常"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Result.error("请先登录"));
            }

            log.info("接收到创建小组请求: name={}, userId={}", request.getName(), userId);

            GroupCreateResultDTO result = groupService.createGroup(request, userId);
            return ResponseEntity.ok(Result.success("小组创建成功，待审核", result));
        } catch (RuntimeException e) {
            log.error("创建小组异常: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        } catch (Exception e) {
            log.error("系统异常: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.error("系统异常"));
        }
    }

    /**
     * 获取小组详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<?>> getGroupDetail(
            @PathVariable("id") Long id,
            Authentication authentication) {

        // ✅ 允许未登录
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            userId = ((CustomUserPrincipal) authentication.getPrincipal()).getUserId();
            log.debug("当前登录用户 userId={}", userId);
        }

        // ❗ 不要 try-catch，让异常直接抛出去
        GroupDetailDTO result = groupService.getGroupDetail(
                id,
                userId == null ? 0L : userId
        );

        return ResponseEntity.ok(Result.success("获取成功", result));
    }


    /**
     * 加入/退出小组
     */
    @PutMapping("/{id}/join-or-quit")
    public ResponseEntity<Result<?>> joinOrQuitGroup(
            @PathVariable("id") Long groupId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            // 方法1：使用 SecurityUtils.getUserId()
            Long userId = SecurityUtils.getUserId();

            // 方法2：或者直接从 Authentication 中获取（更直接）
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserPrincipal) {
                    userId = ((CustomUserPrincipal) principal).getUserId();
                    log.debug("从CustomUserPrincipal获取userId: {}", userId);
                } else {
                    log.error("Principal类型错误: {}", principal.getClass().getName());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Result.error("用户信息异常"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Result.error("请先登录"));
            }

            String action = request.get("action");

            if (action == null) {
                return ResponseEntity.badRequest().body(Result.error("缺少action参数"));
            }

            GroupJoinDTO result = groupService.joinOrQuitGroup(groupId, userId, action);
            String message = "join".equals(action) ? "加入小组成功" : "退出小组成功";
            return ResponseEntity.ok(Result.success(message, result));
        } catch (Exception e) {
            log.error("小组操作失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 发布小组动态 - 修复：使用 @PathVariable 注解
     */
    @PostMapping("/{id}/dynamic")
    public ResponseEntity<Result<?>> publishDynamic(
            @PathVariable("id") Long groupId,  // 使用 @PathVariable
            @RequestBody GroupDynamicDTO request,
            Authentication authentication) {
        try {

            // 方法1：使用 SecurityUtils.getUserId()
            Long userId = SecurityUtils.getUserId();

            // 方法2：或者直接从 Authentication 中获取（更直接）
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserPrincipal) {
                    userId = ((CustomUserPrincipal) principal).getUserId();
                    log.debug("从CustomUserPrincipal获取userId: {}", userId);
                } else {
                    log.error("Principal类型错误: {}", principal.getClass().getName());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Result.error("用户信息异常"));
                }

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Result.error("请先登录"));
            }            GroupDynamicResultDTO result = groupService.publishDynamic(
                    groupId,
                    request,
                    userId
            );

            return ResponseEntity.ok(Result.success("动态发布成功，待审核", result));
        } catch (Exception e) {
            log.error("发布动态失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Result.error("发布失败: " + e.getMessage()));
        }
    }

    /**
     * 上传小组资源 - 修复：使用 @PathVariable 注解
     */
    @PostMapping("/{id}/resource")
    public ResponseEntity<Result<?>> uploadResource(
            @PathVariable("id") Long groupId,  // 使用 @PathVariable
            @RequestBody GroupResourceDTO request,
            Authentication authentication) {
        try {


            // 方法1：使用 SecurityUtils.getUserId()
            Long userId = SecurityUtils.getUserId();

            // 方法2：或者直接从 Authentication 中获取（更直接）
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserPrincipal) {
                    userId = ((CustomUserPrincipal) principal).getUserId();
                    log.debug("从CustomUserPrincipal获取userId: {}", userId);
                } else {
                    log.error("Principal类型错误: {}", principal.getClass().getName());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Result.error("用户信息异常"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Result.error("请先登录"));
            }

            GroupResourceResultDTO result = groupService.uploadResource(
                    groupId,  // 转换为 String
                    request,
                    userId    // 转换为 String
            );
            return ResponseEntity.ok(Result.success("资源上传成功，待审核", result));
        } catch (Exception e) {
            log.error("上传资源失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Result.error("上传失败: " + e.getMessage()));
        }
    }

    /**
     * 处理根路径请求 - 重定向到列表或返回默认信息
     */
    @GetMapping({"", "/"})
    public ResponseEntity<Result<?>> getGroupRoot(Authentication authentication) {
        try {
            // 可以选择重定向到列表，或返回其他信息
            // 这里重定向到列表（调用现有方法，使用默认参数）
            Long userId = getUserIdFromAuth(authentication);
            GroupDTO result = groupService.getGroupList(null, null, "member", 1, 10, userId);
            return ResponseEntity.ok(Result.success("获取小组列表成功", result));
        } catch (Exception e) {
            log.error("获取小组列表失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Result.error("获取失败: " + e.getMessage()));
        }
    }

    // 辅助方法：获取用户ID（允许未登录）
    private Long getUserIdFromAuth(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                log.warn("用户ID格式错误: {}", authentication.getName());
                return null;
            }
        }
        return null; // 未登录返回null
    }

    // 辅助方法：获取用户ID（必须登录）
    private Long getRequiredUserIdFromAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("请先登录");
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("用户ID格式错误: {}", authentication.getName());
            throw new RuntimeException("用户信息异常");
        }
    }
}