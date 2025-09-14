package com.capstone.domain.project.controller;

import com.capstone.docs.ProjectControllerDocs;
import com.capstone.domain.project.dto.request.*;

import com.capstone.domain.project.dto.response.InviteCheckResult;
import com.capstone.domain.project.dto.response.ProjectResponse;
import com.capstone.domain.project.entity.Project;
import com.capstone.domain.project.service.ProjectService;

import com.capstone.domain.user.service.ProjectUserService;
import com.capstone.global.response.ApiResponse;
import com.capstone.global.response.status.ErrorStatus;
import com.capstone.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
@Validated
public class ProjectController implements ProjectControllerDocs {
    private final ProjectService projectService;
    private final ProjectUserService projectUserService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Project>> registerProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ProjectSaveRequest projectSaveRequest){

        return ResponseEntity.ok(ApiResponse.onSuccess(projectService.processRegister(customUserDetails, projectSaveRequest)));
    }


    @PutMapping("/update/{projectId}")
    @PreAuthorize("@projectAuthorityEvaluator.hasPermission(#projectId, {'ROLE_MANAGER','ROLE_MEMBER'}, authentication)")
    public ResponseEntity<ApiResponse<Project>> updateProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("projectId") String projectId,
            @Valid @RequestBody ProjectUpdateRequest projectUpdateRequest){
        return ResponseEntity.ok(ApiResponse.onSuccess(projectService.processUpdate(projectId, projectUpdateRequest, customUserDetails)));
    }


    @PutMapping("/auth/{projectId}")
    @PreAuthorize("@projectAuthorityEvaluator.hasPermission(#projectId, {'ROLE_MANAGER','ROLE_MEMBER'}, authentication)")
    public ResponseEntity<ApiResponse<Project>> updateAuthority(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("projectId") String projectId,
            @RequestBody ProjectAuthorityRequest projectAuthority){
        return ResponseEntity.ok(ApiResponse.onSuccess(projectUserService.processAuth(customUserDetails, projectId, projectAuthority)));
    }

    @GetMapping("/invite")
    public ResponseEntity<ApiResponse<InviteCheckResult>> validateInviteMember(
            @RequestParam(required = false) String projectId,
            @Email(message = "이메일 형식이 올바르지 않습니다.") @RequestParam String email){
        InviteCheckResult result = projectUserService.checkInvitedMember(projectId, email);

        if (!result.available()) { // 이미 존재하는 사용자
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.onFailure(ErrorStatus.CONFLICT.getCode(), result.message(), null));
        }

        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @PutMapping("/invite/{projectId}")
    @PreAuthorize("@projectAuthorityEvaluator.hasPermission(#projectId, {'ROLE_MANAGER','ROLE_MEMBER'}, authentication)")
    public ResponseEntity<ApiResponse<Void>> inviteProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("projectId") String projectId,
            @RequestBody ProjectInviteRequest inviteRequest){
        projectUserService.processInvite(customUserDetails, projectId, inviteRequest);
        return ResponseEntity.ok(ApiResponse.onSuccess());
    }


    @GetMapping("/invite/accept")
    public ResponseEntity<Object> acceptInvitation(@RequestParam String credentialCode){
        String redirectUri = projectUserService.processInviteAcceptProjectUser(credentialCode);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUri)).build();
    }

    @GetMapping("/load")
    @PreAuthorize("@projectAuthorityEvaluator.hasPermission(#projectId, {'ROLE_MANAGER','ROLE_MEMBER'}, authentication)")
    public ResponseEntity<ApiResponse<ProjectResponse>> loadProject(@RequestParam String projectId){
        return ResponseEntity.ok(ApiResponse.onSuccess(projectService.getProjectContent(projectId)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> loadProjectList(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(ApiResponse.onSuccess(projectService.getProjectList(userDetails)));
    }

    @DeleteMapping("/{projectId}/user/{email}")
    @PreAuthorize("@projectAuthorityEvaluator.hasPermission(#projectId, {'ROLE_MANAGER'}, authentication)")
    public ResponseEntity<ApiResponse<Void>> deleteProjectUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String projectId,
            @PathVariable String email){
        projectUserService.deleteProjectUser(customUserDetails, projectId, email);
        return ResponseEntity.ok(ApiResponse.onSuccess());
    }

}
