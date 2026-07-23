package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class AuthController {

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationAttemptService registrationAttemptService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userForm", new UserAccount());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserAccount userForm, Model model, HttpServletRequest request) {
        // ★register.htmlはth:object="${userForm}"でフォームバインドしているため、
        // 再表示する全てのパターンで"userForm"をmodelに積み直す必要がある（@ModelAttributeの暗黙キーは
        // クラス名から決まる"userAccount"であり"userForm"にはならず、積み忘れるとThymeleafが
        // th:field解決時にIllegalStateExceptionを投げて500→エラーページ経由でログイン画面に
        // リダイレクトされてしまい、エラーメッセージがユーザーに一切見えなくなる）
        String clientIp = request.getRemoteAddr();
        if (registrationAttemptService.isRateLimited(clientIp)) {
            model.addAttribute("error", "アカウント登録の試行回数が多すぎます。しばらく時間をおいてから再度お試しください。");
            userForm.setPassword(""); // ★ユーザー名は残しつつ、入力し直したパスワードがHTMLソースに平文で残らないようにする
            model.addAttribute("userForm", userForm);
            return "register";
        }

        // ★レスキュー：もし同じ名前の人がいたら、エラー画面にならずに登録画面に戻す
        if (userRepository.findByUsername(userForm.getUsername()) != null) {
            model.addAttribute("error", "その名前はすでに使われています！別の名前にしてください。");
            userForm.setPassword(""); // ★ユーザー名は残しつつ、入力し直したパスワードがHTMLソースに平文で残らないようにする
            model.addAttribute("userForm", userForm);
            return "register";
        }

        // ★HTML側のminlength属性は開発者ツール等で簡単に外せるため、サーバー側でも必ず検証する
        String passwordError = PasswordPolicy.validate(userForm.getPassword());
        if (passwordError != null) {
            model.addAttribute("error", passwordError);
            userForm.setPassword(""); // ★ユーザー名は残しつつ、入力し直したパスワードがHTMLソースに平文で残らないようにする
            model.addAttribute("userForm", userForm);
            return "register";
        }

        // パスワードを確実に暗号化して保存
        userForm.setPassword(passwordEncoder.encode(userForm.getPassword()));

        // 新しいユーザーなので経験値を0にセット
        userForm.setExp(0);

        userRepository.save(userForm);
        registrationAttemptService.record(clientIp);

        return "redirect:/login";
    }
}