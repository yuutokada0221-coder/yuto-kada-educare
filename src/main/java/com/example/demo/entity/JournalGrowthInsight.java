package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

// AIによるジャーナル成長分析コメントを1ユーザー1件だけキャッシュする（1日1回だけ再生成すればよいため）
@Entity
@Table(name = "journal_growth_insight")
public class JournalGrowthInsight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private UserAccount userAccount;

    private LocalDate generatedDate; // この日付とtodayが一致する間はキャッシュを再利用する

    @Column(columnDefinition = "TEXT")
    private String content;

    public Long getId() { return id; }
    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }
    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
