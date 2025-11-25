package com.cyd.xs.service.Impl;

import com.cyd.xs.entity.User.Circle.CircleMember;
import com.cyd.xs.dto.user.Circle.CircleDTO;
import com.cyd.xs.entity.User.Circle.Circle;
import com.cyd.xs.mapper.CircleMapper;
import com.cyd.xs.mapper.CircleMemberMapper;
import com.cyd.xs.service.CircleService;
import com.cyd.xs.util.IDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CircleServiceImpl implements CircleService {

    private final CircleMapper circleMapper;
    private final CircleMemberMapper circleMemberMapper;
    private final ObjectMapper objectMapper;

    @Override
    public CircleDTO getCategoryTools() {
        log.info("获取圈子分类标签与工具入口");

        try {
            CircleDTO circleDTO = new CircleDTO();

            // 设置分类
            circleDTO.setCategories(Arrays.asList(
                    createCategory("freshGraduate", "应届生专属"),
                    createCategory("newbie", "职场新人"),
                    createCategory("veteran", "职场老手"),
                    createCategory("expert", "专家圈子")
            ));

            // 设置工具
            CircleDTO.Tools tools = new CircleDTO.Tools();
            tools.setSearchUrl("/circle/search");
            tools.setCreateGroupUrl("/circle/create-group");
            circleDTO.setTools(tools);

            return circleDTO;
        } catch (Exception e) {
            log.error("获取圈子分类标签失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取圈子分类标签失败");
        }
    }

    @Override
    public Map<String, Object> searchCircles(String keyword, Integer page, Integer pageSize) {
        log.info("搜索圈子: keyword={}, page={}, pageSize={}", keyword, page, pageSize);

        try {
            if (page == null) page = 1;
            if (pageSize == null) pageSize = 20;
            int offset = (page - 1) * pageSize;

            List<Circle> circles = circleMapper.searchCircles(keyword, offset, pageSize);
            int total = circleMapper.countSearchCircles(keyword);

            Map<String, Object> result = new HashMap<>();
            result.put("data", circles);
            result.put("total", total);

            return result;
        } catch (Exception e) {
            log.error("搜索圈子失败: {}", e.getMessage(), e);
            throw new RuntimeException("搜索圈子失败");
        }
    }

    @Override
    @Transactional
    public String createGroup(String userId, String groupName, List<String> tags, String intro, String joinType) {
        log.info("用户 {} 创建小组: name={}, joinType={}", userId, groupName, joinType);

        try {
            Circle circle = new Circle();
            circle.setId(IDGenerator.generateCircleId());
            circle.setGroupName(groupName);
            circle.setIntro(intro);
            circle.setJoinType(joinType);
            circle.setCreatedBy(userId);
            circle.setCreatedAt(LocalDateTime.now());

            // 处理标签列表为JSON
            if (tags != null && !tags.isEmpty()) {
                circle.setTags(objectMapper.writeValueAsString(tags));
            }

            int result = circleMapper.insert(circle);
            if (result > 0) {
                log.info("小组创建申请提交成功: {}", circle.getId());

                // 如果是自由加入，自动将创建者加入小组
                if ("free".equals(joinType)) {
                    CircleMember member = new CircleMember();
                    member.setCircleId(circle.getId());
                    member.setUserId(userId);
                    member.setRole("creator");
                    member.setJoinTime(LocalDateTime.now());
                    circleMemberMapper.insert(member);

                    // 更新成员数量
                    circle.setMemberCount(1);
                    circleMapper.updateById(circle);
                }

                return circle.getId();
            } else {
                throw new RuntimeException("小组创建失败");
            }
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            throw new RuntimeException("小组创建失败");
        } catch (Exception e) {
            log.error("创建小组失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建小组失败");
        }
    }

    @Override
    public List<Circle> getRecommendedCircles(String userId, Integer page, Integer pageSize) {
        log.info("获取推荐小组列表: userId={}, page={}, pageSize={}", userId, page, pageSize);

        try {
            if (pageSize == null) pageSize = 20;
            return circleMapper.findRecommendedCircles(pageSize);
        } catch (Exception e) {
            log.error("获取推荐小组列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取推荐小组列表失败");
        }
    }

    @Override
    public Map<String, Object> getGroupDetail(String groupId, String userId) {
        log.info("获取小组详情: groupId={}, userId={}", groupId, userId);

        try {
            Map<String, Object> result = new HashMap<>();

            // 获取小组基础信息
            Circle circle = circleMapper.selectById(groupId);
            if (circle == null) {
                throw new RuntimeException("小组不存在");
            }

            result.put("groupInfo", circle);

            // 获取小组动态（简化处理）
            result.put("groupDynamic", List.of());

            // 检查用户是否已加入该小组
            boolean isJoined = circleMemberMapper.checkUserInCircle(userId, groupId);
            result.put("isJoined", isJoined);

            return result;
        } catch (Exception e) {
            log.error("获取小组详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取小组详情失败");
        }
    }

    @Override
    @Transactional
    public String joinOrQuitGroup(String userId, String groupId, String operateType, String applyReason) {
        log.info("用户 {} {} 小组 {}", userId, "join".equals(operateType) ? "加入" : "退出", groupId);

        try {
            Circle circle = circleMapper.selectById(groupId);
            if (circle == null) {
                throw new RuntimeException("小组不存在");
            }

            if ("join".equals(operateType)) {
                // 检查是否已加入
                boolean isJoined = circleMemberMapper.checkUserInCircle(userId, groupId);
                if (isJoined) {
                    throw new RuntimeException("您已经加入该小组");
                }

                // 根据入组方式处理
                if ("free".equals(circle.getJoinType())) {
                    // 自由加入，直接加入
                    CircleMember member = new CircleMember();
                    member.setCircleId(groupId);
                    member.setUserId(userId);
                    member.setRole("member");
                    member.setJoinTime(LocalDateTime.now());
                    circleMemberMapper.insert(member);

                    // 更新成员数量
                    circle.setMemberCount(circle.getMemberCount() + 1);
                    circleMapper.updateById(circle);

                    return "加入成功";
                } else if ("audit".equals(circle.getJoinType())) {
                    // 审核加入，创建申请记录
                    // 这里应该创建申请记录，等待管理员审核
                    return "申请已提交，等待管理员审核";
                }
            } else if ("quit".equals(operateType)) {
                // 退出小组
                int deleted = circleMemberMapper.deleteByUserAndCircle(userId, groupId);
                if (deleted > 0) {
                    // 更新成员数量
                    circle.setMemberCount(Math.max(0, circle.getMemberCount() - 1));
                    circleMapper.updateById(circle);
                    return "退出成功";
                } else {
                    throw new RuntimeException("您未加入该小组");
                }
            }

            throw new RuntimeException("操作失败");
        } catch (Exception e) {
            log.error("小组操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("小组操作失败");
        }
    }

    private CircleDTO.Category createCategory(String key, String name) {
        CircleDTO.Category category = new CircleDTO.Category();
        category.setKey(key);
        category.setName(name);
        return category;
    }
}
