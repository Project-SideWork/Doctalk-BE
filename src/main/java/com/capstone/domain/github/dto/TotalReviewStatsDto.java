package com.capstone.domain.github.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TotalReviewStatsDto {
    private int approved;
    private int changesRequested;
    private int commented;
}
