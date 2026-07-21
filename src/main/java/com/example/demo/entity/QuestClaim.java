package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// 「どのユーザーがどのクエストをどの期間（日付／週）に受け取り済みか」を記録する
@Entity
@Table(name = "quest_claim", indexes = @Index(name = "idx_quest_claim_user_quest_period", columnList = "user_account_id, quest_id, period_key"))
public class QuestClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount userAccount;

    @ManyToOne
    private Quest quest;

    // デイリーなら "2026-07-19"、ウィークリーならその週の月曜日の日付文字列
    private String periodKey;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }
    public Quest getQuest() { return quest; }
    public void setQuest(Quest quest) { this.quest = quest; }
    public String getPeriodKey() { return periodKey; }
    public void setPeriodKey(String periodKey) { this.periodKey = periodKey; }
}
