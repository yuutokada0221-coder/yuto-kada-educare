package com.example.demo;

import com.example.demo.entity.UserAccount;

// 管理画面のユーザー一覧表示用：UserAccountに集計値（レベル・ストリーク等）を添えたもの
public class AdminUserView {
    private final UserAccount user;
    private final int level;
    private final int streak;
    private final int cumulativeDays;

    public AdminUserView(UserAccount user, int level, int streak, int cumulativeDays) {
        this.user = user;
        this.level = level;
        this.streak = streak;
        this.cumulativeDays = cumulativeDays;
    }

    public UserAccount getUser() { return user; }
    public int getLevel() { return level; }
    public int getStreak() { return streak; }
    public int getCumulativeDays() { return cumulativeDays; }
}
