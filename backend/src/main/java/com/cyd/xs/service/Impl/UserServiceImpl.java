package com.cyd.xs.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyd.xs.config.JwtConfig;
import com.cyd.xs.dto.profile.DTO.UserPrivacyUpdateDTO;
import com.cyd.xs.dto.profile.DTO.UserProfileUpdateDTO;
import com.cyd.xs.dto.profile.VO.PersonalHomePageVO;
import com.cyd.xs.dto.profile.VO.UserDataStatsVO;
import com.cyd.xs.dto.profile.VO.UserPrivacyVO;
import com.cyd.xs.dto.profile.VO.UserProfileVO;
import com.cyd.xs.dto.user.*;
import com.cyd.xs.entity.User.*;
import com.cyd.xs.exception.BusinessException;
import com.cyd.xs.mapper.*;
import com.cyd.xs.mapper.groups.GroupMapper;
import com.cyd.xs.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.Map;
import java.util.List;
import java.util.Map;

import com.cyd.xs.entity.Topic.Topic;
import com.cyd.xs.mapper.Topic.TopicMapper;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TopicMapper topicMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserBrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private UserGroupMapper userGroupMapper;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private GroupMapper groupMapper;

    // ===============================
    // 原有方法（完全不动）
    // ===============================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterDTO registerDTO) {
        if (checkUsernameExists(registerDTO.getUsername())) {
            throw new BusinessException("用户名已被占用");
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setDisplayName(Convert.toStr(registerDTO.getDisplayName(), registerDTO.getUsername()));
        user.setAvatarUrl(registerDTO.getAvatarUrl());
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setCreditScore(100);

        if (registerDTO.getIdentityTag() != null && !registerDTO.getIdentityTag().trim().isEmpty()) {
            user.setIdentityTag(registerDTO.getIdentityTag());
            logger.info("【注册】设置身份标签: {}", registerDTO.getIdentityTag());
        }

        UserProfile profile = new UserProfile();
        profile.setBio(registerDTO.getBio());
        profile.setCareerStage(registerDTO.getCareerStage());
        profile.setFields(registerDTO.getFields());
        profile.setLocation(registerDTO.getLocation());
        profile.setEducation(registerDTO.getEducation());
        user.setProfileJson(JSONUtil.toJsonStr(profile));

        user.setPrivacyJson(JSONUtil.toJsonStr(new UserPrivacy()));
        user.setPublicStats(JSONUtil.toJsonStr(new UserPublicStats()));
        user.setSensitiveJson(JSONUtil.toJsonStr(new UserSensitive()));

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional
    public void updateCareerStage(Long userId, String careerStage) {
        if (!CareerStageEnum.isValid(careerStage)) {
            throw new BusinessException("身份选择无效");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfile profile = JSONUtil.toBean(user.getProfileJson(), UserProfile.class);
        profile.setCareerStage(careerStage);
        user.setProfileJson(JSONUtil.toJsonStr(profile));
        user.setStatus("ACTIVE");

        userMapper.updateById(user);
    }

    @Override
    public LoginResponseDTO login(UserLoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }

        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setDisplayName(user.getDisplayName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setRole(user.getRole());
        dto.setToken(jwtConfig.generateToken(user.getId().toString()));
        dto.setExpireTime(jwtConfig.getExpireTime());

        dto.setIdentityTag(user.getIdentityTag());

        if (dto.getIdentityTag() == null || dto.getIdentityTag().trim().isEmpty()) {
            try {
                UserProfile profile = JSONUtil.toBean(user.getProfileJson(), UserProfile.class);
                if (profile != null && profile.getCareerStage() != null) {
                    dto.setIdentityTag(profile.getCareerStage());
                    logger.info("【登录】从profileJson中获取身份标签: {}", profile.getCareerStage());
                }
            } catch (Exception e) {
                logger.warn("【登录】解析profileJson失败", e);
            }
        }
        logger.info("【登录】用户 {} 登录成功，身份标签: {}", user.getUsername(), dto.getIdentityTag());
        return dto;
    }

    @Override
    public boolean checkUsernameExists(String username) {
        Integer count = userMapper.countByUsername(username);
        return count != null && count > 0;
    }

    @Override
    public void changePassword(UserLoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(loginDTO.getPassword()));
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void forgotPasswordReset(ForgotPasswordResetDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    public UserProfileVO getPersonalProfile(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserProfile profile = JSONUtil.toBean(user.getProfileJson(), UserProfile.class);

        UserProfileVO vo = new UserProfileVO();
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setDisplayName(user.getDisplayName());
        vo.setBio(profile.getBio());
        vo.setCareerStage(profile.getCareerStage());
        vo.setFields(profile.getFields());
        vo.setLocation(profile.getLocation());
        vo.setEducation(profile.getEducation());
        return vo;
    }

    @Override
    @Transactional
    public void updatePersonalProfile(String username, UserProfileUpdateDTO updateDTO) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setDisplayName(updateDTO.getDisplayName());

        UserProfile profile = JSONUtil.toBean(user.getProfileJson(), UserProfile.class);
        BeanUtil.copyProperties(updateDTO, profile, CopyOptions.create().ignoreNullValue());
        user.setProfileJson(JSONUtil.toJsonStr(profile));

        userMapper.updateById(user);
    }

    @Override
    public UserPrivacyVO getPrivacySettings(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserPrivacy privacy = JSONUtil.toBean(
                StringUtils.isEmpty(user.getPrivacyJson()) ? "{}" : user.getPrivacyJson(),
                UserPrivacy.class
        );

        UserPrivacyVO vo = new UserPrivacyVO();
        BeanUtil.copyProperties(privacy, vo);
        return vo;
    }

    @Override
    @Transactional
    public void updatePrivacySettings(String username, UserPrivacyUpdateDTO updateDTO) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserPrivacy privacy = JSONUtil.toBean(
                StringUtils.isEmpty(user.getPrivacyJson()) ? "{}" : user.getPrivacyJson(),
                UserPrivacy.class
        );

        BeanUtil.copyProperties(updateDTO, privacy, CopyOptions.create().ignoreNullValue());
        user.setPrivacyJson(JSONUtil.toJsonStr(privacy));
        userMapper.updateById(user);
    }

    @Override
    public PersonalHomePageVO getPersonalHomePage(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        PersonalHomePageVO vo = new PersonalHomePageVO();
        vo.setBaseInfo(buildBaseInfo(user));
        vo.setPrivacySettings(buildPrivacySettings(user));
        vo.setDataStats(buildDataStats(user.getId()));
        return vo;
    }

    @Override
    public PersonalHomePageVO getPersonalHomePageByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        PersonalHomePageVO vo = new PersonalHomePageVO();
        vo.setBaseInfo(buildBaseInfo(user));
        vo.setPrivacySettings(buildPrivacySettings(user));
        vo.setDataStats(buildDataStats(userId));
        return vo;
    }

    // ===============================
    // ⭐⭐⭐ 补充实现的方法（仅新增）
    // ===============================

    @Override
    public UserProfileVO getPersonalProfileByUserId(Long userId) {
        return getPersonalProfile(userMapper.selectById(userId).getUsername());
    }

    @Override
    @Transactional
    public void updatePersonalProfileByUserId(Long userId, UserProfileUpdateDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        updatePersonalProfile(user.getUsername(), updateDTO);
    }

    @Override
    public UserPrivacyVO getPrivacySettingsByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return getPrivacySettings(user.getUsername());
    }

    @Override
    @Transactional
    public void updatePrivacySettingsByUserId(Long userId, UserPrivacyUpdateDTO updateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        updatePrivacySettings(user.getUsername(), updateDTO);
    }

    // ===============================
    // 私有方法（原样）
    // ===============================

    private UserProfileVO buildBaseInfo(User user) {
        UserProfile profile = JSONUtil.toBean(
                user.getProfileJson() == null ? "{}" : user.getProfileJson(),
                UserProfile.class
        );

        UserProfileVO vo = new UserProfileVO();
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setDisplayName(user.getDisplayName());
        vo.setBio(profile.getBio());
        vo.setCareerStage(profile.getCareerStage());
        vo.setFields(profile.getFields());
        vo.setLocation(profile.getLocation());
        vo.setEducation(profile.getEducation());
        return vo;
    }

    private UserPrivacyVO buildPrivacySettings(User user) {
        return JSONUtil.toBean(
                user.getPrivacyJson() == null ? "{}" : user.getPrivacyJson(),
                UserPrivacyVO.class
        );
    }

    private UserDataStatsVO buildDataStats(Long userId) {
        UserDataStatsVO vo = new UserDataStatsVO();
        vo.setBrowseHistoryCount(browseHistoryMapper.countByUserId(userId));
        vo.setGroupCount(userGroupMapper.countValidGroupsByUserId(userId));
        vo.setPostCount(entityMapper.countByAuthorIdAndStatus(userId, "PUBLISHED"));
        vo.setCollectionCount(collectionMapper.countByUserId(userId));
        return vo;
    }
    @Override
    @Transactional
    public void updateIdentityTag(Long userId, String identityTag) {

        // 复用你已有的枚举校验（学生 / 专家 等）
        if (!CareerStageEnum.isValid(identityTag)) {
            throw new BusinessException("身份标签不合法");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setIdentityTag(identityTag);
        userMapper.updateById(user);
    }

    @Override
    public String getIdentityTag(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getIdentityTag();
    }
    private static final Map<String, List<String>> RECOMMEND_TAG_MAP = Map.of(
            "学生", List.of("秋招面试", "第一份实习", "简历优化"),
            "职场菜鸟", List.of("职场新人避坑", "转正汇报", "沟通技巧"),
            "职场老手", List.of("行业交流", "管理进阶", "经验分享", "offer选择")
    );

    @Override
    public Map<String, Object> getHomeOverview(String identity) {

        // ① 打印原始身份（必须）
        logger.info("【首页推荐】原始 identity = [{}]", identity);

        // ② 统一清洗 identity（去空格）
        String safeIdentity = identity == null ? "" : identity.trim();

        // ③ 从中文 Map 取推荐标签（关键）
        List<String> tags = RECOMMEND_TAG_MAP.get(safeIdentity);

        // ④ 如果没取到，强制兜底为“学生”
        if (tags == null || tags.isEmpty()) {
            logger.warn("【首页推荐】identity [{}] 未命中推荐规则，使用【学生】兜底", safeIdentity);
            tags = RECOMMEND_TAG_MAP.get("学生");
            safeIdentity = "学生";
        }

        logger.info("【首页推荐】最终 identity = {}", safeIdentity);
        logger.info("【首页推荐】推荐 tags = {}", tags);

        // ⑤ 查询 topics（你之前已经写好的方法）
        List<Topic> topics =
                topicMapper.findRecommendedTopicsByTags(tags, 6);

        logger.info("【首页推荐】查询到 topics 数量 = {}", topics.size());

        return Map.of(
                "identity", safeIdentity,
                "recommendTags", tags,
                "topics", topics
        );
    }
    @Override
    public Long getUserIdByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getId();
    }

}
