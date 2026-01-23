package com.capstone.domain.github.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewCommentResponse {
    Long commentId;
    String body;
    String createdAt;

    // 작성자
    String authorLogin;
    String authorAvatarUrl;

    // PR 정보
    String pullRequestUrl;

    // 코드 위치
    String filePath;
    String diffHunk;

    // 링크
    String commentUrl;
}
