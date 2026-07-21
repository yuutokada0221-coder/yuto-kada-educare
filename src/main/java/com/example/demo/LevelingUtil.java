package com.example.demo;

// ★レベル計算はこのクラスに一本化する（以前は各コントローラーが exp/100+1 を個別に書いていたため、
// 計算式を変える際に直し漏れが起きやすかった）。
// レベルNからN+1への昇格に必要なEXPは 5*N（Lv1→2は5、Lv2→3は10、Lv3→4は15…と5ずつ増えていく）。
public final class LevelingUtil {

    private LevelingUtil() {}

    // レベルアップ演出やプログレスバー表示に必要な情報をまとめて返す
    public record LevelInfo(int level, int expIntoLevel, int expRequiredForLevel) {
        public int progressPercent() {
            return expRequiredForLevel == 0 ? 0 : (int) Math.round(expIntoLevel * 100.0 / expRequiredForLevel);
        }
    }

    public static LevelInfo compute(int totalExp) {
        int level = 1;
        int remaining = Math.max(0, totalExp);
        int required = level * 5;
        while (remaining >= required) {
            remaining -= required;
            level++;
            required = level * 5;
        }
        return new LevelInfo(level, remaining, required);
    }

    public static int levelOf(int totalExp) {
        return compute(totalExp).level();
    }
}
