package com.capstone.docs;

import com.capstone.domain.project.dto.request.ProjectAuthorityRequest;
import com.capstone.domain.project.dto.request.ProjectInviteRequest;
import com.capstone.domain.project.dto.request.ProjectSaveRequest;
import com.capstone.domain.project.dto.request.ProjectUpdateRequest;
import com.capstone.domain.project.dto.response.InviteCheckResult;
import com.capstone.domain.project.dto.response.ProjectResponse;
import com.capstone.domain.project.entity.Project;
import com.capstone.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "프로젝트 관련 API")
public interface ProjectControllerDocs {

    @Operation(summary = "✅ RequestBody 변경 ✅", description = "신규 프로젝트 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패 응답",
                                    summary = "유효하지 않은 토큰 또는 로그인 필요",
                                    value = """
            {
              "success": false,
              "code": "COMMON_401",
              "message": "인증이 필요합니다."
            }
            """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<Project>> registerProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ProjectSaveRequest projectSaveRequest);

    @Operation(description = "프로젝트 업데이트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(
    responseCode = "401",
    description = "인증 실패",
    content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        name = "인증 실패 응답",
                        summary = "유효하지 않은 토큰 또는 로그인 필요",
                        value = """
                        {
                          "success": false,
                          "code": "COMMON_401",
                          "message": "인증이 필요합니다."
                        }
                        """
                    )
                )
            ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 에러 응답",
                                    summary = "예상치 못한 서버 에러",
                                    value = """
                {
                  "success": false,
                  "code": "COMMON_500",
                  "message": "서버 에러, 관리자에게 문의 바랍니다."
                }
                """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<Project>> updateProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("projectId") String projectId,
            @Valid @RequestBody ProjectUpdateRequest projectUpdateRequest);

    @Operation(description = "프로젝트 내 권한 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "권한 변경 성공"),
            @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "인증 실패 응답",
                    summary = "유효하지 않은 토큰 또는 로그인 필요",
                    value = """
                    {
                      "success": false,
                      "code": "COMMON_401",
                      "message": "인증이 필요합니다."
                    }
                    """
                )
            )
        ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 에러 응답",
                                    summary = "예상치 못한 서버 에러",
                                    value = """
                {
                  "success": false,
                  "code": "COMMON_500",
                  "message": "서버 에러, 관리자에게 문의 바랍니다."
                }
                """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<Project>> updateAuthority(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String projectId,
            @RequestBody ProjectAuthorityRequest projectAuthority);

    @Operation(description = "프로젝트 초대 중 이메일 유효성 검사")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능"),
            @ApiResponse(
                    responseCode = "200",
                    description = "초대 가능",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "초대 가능한 이메일",
                                    value = """
                    {
                      "success": true,
                      "code": "COMMON_200",
                      "result": {
                        "available": true,
                        "message": "초대 가능한 사용자 입니다."
                      },
                      "message": "성공입니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 이메일"),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "이메일 형식 미준수",
                                            summary = "유효하지 않은 이메일 형식",
                                            value = """
                {
                  "success": false,
                  "code": "COMMON_400",
                  "message": "이메일 형식이 올바르지 않습니다."
                }
                """
                                    ),
                                    @ExampleObject(
                                            name = "존재하지 않는 사용자",
                                            summary = "회원가입 되지 않은 사용자",
                                            value = """
                {
                  "success": false,
                  "code": "USER_002",
                  "message": "해당 사용자를 찾을 수 없습니다."
                }
                """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "409", description = "이미 프로젝트에 존재하는 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "이미 프로젝트에 존재하는 사용자",
                                    value = """
                {
                  "success": false,
                  "code": "COMMON_409",
                  "message": "이미 프로젝트에 존재하는 사용자입니다."
                }
                """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<InviteCheckResult>> validateInviteMember(
            @PathVariable(required = false) String projectId,
            @Email(message = "이메일 형식이 올바르지 않습니다.") @RequestParam String email);



    @Operation(description = "프로젝트에 신규 인원 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공"),
            @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "인증 실패 응답",
                    summary = "유효하지 않은 토큰 또는 로그인 필요",
                    value = """
                    {
                      "success": false,
                      "code": "COMMON_401",
                      "message": "인증이 필요합니다."
                    }
                    """
                )
            )
        ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 에러 응답",
                                    summary = "예상치 못한 서버 에러",
                                    value = """
                {
                  "success": false,
                  "code": "COMMON_500",
                  "message": "서버 에러, 관리자에게 문의 바랍니다."
                }
                """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<Void>> inviteProject(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String projectId,
            @RequestBody ProjectInviteRequest projectInviteRequest);

    @Operation(description = "프로젝트의 세부 내용 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "불러오기 성공"),
            @ApiResponse(
            responseCode = "401",
            description = "인증 실패",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "인증 실패 응답",
                    summary = "유효하지 않은 토큰 또는 로그인 필요",
                    value = """
                    {
                      "success": false,
                      "code": "COMMON_401",
                      "message": "인증이 필요합니다."
                    }
                    """
                )
            )
        ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 에러 응답",
                                    summary = "예상치 못한 서버 에러",
                                    value = """
                {
                  "success": false,
                  "code": "COMMON_500",
                  "message": "서버 에러, 관리자에게 문의 바랍니다."
                }
                """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<ProjectResponse>> loadProject(@RequestParam String projectId);

    @Operation(description = "사용자가 참여 중인 프로젝트 목록 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "불러오기 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패 응답",
                                    summary = "유효하지 않은 토큰 또는 로그인 필요",
                                    value = """
                    {
                      "success": false,
                      "code": "COMMON_401",
                      "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 에러 응답",
                                    summary = "예상치 못한 서버 에러",
                                    value = """
                {
                  "success": false,
                  "code": "COMMON_500",
                  "message": "서버 에러, 관리자에게 문의 바랍니다."
                }
                """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<List<ProjectResponse>>> loadProjectList(@AuthenticationPrincipal CustomUserDetails userDetails);


    @Operation(description = "MANAGER 권한이 있는 프로젝트 유저가 다른 프로젝트 유저 강퇴하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "강퇴 성공"),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패 응답",
                                    summary = "유효하지 않은 토큰 또는 로그인 필요",
                                    value = """
                    {
                      "success": false,
                      "code": "COMMON_401",
                      "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 에러",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 에러 응답",
                                    summary = "예상치 못한 서버 에러",
                                    value = """
                {
                  "success": false,
                  "code": "COMMON_500",
                  "message": "서버 에러, 관리자에게 문의 바랍니다."
                }
                """
                            )
                    )
            )

    })
    ResponseEntity<com.capstone.global.response.ApiResponse<Void>> deleteProjectUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String projectId,
            @PathVariable String email);

}
