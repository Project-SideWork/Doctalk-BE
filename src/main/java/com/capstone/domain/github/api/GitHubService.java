package com.capstone.domain.github.api;

import com.capstone.domain.github.dto.*;
import com.capstone.domain.github.dto.request.OrgRepoRequest;
import com.capstone.domain.github.dto.response.GitHubOrgEventResponse;
import com.capstone.domain.github.dto.response.GithubIssueResponse;
import com.capstone.domain.github.dto.response.GithubPrResponse;

import com.capstone.domain.github.dto.response.ReviewCommentResponse;
import com.capstone.domain.project.entity.Project;
import com.capstone.domain.project.entity.ProjectOrganization;
import com.capstone.domain.project.repository.ProjectRepository;
import com.capstone.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

import static com.capstone.domain.github.util.HttpSetter.githubEntity;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubService {
    private final ProjectRepository projectRepository;

    @Value("${github.api-url}")
    private String apiUrl;

    @Value("${github.token}")
    private String token;

    private final RestTemplate restTemplate;

    public void createOrganizationRepositoryOnGithub(OrgRepoRequest request) {
        String url = String.format("%s/orgs/%s/repos", apiUrl, request.orgName());

        Map<String, Object> body = new HashMap<>();
        body.put("name", request.repoName());
        body.put("private", request.isPrivate());
        body.put("auto_init", true);

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                githubEntity(token, body),
                String.class
        );
    }

//    public void inviteUsersToOrg(String orgName, OrgInvitationRequest orgInvitationRequest){
//        String url = String.format("https://api.github.com//orgs/%s/invitations", orgName);
//        HttpHeaders headers = HttpSetter.httpPreset(token);
//        Map<String, Object> body = new HashMap<>();
//
//        for(String email : orgInvitationRequest.email()){
//            body.put("email", email);
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//            restTemplate.exchange(
//                    url,
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//        }
//    }
//
//    public void uploadUserIssueTemplate(MultipartFile file, RepoTemplateRequest repoTemplateRequest) throws IOException {
//        String fileName = file.getOriginalFilename();
//        if (fileName == null || !fileName.endsWith(".md")) {
//            throw new IllegalArgumentException("이슈 템플릿은 .md 파일만 허용됩니다.");
//        }
//        String path;
//        switch(repoTemplateRequest.category()){
//            case "bug": path = ".github/ISSUE_TEMPLATE/bug_template.md";
//            case "feat": path = ".github/ISSUE_TEMPLATE/feat_template.md";
//            default: path = ".github/ISSUE_TEMPLATE/" + fileName;
//        }
//
//        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", repoTemplateRequest.owner(), repoTemplateRequest.repo(), path);
//
//        String encodedContent = Base64.getEncoder().encodeToString(file.getBytes());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
//        headers.set("Accept", "application/vnd.github+json");
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("message", "feat: 사용자 업로드 이슈 템플릿 추가");
//        body.put("content", encodedContent);
//        body.put("branch", "main");
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.PUT,
//                request,
//                String.class
//        );
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            log.info("✅ 이슈 템플릿 업로드 완료: {}", fileName);
//        } else {
//            log.error("❌ 업로드 실패: {} | {}", response.getStatusCode(), response.getBody());
//        }
//    }

    public List<GitHubOrgDto> fetchMyGithubOrganizations() {
        String url = String.format("%s/user/orgs", apiUrl);

        ResponseEntity<GitHubOrgDto[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                githubEntity(token),
                GitHubOrgDto[].class
        );

        return Optional.ofNullable(response.getBody())
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);
    }

    public GithubIssueResponse fetchGithubIssuesByProject(String teamId) {
        Project project = projectRepository.findById(teamId).orElseThrow();

        List<GitHubIssueDto> all = new ArrayList<>();

        for (ProjectOrganization org : Optional.ofNullable(project.getProjectOrganizations()).orElse(Collections.emptyList())) {
            String organization = org.getOrgName();
            for (String repoName : org.getProjectRepos()) {
                all.addAll(fetchIssues(organization, repoName));
            }
        }

         all.removeIf(dto -> dto.getPullRequest() != null);

        return new GithubIssueResponse(all.size(), all);
    }

    private List<GitHubIssueDto> fetchIssues(String organization, String repo) {
        List<GitHubIssueDto> acc = new ArrayList<>();
        int page = 1;
        final int perPage = 100;

        while (true) {
            String url = String.format("%s/repos/%s/%s/issues?state=open&per_page=%d&page=%d",
                    apiUrl, organization, repo, perPage, page);

            ResponseEntity<GitHubIssueDto[]> res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    githubEntity(token),           // 공통 헤더 분리
                    GitHubIssueDto[].class
            );

            GitHubIssueDto[] body = res.getBody();
            if (body == null || body.length == 0) break;

            acc.addAll(Arrays.asList(body));

            // 마지막 페이지면 종료
            if (body.length < perPage) break;
            page++;
        }

        return acc;
    }
    private List<GitHubIssueDto> fetchAllIssues(String organization, String repo) {
        List<GitHubIssueDto> acc = new ArrayList<>();
        int page = 1;
        final int perPage = 100;

        while (true) {
            String url = String.format("%s/repos/%s/%s/issues?state=all&per_page=%d&page=%d",
                    apiUrl, organization, repo, perPage, page);

            ResponseEntity<GitHubIssueDto[]> res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    githubEntity(token),
                    GitHubIssueDto[].class
            );

            GitHubIssueDto[] body = res.getBody();
            if (body == null || body.length == 0) break;

            acc.addAll(Arrays.asList(body));

            // 마지막 페이지면 종료
            if (body.length < perPage) break;
            page++;
        }

        return acc;
    }

//    public List<GitHubIssueDto> getAssignedIssues(String organization, String repo) {
//        String url = String.format("%s/repos/%s/%s/issues?creator=kamillcream", apiUrl, organization, repo);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token);
//        headers.set("Accept", "application/vnd.github+json");
//
//        HttpEntity<Void> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<GitHubIssueDto[]> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                GitHubIssueDto[].class
//        );
//
//        return Arrays.asList(response.getBody());
//    }


    public List<GitHubPullRequestDto> fetchPullRequestsFromRepository(String organization, String repo) {
        String url = String.format("%s/repos/%s/%s/pulls?state=all", apiUrl, organization, repo);
        ResponseEntity<GitHubPullRequestDto[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                githubEntity(token),
                GitHubPullRequestDto[].class
        );

        return Optional.ofNullable(response.getBody())
                .map(Arrays::asList)
                .orElseGet(Collections::emptyList);

    }

    public GithubPrResponse fetchReviewRequestPullRequestsInProject(CustomUserDetails userDetails, String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();

        List<GitHubPullRequestDto> all = new ArrayList<>();

        for (ProjectOrganization org : Optional.ofNullable(project.getProjectOrganizations()).orElse(Collections.emptyList())) {
            String organization = org.getOrgName();
            for (String repoName : org.getProjectRepos()) {
                all.addAll(fetchReviewRequestedPullRequests(organization, repoName, "kamillcream"));
            }
        }

        return new GithubPrResponse(all.size(), all);
    }

    private List<GitHubPullRequestDto> fetchReviewRequestedPullRequests(String organization, String repo, String username) {
        String url = String.format(
                "%s/search/issues?q=type:pr+state:open+repo:%s/%s+review-requested:%s",
                apiUrl, organization, repo, username
        );

        ResponseEntity<GitHubPrQueryResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                githubEntity(token),
                GitHubPrQueryResponse.class
        );

        if (response.getBody() == null || response.getBody().getItems() == null) {
            return Collections.emptyList();
        }

        return response.getBody().getItems();
    }

    public GitHubOrgEventResponse fetchGithubEventsByProject(String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow();

        List<GitHubOrgEventDto> all = new ArrayList<>();
        List<ProjectOrganization> orgs = Optional.ofNullable(project.getProjectOrganizations()).orElse(Collections.emptyList());

        List<String> orgNames = orgs.stream().map(
                ProjectOrganization::getOrgName
        ).toList();

        for (String orgName: orgNames) {
            all.addAll(fetchOrgIssuePrEvents(orgName));
        }

        all.sort(Comparator.comparing(GitHubOrgEventDto::getCreatedAt).reversed());

        return new GitHubOrgEventResponse(all.size(), all);
    }

    private List<GitHubOrgEventDto> fetchOrgIssuePrEvents(String org) {
        final int perPage = 100;
        int page = 1;

        List<GitHubOrgEventDto> acc = new ArrayList<>();

        while (true) {
            String url = String.format("%s/orgs/%s/events?per_page=%d&page=%d", apiUrl, org, perPage, page);

            ResponseEntity<GitHubOrgEventDto[]> res = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    githubEntity(token),
                    GitHubOrgEventDto[].class
            );

            GitHubOrgEventDto[] body = res.getBody();
            if (body == null || body.length == 0) {
                break; // 더 이상 이벤트 없음
            }

            List<GitHubOrgEventDto> filteredEvents = Arrays.stream(body)
                    .filter(e -> !e.getActor().getLogin().contains("coderabbit"))
                    .filter(e -> Set.of(
                            "IssuesEvent",
                            "PullRequestEvent",
                            "PullRequestReviewEvent",
                            "PullRequestReviewCommentEvent"
                    ).contains(e.getType()))
                    .toList();

            // PullRequestEvent인 경우 html_url 가져오기
            for (GitHubOrgEventDto event : filteredEvents) {
                if ("PullRequestEvent".equals(event.getType()) || "PullRequestReviewCommentEvent".equals(event.getType())) {
                    convertApiUrlToPrUrl(event);
                }
                convertApiUrlToRepoUrl(event);
            }

            acc.addAll(filteredEvents);

            // 마지막 페이지 도달
            if (body.length < perPage) {
                break;
            }
            page++;
        }

        return acc;
    }
    private void convertApiUrlToPrUrl(GitHubOrgEventDto event) {
        String apiUrl = event.getPayload().getPullRequest().getHtmlUrl();

        String htmlUrl = apiUrl
                .replace("https://api.github.com/repos/", "https://github.com/")
                .replace("/pulls/", "/pull/");

        event.getPayload().getPullRequest().setHtmlUrl(htmlUrl);
    }

    private void convertApiUrlToRepoUrl(GitHubOrgEventDto event) {
        String apiUrl = event.getRepo().getUrl();

        String htmlUrl = apiUrl
                .replace("https://api.github.com/repos/", "https://github.com/");

        event.getRepo().setUrl(htmlUrl);
    }

    public List<ContributionMetricWithShareDto> aggregateMyGithubStatsByProject(String projectId, String username) {
        Project project = projectRepository.findById(projectId).orElseThrow();

        int totIssues = 0, totPrs = 0;
        int myIssues = 0, myPrs = 0;

        List<ProjectOrganization> orgs = Optional.ofNullable(project.getProjectOrganizations()).orElse(Collections.emptyList());
        List<String> orgNames = orgs.stream().map(
                ProjectOrganization::getOrgName
        ).toList();

        for (String orgName : orgNames) {
            // 전체
            totIssues  += searchIssuesPrCountOrg(orgName, "is:issue", null);
            totPrs     += searchIssuesPrCountOrg(orgName, "is:pr", null);

            // 내 기여
            String author = "author:" + username;
            myIssues   += searchIssuesPrCountOrg(orgName, "is:issue", author);
            myPrs      += searchIssuesPrCountOrg(orgName, "is:pr", author);
        }

        return List.of(
                ContributionMetricWithShareDto.of("이슈 생성", totIssues, myIssues, pct(myIssues, totIssues)),
                ContributionMetricWithShareDto.of("PR 생성", totPrs, myPrs, pct(myPrs, totPrs))
        );
    }

    private int searchIssuesPrCountOrg(String org, String kindQualifier, String extra) {
        // org 단위 검색
        String q = "%s+org:%s".formatted(kindQualifier, org);
        if (extra != null && !extra.isBlank()) q += "+" + extra;

        String url = "%s/search/issues?q=%s&per_page=1".formatted(apiUrl, q);

        ResponseEntity<com.fasterxml.jackson.databind.JsonNode> resp =
                restTemplate.exchange(url, HttpMethod.GET, githubEntity(token), com.fasterxml.jackson.databind.JsonNode.class);

        var body = resp.getBody();
        return (body == null || body.get("total_count") == null) ? 0 : body.get("total_count").asInt();
    }


    private double pct(int mine, int total) {
        return total == 0 ? 0.0 : (mine * 100.0 / total);
    }


    public List<ReviewCommentResponse> fetchReviewCommentsFromRepository(String organization, String repo) {
        String url = String.format("%s/repos/%s/%s/pulls/comments", apiUrl, organization, repo);

        try {
            ResponseEntity<GitHubReviewCommentDto[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, githubEntity(token), GitHubReviewCommentDto[].class
            );
            List<ReviewCommentResponse> responseList = new ArrayList<>();

            for (GitHubReviewCommentDto reviewComment : Objects.requireNonNull(response.getBody())) {
                if (reviewComment.getUser() == null ) continue;
                GitHubReviewCommentDto.User user = reviewComment.getUser();
                String reviewer = user.getLogin();
                if(!reviewer.contains("coderabbit")){
                    responseList.add(
                            new ReviewCommentResponse(
                                    reviewComment.getId(),
                                    reviewComment.getBody(),
                                    reviewComment.getCreated_at().toString(),
                                    user.getLogin(), user.getAvatar_url(),
                                    reviewComment.getPull_request_url(),
                                    reviewComment.getPath(),
                                    reviewComment.getDiff_hunk(),
                                    reviewComment.getHtml_url()
                            )
                    );
                }
            }
            return responseList;

        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();
        } catch (Exception e) {
            // 로그로 예외 확인 권장
            return Collections.emptyList();
        }
    }

    public ReviewStatsResponse getRepositoryReviewStats(String organization, String repo, int prCount) {
        Map<String, UserReviewStatsDto> statsMap = new HashMap<>();
        int approved = 0;
        int changesRequested = 0;
        int commented = 0;

        List<GitHubPullRequestDto> pullRequests = fetchPullRequestsFromRepository(organization, repo);

        for (GitHubPullRequestDto pr : pullRequests) {
            String url = String.format("%s/repos/%s/%s/pulls/%d/reviews", apiUrl, organization, repo, pr.getNumber());

            try {
                ResponseEntity<GitHubReviewDto[]> response = restTemplate.exchange(
                        url, HttpMethod.GET, githubEntity(token), GitHubReviewDto[].class
                );

                GitHubReviewDto[] reviews = response.getBody();
                if (reviews == null) continue;
                for (GitHubReviewDto review : reviews) {
                    if (review.getUser() == null ) continue;
                    String reviewer = review.getUser().getLogin();
                    String avatarUrl = review.getUser().getAvatarUrl();

                    UserReviewStatsDto stat = statsMap.getOrDefault(
                            reviewer,
                            new UserReviewStatsDto(reviewer, avatarUrl, 0, 0, 0)
                    );
                    if(!reviewer.contains("coderabbit")){
                        switch (review.getState()) {
                            case "APPROVED":
                                stat.setApproved(stat.getApproved() + 1);
                                approved++;
                                break;
                            case "CHANGES_REQUESTED":
                                stat.setChangesRequested(stat.getChangesRequested() + 1);
                                changesRequested++;
                                break;
                            case "COMMENTED":
                                stat.setCommented(stat.getCommented() + 1);
                                commented++;
                                break;
                            default:
                                break;
                        }
                        statsMap.put(reviewer, stat);
                    }

                }
            } catch (Exception e) {
                System.err.println("PR#" + pr + " 에 대한 리뷰 조회 실패: " + e.getMessage());
            }
        }

        TotalReviewStatsDto totalReviewStatsDto = new TotalReviewStatsDto(approved, changesRequested, commented);
        List<UserReviewStatsDto> userReviewStatsDtoList = new ArrayList<>(statsMap.values());

        return new ReviewStatsResponse(totalReviewStatsDto, userReviewStatsDtoList);
    }
}