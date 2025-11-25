package com.cyd.xs.entity.User;

import lombok.Data;

import java.util.Map;

// 隐私设置（privacy_json）
@Data
public class UserPrivacy {
    private Boolean hideCompany = true;    // 隐藏公司
    private Boolean hideSalary = true;     // 隐藏薪资
    private Boolean hideHistory = false;   // 隐藏浏览历史
    private Boolean hidePosts = false;     // 隐藏发布内容
    private Map<String, Object> custom;    // 自定义隐私设置

    public Boolean getHideCompany() {
        return hideCompany;
    }

    public void setHideCompany(Boolean hideCompany) {
        this.hideCompany = hideCompany;
    }

    public Boolean getHideSalary() {
        return hideSalary;
    }

    public void setHideSalary(Boolean hideSalary) {
        this.hideSalary = hideSalary;
    }

    public Boolean getHideHistory() {
        return hideHistory;
    }

    public void setHideHistory(Boolean hideHistory) {
        this.hideHistory = hideHistory;
    }

    public Boolean getHidePosts() {
        return hidePosts;
    }

    public void setHidePosts(Boolean hidePosts) {
        this.hidePosts = hidePosts;
    }

    public Map<String, Object> getCustom() {
        return custom;
    }

    public void setCustom(Map<String, Object> custom) {
        this.custom = custom;
    }
}