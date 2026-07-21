package com.example.demo;

import com.example.demo.entity.Quest;

// 画面表示用：クエスト定義＋そのユーザーの現在の進捗をまとめたもの
public class QuestProgressView {
    private final Quest quest;
    private final int current;
    private final boolean completed;
    private final boolean claimed;

    public QuestProgressView(Quest quest, int current, boolean completed, boolean claimed) {
        this.quest = quest;
        this.current = current;
        this.completed = completed;
        this.claimed = claimed;
    }

    public Quest getQuest() { return quest; }
    public int getCurrent() { return current; }
    public boolean isCompleted() { return completed; }
    public boolean isClaimed() { return claimed; }
    public int getPercent() {
        if (quest.getTargetCount() <= 0) return 0;
        return Math.min(100, (int) Math.round(current * 100.0 / quest.getTargetCount()));
    }
}
