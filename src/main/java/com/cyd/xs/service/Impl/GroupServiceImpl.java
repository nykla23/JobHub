package com.cyd.xs.service.Impl;

import com.cyd.xs.dto.Group.*;
import com.cyd.xs.entity.User.Group.*;
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
import java.util.Arrays;
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

    @Override
    public GroupDTO getGroupList(String keyword, String tag, String sort, Integer pageNum, Integer pageSize) {
        log.info("获取小组列表: keyword={}, tag={}, sort={}, pageNum={}, pageSize={}",
                keyword, tag, sort, pageNum, pageSize);

        try {
            int offset = (pageNum - 1) * pageSize;
            List<Group> groups = groupMapper.findGroups(keyword, tag, sort, offset, pageSize);

            GroupDTO result = new GroupDTO();
            result.setTotal(groupMapper.countGroups());
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);

            List<GroupDTO.GroupItem> groupList = groups.stream().map(group -> {
                GroupDTO.GroupItem item = new GroupDTO.GroupItem();
                item.setId(group.getId());
                item.setName(group.getName());
                // 解析tags
                if (group.getTags() != null) {
                    try {
                        item.setTags(Arrays.asList(objectMapper.readValue(group.getTags(), String[].class)));
                    } catch (JsonProcessingException e) {
                        item.setTags(Arrays.asList(group.getTags().split(",")));
                    }
                }
                item.setMemberCount(group.getMemberCount());
                item.setActivityType(group.getActivityType());
                item.setIntro(group.getIntro());
                item.setAvatar(group.getAvatar());
                item.setJoined(false); // 需要根据当前用户查询是否已加入
                return item;
            }).collect(Collectors.toList());

            result.setList(groupList);
            return result;
        } catch (Exception e) {
            log.error("获取小组列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取小组列表失败");
        }
    }

    @Override
    @Transactional
    public GroupCreateResultDTO createGroup(GroupCreateDTO request, String userId) {
        log.info("用户 {} 创建小组: name={}", userId, request.getName());

        try {
            Group group = new Group();
            group.setId(IDGenerator.generateCircleId());
            group.setName(request.getName());
            group.setIntro(request.getIntro());
            group.setAvatar(request.getAvatar());
            group.setActivityType(request.getActivityDesc());
            group.setCreator(userId);
            group.setCreateTime(LocalDateTime.now());
            group.setMemberCount(1); // 创建者自动成为成员
            group.setStatus("pending"); // 待审核

            // 处理tags
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                group.setTags(objectMapper.writeValueAsString(request.getTags()));
            }

            int result = groupMapper.insert(group);
            if (result > 0) {
                // 创建者自动加入小组
                GroupMember creatorMember = new GroupMember();
                creatorMember.setId(IDGenerator.generateId());
                creatorMember.setGroupId(group.getId());
                creatorMember.setUserId(userId);
                creatorMember.setRole("creator");
                creatorMember.setJoinTime(LocalDateTime.now());
                groupMemberMapper.insert(creatorMember);

                GroupCreateResultDTO response = new GroupCreateResultDTO();
                response.setGroupId(group.getId());
                response.setStatus("pending");
                response.setSubmitTime(LocalDateTime.now());
                return response;
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
    public GroupDetailDTO getGroupDetail(String groupId, String userId) {
        log.info("获取小组详情: groupId={}, userId={}", groupId, userId);

        try {
            Group group = groupMapper.findById(groupId);
            if (group == null) {
                throw new RuntimeException("小组不存在");
            }

            GroupDetailDTO result = new GroupDetailDTO();

            // 小组基本信息
            GroupDetailDTO.GroupInfo groupInfo = new GroupDetailDTO.GroupInfo();
            groupInfo.setId(group.getId());
            groupInfo.setName(group.getName());
            // 解析tags
            if (group.getTags() != null) {
                try {
                    groupInfo.setTags(Arrays.asList(objectMapper.readValue(group.getTags(), String[].class)));
                } catch (JsonProcessingException e) {
                    groupInfo.setTags(Arrays.asList(group.getTags().split(",")));
                }
            }
            groupInfo.setMemberCount(group.getMemberCount());
            groupInfo.setActivityType(group.getActivityType());
            groupInfo.setIntro(group.getIntro());
            groupInfo.setAvatar(group.getAvatar());
            groupInfo.setCreator(group.getCreator());
            groupInfo.setCreateTime(group.getCreateTime());

            // 检查用户是否已加入
            GroupMember member = groupMemberMapper.findByGroupAndUser(groupId, userId);
            groupInfo.setJoined(member != null);
            groupInfo.setManager(member != null && ("manager".equals(member.getRole()) || "creator".equals(member.getRole())));

            result.setGroupInfo(groupInfo);

            // 小组动态
            GroupDetailDTO.GroupDynamic groupDynamic = new GroupDetailDTO.GroupDynamic();
            List<GroupDynamic> dynamics = groupDynamicMapper.findDynamicsByGroupId(groupId, 0, 10);
            groupDynamic.setTotal(groupDynamicMapper.countDynamicsByGroupId(groupId));
            groupDynamic.setPageNum(1);
            groupDynamic.setPageSize(10);

            List<GroupDetailDTO.DynamicItem> dynamicList = dynamics.stream().map(dynamic -> {
                GroupDetailDTO.DynamicItem item = new GroupDetailDTO.DynamicItem();
                item.setId(dynamic.getId());
                item.setUserId(dynamic.getUserId());
                item.setNickname(dynamic.getNickname());
                item.setAvatar(dynamic.getAvatar());
                item.setTitle(dynamic.getTitle());
                item.setContent(dynamic.getContent());
                item.setPublishTime(dynamic.getPublishTime());
                item.setLikeCount(dynamic.getLikeCount());
                item.setCommentCount(dynamic.getCommentCount());
                // 解析图片URL
                if (dynamic.getImageUrls() != null) {
                    try {
                        item.setImageUrls(Arrays.asList(objectMapper.readValue(dynamic.getImageUrls(), String[].class)));
                    } catch (JsonProcessingException e) {
                        // 处理异常
                    }
                }
                return item;
            }).collect(Collectors.toList());
            groupDynamic.setList(dynamicList);
            result.setGroupDynamic(groupDynamic);

            // 小组资源
            GroupDetailDTO.GroupResource groupResource = new GroupDetailDTO.GroupResource();
            List<GroupResource> resources = groupResourceMapper.findResourcesByGroupId(groupId, 0, 5);
            groupResource.setTotal(groupResourceMapper.countResourcesByGroupId(groupId));
            groupResource.setPageNum(1);
            groupResource.setPageSize(5);

            List<GroupDetailDTO.ResourceItem> resourceList = resources.stream().map(resource -> {
                GroupDetailDTO.ResourceItem item = new GroupDetailDTO.ResourceItem();
                item.setId(resource.getId());
                item.setTitle(resource.getTitle());
                item.setType(getFileType(resource.getFileName()));
                item.setUploader(resource.getUploader());
                item.setUploadTime(resource.getUploadTime());
                item.setDownloadCount(resource.getDownloadCount());
                item.setSize(resource.getFileSize());
                item.setLink("/api/v1/resource/" + resource.getId() + "/download");
                return item;
            }).collect(Collectors.toList());
            groupResource.setList(resourceList);
            result.setGroupResource(groupResource);

            // 小组通知
            GroupDetailDTO.GroupNotice groupNotice = new GroupDetailDTO.GroupNotice();
            List<GroupNotice> notices = groupNoticeMapper.findNoticesByGroupId(groupId);
            groupNotice.setTotal(groupNoticeMapper.countNoticesByGroupId(groupId));

            List<GroupDetailDTO.NoticeItem> noticeList = notices.stream().map(notice -> {
                GroupDetailDTO.NoticeItem item = new GroupDetailDTO.NoticeItem();
                item.setId(notice.getId());
                item.setTitle(notice.getTitle());
                item.setContent(notice.getContent());
                item.setPublishTime(notice.getPublishTime());
                return item;
            }).collect(Collectors.toList());
            groupNotice.setList(noticeList);
            result.setGroupNotice(groupNotice);

            return result;
        } catch (Exception e) {
            log.error("获取小组详情失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取小组详情失败");
        }
    }

    @Override
    @Transactional
    public GroupJoinDTO joinOrQuitGroup(String groupId, String userId, String action) {
        log.info("用户 {} {}小组 {}", userId, "join".equals(action) ? "加入" : "退出", groupId);

        try {
            Group group = groupMapper.findById(groupId);
            if (group == null) {
                throw new RuntimeException("小组不存在");
            }

            GroupJoinDTO result = new GroupJoinDTO();
            result.setGroupId(groupId);

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
                result.setMemberCount(group.getMemberCount() + 1);
            } else if ("quit".equals(action)) {
                // 退出小组
                int deleted = groupMemberMapper.deleteByGroupAndUser(groupId, userId);
                if (deleted > 0) {
                    // 更新小组成员数
                    groupMapper.updateMemberCount(groupId, -1);
                    result.setMemberCount(Math.max(0, group.getMemberCount() - 1));
                } else {
                    throw new RuntimeException("未加入该小组");
                }
            } else {
                throw new RuntimeException("不支持的操作类型");
            }

            return result;
        } catch (Exception e) {
            log.error("小组操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("小组操作失败");
        }
    }

    @Override
    @Transactional
    public GroupDynamicResultDTO publishDynamic(String groupId, GroupDynamicDTO request, String userId) {
        log.info("用户 {} 在小组 {} 发布动态", userId, groupId);

        try {
            // 检查用户是否已加入小组
            GroupMember member = groupMemberMapper.findByGroupAndUser(groupId, userId);
            if (member == null) {
                throw new RuntimeException("请先加入小组");
            }

            GroupDynamic dynamic = new GroupDynamic();
            dynamic.setId(IDGenerator.generateId());
            dynamic.setGroupId(groupId);
            dynamic.setUserId(userId);
            dynamic.setNickname("用户" + userId.substring(0, 6)); // 简化处理
            dynamic.setAvatar("https://jobhub.com/avatar/default.png");
            dynamic.setTitle(request.getTitle());
            dynamic.setContent(request.getContent());
            dynamic.setPublishTime(LocalDateTime.now());
            dynamic.setLikeCount(0);
            dynamic.setCommentCount(0);
            dynamic.setStatus("pending"); // 待审核

            // 处理图片URL
            if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
                dynamic.setImageUrls(objectMapper.writeValueAsString(request.getImageUrls()));
            }

            // 处理标签
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                dynamic.setTags(objectMapper.writeValueAsString(request.getTags()));
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
    public GroupResourceResultDTO uploadResource(String groupId, GroupResourceDTO request, String userId) {
        log.info("用户 {} 在小组 {} 上传资源", userId, groupId);

        try {
            // 检查用户是否已加入小组
            GroupMember member = groupMemberMapper.findByGroupAndUser(groupId, userId);
            if (member == null) {
                throw new RuntimeException("请先加入小组");
            }

            GroupResource resource = new GroupResource();
            resource.setId(IDGenerator.generateId());
            resource.setGroupId(groupId);
            resource.setTitle(request.getTitle());
            resource.setDescription(request.getDescription());
            resource.setFileUrl(request.getFile());
            resource.setFileName(extractFileName(request.getFile()));
            resource.setFileSize(calculateFileSize(request.getFile())); // 简化处理
            resource.setTag(request.getTag());
            resource.setUploader("用户" + userId.substring(0, 6)); // 简化处理
            resource.setUploadTime(LocalDateTime.now());
            resource.setDownloadCount(0);
            resource.setStatus("pending"); // 待审核

            int result = groupResourceMapper.insert(resource);
            if (result > 0) {
                GroupResourceResultDTO response = new GroupResourceResultDTO();
                response.setResourceId(resource.getId());
                response.setStatus("pending");
                response.setSubmitTime(LocalDateTime.now());
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