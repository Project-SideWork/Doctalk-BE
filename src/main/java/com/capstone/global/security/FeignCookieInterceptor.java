package com.capstone.global.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignCookieInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        Cookie[] cookieStore = request.getCookies();

        if (cookieStore != null && cookieStore.length > 0) {
            StringBuilder cookieHeaders = new StringBuilder();

            for (Cookie cookie : cookieStore) {
                cookieHeaders.append(cookie.getName())
                        .append("=")
                        .append(cookie.getValue())
                        .append(";");
            }

            requestTemplate.header("Cookie", cookieHeaders.toString());
        }
    }
}