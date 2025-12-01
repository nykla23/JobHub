package com.cyd.xs.service;

import com.cyd.xs.dto.ChatRoom.ChatRoomDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomDetailDTO;
import com.cyd.xs.dto.ChatRoom.ChatRoomMessageDTO;
import com.cyd.xs.dto.Topic.*;

public interface TopicService {

    /**
     * 获取话题列表
     */
    TopicDTO getTopicList(String tag, String level, String sort, Integer pageNum, Integer pageSize);

    /**
     * 获取话题详情
     */
    TopicDetailDTO getTopicDetail(String topicId, Integer pageNum, Integer pageSize, String userId);

    /**
     * 发布话题评论
     */
    TopicCommentDTO publishTopicComment(String topicId, String userId, TopicCommentRequest request);

    // 新增方法

    TopicCommentLikeDTO likeTopicComment(String commentId, String userId, Boolean isLike);
    ChatRoomDTO getChatRoomList(String status, String keyword, Integer pageNum, Integer pageSize);
    ChatRoomDetailDTO getChatRoomDetail(String chatRoomId, String userId);
    ChatRoomMessageDTO sendChatRoomMessage(String chatRoomId, String userId, String content);
    EssenceNoteDTO generateEssenceNote(String chatRoomId, String userId);

}