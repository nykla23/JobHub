package com.cyd.xs.entity.User;

import lombok.Data;

import java.util.List;

// 扩展信息（profile_json）
@Data
public class UserProfile {
    private String bio;              // 简介（如"2025届准毕业生，目标互联网运营岗"）
    private String careerStage;      // 职业阶段（准毕业生/职场新人/职场老手）
    private List<String> fields;     // 关注领域（如["互联网运营","数据分析"]）
    private String location;         // 所在地
    private String education;        // 教育背景

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCareerStage() {
        return careerStage;
    }

    public void setCareerStage(String careerStage) {
        this.careerStage = careerStage;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }
}