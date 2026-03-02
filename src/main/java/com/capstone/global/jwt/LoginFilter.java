package com.capstone.global.jwt;

//import com.capstone.domain.auth.exception.SocialLoginException;
import com.capstone.domain.auth.login.dto.LoginRequest;
import com.capstone.domain.user.entity.User;
import com.capstone.domain.user.exception.UserNotFoundException;
import com.capstone.domain.user.message.UserMessages;
import com.capstone.domain.user.repository.UserRepository;
import com.capstone.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final UserRepository userRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, CookieUtil cookieUtil, UserRepository userRepository){
        this.jwtUtil = jwtUtil;
        this.cookieUtil = cookieUtil;
        this.userRepository = userRepository;
        super.setAuthenticationManager(authenticationManager);
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("Attempting authentication...");
        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            LoginRequest loginRequest = new ObjectMapper().readValue(messageBody, LoginRequest.class);

            Optional<User> user = userRepository.findUserByEmail(loginRequest.getEmail());

            if(user.isPresent()){
                if (user.get().getSocial() != null){
                    throw new RuntimeException(user.get().getSocial() + "계정으로 가입된 회원입니다.");
                }
            } else {
                throw new UserNotFoundException();
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword(), null);

            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read request body", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication)
            throws IOException, ServletException {
        log.info("Authentication successful...");

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String email = userDetails.getEmail();
        try {
            String accessToken = jwtUtil.createAccess(email);
            String refreshToken = jwtUtil.createRefresh(email);
            response.addHeader("access", accessToken);
            response.addHeader("refresh", refreshToken);
            response.addCookie(cookieUtil.createAccessCookie(accessToken));
            response.addCookie(cookieUtil.createCookie("refresh", refreshToken));
        } catch (Exception e) {
            log.error("Error generating JWT: ", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        response.setStatus(HttpStatus.OK.value());
    }

}
