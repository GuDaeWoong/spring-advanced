package org.example.expert.config;


import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Slf4j
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 클라이언트가 서버로 보낸 요청에 대한 모든 정보
        String requestURI = request.getRequestURI();
        // userId 정보
        String requestUserId = request.getAttribute("userId").toString();
        // userRole 정보
        UserRole role = UserRole.of(request.getAttribute("userRole").toString());

        // URI
        log.info("RequestURI : {}", requestURI);
        // userId 정보
        log.info("RequestUserId : {}", requestUserId);
        // userRole 정보
        log.info("RequestUserRole : {}", role);
        // 로그 찍힌 시간
        log.info("RequestTime : {}", LocalDateTime.now());

        if (!UserRole.ADMIN.equals(role)) {
            throw new AuthException("관리자 권한이 아닙니다.");
        }
        return true;
    }
}
