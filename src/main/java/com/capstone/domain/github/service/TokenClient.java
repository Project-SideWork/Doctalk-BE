//package com.capstone.domain.github.service;
//
//import com.capstone.global.security.CustomUserDetails;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@FeignClient(name = "user-service", url = "${auth.service.url}")
//public interface TokenClient {
//
//    @GetMapping("/api/v1/user/github")
//    String getGithubToken(@AuthenticationPrincipal CustomUserDetails userDetails);
//}