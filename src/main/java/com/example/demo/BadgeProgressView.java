package com.example.demo;

import com.example.demo.entity.Badge;

// 実績一覧表示用：バッジ定義＋現在の進捗（未獲得でも「あと◯」がわかるように）
public class BadgeProgressView {
    private final Badge badge;
    private final int current;
    private final boolean earned;

    public BadgeProgressView(Badge badge, int current, boolean earned) {
        this.badge = badge;
        this.current = current;
        this.earned = earned;
    }

    public Badge getBadge() { return badge; }
    public int getCurrent() { return current; }
    public boolean isEarned() { return earned; }
    public int getPercent() {
        if (badge.getConditionValue() <= 0) return 0;
        return Math.min(100, (int) Math.round(current * 100.0 / badge.getConditionValue()));
    }
}
