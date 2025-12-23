package com.cyd.xs.service;

import com.cyd.xs.dto.ChatRoom.ChatRoomDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomDetailDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomMessageDTO;
import com.cyd.xs.dto.ChatRoom.EssenceNoteDTO;
import com.cyd.xs.dto.Topic.*;
import com.cyd.xs.dto.Topic.vo.TopicDetailVO;

public interface TopicService {

    /**
     * 获取话题列表
     */
    TopicDTO getTopicList(String tag, String level, String sort, Integer pageNum, Integer pageSize);

    /**
     * 发布话题评论
     */
    TopicCommentDTO publishTopicComment(Long topicId, Long userId, TopicCommentRequest request);

    // 新增方法
    TopicDetailVO getTopicDetail(Long topicId, Integer pageNum, Integer pageSize);
    TopicCommentLikeDTO likeTopicComment(Long commentId, Long userId, Boolean isLike);
    ChatRoomDTO getChatRoomList(String status, String keyword, Integer pageNum, Integer pageSize);
    ChatRoomDetailDTO getChatRoomDetail(Long chatRoomId, Long userId);
    ChatRoomMessageDTO sendChatRoomMessage(Long chatRoomId, Long userId, String content);
    EssenceNoteDTO generateEssenceNote(Long chatRoomId, Long userId);

}