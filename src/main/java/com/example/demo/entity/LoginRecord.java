package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "login_record", indexes = @Index(name = "idx_login_record_user_date", columnList = "user_account_id, login_date"))
public class LoginRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 誰の記録かを紐づける
    @ManyToOne
    private UserAccount userAccount;

    // ログインした日付
    private LocalDate loginDate;

    // ★追加：ストリークフリーズ（お休みチケット）で保護された日かどうか
    private boolean frozen;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }

    public LocalDate getLoginDate() { return loginDate; }
    public void setLoginDate(LocalDate loginDate) { this.loginDate = loginDate; }

    public boolean isFrozen() { return frozen; }
    public void setFrozen(boolean frozen) { this.frozen = frozen; }
}
