package com.example.Client.dto;

import lombok.Data;

@Data
public class PlaylistDto {
    private Long id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Long createdById;
    private String createdByUsername;
    private Integer itemsCount;
} 