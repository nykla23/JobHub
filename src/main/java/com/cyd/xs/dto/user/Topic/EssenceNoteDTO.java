package com.cyd.xs.dto.user.Topic;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EssenceNoteDTO {
    private String noteId;
    private String noteUrl;
    private LocalDateTime generateTime;
}