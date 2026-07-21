package com.example.demo;

import com.example.demo.entity.LoginRecord;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;

// ★設定タブ関連のエンドポイントをHomeControllerから分離したもの
// （目標・アイデンティティ・メール・固定習慣・テーマ・通知設定・お休みチケット使用）。
// HomeController本体はダッシュボード表示（home()）とTODO/ジャーナル/クエストのコア機能に専念させる。
@Controller
public class SettingsController {

    @Autowired private UserAccountRepository userRepository;
    @Autowired private LoginRecordRepository loginRecordRepository;

    @PostMapping("/updateGoal")
    public String updateGoal(Principal principal, String newGoal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        currentUser.setLongTermGoal(newGoal);
        userRepository.save(currentUser);
        return "redirect:/";
    }

    @PostMapping("/updateIdentity")
    public String updateIdentity(Principal principal, String identityDeclaration) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        currentUser.setIdentityDeclaration(identityDeclaration);
        userRepository.save(currentUser);
        return "redirect:/";
    }

    // ★パスワードリセットの受け取り先となるメールアドレスの登録・変更
    @PostMapping("/updateEmail")
    public String updateEmail(Principal principal, String email) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        if (email != null && !email.isBlank()) {
            UserAccount existing = userRepository.findByEmail(email.trim());
            if (existing != null && !existing.getId().equals(currentUser.getId())) {
                return "redirect:/?error=emailTaken";
            }
            currentUser.setEmail(email.trim());
        } else {
            currentUser.setEmail(null);
        }
        userRepository.save(currentUser);
        return "redirect:/";
    }

    @PostMapping("/updateFixedHabits")
    public String updateFixedHabits(Principal principal, String habit1, String habit2, String habit3, String badHabit) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        currentUser.setFixedHabit1(habit1);
        currentUser.setFixedHabit2(habit2);
        currentUser.setFixedHabit3(habit3);
        currentUser.setFixedBadHabit(badHabit);
        userRepository.save(currentUser);
        return "redirect:/";
    }

    @PostMapping("/updateTheme")
    public String updateTheme(Principal principal, String theme) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        currentUser.setTheme(theme);
        userRepository.save(currentUser);
        return "redirect:/";
    }

    @PostMapping("/updateNotificationSettings")
    public String updateNotificationSettings(Principal principal, String notificationFrequency) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        if ("NONE".equals(notificationFrequency) || "LOW".equals(notificationFrequency) || "NORMAL".equals(notificationFrequency)) {
            currentUser.setNotificationFrequency(notificationFrequency);
            userRepository.save(currentUser);
        }
        return "redirect:/";
    }

    // ★カレンダー上の「未ログインの日」をクリック→お休みチケットを消費してその日を
    // ログイン済み扱いにする機能。週次リセットで付与される既存のstreakFreezeRemainingを、
    // 「昨日を自動で救済する」仕組みとは別に、ユーザー自身が任意の過去日に使えるようにする。
    @PostMapping("/useStreakFreezeTicket")
    public String useStreakFreezeTicket(@RequestParam String date, Principal principal) {
        UserAccount currentUser = userRepository.findByUsername(principal.getName());
        LocalDate today = LocalDate.now();
        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(date);
        } catch (Exception e) {
            return "redirect:/?error=ticketInvalidDate";
        }

        if (!targetDate.isBefore(today)) {
            return "redirect:/?error=ticketInvalidDate"; // 今日・未来日には使えない
        }
        if (currentUser.getCreatedAt() != null && targetDate.isBefore(currentUser.getCreatedAt())) {
            return "redirect:/?error=ticketInvalidDate"; // アカウント作成前の日には使えない
        }
        if (loginRecordRepository.existsByUserAccountAndLoginDate(currentUser, targetDate)) {
            return "redirect:/?error=ticketAlreadyLogged"; // すでに記録がある日
        }
        if (currentUser.getStreakFreezeRemaining() <= 0) {
            return "redirect:/?error=ticketNone"; // チケット切れ
        }

        LoginRecord record = new LoginRecord();
        record.setUserAccount(currentUser);
        record.setLoginDate(targetDate);
        record.setFrozen(true);
        loginRecordRepository.save(record);

        currentUser.setStreakFreezeRemaining(currentUser.getStreakFreezeRemaining() - 1);
        userRepository.save(currentUser);

        return "redirect:/?ticketUsed=true";
    }
}
