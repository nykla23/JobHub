package com.cyd.xs.entity.User;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "carousels")
public class Carousel {
    @Id
    private String id;

    private String title;
    private String imageUrl;
    private String description;
    private String link;
    private Integer sortOrder;
    private String status; // active/inactive
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}