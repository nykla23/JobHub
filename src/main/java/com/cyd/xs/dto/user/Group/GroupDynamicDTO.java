package com.cyd.xs.dto.user.Group;

import lombok.Data;
import java.util.List;

@Data
public class GroupDynamicDTO {
    private String title;
    private String content;
    private List<String> imageUrls;
    private List<String> tags;
}