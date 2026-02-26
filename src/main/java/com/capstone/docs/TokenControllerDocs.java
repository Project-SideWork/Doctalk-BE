package com.capstone.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "토큰 관련 API")
public interface TokenControllerDocs {

    @Operation(summary = "액세스 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰 재발급 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken, HttpServletRequest request, HttpServletResponse response);
}
