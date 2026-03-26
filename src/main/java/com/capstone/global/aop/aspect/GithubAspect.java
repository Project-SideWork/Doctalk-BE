package com.capstone.global.aop.aspect;

import com.capstone.domain.github.util.GithubInformationManager;
import com.capstone.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class GithubAspect {

    private final GithubInformationManager githubTokenManager;

    @Around("@within(com.capstone.global.aop.annotation)")
    public Object validateAndFetch(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("validateAndFetch");
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        log.info("username" + userDetails.getUsername());
        log.info("email" + userDetails.email());
        log.info("growpid" + userDetails.userId());


        githubTokenManager.ensureValidToken(userDetails.userId());

        return joinPoint.proceed();
    }
}
