package com.capstone.domain.project.dto.response;

public record ProjectContributionResult(
        double issueContributionRate,
        double prContributionRate,
        double reviewContributionRate,
        double onTimeCompletionRate
) {
}
