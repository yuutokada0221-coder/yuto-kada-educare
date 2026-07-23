package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ユーザー名（重複禁止）
    @Column(unique = true, nullable = false)
    private String username;

    // パスワード
    @Column(nullable = false)
    private String password;
    // ★追加：長期目標
    private String longTermGoal;

    public String getLongTermGoal() { return longTermGoal; }
    public void setLongTermGoal(String longTermGoal) { this.longTermGoal = longTermGoal; }

    // --- ここから下は出し入れ口（Getter/Setter） ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    // ★追加：経験値（EXP）
    private int exp = 0;

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }
    // ★追加：マイ固定習慣（最大3つ）
    private String fixedHabit1;
    private String fixedHabit2;
    private String fixedHabit3;

    public String getFixedHabit1() { return fixedHabit1; }
    public void setFixedHabit1(String fixedHabit1) { this.fixedHabit1 = fixedHabit1; }

    public String getFixedHabit2() { return fixedHabit2; }
    public void setFixedHabit2(String fixedHabit2) { this.fixedHabit2 = fixedHabit2; }

    public String getFixedHabit3() { return fixedHabit3; }
    public void setFixedHabit3(String fixedHabit3) { this.fixedHabit3 = fixedHabit3; }
    // ★これを追加：ユーザーの選んだテーマ（初期値はデフォルト）
    private String theme = "default";

    // ★ファイルの一番下の方に、Getter/Setterを追加
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    // ★追加：固定の「やらないこと（悪習）」
    private String fixedBadHabit;

    // ★一番下に追加
    public String getFixedBadHabit() { return fixedBadHabit; }
    public void setFixedBadHabit(String fixedBadHabit) { this.fixedBadHabit = fixedBadHabit; }

    // ★追加：ストリークフリーズ（お休みチケット、週1回まで）
    private int streakFreezeRemaining = 1;
    // このチケット残数がどの週の分かを判定するための「その週の月曜日」
    private LocalDate freezeWeekStart;

    public int getStreakFreezeRemaining() { return streakFreezeRemaining; }
    public void setStreakFreezeRemaining(int streakFreezeRemaining) { this.streakFreezeRemaining = streakFreezeRemaining; }
    public LocalDate getFreezeWeekStart() { return freezeWeekStart; }
    public void setFreezeWeekStart(LocalDate freezeWeekStart) { this.freezeWeekStart = freezeWeekStart; }

    // ★追加：登録日（累計達成率の計算基準日）
    private LocalDate createdAt = LocalDate.now();

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    // ★追加：権限（USER / ADMIN）。管理画面へのアクセス制御に使う
    @Column(nullable = false)
    private String role = "USER";

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ★追加：アイデンティティ宣言（「なりたい自分」を一言で）。オンボーディングで設定
    private String identityDeclaration;

    public String getIdentityDeclaration() { return identityDeclaration; }
    public void setIdentityDeclaration(String identityDeclaration) { this.identityDeclaration = identityDeclaration; }

    // ★追加：オンボーディング（初回セットアップ）が完了しているか
    @Column(nullable = false)
    private boolean onboardingCompleted = false;

    public boolean isOnboardingCompleted() { return onboardingCompleted; }
    public void setOnboardingCompleted(boolean onboardingCompleted) { this.onboardingCompleted = onboardingCompleted; }

    // ★追加：Lv30で解放される「好きな画像の背景」。アップロード済み画像のファイル名（例：user-3.png）
    private String customBackgroundFilename;

    public String getCustomBackgroundFilename() { return customBackgroundFilename; }
    public void setCustomBackgroundFilename(String customBackgroundFilename) { this.customBackgroundFilename = customBackgroundFilename; }

    // ★追加：通知頻度設定（NONE / LOW / NORMAL）
    @Column(nullable = false)
    private String notificationFrequency = "NORMAL";

    public String getNotificationFrequency() { return notificationFrequency; }
    public void setNotificationFrequency(String notificationFrequency) { this.notificationFrequency = notificationFrequency; }

    // ★追加：パスワードリセット・通知メール送付先。既存ユーザーは未設定のことがあるためnullable
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ★追加：週間リーグ（Duolingo風）。tierは0=ブロンズ〜4=ダイヤモンド。
    // weeklyExpSnapshotは「今週の開始時点でのexp」で、今週の獲得量は exp - weeklyExpSnapshot で求める。
    @Column(nullable = false)
    private int leagueTier = 0;

    @Column(nullable = false)
    private int weeklyExpSnapshot = 0;

    public int getLeagueTier() { return leagueTier; }
    public void setLeagueTier(int leagueTier) { this.leagueTier = leagueTier; }
    public int getWeeklyExpSnapshot() { return weeklyExpSnapshot; }
    public void setWeeklyExpSnapshot(int weeklyExpSnapshot) { this.weeklyExpSnapshot = weeklyExpSnapshot; }

    // ★追加：モンスターバトルの体数（1体目=HP3、以降撃破するたびにHPが2倍になる）と、
    // 現在の体に対する累積ダメージ（日をまたいで持ち越す。撃破すると次の体に繰り越して0にリセット）
    @Column(nullable = false)
    private int monsterTier = 1;

    @Column(nullable = false)
    private int monsterDamageAccumulated = 0;

    public int getMonsterTier() { return monsterTier; }
    public void setMonsterTier(int monsterTier) { this.monsterTier = monsterTier; }
    public int getMonsterDamageAccumulated() { return monsterDamageAccumulated; }
    public void setMonsterDamageAccumulated(int monsterDamageAccumulated) { this.monsterDamageAccumulated = monsterDamageAccumulated; }
}