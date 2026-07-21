package com.example.demo;

import com.example.demo.entity.DailyJournal;
import com.example.demo.entity.DailyTask;
import com.example.demo.entity.LoginRecord;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.DailyJournalRepository;
import com.example.demo.repository.DailyTaskRepository;
import com.example.demo.repository.LoginRecordRepository;
import com.example.demo.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

// ★週次サマリー通知（WeeklySummaryScheduler）のロジックを検証する単体テスト。
// H2インメモリDBを使うため、実際のPostgres（habit_app）には触れない。
@SpringBootTest
@Transactional
class WeeklySummarySchedulerTest {

    @Autowired private WeeklySummaryScheduler scheduler;
    @Autowired private UserAccountRepository userRepository;
    @Autowired private DailyTaskRepository taskRepository;
    @Autowired private DailyJournalRepository journalRepository;
    @Autowired private LoginRecordRepository loginRecordRepository;

    private UserAccount makeUser(String username, int exp, int weeklyExpSnapshot) {
        UserAccount u = new UserAccount();
        u.setUsername(username);
        u.setPassword("dummy");
        u.setExp(exp);
        u.setWeeklyExpSnapshot(weeklyExpSnapshot);
        return userRepository.save(u);
    }

    @Test
    void summarizesWeeklyExpTasksJournalsAndStreak() {
        LocalDate today = LocalDate.now();
        UserAccount user = makeUser("weekly_active", 130, 100); // 今週+30EXP

        DailyTask t = new DailyTask();
        t.setUserAccount(user);
        t.setDate(today);
        t.setTask1Done(true);
        t.setTask2Done(true);
        taskRepository.save(t);

        DailyJournal j = new DailyJournal();
        j.setUserAccount(user);
        j.setDate(today);
        journalRepository.save(j);

        LoginRecord r1 = new LoginRecord();
        r1.setUserAccount(user);
        r1.setLoginDate(today);
        loginRecordRepository.save(r1);
        LoginRecord r2 = new LoginRecord();
        r2.setUserAccount(user);
        r2.setLoginDate(today.minusDays(1));
        loginRecordRepository.save(r2);

        String body = scheduler.buildSummaryBody(user, today);

        assertThat(body).contains("EXP+30");
        assertThat(body).contains("TODO達成2件");
        assertThat(body).contains("ジャーナル1件");
        assertThat(body).contains("連続2日");
    }

    @Test
    void returnsNullWhenNoActivityThisWeek() {
        LocalDate today = LocalDate.now();
        UserAccount user = makeUser("weekly_idle", 100, 100); // 今週+0EXP、TODO/ジャーナルなし

        assertThat(scheduler.buildSummaryBody(user, today)).isNull();
    }
}
