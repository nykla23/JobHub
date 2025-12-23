package com.cyd.xs.controller.profile;

import com.cyd.xs.Response.Result;
import com.cyd.xs.Utils.SecurityUtils;
import com.cyd.xs.dto.profile.DTO.UserPrivacyUpdateDTO;
import com.cyd.xs.dto.profile.DTO.UserProfileUpdateDTO;
import com.cyd.xs.dto.profile.VO.PersonalHomePageVO;
import com.cyd.xs.dto.profile.VO.UserPrivacyVO;
import com.cyd.xs.dto.profile.VO.UserProfileVO;
import com.cyd.xs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// ===== 新增：logger 所需 import =====
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// ===================================

@RestController
@RequestMapping("/api/user/profile")
public class UserProfileController {

    // ===== 新增：logger 定义 =====
    private static final Logger logger =
            LoggerFactory.getLogger(UserProfileController.class);
    // =============================

    @Autowired
    private UserService userService;

    /** 获取个人基础信息（展示用） */
    @GetMapping
    public Result<UserProfileVO> getPersonalProfile() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");

        // ===== 新增：获取接口日志 =====
        logger.info("【获取资料】current userId = {}", userId);
        // ==============================

        UserProfileVO profileVO = userService.getPersonalProfileByUserId(userId);
        return Result.success("获取个人信息成功", profileVO);
    }

    /** 编辑个人基础信息（保存用） */
    @PutMapping
    public Result<Void> updatePersonalProfile(@Valid @RequestBody UserProfileUpdateDTO updateDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");

        // ===== 新增：保存接口日志 =====
        logger.info("【更新资料】current userId = {}", userId);
        // ==============================

        userService.updatePersonalProfileByUserId(userId, updateDTO);
        return Result.success("编辑个人信息成功");
    }

    /** 获取隐私设置 */
    @GetMapping("/privacy")
    public Result<UserPrivacyVO> getPrivacySettings() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        UserPrivacyVO privacyVO = userService.getPrivacySettingsByUserId(userId);
        return Result.success("获取隐私设置成功", privacyVO);
    }

    /** 修改隐私设置 */
    @PutMapping("/privacy")
    public Result<Void> updatePrivacySettings(@Valid @RequestBody UserPrivacyUpdateDTO updateDTO) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        userService.updatePrivacySettingsByUserId(userId, updateDTO);
        return Result.success("隐私设置修改成功");
    }

    /** 获取个人主页 */
    @GetMapping("/homepage")
    public Result<PersonalHomePageVO> getPersonalHomePage() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        PersonalHomePageVO homePageVO = userService.getPersonalHomePageByUserId(userId);
        return Result.success("获取个人主页成功", homePageVO);
    }

    /** 更新用户身份（职业阶段） */
    @PutMapping("/career-stage")
    public Result<Void> updateCareerStage(@RequestParam String careerStage) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");
        userService.updateCareerStage(userId, careerStage);
        return Result.success("身份更新成功");
    }
    // =====================================================
    // ===== 新增：身份标签（独立按钮，不走 profile） =====
    // =====================================================

    /** 获取身份标签（页面初始化用） */
    @GetMapping("/identity")
    public Result<String> getIdentityTag() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");

        logger.info("【获取身份标签】current userId = {}", userId);

        String identityTag = userService.getIdentityTag(userId);
        return Result.success("获取身份标签成功", identityTag);

    }

    /** 保存身份标签（身份弹窗按钮用） */
    @PutMapping("/identity")
    public Result<Void> updateIdentityTag(
            @RequestParam String identityTag) {

        Long userId = SecurityUtils.getUserId();
        if (userId == null) return Result.error("未登录或登录态无效");

        logger.info("【更新身份标签】current userId = {}, identityTag={}",
                userId, identityTag);

        userService.updateIdentityTag(userId, identityTag);
        return Result.success("身份标签保存成功");
    }
}
