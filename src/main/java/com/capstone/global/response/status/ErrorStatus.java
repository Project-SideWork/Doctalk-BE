package com.capstone.global.response.status;

import com.capstone.global.response.BaseErrorCode;
import com.capstone.global.response.ErrorInfoDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    // 일반 응답
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON_409", "이미 존재하는 데이터입니다."),

    // Jwt
    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "COMMON_404", "토큰이 비어있습니다."),
    INVALID_ACCESS(HttpStatus.UNAUTHORIZED, "COMMON_405", "유효하지 않은 액세스 토큰입니다."),
    INVALID_REFRESH(HttpStatus.UNAUTHORIZED, "COMMON_406", "유효하지 않은 리프레쉬 토큰입니다."),

    // User
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_001", "이미 존재하는 사용자이며, 비밀번호가 틀렸습니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER_002", "해당 사용자를 찾을 수 없습니다."),

    // Document
    DOCUMENT_NOT_FOUND (HttpStatus.NOT_FOUND, "DOCUMENT_001", "해당 프로젝트를 찾을 수 없습니다."),

    // File
    FILE_NOT_SUPPORTED(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "FILE_001", "지원하지 않는 파일 형식입니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE_002", "파일을 찾을 수 없습니다."),
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "FILE_003", "파일이 비어있습니다."),

    // Project
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_001", "프로젝트를 찾을 수 없습니다."),
    PROJECT_NOT_ACCESS(HttpStatus.FORBIDDEN, "PROJECT_002", "프로젝트에 접근할 권한이 없습니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "PROJECT_003", "정의되지 않은 역할입니다."),
    INVALID_MEMBER(HttpStatus.BAD_REQUEST, "PROJECT_004", "프로젝트에 없는 멤버입니다."),
    ALREADY_EXIST_MEMBER(HttpStatus.CONFLICT, "PROJECT_005", "프로젝트에 이미 존재하는 멤버입니다."),

    // Task
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK_001", "기존에 없던 작업 입니다."),
    VERSION_NOT_FOUND(HttpStatus.NOT_FOUND, "TASK_002", "기존에 없던 버전 입니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "TASK_003", "유효하지 않은 작업 상태 값입니다."),

    //Invite Code
    CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "INVITE_CODE_001", "이미 만료되었거나 존재하지 않는 초대코드 입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    @Override
    public ErrorInfoDto getReasonHttpStatus() {
        return ErrorInfoDto.builder()
                .kind("Failure")
                .message(message)
                .code(code)
                .httpStatus(httpStatus)
                .build();
    }
}
