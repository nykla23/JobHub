package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.Group.*;
import com.cyd.xs.entity.Group.*;
import com.cyd.xs.entity.User.User;
import com.cyd.xs.entity.User.UserGroup;
import com.cyd.xs.mapper.UserGroupMapper;
import com.cyd.xs.mapper.UserMapper;
import com.cyd.xs.mapper.groups.*;
import com.cyd.xs.service.GroupService;
import com.cyd.xs.util.IDGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final GroupMapper groupMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final GroupDynamicMapper groupDynamicMapper;
    private final GroupResourceMapper groupResourceMapper;
    private final GroupNoticeMapper groupNoticeMapper;
    private final ObjectMapper objectMapper;
    private final UserGroupMapper userGroupMapper;
    private final GroupTagMapper groupTagMapper;
    private final UserMapper userMapper;



    @Override
    public GroupDTO getGroupList(String keyword, String tag, String sort, Integer pageNum, Integer pageSize, Long userId) {
        log.info("获取小组列表: keyword={}, tag={}, sort={}, pageNum={}, pageSize={}, userId={}",
                keyword, tag, sort, pageNum, pageSize, userId);


        try {
            int offset = (pageNum - 1) * pageSize;

            // 如果 userId 为 null 或 0，传入字符串 "0"
            Long safeUserId = (userId != null ? userId : 0L);


            // 处理空字符串参数
            String keywordParam = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
            String tagParam = (tag != null && !tag.trim().isEmpty()) ? tag.trim() : null;
            int safePageSize = pageSize;

            // 查询小组基本信息，传入 userId
            List<Group> groups = groupMapper.findGroups(
                    keywordParam,
                    tagParam,
                    offset,
                    pageSize
            );


            // 查询总数，传入过滤参数
            Long total = groupMapper.countGroups(keywordParam, tagParam);

            // 转换DTO
            List<GroupDTO.GroupItem> groupList = groups.stream().map(group -> {
                GroupDTO.GroupItem item = new GroupDTO.GroupItem();
                item.setId(group.getId());
                item.setName(group.getName());

                // 解析标签
                if (group.getTags() != null && !group.getTags().isEmpty()) {
                    item.setTags(Arrays.asList(group.getTags().split(",")));
                } else {
                    item.setTags(new ArrayList<>());
                }


                item.setMemberCount(group.getMemberCount() != null ? group.getMemberCount() : 0);
                item.setActivityType(group.getActivityType());
                item.setIntro(group.getIntro());
                item.setAvatar(group.getAvatar());

                // 使用从SQL查询中获取的 isJoined 字段
                item.setJoined(
                        group.getIsJoined() != null && group.getIsJoined() == 1
                );


                return item;
            }).collect(Collectors.toList());

            GroupDTO result = new GroupDTO();
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setTotal(total);
            result.setList(groupList);

            return result;
        } catch (Exception e) {
            log.error("获取小组列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取小组列表失败: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public GroupCreateResultDTO createGroup(GroupCreateDTO request, Long userId) {
        log.info("用户 {} 创建小组: name={}, tags={}, intro={}",
                userId, request.getName(), request.getTags(), request.getIntro());

        try {
            // 验证请求参数
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("小组名称不能为空");
            }

            // 创建小组记录
            Group group = new Group();
            group.setName(request.getName());
            group.setIntro(request.getIntro());
            group.setAvatar(request.getAvatar());
            group.setActivityType(request.getActivityDesc());
            group.setCreatorId(userId);
            group.setStatus("pending"); // 待审核
            group.setCreatedAt(LocalDateTime.now());

            log.debug("插入小组前: group={}", group);

            int result = groupMapper.insert(group);

            log.debug("插入小组后: result={}, group.id={}", result, group.getId());

            if (result > 0 && group.getId() != null) {
                // 创建者自动加入小组
                UserGroup userGroup = new UserGroup();
                userGroup.setUserId(userId);
                userGroup.setGroupId(group.getId());
                userGroup.setRoleInGroup("creator");
                userGroup.setJoinedAt(LocalDateTime.now());

                log.debug("插入用户小组关系: userGroup={}", userGroup);
                int userGroupResult = userGroupMapper.insert(userGroup);

                if (userGroupResult <= 0) {
                    throw new RuntimeException("用户加入小组失败");
                }

                // 添加标签到 group_tags 表
                if (request.getTags() != null && !request.getTags().isEmpty()) {
                    for (String tag : request.getTags()) {
                        log.debug("添加标签: groupId={}, tag={}", group.getId(), tag);
                        // 方法1：使用 GroupTag 实体类
                        GroupTag groupTag = new GroupTag();
                        groupTag.setGroupId(group.getId());
                        groupTag.setTag(tag);
                        groupTagMapper.insertTag(groupTag.getGroupId(),tag);
                    }
                }

                GroupCreateResultDTO response = new GroupCreateResultDTO();
                response.setGroupId(group.getId());
                response.setStatus("pending");
                response.setSubmitTime(LocalDateTime.now());

                log.info("小组创建成功: groupId={}, userId={}", group.getId(), userId);
                return response;
            } else {
                log.error("小组创建失败: result={}, groupId={}", result, group.getId());
                throw new RuntimeException("小组创建失败，未获取到小组ID");
            }
        } catch (Exception e) {
            log.error("创建小组失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建小组失败: " + e.getMessage());
        }
    }

    @Override
    public GroupDetailDTO getGroupDetail(Long groupId, Long userId) {
        log.info("获取小组详情: groupId={}, userId={}", groupId, userId);

        try {
            // 1️⃣ 查询小组
            Group group = groupMapper.findById(groupId);
            if (group == null) {
                throw new RuntimeException("小组不存在");
            }

            GroupDetailDTO result = new GroupDetailDTO();

            // 2️⃣ 小组基本信息
            GroupDetailDTO.GroupInfo groupInfo = new GroupDetailDTO.GroupInfo();
            groupInfo.setId(group.getId());
            groupInfo.setName(group.getName());
            groupInfo.setMemberCount(group.getMemberCount());
            groupInfo.setActivityType(group.getActivityType());
            groupInfo.setIntro(group.getIntro());
            groupInfo.setAvatar(group.getAvatar());

            if (group.getTags() != null && !group.getTags().isEmpty()) {
                groupInfo.setTags(Arrays.asList(group.getTags().split(",")));
            }

            // 3️⃣ 当前用户状态
            GroupMember member = groupMemberMapper.findByGroupAndUser(groupId, userId);
            groupInfo.setIsJoined(member != null);
            groupInfo.setIsManager(
                    member != null &&
                            ("manager".equals(member.getRole()) || "creator".equals(member.getRole()))
            );

            result.setGroupInfo(groupInfo);

            // 4️⃣ 小组动态
            List<GroupDynamic> dynamics =
                    groupDynamicMapper.findDynamicsByGroupId(groupId, 0, 10);

            GroupDetailDTO.GroupDynamic groupDynamic = new GroupDetailDTO.GroupDynamic();
            groupDynamic.setTotal(groupDynamicMapper.countDynamicsByGroupId(groupId));
            groupDynamic.setPageNum(1);
            groupDynamic.setPageSize(10);

            groupDynamic.setList(
                    dynamics.stream().map(dynamic -> {
                        GroupDetailDTO.DynamicItem item =
                                new GroupDetailDTO.DynamicItem();
                        item.setId(dynamic.getId());
                        item.setUserId(Collections.singletonList(dynamic.getUserId()));
                        item.setNickname(dynamic.getNickname());
                        item.setAvatar(dynamic.getAvatar());
                        item.setTitle(dynamic.getTitle());
                        item.setContent(dynamic.getContent());
                        item.setPublishTime(dynamic.getCreatedAt());
                        item.setLikeCount(dynamic.getLikeCount());
                        item.setCommentCount(dynamic.getCommentCount());

                        if (dynamic.getImageUrls() != null) {
                            try {
                                item.setImageUrls(
                                        Arrays.asList(
                                                objectMapper.readValue(
                                                        dynamic.getImageUrls(),
                                                        String[].class
                                                )
                                        )
                                );
                            } catch (Exception e) {
                                item.setImageUrls(
                                        Arrays.asList(dynamic.getImageUrls().split(","))
                                );
                            }
                        }
                        return item;
                    }).collect(Collectors.toList())
            );

            result.setGroupDynamic(groupDynamic);

            // 5️⃣ 小组资源
            List<GroupResource> resources =
                    groupResourceMapper.findResourcesByGroupId(groupId, 5);

            GroupDetailDTO.GroupResource groupResource =
                    new GroupDetailDTO.GroupResource();
            groupResource.setTotal(
                    groupResourceMapper.countResourcesByGroupId(groupId)
            );
            groupResource.setPageNum(1);
            groupResource.setPageSize(5);

            groupResource.setList(
                    resources.stream().map(resource -> {
                        GroupDetailDTO.ResourceItem item =
                                new GroupDetailDTO.ResourceItem();
                        item.setId(resource.getId());
                        item.setTitle(resource.getTitle());
                        item.setType(resource.getType());
                        item.setUploader(resource.getUploader());
                        item.setUploadTime(resource.getCreatedAt());
                        item.setDownloadCount(resource.getDownloadCount());
                        item.setSize(resource.getSize());
                        item.setLink(resource.getLink());
                        return item;
                    }).collect(Collectors.toList())
            );

            result.setGroupResource(groupResource);

            // 6️⃣ 小组公告
            List<GroupNotice> notices =
                    groupNoticeMapper.findNoticesByGroupId(groupId);

            GroupDetailDTO.GroupNotice groupNotice =
                    new GroupDetailDTO.GroupNotice();
            groupNotice.setTotal(
                    groupNoticeMapper.countNoticesByGroupId(groupId)
            );
            groupNotice.setList(
                    notices.stream().map(notice -> {
                        GroupDetailDTO.NoticeItem item =
                                new GroupDetailDTO.NoticeItem();
                        item.setId(notice.getId());
                        item.setTitle(notice.getTitle());
                        item.setContent(notice.getContent());
                        item.setPublishTime(notice.getCreatedAt());
                        return item;
                    }).collect(Collectors.toList())
            );

            result.setGroupNotice(groupNotice);

            return result;

        } catch (Exception e) {
            // 🔥🔥🔥 关键：打印完整真实异常
            log.error("【获取小组详情发生异常】", e);
            throw e; // 不包装，保证看到真实 MyBatis / SQL 错误
        }
    }



    @Override
    @Transactional
    public GroupJoinDTO joinOrQuitGroup(Long groupId, Long userId, String action) {
        log.info("用户 {} {}小组 {}", userId, "join".equals(action) ? "加入" : "退出", groupId);

        try {
            Group group = groupMapper.findById(groupId);
            if (group == null) {
                throw new RuntimeException("小组不存在");
            }

            GroupJoinDTO result = new GroupJoinDTO();
            result.setGroupId(groupId);

            // 获取当前成员数，处理null值
            Integer currentMemberCount = group.getMemberCount();
            if (currentMemberCount == null) {
                currentMemberCount = 0;
            }

            if ("join".equals(action)) {
                // 检查是否已加入
                GroupMember existingMember = groupMemberMapper.findByGroupAndUser(groupId, userId);
                if (existingMember != null) {
                    throw new RuntimeException("已加入该小组");
                }

                // 加入小组
                GroupMember member = new GroupMember();
                member.setId(IDGenerator.generateId());
                member.setGroupId(groupId);
                member.setUserId(userId);
                member.setRole("member");
                member.setJoinTime(LocalDateTime.now());
                groupMemberMapper.insert(member);

                // 更新小组成员数
                groupMapper.updateMemberCount(groupId, 1);

                result.setJoinTime(LocalDateTime.now());
                result.setMemberCount(currentMemberCount + 1);
            } else if ("quit".equals(action)) {
                // 退出小组
                int deleted = groupMemberMapper.deleteByGroupAndUser(groupId, userId);
                if (deleted > 0) {
                    // 更新小组成员数
                    groupMapper.updateMemberCount(groupId, -1);
                    result.setMemberCount(Math.max(0, currentMemberCount - 1));
                } else {
                    throw new RuntimeException("未加入该小组");
                }
            } else {
                throw new RuntimeException("不支持的操作类型");
            }

            return result;
        } catch (Exception e) {
            log.error("小组操作失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public GroupDynamicResultDTO publishDynamic(Long groupId, GroupDynamicDTO request, Long userId) {
        log.info("用户 {} 在小组 {} 发布动态", userId, groupId);

        try {
            // 获取用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            // 检查用户是否已加入小组
            GroupMember member = groupMemberMapper.findByGroupAndUser(groupId, userId);
            if (member == null) {
                throw new RuntimeException("请先加入小组");
            }

            GroupDynamic dynamic = new GroupDynamic();
            dynamic.setId(IDGenerator.generateId());
            dynamic.setGroupId(groupId);
            dynamic.setUserId(userId);
            dynamic.setNickname(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername()); // 防止索引越界
            dynamic.setAvatar(user.getAvatarUrl() != null ? user.getAvatarUrl() : "https://jobhub.com/avatar/default.png");
            dynamic.setTitle(request.getTitle());
            dynamic.setContent(request.getContent());
            dynamic.setCreatedAt(LocalDateTime.now());
            dynamic.setLikeCount(0);
            dynamic.setCommentCount(0);
            dynamic.setStatus("pending"); // 待审核

            // 处理图片URL
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                dynamic.setImageUrls(objectMapper.writeValueAsString(request.getImageUrls()));
            }

            // 处理标签 - 修复这里
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                dynamic.setTags(Collections.singletonList(objectMapper.writeValueAsString(request.getTags())));
            }

            int result = groupDynamicMapper.insert(dynamic);
            if (result > 0) {
                GroupDynamicResultDTO response = new GroupDynamicResultDTO();
                response.setDynamicId(dynamic.getId());
                response.setStatus("pending");
                response.setSubmitTime(LocalDateTime.now());
                return response;
            } else {
                throw new RuntimeException("动态发布失败");
            }
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败: {}", e.getMessage(), e);
            throw new RuntimeException("动态发布失败");
        } catch (Exception e) {
            log.error("发布动态失败: {}", e.getMessage(), e);
            throw new RuntimeException("发布动态失败");
        }
    }

    @Override
    @Transactional
    public GroupResourceResultDTO uploadResource(Long groupId, GroupResourceDTO request, Long userId) {
        log.info("用户 {} 在小组 {} 上传资源", userId, groupId);

        try {


            // 1. 验证小组是否存在
            Group group = groupMapper.findById(groupId);
            if (group == null) {
                throw new RuntimeException("小组不存在");
            }

            // 2. 检查小组状态
//            if (!"active".equals(group.getStatus())) {
//                throw new RuntimeException("小组当前不可用");
//            }
            if (!"active".equals(group.getStatus()) && !"pending".equals(group.getStatus())) {
                throw new RuntimeException("小组当前未激活");
            }

            // 检查用户是否已加入小组
            GroupMember member = groupMemberMapper.findByGroupAndUser(groupId, userId);
            if (member == null) {
                throw new RuntimeException("请先加入小组");
            }
            // 获取用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            String uploaderName = user.getDisplayName() != null ? user.getDisplayName() : user.getUsername();

            // 5. 验证请求参数
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                throw new RuntimeException("资源标题不能为空");
            }
            if (request.getFileUrl() == null || request.getFileUrl().trim().isEmpty()) {
                throw new RuntimeException("文件URL不能为空");
            }

            GroupResource resource = new GroupResource();
            resource.setId(IDGenerator.generateId());
            resource.setGroupId(groupId);
            resource.setTitle(request.getTitle());
            resource.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
            resource.setUploaderId(userId);
            resource.setTag(request.getTag() != null ? request.getTag().trim() : null);
            resource.setUploader(uploaderName);
            resource.setCreatedAt(LocalDateTime.now());
            resource.setDownloadCount(0);
            resource.setLink(request.getFileUrl());
            resource.setType(getFileType(request.getFileUrl())); // 根据文件URL判断类型
            resource.setStatus("pending"); // 待审核

            int result = groupResourceMapper.insert(resource);
            if (result > 0) {
                GroupResourceResultDTO response = new GroupResourceResultDTO();
                response.setResourceId(resource.getId());
                response.setStatus("pending");
                response.setSubmitTime(LocalDateTime.now());
                log.info("资源上传成功: 资源ID={}, 用户ID={}, 小组ID={}",
                        resource.getId(), userId, groupId);
                return response;
            } else {
                throw new RuntimeException("资源上传失败");
            }
        } catch (Exception e) {
            log.error("上传资源失败: {}", e.getMessage(), e);
            throw new RuntimeException("上传资源失败");
        }
    }

    // 辅助方法
    private String getFileType(String fileName) {
        if (fileName == null) return "file";
        if (fileName.endsWith(".pdf")) return "pdf";
        if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) return "word";
        if (fileName.endsWith(".ppt") || fileName.endsWith(".pptx")) return "ppt";
        return "file";
    }

    private String extractFileName(String fileUrl) {
        if (fileUrl == null) return "unknown";
        int lastSlash = fileUrl.lastIndexOf('/');
        return lastSlash >= 0 ? fileUrl.substring(lastSlash + 1) : fileUrl;
    }

    private String calculateFileSize(String fileUrl) {
        // 简化处理，实际应该从文件信息中获取
        return "2.5MB";
    }
}