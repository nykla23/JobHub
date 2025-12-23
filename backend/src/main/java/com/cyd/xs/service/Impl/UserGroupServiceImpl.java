package com.cyd.xs.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyd.xs.Utils.SecurityUtils;

import com.cyd.xs.dto.profile.VO.GroupWithUserRoleVO;

import com.cyd.xs.entity.Group.Group;
import com.cyd.xs.entity.User.UserGroup;

import com.cyd.xs.mapper.UserGroupMapper;
import com.cyd.xs.mapper.groups.GroupMapper;
import com.cyd.xs.service.UserGroupService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
/*
* 用户-小组关联服务实现类
 */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup>
        implements UserGroupService {

    private final GroupMapper groupMapper;

    public UserGroupServiceImpl(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @Override
    public List<GroupWithUserRoleVO> listMyGroups() {
        Long userId = SecurityUtils.getUserId();
        return baseMapper.listUserGroups(userId);
    }

    @Override
    public boolean joinGroup(Long groupId) {
        Long userId = SecurityUtils.getUserId();
        // 1. 校验小组是否有效
        Group group = groupMapper.selectById(groupId);
        if (group == null) {
            throw new RuntimeException("小组不存在或已失效");
        }
        // 2. 校验是否已加入该小组
        Integer count = baseMapper.checkUserInGroup(userId, groupId);
        if (count > 0) {
            throw new RuntimeException("已加入该小组");
        }
        // 3. 新增用户-小组关联记录
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(userId);
        userGroup.setGroupId(groupId);
        userGroup.setRoleInGroup("MEMBER"); // 默认普通成员
        userGroup.setJoinedAt(LocalDateTime.now());
        return save(userGroup);
    }

    @Override
    public boolean leaveGroup(Long groupId) {
        Long userId = SecurityUtils.getUserId();
        // 1. 校验是否是小组创建者（创建者不能退出，只能解散）
        Group group = groupMapper.selectById(groupId);
        if (group != null && group.getCreatorId().equals(userId)) {
            throw new RuntimeException("创建者不能退出小组，请解散小组");
        }
        // 2. 删除用户-小组关联记录
        int affectedRows = baseMapper.delete(
                Wrappers.<UserGroup>lambdaQuery()
                        .eq(UserGroup::getUserId, userId)
                        .eq(UserGroup::getGroupId, groupId)
        );
        return affectedRows > 0;
    }

    @Override
    public List<Group> listDiscoverGroups(Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;

        return groupMapper.selectList(
                Wrappers.<Group>lambdaQuery()
                        .eq(Group::getStatus, "normal")
                        .last("LIMIT " + pageSize + " OFFSET " + offset)
        );
    }

}