package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.repository.DailyJournalRepository;
import com.example.demo.repository.DailyTaskRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// ★通知設定（設定タブの「通知頻度」）に応じて、まだ今日のTODO/ジャーナルを
// 記録していないユーザーにリマインドのプッシュ通知を送る定期ジョブ。
// NORMAL（1日2回）＝朝夕、LOW（1日1回）＝夕方のみ、NONE＝送らない。
@Component
public class ReminderScheduler {

    @Autowired private UserAccountRepository userRepository;
    @Autowired private DailyTaskRepository taskRepository;
    @Autowired private DailyJournalRepository journalRepository;
    @Autowired private PushNotificationService pushNotificationService;

    @Scheduled(cron = "0 0 9 * * *")
    public void morningReminder() {
        sendReminders(true);
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void eveningReminder() {
        sendReminders(false);
    }

    // パッケージ内から直接呼べるようにvisibility broad目にしておく（動作確認・テスト用）
    void sendReminders(boolean isMorning) {
        LocalDate today = LocalDate.now();
        for (UserAccount user : userRepository.findAll()) {
            String freq = user.getNotificationFrequency();
            if ("NONE".equals(freq)) continue;
            // 朝の便はNORMAL（1日2回まで）のユーザーだけ。LOW（1日1回）は夕方の便でまとめて送る
            if (isMorning && !"NORMAL".equals(freq)) continue;

            boolean taskDone = taskRepository.findByUserAccountAndDate(user, today).isPresent();
            boolean journalDone = journalRepository.findByUserAccountAndDate(user, today).isPresent();
            if (taskDone && journalDone) continue; // やることは既に全部やっている

            String body = isMorning
                    ? "おはようございます！今日のTODOを3つ決めましょう。"
                    : (!taskDone
                        ? "今日のTODOはまだ記録されていません。今日を振り返りましょう！"
                        : "今日のジャーナルはまだです。今日を振り返りましょう！");
            pushNotificationService.sendToUser(user, "目標達成RPG", body);
        }
    }
}
