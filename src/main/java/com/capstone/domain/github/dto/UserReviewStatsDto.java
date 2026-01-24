package com.capstone.domain.github.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReviewStatsDto {
    private String reviewerName;
    private String avatarUrl;
    private int approved;
    private int changesRequested;
    private int commented;
}