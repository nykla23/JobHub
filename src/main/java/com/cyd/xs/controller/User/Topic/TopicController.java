package com.cyd.xs.controller.User.Topic;

import com.cyd.xs.Response.Result;
import com.cyd.xs.dto.user.Topic.TopicDTO;
import com.cyd.xs.dto.user.Topic.TopicDetailDTO;
import com.cyd.xs.dto.user.Topic.TopicInteractDTO;
import com.cyd.xs.dto.user.Topic.TopicPostRequest;
import com.cyd.xs.entity.User.Topic.Topic;
import com.cyd.xs.entity.User.Topic.TopicPost;
import com.cyd.xs.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    //获取话题列表
    @GetMapping("/list")
    public ResponseEntity<Result<?>> getTopicList(@RequestParam(required = false) String tab,
                                                  @RequestParam(required = false) String sortType,
                                                  @RequestParam(required = false) Integer page,
                                                  @RequestParam(required = false) Integer pageSize) {
        try {
            TopicDTO topicDTO = topicService.getTopicList(tab, sortType, page, pageSize);
            return ResponseEntity.ok(Result.success("获取成功", topicDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }

    //获取话题详情
    @GetMapping("/detail")
    public ResponseEntity<Result<?>> getTopicDetail(@RequestParam String topicId,
                                                     @RequestParam(required = false) Integer page,
                                                     @RequestParam(required = false) Integer pageSize) {
        try {
            TopicDetailDTO topicDetailDTO = (TopicDetailDTO) topicService.getTopicDetail(topicId, page, pageSize);
            return ResponseEntity.ok(Result.success("获取成功", topicDetailDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("获取失败"));
        }
    }


    //发布话题帖子
    @PostMapping("/publish-post")
    public ResponseEntity<Result<?>> publishPost(@RequestBody TopicPostRequest request) {
        try {
            TopicPost topicPost = topicService.publishPost(request);

            return ResponseEntity.ok(Result.success("发布成功", topicPost));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("发布失败: " + e.getMessage()));
        }
    }

    //话题互动帖子
    @PostMapping("/interact")
    public ResponseEntity<Result<?>> interactPost(@RequestParam String userId,
                                                  @RequestParam String postId,
                                                  @RequestParam String interactType,
                                                  @RequestParam(required = false) String commentContent,
                                                  @RequestParam(required = false) String quotingPostId,
                                                  @RequestParam(required = false) Boolean isCancel) {
        try {
            TopicInteractDTO topicInteractDTO = (TopicInteractDTO) topicService.interactPost(userId, postId, interactType, commentContent, quotingPostId, isCancel);
            return ResponseEntity.ok(Result.success("操作成功", topicInteractDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Result.error("操作失败"));
        }
    }
}
