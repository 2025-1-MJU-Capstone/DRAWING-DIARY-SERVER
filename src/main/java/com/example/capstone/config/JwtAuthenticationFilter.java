package com.example.capstone.config;

import com.example.capstone.auth.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String token = resolveToken(request);

        if (token == null) {
            log.info("[JwtFilter] 토큰 없음. {} 요청 - URI: {}", method, uri);
        } else if (!tokenProvider.validateToken(token)) {
            log.info("[JwtFilter] 유효하지 않은 토큰. {} 요청 - URI: {}", method, uri);
        } else {
            Authentication auth = tokenProvider.getAuthentication(token);
            log.info("[JwtFilter] 인증 성공 - loginId: {}, {} 요청 - URI: {}", auth.getName(), method, uri);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}