package com.capstone.domain.github.docs;

import com.capstone.domain.github.dto.*;
import com.capstone.domain.github.dto.request.OrgRepoRequest;
import com.capstone.domain.github.dto.response.GitHubOrgEventResponse;
import com.capstone.domain.github.dto.response.GithubIssueResponse;
import com.capstone.domain.github.dto.response.GithubPrResponse;
import com.capstone.domain.github.dto.response.ReviewCommentResponse;
import com.capstone.global.response.ApiResponse;
import com.capstone.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "GitHub 연동 API")
public interface GithubControllerDocs {

    @Operation(description = "내 GitHub 조직 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<List<GitHubOrgDto>>> getMyGithubOrganizations();

    @Operation(description = "조직 내 신규 레포지토리 생성")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 부족")
    })
    ResponseEntity<ApiResponse<Void>> createNewRepositoryInOrganization(
            @RequestBody OrgRepoRequest request
    );

    @Operation(description = "프로젝트 레포지토리의 Issue 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "프로젝트 또는 레포지토리 없음")
    })
    ResponseEntity<ApiResponse<GithubIssueResponse>> getProjectGithubIssues(
            @PathVariable String projectId
    );
    
    @Operation(description = "레포지토리의 PR 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<GitHubPullRequestDto>>> getRepositoryPullRequests(
            @RequestParam String org,
            @RequestParam String repo
    );

    @Operation(description = "나에게 리뷰를 요청한 PR 목록 조회 (프로젝트 기준)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<GithubPrResponse>> getReviewRequestedProjectPullRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String projectId
    );

   
    @Operation(description = "프로젝트 내 Issue/PR 관련 이벤트 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<GitHubOrgEventResponse>> getProjectGithubEvents(
            @PathVariable String projectId
    );
    
    @Operation(description = "프로젝트 내 나의 GitHub 활동 통계 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패")
    })
    ResponseEntity<ApiResponse<List<ContributionMetricWithShareDto>>> getMyProjectGithubStats(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String projectId
    );

   
    @Operation(description = "PR 리뷰 코멘트 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<ReviewCommentResponse>>> getRepositoryReviewComments(
            @RequestParam String org,
            @RequestParam String repo
    );

    @Operation(description = "레포지토리에서 발생한 PR에 대한 리뷰 활동 통계 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<ReviewStatsResponse>> getRepositoryReviewStats(
            @RequestParam String org,
            @RequestParam String repo,
            @RequestParam(defaultValue = "10") int prCount
    );
}