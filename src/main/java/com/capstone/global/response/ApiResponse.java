package com.capstone.global.response;

import com.capstone.global.response.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"kind", "code", "message", "result"})
@Schema(description = "API 응답 포맷")
public class ApiResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private final Boolean isSuccess;
    @Schema(description = "응답 코드", example = "USER_001")
    private final String code;
    @Schema(description = "응답 메시지", example = "사용자가 존재하지 않습니다.")
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "응답 데이터")
    private T result;

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> onSuccess(T result){
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> onSuccess(){
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), null);
    }

    public static <T> ApiResponse<T> onSuccessVoid(){
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), null);
    }


    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> onFailure(String code, String message, T data){
        return new ApiResponse<>(false, code, message, data);
    }
}
