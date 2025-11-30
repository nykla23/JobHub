package com.cyd.xs.mapper.ChatRoom;


import com.cyd.xs.entity.User.Topic.ChatRoom.ChatRoomMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatRoomMessageMapper {

    @Insert("INSERT INTO chat_room_messages (id, chat_room_id, user_id, nickname, avatar, content, send_time, is_host) " +
            "VALUES (#{id}, #{chatRoomId}, #{userId}, #{nickname}, #{avatar}, #{content}, #{sendTime}, #{isHost})")
    int insert(ChatRoomMessage message);

    @Select("SELECT * FROM chat_room_messages WHERE chat_room_id = #{chatRoomId} ORDER BY send_time DESC LIMIT 50")
    List<ChatRoomMessage> findRecentMessages(String chatRoomId);

    @Select("SELECT * FROM chat_room_messages WHERE chat_room_id = #{chatRoomId} AND is_pinned = true ORDER BY send_time DESC LIMIT 1")
    ChatRoomMessage findPinnedMessage(String chatRoomId);
}