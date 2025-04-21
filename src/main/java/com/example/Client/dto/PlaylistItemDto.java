package com.example.Client.dto;

import lombok.Data;

@Data
public class PlaylistItemDto {
    private Long id;
    private Long playlistId;
    private Long audioFileId;
    private String audioFileTitle;
    private String audioFileUrl;
    private Integer position;
    private Integer duration;
} 