package com.cyd.xs.controller.Topic;

import com.cyd.xs.Response.Result;
import com.cyd.xs.Utils.SecurityUtils;
import com.cyd.xs.config.CustomUserPrincipal;
import com.cyd.xs.dto.ChatRoom.ChatRoomDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomDetailDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomMessageDTO;
import com.cyd.xs.dto.ChatRoom.EssenceNoteDTO;
import com.cyd.xs.dto.Topic.TopicDTO;
import com.cyd.xs.dto.Topic.TopicCommentDTO;
import com.cyd.xs.dto.Topic.TopicCommentLikeDTO;
import com.cyd.xs.dto.Topic.TopicCommentRequest;
import com.cyd.xs.dto.Topic.vo.TopicDetailVO;

import com.cyd.xs.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.cyd.xs.dto.Topic.vo.TopicDetailVO;


@Slf4j
@RestController
@RequestMapping("/api/v1")
//@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }
    /**
     * 获取话题列表
     * 文档路径：GET /api/v1/topic/list
     */
    @GetMapping("/topic/list")
    public ResponseEntity<Result<?>> getTopicList(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "hot") String sort,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        try {
            // 获取用户ID，允许未登录用户访问
            Long userId = getUserIdFromAuth(authentication);

            TopicDTO result = topicService.getTopicList(tag, level, sort, pageNum, pageSize);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    /**
     * 获取话题详情（包含评论分页）
     * 文档路径：GET /api/v1/topic/{id}
     * ✅ 允许未登录访问
     */
    @GetMapping("/topic/{id}")
    public ResponseEntity<Result<?>> getTopicDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        try {
            // ✅ 未登录允许查看：userId 可为 null
            Long userId = null;
            if (authentication != null && authentication.isAuthenticated()
                    && authentication.getPrincipal() instanceof CustomUserPrincipal p) {
                userId = p.getUserId();
            }

            // ✅ 调用新 VO 版 service（你需要在 TopicService/Impl 里加这个重载或替换原方法）
            TopicDetailVO detail = topicService.getTopicDetail(id, pageNum, pageSize);

            // ✅ 按你现有 Result 的签名返回（重点看这里）
            return ResponseEntity.ok(Result.success("获取成功", detail));
        } catch (Exception e) {
            log.error("获取话题详情失败", e);
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }


    /**
     * 发布话题评论
     * 文档路径：POST /api/v1/topic/{id}/comment
     */
    @PostMapping("/topic/{id}/comment")
    public ResponseEntity<Result<?>> publishTopicComment(
            @PathVariable Long id,
            @RequestBody TopicCommentRequest request,
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

            TopicCommentDTO result = topicService.publishTopicComment(id, userId, request);
            return ResponseEntity.ok(Result.success("评论提交成功，待审核", result));
        } catch (Exception e) {
            log.error("评论提交异常，topicId={}, request={}", id, request, e);

            return ResponseEntity.badRequest().body(
                    Result.error(
                            e.getClass().getSimpleName() + ": " +
                                    String.valueOf(e.getMessage())
                    )
            );
        }
    }

    /**
     * 点赞/取消点赞话题评论
     * 文档路径：PUT /api/v1/comment/{id}/like
     */
    @PutMapping("/comment/{id}/like")
    public ResponseEntity<Result<?>> likeTopicComment(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") Boolean isLike,
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

            TopicCommentLikeDTO result = topicService.likeTopicComment(id, userId, isLike);
            String message = isLike ? "点赞成功" : "取消点赞成功";
            return ResponseEntity.ok(Result.success(message, result));
        } catch (Exception e) {
            log.error("点赞失败", e);
            return ResponseEntity.badRequest()
                    .body(Result.error(e.getMessage()));
        }
    }


    // ========== 聊天室相关接口 ==========

    /**
     * 获取聊天室列表
     * 文档路径：GET /api/v1/chatroom/list
     */
    @GetMapping("/chatroom/list")
    public ResponseEntity<Result<?>> getChatRoomList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
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
            ChatRoomDTO result = topicService.getChatRoomList(status, keyword, pageNum, pageSize);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    /**
     * 获取聊天室详情
     * 文档路径：GET /api/v1/chatroom/{id}
     */
    @GetMapping("/chatroom/{id}")
    public ResponseEntity<Result<?>> getChatRoomDetail(
            @PathVariable Long id,
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

            ChatRoomDetailDTO result = topicService.getChatRoomDetail(id, userId);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    /**
     * 发送聊天室消息
     * 文档路径：POST /api/v1/chatroom/{id}/message
     */
    @PostMapping("/chatroom/{id}/message")
    public ResponseEntity<Result<?>> sendChatRoomMessage(
            @PathVariable Long id,
            @RequestParam String content,
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
            ChatRoomMessageDTO result = topicService.sendChatRoomMessage(id, userId, content);
            return ResponseEntity.ok(Result.success("消息发送成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("消息发送失败"));
        }
    }

    /**
     * 生成聊天室精华笔记
     * 文档路径：POST /api/v1/chatroom/{id}/essence-note
     */
    @PostMapping("/chatroom/{id}/essence-note")
    public ResponseEntity<Result<?>> generateEssenceNote(
            @PathVariable Long id,
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
            EssenceNoteDTO result = topicService.generateEssenceNote(id, userId);
            return ResponseEntity.ok(Result.success("精华笔记生成成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("生成失败"));
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

    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("用户未认证");
    }


//    //话题互动帖子
//    @PostMapping("/interact")
//    public ResponseEntity<Result<?>> interactPost(@RequestParam String userId,
//                                                  @RequestParam String postId,
//                                                  @RequestParam String interactType,
//                                                  @RequestParam(required = false) String commentContent,
//                                                  @RequestParam(required = false) String quotingPostId,
//                                                  @RequestParam(required = false) Boolean isCancel) {
//        try {
//            TopicInteractDTO topicInteractDTO = (TopicInteractDTO) topicService.interactPost(userId, postId, interactType, commentContent, quotingPostId, isCancel);
//            return ResponseEntity.ok(Result.success("操作成功", topicInteractDTO));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Result.error("操作失败"));
//        }
//    }
}
