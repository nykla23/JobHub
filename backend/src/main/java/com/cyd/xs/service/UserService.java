package com.cyd.xs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyd.xs.dto.profile.DTO.UserPrivacyUpdateDTO;
import com.cyd.xs.dto.profile.DTO.UserProfileUpdateDTO;
import com.cyd.xs.dto.profile.VO.PersonalHomePageVO;
import com.cyd.xs.dto.profile.VO.UserPrivacyVO;
import com.cyd.xs.dto.profile.VO.UserProfileVO;
import com.cyd.xs.dto.user.*;
import com.cyd.xs.entity.User.User;
import java.util.Map;
import java.util.List;
public interface UserService extends IService<User> {

    // ===============================
    // 原有方法（保持不动）
    // ===============================

    void updateIdentityTag(Long userId, String identityTag);
    String getIdentityTag(Long userId);
    Long register(UserRegisterDTO registerDTO);

    void updateCareerStage(Long userId, String careerStage);

    LoginResponseDTO login(UserLoginDTO loginDTO);

    boolean checkUsernameExists(String username);

    void changePassword(UserLoginDTO loginDTO);

    void forgotPasswordReset(ForgotPasswordResetDTO resetdto);

    UserProfileVO getPersonalProfile(String username);

    void updatePersonalProfile(String username, UserProfileUpdateDTO updateDTO);

    UserPrivacyVO getPrivacySettings(String username);

    void updatePrivacySettings(String username, UserPrivacyUpdateDTO updateDTO);

    PersonalHomePageVO getPersonalHomePage(String username);

    PersonalHomePageVO getPersonalHomePageByUserId(Long userId);

    // ===============================
    // ⭐⭐⭐ 新增：基于 userId 的方法（关键）
    // ===============================

    /** 通过 userId 获取个人资料 */
    UserProfileVO getPersonalProfileByUserId(Long userId);

    /** 通过 userId 更新个人资料 */
    void updatePersonalProfileByUserId(Long userId, UserProfileUpdateDTO updateDTO);

    /** 通过 userId 获取隐私设置 */
    UserPrivacyVO getPrivacySettingsByUserId(Long userId);
    Map<String, Object> getHomeOverview(String identity);
    /** 通过 userId 更新隐私设置 */
    void updatePrivacySettingsByUserId(Long userId, UserPrivacyUpdateDTO updateDTO);
    Long getUserIdByUsername(String username);

}
