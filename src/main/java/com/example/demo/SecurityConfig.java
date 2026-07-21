package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private LoginRateLimitFilter loginRateLimitFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ★フォームは全てth:actionで生成しており、Thymeleafのspring-securityダイアレクトが
            // 自動でCSRFトークンをhidden inputとして埋め込むため、個別のフォーム修正なしで有効化できる。
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/register", "/css/**", "/icons.svg", "/backgrounds/**", "/journal-photos/**",
                        "/forgot-password", "/reset-password", "/push/vapid-public-key", "/sw.js",
                        "/actuator/health").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }

    // ★追加：ログイン画面の「ユーザー／管理者」切り替えに応じて遷移先を分ける
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            loginAttemptService.recordSuccess(authentication.getName());
            String loginType = request.getParameter("loginType");
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> a.equals("ROLE_ADMIN"));

            if ("admin".equals(loginType) && isAdmin) {
                response.sendRedirect("/admin");
            } else if ("admin".equals(loginType)) {
                response.sendRedirect("/?loginSuccess=true&notAdmin=true");
            } else {
                response.sendRedirect("/?loginSuccess=true");
            }
        };
    }

    // ★ブルートフォース対策：失敗のたびにカウントし、規定回数を超えたらLoginRateLimitFilterが弾くようになる
    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            loginAttemptService.recordFailure(request.getParameter("username"));
            response.sendRedirect(request.getContextPath() + "/login?error");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}