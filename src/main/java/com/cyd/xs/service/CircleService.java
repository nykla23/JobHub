package com.cyd.xs.service;

import com.cyd.xs.dto.user.Circle.CircleDTO;
import com.cyd.xs.entity.User.Circle.Circle;

import java.util.List;
import java.util.Map;

public interface CircleService {

    /**
     * 获取圈子分类标签与工具入口
     */
    CircleDTO getCategoryTools();

    /**
     * 搜索圈子（小组）
     */
    Map<String, Object> searchCircles(String keyword, Integer page, Integer pageSize);

    /**
     * 创建小组
     */
    String createGroup(String userId, String groupName, List<String> tags, String intro, String joinType);

    /**
     * 获取推荐小组列表
     */
    List<Circle> getRecommendedCircles(String userId, Integer page, Integer pageSize);

    /**
     * 获取小组详情
     */
    Map<String, Object> getGroupDetail(String groupId, String userId);

    /**
     * 加入/退出小组
     */
    String joinOrQuitGroup(String userId, String groupId, String operateType, String applyReason);
}
