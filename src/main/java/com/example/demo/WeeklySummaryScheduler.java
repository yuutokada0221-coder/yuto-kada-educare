package com.example.demo;

import com.example.demo.entity.DailyTask;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.DailyJournalRepository;
import com.example.demo.repository.DailyTaskRepository;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// ★Strava風の「週間サマリー」通知。日曜の夜に、今週の成果（獲得EXP・TODO達成数・
// ジャーナル記録数・現在の連続ログイン日数）をまとめてプッシュ通知で振り返らせる。
// 週間リーグの階層昇格・降格処理（LeagueService、毎週月曜0:05）より前に送ることで、
// weeklyExpSnapshotがリセットされる前の「今週の獲得EXP」を正しく表示できる。
@Component
public class WeeklySummaryScheduler {

    @Autowired private UserAccountRepository userRepository;
    @Autowired private DailyTaskRepository taskRepository;
    @Autowired private DailyJournalRepository journalRepository;
    @Autowired private LoginRecordRepository loginRecordRepository;
    @Autowired private LeagueService leagueService;
    @Autowired private PushNotificationService pushNotificationService;

    @Scheduled(cron = "0 0 21 * * SUN")
    public void sendWeeklySummaries() {
        LocalDate today = LocalDate.now();
        for (UserAccount user : userRepository.findAll()) {
            if ("NONE".equals(user.getNotificationFrequency())) continue;
            String body = buildSummaryBody(user, today);
            if (body == null) continue; // 今週の活動がなければ送らない
            pushNotificationService.sendToUser(user, "目標達成RPG：週間サマリー", body);
        }
    }

    // パッケージ内から直接呼べるようにvisibility broad目にしておく（動作確認・テスト用）
    String buildSummaryBody(UserAccount user, LocalDate today) {
        LocalDate weekStart = today.minusDays(6);
        int weeklyExp = leagueService.weeklyExpOf(user);
        int taskDoneCount = taskRepository.findByUserAccountAndDateBetween(user, weekStart, today)
                .stream().mapToInt(this::countDone).sum();
        int journalCount = journalRepository.findByUserAccountAndDateBetween(user, weekStart, today).size();
        int streak = calculateStreak(user, today);

        if (weeklyExp == 0 && taskDoneCount == 0 && journalCount == 0) return null;

        return String.format("今週の振り返り：EXP+%d／TODO達成%d件／ジャーナル%d件／連続%d日達成中！お疲れさまでした🎉",
                weeklyExp, taskDoneCount, journalCount, streak);
    }

    private int countDone(DailyTask t) {
        int c = 0;
        if (t.isTask1Done()) c++;
        if (t.isTask2Done()) c++;
        if (t.isTask3Done()) c++;
        if (t.isBadHabitDone()) c++;
        return c;
    }

    private int calculateStreak(UserAccount user, LocalDate today) {
        int streak = 0;
        LocalDate checkDate = today;
        while (loginRecordRepository.existsByUserAccountAndLoginDate(user, checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }
        return streak;
    }
}
