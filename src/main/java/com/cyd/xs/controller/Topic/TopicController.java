package com.cyd.xs.controller.Topic;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.ChatRoom.ChatRoomDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomDetailDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomMessageDTO;
import com.cyd.xs.dto.Topic.*;
import com.cyd.xs.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            TopicDTO result = topicService.getTopicList(tag, level, sort, pageNum, pageSize);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    /**
     * 获取话题详情
     * 文档路径：GET /api/v1/topic/{id}
     */
    @GetMapping("/topic/{id}")
    public ResponseEntity<Result<?>> getTopicDetail(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            TopicDetailDTO result = topicService.getTopicDetail(id, pageNum, pageSize, userId);
            return ResponseEntity.ok(Result.success("获取成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }


    /**
     * 发布话题评论
     * 文档路径：POST /api/v1/topic/{id}/comment
     */
    @PostMapping("/topic/{id}/comment")
    public ResponseEntity<Result<?>> publishTopicComment(
            @PathVariable String id,
            @RequestBody TopicCommentRequest request,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            TopicCommentDTO result = topicService.publishTopicComment(id, userId, request);
            return ResponseEntity.ok(Result.success("评论提交成功，待审核", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("评论提交失败"));
        }
    }



    /**
     * 点赞/取消点赞话题评论
     * 文档路径：PUT /api/v1/comment/{id}/like
     */
    @PutMapping("/comment/{id}/like")
    public ResponseEntity<Result<?>> likeTopicComment(
            @PathVariable String id,
            @RequestParam Boolean isLike,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            TopicCommentLikeDTO result = topicService.likeTopicComment(id, userId, isLike);
            String message = isLike ? "点赞成功" : "取消点赞成功";
            return ResponseEntity.ok(Result.success(message, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("操作失败"));
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
            String userId = getUserIdFromAuthentication(authentication);
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
            @PathVariable String id,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
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
            @PathVariable String id,
            @RequestParam String content,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
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
            @PathVariable String id,
            Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            EssenceNoteDTO result = topicService.generateEssenceNote(id, userId);
            return ResponseEntity.ok(Result.success("精华笔记生成成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("生成失败"));
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
