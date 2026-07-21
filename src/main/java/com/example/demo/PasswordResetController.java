package com.example.demo;

import com.example.demo.entity.PasswordResetToken;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

// パスワードを忘れたユーザー向けの再設定フロー。
// メールアドレスが登録されている場合のみ有効（ユーザー列挙を避けるため、未登録でも常に同じ案内を返す）。
@Controller
public class PasswordResetController {

    private static final int TOKEN_VALID_MINUTES = 30;

    @Autowired private UserAccountRepository userRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "forgot-password";
    }

    // ★deleteByUserAccount()とsave()の2回のリポジトリ呼び出しを1つのトランザクションにまとめる。
    // これがないと（AdminController.deleteUserで踏んだのと同じ理由で）削除が反映されないことがあった。
    @Transactional
    @PostMapping("/forgot-password")
    public String requestReset(@RequestParam String email, Model model) {
        UserAccount user = userRepository.findByEmail(email);
        if (user != null) {
            // 古いトークンが残っていても混乱しないよう、発行のたびに前のものは無効化する
            tokenRepository.deleteByUserAccount(user);

            PasswordResetToken token = new PasswordResetToken();
            token.setToken(UUID.randomUUID().toString());
            token.setUserAccount(user);
            token.setExpiresAt(Instant.now().plus(TOKEN_VALID_MINUTES, ChronoUnit.MINUTES));
            tokenRepository.save(token);

            String resetLink = baseUrl + "/reset-password?token=" + token.getToken();
            emailService.sendPasswordResetEmail(email, resetLink);
        }
        // ★セキュリティ上、そのメールアドレスが登録されているかどうかは明かさない
        model.addAttribute("sent", true);
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);
        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("invalid", true);
            return "reset-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @Transactional
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword, Model model) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElse(null);
        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("invalid", true);
            return "reset-password";
        }

        String passwordError = PasswordPolicy.validate(newPassword);
        if (passwordError != null) {
            model.addAttribute("token", token);
            model.addAttribute("passwordError", passwordError);
            return "reset-password";
        }

        UserAccount user = resetToken.getUserAccount();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.deleteByUserAccount(user);

        return "redirect:/login?resetSuccess=true";
    }
}
