package com.capstone.domain.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ContributionMetricWithShareDto {
    private String label;

    private int totalCount;
    private int myCount;

    private double sharePercent;
}