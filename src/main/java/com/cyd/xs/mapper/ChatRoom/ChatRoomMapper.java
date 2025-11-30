package com.cyd.xs.mapper.ChatRoom;


import com.cyd.xs.entity.User.Topic.ChatRoom.ChatRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatRoomMapper {

    @Select("<script>" +
            "SELECT * FROM chat_rooms WHERE 1=1" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='keyword != null'> AND (title LIKE CONCAT('%', #{keyword}, '%') OR theme LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            " ORDER BY start_time DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<ChatRoom> findChatRooms(@Param("status") String status, @Param("keyword") String keyword,
                                 @Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("SELECT * FROM chat_rooms WHERE id = #{id}")
    ChatRoom findById(String id);
}