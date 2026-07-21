package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// ブラウザのWeb Push購読情報（endpoint URLと暗号化用の鍵）を保存する。
// 1ユーザーが複数端末（PC/スマホ）から購読することもあるので、ユーザーごとに複数行持てる。
@Entity
@Table(name = "push_subscription")
public class PushSubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount userAccount;

    @Column(length = 512)
    private String endpoint;

    private String p256dh;
    private String auth;

    public Long getId() { return id; }
    public UserAccount getUserAccount() { return userAccount; }
    public void setUserAccount(UserAccount userAccount) { this.userAccount = userAccount; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public String getP256dh() { return p256dh; }
    public void setP256dh(String p256dh) { this.p256dh = p256dh; }
    public String getAuth() { return auth; }
    public void setAuth(String auth) { this.auth = auth; }
}
