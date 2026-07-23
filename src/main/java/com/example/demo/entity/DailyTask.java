package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity // これをつけると「データベースのテーブル」になります！
// ★ユーザーごとの日付検索（findByUserAccountAndDate等）が最も多いクエリなのでインデックスを張る
@Table(name = "daily_task", indexes = @Index(name = "idx_daily_task_user_date", columnList = "user_account_id, date"))
public class DailyTask {

    @Id // これが主キー（PRIMARY KEY）です
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT（自動採番）です
    private Long id;

    // 誰の記録かを紐づける
    @ManyToOne
    private UserAccount userAccount;

    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }

    private LocalDate date; // 日付

    private String task1; // タスク1の内容
    private boolean isTask1Done; // タスク1が終わったかどうか（true/false）

    private String task2; // タスク2
    private boolean isTask2Done;

    private String task3; // タスク3
    private boolean isTask3Done;
    // ★追加：今日の「やらないこと（防御）」
    private String badHabit;
    private boolean badHabitDone;

    // ★追加：達成率の分母を日をまたいでも保持するためのスナップショット。
    // 新方式ではチェックしなかった項目はDBに一切残らないため、「その日は3つ入力欄があったが1つしか
    // チェックしなかった」という事実を後から復元できない。そこでチェックするたびに
    // 「固定習慣の非空数」と「その時点でのチェック済み数」の大きい方を記録しておき、
    // 日が変わって過去の記録として集計される時もその日の本来の分母をそのまま使えるようにする。
    // 未設定（過去のレコード等）の場合はnullのまま——集計側でフォールバック計算する。
    private Integer inputCount;
    public Integer getInputCount() { return inputCount; }
    public void setInputCount(Integer inputCount) { this.inputCount = inputCount; }

    // ★一番下に追加
    public String getBadHabit() { return badHabit; }
    public void setBadHabit(String badHabit) { this.badHabit = badHabit; }
    public boolean isBadHabitDone() { return badHabitDone; }
    public void setBadHabitDone(boolean badHabitDone) { this.badHabitDone = badHabitDone; }

    // ※今回は初学者向けに、細かい「ゲッター・セッター」は自動生成ツール等で後で追加するか、
    // まずはこのまま「箱」として定義しておきます。
    public void setDate(LocalDate date) { this.date = date; }
    public String getTask1() { return task1; }
    public void setTask1(String task1) { this.task1 = task1; }
    public boolean isTask1Done() { return isTask1Done; }
    public void setTask1Done(boolean task1Done) { this.isTask1Done = task1Done; }
    
    public String getTask2() { return task2; }
    public void setTask2(String task2) { this.task2 = task2; }
    public boolean isTask2Done() { return isTask2Done; }
    public void setTask2Done(boolean task2Done) { this.isTask2Done = task2Done; }
    
    public String getTask3() { return task3; }
    public void setTask3(String task3) { this.task3 = task3; }
    public boolean isTask3Done() { return isTask3Done; }
    public void setTask3Done(boolean task3Done) { this.isTask3Done = task3Done; }
    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
}
