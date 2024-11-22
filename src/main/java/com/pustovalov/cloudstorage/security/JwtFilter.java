package com.pustovalov.cloudstorage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtManager jwtManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String username = null;

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String jwt = authHeader.substring(BEARER_PREFIX.length());
            username = jwtManager.parseClaims(jwt, "username");
        }

        SecurityContext context = SecurityContextHolder.getContext();
        if (username != null && context.getAuthentication() == null) {
            context.setAuthentication(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
        }

        filterChain.doFilter(request, response);
    }
}