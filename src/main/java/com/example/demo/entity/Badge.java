package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// 管理者が追加・編集できる「実績（称号）」の定義
@Entity
public class Badge {

    public enum ConditionType {
        LEVEL,          // レベルが一定以上
        STREAK,         // 連続ログイン日数が一定以上
        JOURNAL_COUNT,  // ジャーナル記録数が一定以上
        TASK_COUNT      // TODO達成記録数が一定以上
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;   // 表示名（例：🏆 継続の鬼）
    private String icon;   // 絵文字アイコン
    private String description;

    @Enumerated(EnumType.STRING)
    private ConditionType conditionType;

    private int conditionValue; // 達成に必要な数値

    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }
    public int getConditionValue() { return conditionValue; }
    public void setConditionValue(int conditionValue) { this.conditionValue = conditionValue; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
