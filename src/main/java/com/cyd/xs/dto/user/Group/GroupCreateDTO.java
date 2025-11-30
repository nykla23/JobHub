package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.util.List;

@Data
public class GroupCreateDTO {
    private String name;
    private List<String> tags;
    private String intro;
    private String avatar;
    private String activityDesc;
}