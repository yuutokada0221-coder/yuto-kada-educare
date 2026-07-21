package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

// 初回ログイン時の最小オンボーディング（アイデンティティ宣言1問＋TODO1つ）
// 要件設計書6.1：初回入力は最小限に絞り、価値実感（Aha! moment）までの時間を最短化する
@Controller
public class OnboardingController {

    // 完了ボーナス：必ずLv.2への初回レベルアップを体験させるための一回きりの祝福EXP。
    // LevelingUtilの経験値カーブ（Lv1→2は累計5EXP）に合わせてある。カーブを変えたらここも見直すこと。
    private static final int ONBOARDING_BONUS_EXP = 5;

    @Autowired private UserAccountRepository userRepository;

    @GetMapping("/onboarding")
    public String onboarding(Model model, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        if (currentUser.isOnboardingCompleted()) {
            return "redirect:/";
        }
        return "onboarding";
    }

    @PostMapping("/onboarding/complete")
    public String complete(Principal principal, String identityDeclaration, String firstTodo) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        if (currentUser.isOnboardingCompleted()) {
            return "redirect:/";
        }
        currentUser.setIdentityDeclaration(identityDeclaration);
        if (firstTodo != null && !firstTodo.isBlank()) {
            currentUser.setFixedHabit1(firstTodo.trim());
        }
        int levelBefore = LevelingUtil.levelOf(currentUser.getExp());
        currentUser.setExp(currentUser.getExp() + ONBOARDING_BONUS_EXP);
        int levelAfter = LevelingUtil.levelOf(currentUser.getExp());
        currentUser.setOnboardingCompleted(true);
        userRepository.save(currentUser);

        String redirect = "redirect:/?loginSuccess=true&onboarded=true";
        return levelAfter > levelBefore ? redirect + "&levelUp=" + levelAfter : redirect;
    }
}
