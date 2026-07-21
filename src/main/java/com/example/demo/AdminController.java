package com.example.demo;

import com.example.demo.entity.Badge;
import com.example.demo.entity.DailyJournal;
import com.example.demo.entity.DailyTask;
import com.example.demo.entity.Quest;
import com.example.demo.entity.ThemeOption;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.DailyJournalRepository;
import com.example.demo.repository.DailyTaskRepository;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.QuestClaimRepository;
import com.example.demo.repository.QuestRepository;
import com.example.demo.repository.ThemeOptionRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 管理者専用画面：ユーザー管理・KPIダッシュボード・コンテンツ（テーマ/実績/クエスト）管理
// SecurityConfigで /admin/** はROLE_ADMINのみアクセス可
@Controller
public class AdminController {

    @Autowired private UserAccountRepository userRepository;
    @Autowired private LoginRecordRepository loginRecordRepository;
    @Autowired private DailyTaskRepository taskRepository;
    @Autowired private DailyJournalRepository journalRepository;
    @Autowired private BadgeRepository badgeRepository;
    @Autowired private ThemeOptionRepository themeOptionRepository;
    @Autowired private QuestRepository questRepository;
    @Autowired private QuestClaimRepository questClaimRepository;

    @GetMapping("/admin")
    public String dashboard(Model model, Principal principal,
                             @RequestParam(name = "tab", required = false, defaultValue = "kpi") String tab) {
        UserAccount admin = userRepository.findByUsername(principal.getName());
        LocalDate today = LocalDate.now();
        List<UserAccount> allUsers = userRepository.findAll();

        // --- KPI ---
        model.addAttribute("totalUsers", allUsers.size());
        model.addAttribute("d1Rate", retentionRate(allUsers, 1, today));
        model.addAttribute("d7Rate", retentionRate(allUsers, 7, today));
        model.addAttribute("d30Rate", retentionRate(allUsers, 30, today));
        model.addAttribute("streakEstablishedRate", streakEstablishedRate(allUsers, today));
        model.addAttribute("totalTasks", taskRepository.count());
        model.addAttribute("totalJournals", journalRepository.count());

        // --- ユーザー一覧 ---
        List<AdminUserView> userViews = new ArrayList<>();
        for (UserAccount u : allUsers) {
            int level = LevelingUtil.levelOf(u.getExp());
            int streak = calculateStreak(u, today);
            int cumulativeDays = loginRecordRepository.findByUserAccount(u).size();
            userViews.add(new AdminUserView(u, level, streak, cumulativeDays));
        }
        model.addAttribute("userViews", userViews);
        model.addAttribute("currentAdminId", admin.getId());

        // --- コンテンツ管理 ---
        model.addAttribute("themes", themeOptionRepository.findAll());
        model.addAttribute("badges", badgeRepository.findAll());
        model.addAttribute("quests", questRepository.findAll());
        model.addAttribute("questPeriods", Quest.Period.values());
        model.addAttribute("questConditionTypes", Quest.ConditionType.values());
        model.addAttribute("badgeConditionTypes", Badge.ConditionType.values());

        model.addAttribute("activeTab", tab);
        return "admin/dashboard";
    }

    // ---------------- ユーザー管理 ----------------

    // ★関連レコードの削除〜本体削除までを1つのトランザクションにまとめる。
    // これがないとリクエストによってはトランザクション境界の問題で削除が失敗することがあった。
    @Transactional
    @PostMapping("/admin/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, Principal principal) {
        UserAccount admin = userRepository.findByUsername(principal.getName());
        if (admin.getId().equals(id)) {
            return "redirect:/admin?tab=users"; // 自分自身は削除させない
        }
        userRepository.findById(id).ifPresent(target -> {
            taskRepository.deleteByUserAccount(target);
            journalRepository.deleteByUserAccount(target);
            questClaimRepository.deleteByUserAccount(target);
            loginRecordRepository.deleteByUserAccount(target);
            userRepository.delete(target);
        });
        return "redirect:/admin?tab=users";
    }

    @PostMapping("/admin/users/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam String role, Principal principal) {
        UserAccount admin = userRepository.findByUsername(principal.getName());
        if (!admin.getId().equals(id) && ("USER".equals(role) || "ADMIN".equals(role))) {
            userRepository.findById(id).ifPresent(target -> {
                target.setRole(role);
                userRepository.save(target);
            });
        }
        return "redirect:/admin?tab=users";
    }

    // ---------------- テーマ管理 ----------------

    @PostMapping("/admin/themes/add")
    public String addTheme(String name, String emoji, String cssClass, int unlockLevel) {
        ThemeOption t = new ThemeOption();
        t.setName(name);
        t.setEmoji(emoji);
        t.setCssClass(cssClass);
        t.setUnlockLevel(unlockLevel);
        themeOptionRepository.save(t);
        return "redirect:/admin?tab=themes";
    }

    @PostMapping("/admin/themes/{id}/toggle")
    public String toggleTheme(@PathVariable Long id) {
        themeOptionRepository.findById(id).ifPresent(t -> {
            t.setActive(!t.isActive());
            themeOptionRepository.save(t);
        });
        return "redirect:/admin?tab=themes";
    }

    @PostMapping("/admin/themes/{id}/delete")
    public String deleteTheme(@PathVariable Long id) {
        themeOptionRepository.deleteById(id);
        return "redirect:/admin?tab=themes";
    }

    // ---------------- 実績（バッジ）管理 ----------------

    @PostMapping("/admin/badges/add")
    public String addBadge(String name, String icon, String description, Badge.ConditionType conditionType, int conditionValue) {
        Badge b = new Badge();
        b.setName(name);
        b.setIcon(icon);
        b.setDescription(description);
        b.setConditionType(conditionType);
        b.setConditionValue(conditionValue);
        badgeRepository.save(b);
        return "redirect:/admin?tab=badges";
    }

    @PostMapping("/admin/badges/{id}/toggle")
    public String toggleBadge(@PathVariable Long id) {
        badgeRepository.findById(id).ifPresent(b -> {
            b.setActive(!b.isActive());
            badgeRepository.save(b);
        });
        return "redirect:/admin?tab=badges";
    }

    @PostMapping("/admin/badges/{id}/delete")
    public String deleteBadge(@PathVariable Long id) {
        badgeRepository.deleteById(id);
        return "redirect:/admin?tab=badges";
    }

    // ---------------- クエスト管理 ----------------

    @PostMapping("/admin/quests/add")
    public String addQuest(String title, String description, String icon, Quest.Period period,
                            Quest.ConditionType conditionType, int targetCount, int rewardExp) {
        Quest q = new Quest();
        q.setTitle(title);
        q.setDescription(description);
        q.setIcon(icon != null && !icon.isBlank() ? icon : "🗺️");
        q.setPeriod(period);
        q.setConditionType(conditionType);
        q.setTargetCount(targetCount);
        q.setRewardExp(rewardExp);
        questRepository.save(q);
        return "redirect:/admin?tab=quests";
    }

    @PostMapping("/admin/quests/{id}/toggle")
    public String toggleQuest(@PathVariable Long id) {
        questRepository.findById(id).ifPresent(q -> {
            q.setActive(!q.isActive());
            questRepository.save(q);
        });
        return "redirect:/admin?tab=quests";
    }

    @PostMapping("/admin/quests/{id}/delete")
    public String deleteQuest(@PathVariable Long id) {
        questRepository.deleteById(id);
        return "redirect:/admin?tab=quests";
    }

    // ---------------- ヘルパー ----------------

    private int calculateStreak(UserAccount user, LocalDate today) {
        int streak = 0;
        LocalDate checkDate = today;
        while (loginRecordRepository.existsByUserAccountAndLoginDate(user, checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }
        return streak;
    }

    private double retentionRate(List<UserAccount> users, int dayOffset, LocalDate today) {
        List<UserAccount> eligible = users.stream()
                .filter(u -> u.getCreatedAt() != null && !u.getCreatedAt().plusDays(dayOffset).isAfter(today))
                .toList();
        if (eligible.isEmpty()) return 0;
        long retained = eligible.stream()
                .filter(u -> loginRecordRepository.existsByUserAccountAndLoginDate(u, u.getCreatedAt().plusDays(dayOffset)))
                .count();
        return Math.round(retained * 1000.0 / eligible.size()) / 10.0;
    }

    private double streakEstablishedRate(List<UserAccount> users, LocalDate today) {
        if (users.isEmpty()) return 0;
        long established = users.stream().filter(u -> calculateStreak(u, today) >= 7).count();
        return Math.round(established * 1000.0 / users.size()) / 10.0;
    }
}
