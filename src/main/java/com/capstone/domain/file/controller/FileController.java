package com.capstone.domain.file.controller;

import com.capstone.docs.FileControllerDocs;
import com.capstone.domain.file.dto.FileResponse;
import com.capstone.domain.file.service.FileService;
import com.capstone.global.ratelimit.annotation.RateLimit;
import com.capstone.global.ratelimit.enums.RateLimitKeyType;
import com.capstone.global.response.ApiResponse;
import com.capstone.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@CrossOrigin("*")
public class FileController implements FileControllerDocs {
    private final FileService fileService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RateLimit(limit = 50, duration = 3600, keyType = RateLimitKeyType.USER)
    public ResponseEntity<ApiResponse<FileResponse>> uploadFile(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(ApiResponse.onSuccess(fileService.upload(customUserDetails,file)));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam("fileId") String fileId) throws IOException {
        return fileService.download(customUserDetails,fileId);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteFile(@AuthenticationPrincipal CustomUserDetails customUserDetails,@RequestParam("fileId") String fileId){
        return ResponseEntity.ok(ApiResponse.onSuccess(fileService.delete(customUserDetails,fileId)));
    }

    @GetMapping("/get")
    public ResponseEntity<Resource> getFile(@AuthenticationPrincipal CustomUserDetails customUserDetails,@RequestParam("fileId") String fileId) throws IOException{

        return fileService.getFile(customUserDetails,fileId);
    }

}
