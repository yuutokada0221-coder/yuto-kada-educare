package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "daily_journal", indexes = @Index(name = "idx_daily_journal_user_date", columnList = "user_account_id, date"))
public class DailyJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 誰の記録かを紐づける
    @ManyToOne
    private UserAccount userAccount;

    private LocalDate date; // 日付

    private String achievement; // 今日できたこと

    private String gratitude1; // 感謝1
    private String gratitude2; // 感謝2
    private String gratitude3; // 感謝3

    private String diaryText; // 今日のできごと（日記本文）

    private int moodScore; // 気分（ムード）トラッカー：1〜5の5段階

    private String photoFilename; // 自由記述に添付した写真（uploads/journal-photos/配下のファイル名）

// データの出し入れ口（ゲッターとセッター）
    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getAchievement() { return achievement; }
    public void setAchievement(String achievement) { this.achievement = achievement; }
    public String getGratitude1() { return gratitude1; }
    public void setGratitude1(String gratitude1) { this.gratitude1 = gratitude1; }
    public String getGratitude2() { return gratitude2; }
    public void setGratitude2(String gratitude2) { this.gratitude2 = gratitude2; }
    public String getGratitude3() { return gratitude3; }
    public void setGratitude3(String gratitude3) { this.gratitude3 = gratitude3; }
    public String getDiaryText() { return diaryText; }
    public void setDiaryText(String diaryText) { this.diaryText = diaryText; }
    public int getMoodScore() { return moodScore; }
    public void setMoodScore(int moodScore) { this.moodScore = moodScore; }
    public String getPhotoFilename() { return photoFilename; }
    public void setPhotoFilename(String photoFilename) { this.photoFilename = photoFilename; }
    public Long getId() { return id; }
    public LocalDate getDate() { return date; }
}

