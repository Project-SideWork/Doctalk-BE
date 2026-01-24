package com.capstone.domain.github.controller;

import com.capstone.domain.github.api.GitHubService;
import com.capstone.domain.github.docs.GithubControllerDocs;
import com.capstone.domain.github.dto.*;
import com.capstone.domain.github.dto.request.OrgRepoRequest;
import com.capstone.domain.github.dto.response.GitHubOrgEventResponse;
import com.capstone.domain.github.dto.response.GithubIssueResponse;
import com.capstone.domain.github.dto.response.GithubPrResponse;
import com.capstone.domain.github.dto.response.ReviewCommentResponse;
import com.capstone.global.response.ApiResponse;
import com.capstone.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/github")
@RequiredArgsConstructor
public class GithubController implements GithubControllerDocs {
    private final GitHubService gitHubService;

    @GetMapping("/organizations/my")
    public ResponseEntity<ApiResponse<List<GitHubOrgDto>>> getMyGithubOrganizations(){
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.fetchMyGithubOrganizations()));
    }

    @PostMapping("/organizations/repository")
    public ResponseEntity<ApiResponse<Void>> createNewRepositoryInOrganization(@RequestBody OrgRepoRequest request){
        gitHubService.createOrganizationRepositoryOnGithub(request);
        return ResponseEntity.ok(ApiResponse.onSuccessVoid());
    }

//    /**
//     *
//     * @param files : 레포지토리에 업로드할 파일 목록. ( Issue 템플릿, PR 템플릿 등 )
//     * @param metadataList : 업로드 파일의 종류(category), 등록인(owner), 업로드할 레포지토리(repo)
//     * @return
//     * @throws IOException
//     */
//    @PostMapping("/org/repo/template")
//    public ResponseEntity<ApiResponse<Void>> uploadTemplates(
//            @RequestPart("files") List<MultipartFile> files,
//            @RequestPart("metadataList") List<RepoTemplateRequest> metadataList) throws IOException {
//
//        if (files.size() != metadataList.size()) {
//            throw new IllegalArgumentException("파일 수와 메타데이터 수가 일치하지 않습니다.");
//        }
//
//        for (int i = 0; i < files.size(); i++) {
//            MultipartFile file = files.get(i);
//            RepoTemplateRequest metadata = metadataList.get(i);
//
//            gitHubService.uploadUserIssueTemplate(file, metadata);
//        }
//
//        return ResponseEntity.ok(ApiResponse.onSuccessVoid());
//    }

//    /**
//     *
//     * @param orgName : 대상을 초대하고자 하는 조직 이름
//     * @param orgInvitationRequest : 초대하고자 하는 사람의 이메일 주소
//     * @return
//     */
//    @PostMapping("/org/invite/{orgName}")
//    public ResponseEntity<ApiResponse<Void>> inviteToOrganization(
//            @PathVariable String orgName,
//            @RequestBody OrgInvitationRequest orgInvitationRequest){
//        gitHubService.inviteUsersToOrg(orgName, orgInvitationRequest);
//        return ResponseEntity.ok(ApiResponse.onSuccessVoid());
//    }

    /**
     * 대상 프로젝트의 레포지토리에서 발생한 Issues 반환
     * @param projectId
     * @return
     */
    @GetMapping("/issues/{projectId}")
    public ResponseEntity<ApiResponse<GithubIssueResponse>> getProjectGithubIssues(@PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.fetchGithubIssuesByProject(projectId)));
    }

    /**
     * 대상 프로젝트의 레포지토리에서 발생한 PRs 반환
     * @param org
     * @param repo
     * @return
     */
    @GetMapping("/prs")
    public ResponseEntity<ApiResponse<List<GitHubPullRequestDto>>> getRepositoryPullRequests(
            @RequestParam String org,
            @RequestParam String repo
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.fetchPullRequestsFromRepository(org, repo)));
    }

    /**
     * 참여 중인 프로젝트의 레포지토리에서 내가 올린 PRs 반환
     * @param userDetails
     * @param projectId
     * @return
     */
    @GetMapping("/prs/requested/{projectId}")
    public ResponseEntity<ApiResponse<GithubPrResponse>> getReviewRequestedProjectPullRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails,  @PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.fetchReviewRequestPullRequestsInProject(userDetails, projectId)));
    }

    /**
     * 프로젝트에서 사용중인 레포지토리에서 발생한 모든 Issue와 PR에 관련된 이벤트 내역 반환 ( 이슈 생성, PR 댓글 등 )
     * @param projectId
     * @return
     */
    @GetMapping("/event/{projectId}")
    public ResponseEntity<ApiResponse<GitHubOrgEventResponse>> getProjectGithubEvents(@PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.fetchGithubEventsByProject(projectId)));
    }


    /**
     * 프로젝트 내에서 내가 생성한 Issue, PR 집계
     * @param customUserDetails
     * @param projectId
     * @return
     */
    @GetMapping("/stat/{projectId}")
    public ResponseEntity<ApiResponse<List<ContributionMetricWithShareDto>>> getMyProjectGithubStats(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.aggregateMyGithubStatsByProject(projectId, "kamillcream")));
    }


    /**
     * 조직에 속한 레포지토리에서 발생한 리뷰 코멘트 목록 반환
     * @param org
     * @param repo
     * @return
     */
    @GetMapping("/repo/reviews/comments")
    public ResponseEntity<ApiResponse<List<ReviewCommentResponse>>> getRepositoryReviewComments(
            @RequestParam String org,
            @RequestParam String repo
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.fetchReviewCommentsFromRepository(org, repo)));
    }

    /**
     * 조직에 속한 레포지토리에서 리뷰 관련 활동(approve, change_request, comment) 등을 반환.
     * @param org
     * @param repo
     * @param prCount
     * @return
     */
    @GetMapping("/repo/stats")
    public ResponseEntity<ApiResponse<ReviewStatsResponse>> getRepositoryReviewStats(
            @RequestParam String org,
            @RequestParam String repo,
            @RequestParam(defaultValue = "10") int prCount
    ) {
        return ResponseEntity.ok(ApiResponse.onSuccess(gitHubService.getRepositoryReviewStats(org, repo, prCount)));
    }
}