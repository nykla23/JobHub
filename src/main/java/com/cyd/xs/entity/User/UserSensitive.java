package com.cyd.xs.entity.User;

import lombok.Data;

// 敏感信息（sensitive_json）
@Data
public class UserSensitive {
    private String company;    // 公司（加密）
    private String salary;     // 薪资（加密）
    private String phone;      // 手机号（加密）

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}