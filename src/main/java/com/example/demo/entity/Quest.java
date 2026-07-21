package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// 管理者が追加・編集できる「デイリー／ウィークリークエスト」の定義
@Entity
public class Quest {

    public enum Period {
        DAILY, WEEKLY
    }

    // 進捗の判定に使う条件の種類
    public enum ConditionType {
        TASK_COMPLETE_COUNT, // 期間内のTODO達成件数
        JOURNAL_COUNT,       // 期間内のジャーナル記録数
        MOOD_LOG_COUNT,      // 期間内の気分記録数
        LOGIN_STREAK         // 現在の連続ログイン日数
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String icon = "🗺️";

    @Enumerated(EnumType.STRING)
    private Period period;

    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    private int targetCount;
    private int rewardExp;
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Period getPeriod() { return period; }
    public void setPeriod(Period period) { this.period = period; }
    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }
    public int getTargetCount() { return targetCount; }
    public void setTargetCount(int targetCount) { this.targetCount = targetCount; }
    public int getRewardExp() { return rewardExp; }
    public void setRewardExp(int rewardExp) { this.rewardExp = rewardExp; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
