package com.cyd.xs.entity.Group;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("`groups`")
public class Group {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String intro;
    private String avatar;
    private String activityType;
    private Long creatorId;
    private String status;
    private LocalDateTime createdAt;

    // ===== 数据库真实字段 =====
    @TableField("member_count")
    private Integer memberCount;

    // ===== SQL 计算字段（不在表中）=====
    @TableField(exist = false)
    private String tags;

    @TableField(exist = false)
    private String tagsStr;

    @TableField(exist = false)
    private Integer isJoined;
}
