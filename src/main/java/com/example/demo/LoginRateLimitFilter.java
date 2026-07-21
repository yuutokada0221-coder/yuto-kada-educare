package com.example.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ★UsernamePasswordAuthenticationFilterより前段でロック状態をチェックし、
// ロックされているユーザー名は認証処理に進ませずに弾く（パスワード自体は検証しない）。
@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod()) && "/login".equals(request.getServletPath())) {
            String username = request.getParameter("username");
            if (loginAttemptService.isLocked(username)) {
                response.sendRedirect(request.getContextPath() + "/login?error=locked");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
