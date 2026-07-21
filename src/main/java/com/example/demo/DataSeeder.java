package com.example.demo;

import com.example.demo.entity.Badge;
import com.example.demo.entity.Quest;
import com.example.demo.entity.ThemeOption;
import com.example.demo.repository.BadgeRepository;
import com.example.demo.repository.QuestRepository;
import com.example.demo.repository.ThemeOptionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// アプリ初回起動時に、テーマ・実績・クエストの初期データを投入する
// （管理画面から追加・編集できるようにDB管理へ移行したため、最初の数件だけここで用意する）
@Component
public class DataSeeder implements CommandLineRunner {

    private final ThemeOptionRepository themeRepository;
    private final BadgeRepository badgeRepository;
    private final QuestRepository questRepository;

    public DataSeeder(ThemeOptionRepository themeRepository, BadgeRepository badgeRepository, QuestRepository questRepository) {
        this.themeRepository = themeRepository;
        this.badgeRepository = badgeRepository;
        this.questRepository = questRepository;
    }

    @Override
    public void run(String... args) {
        if (themeRepository.count() == 0) {
            // ★背景テーマは10レベルごとに解放。Lv30到達で自分の好きな画像を背景にできる特別枠が別途アンロックされる
            themeRepository.save(theme("デフォルト", "💧", "default", 1));
            themeRepository.save(theme("サクラピンク", "🌸", "theme-sakura", 10));
            themeRepository.save(theme("フォレストグリーン", "🌲", "theme-forest", 20));
        }

        if (badgeRepository.count() == 0) {
            // ★レベル
            badgeRepository.save(badge("見習い冒険者", "🔰", "レベル3到達", Badge.ConditionType.LEVEL, 3));
            badgeRepository.save(badge("駆け出しヒーロー", "⚔️", "レベル5到達", Badge.ConditionType.LEVEL, 5));
            badgeRepository.save(badge("経験値の達人", "💎", "レベル10到達", Badge.ConditionType.LEVEL, 10));
            badgeRepository.save(badge("ベテラン戦士", "🛡️", "レベル20到達", Badge.ConditionType.LEVEL, 20));
            badgeRepository.save(badge("伝説の勇者", "👑", "レベル30到達", Badge.ConditionType.LEVEL, 30));
            // ★連続ログイン
            badgeRepository.save(badge("三日坊主卒業", "🌱", "3日以上連続でログイン", Badge.ConditionType.STREAK, 3));
            badgeRepository.save(badge("継続の鬼", "🏆", "7日以上連続でログイン", Badge.ConditionType.STREAK, 7));
            badgeRepository.save(badge("習慣の化身", "🔥", "14日以上連続でログイン", Badge.ConditionType.STREAK, 14));
            badgeRepository.save(badge("不屈の精神", "⚡", "30日以上連続でログイン", Badge.ConditionType.STREAK, 30));
            // ★ジャーナル
            badgeRepository.save(badge("駆け出し日記作家", "📝", "ジャーナルを5件記録", Badge.ConditionType.JOURNAL_COUNT, 5));
            badgeRepository.save(badge("ジャーナリスト", "✍️", "ジャーナルを10件記録", Badge.ConditionType.JOURNAL_COUNT, 10));
            badgeRepository.save(badge("内省の達人", "📚", "ジャーナルを30件記録", Badge.ConditionType.JOURNAL_COUNT, 30));
            // ★TODO達成数
            badgeRepository.save(badge("行動派", "🏃", "TODO・防御を10件達成", Badge.ConditionType.TASK_COUNT, 10));
            badgeRepository.save(badge("実行力の証", "💪", "TODO・防御を30件達成", Badge.ConditionType.TASK_COUNT, 30));
            badgeRepository.save(badge("達成マシーン", "🚀", "TODO・防御を100件達成", Badge.ConditionType.TASK_COUNT, 100));
        }

        if (questRepository.count() == 0) {
            // ★EXPは「入力ごとに1pt」のペースに合わせ、クエスト報酬も控えめに設定（レベルアップの体感速度を要件に合わせて調整）
            questRepository.save(quest("今日のTODOを1つ達成", "TODOを1つ完了する", "☀️", Quest.Period.DAILY, Quest.ConditionType.TASK_COMPLETE_COUNT, 1, 2));
            questRepository.save(quest("今日のジャーナルを記録", "夜のジャーナルを記録する", "🌙", Quest.Period.DAILY, Quest.ConditionType.JOURNAL_COUNT, 1, 2));
            questRepository.save(quest("今日の気分を記録", "気分トラッカーで記録する", "😊", Quest.Period.DAILY, Quest.ConditionType.MOOD_LOG_COUNT, 1, 1));
            questRepository.save(quest("週5日ログイン", "今週5日以上連続でログインする", "🔥", Quest.Period.WEEKLY, Quest.ConditionType.LOGIN_STREAK, 5, 8));
            questRepository.save(quest("週10個のTODO達成", "今週TODOを10個達成する", "🗺️", Quest.Period.WEEKLY, Quest.ConditionType.TASK_COMPLETE_COUNT, 10, 10));
        }
    }

    private ThemeOption theme(String name, String emoji, String cssClass, int level) {
        ThemeOption t = new ThemeOption();
        t.setName(name);
        t.setEmoji(emoji);
        t.setCssClass(cssClass);
        t.setUnlockLevel(level);
        return t;
    }

    private Badge badge(String name, String icon, String description, Badge.ConditionType type, int value) {
        Badge b = new Badge();
        b.setName(name);
        b.setIcon(icon);
        b.setDescription(description);
        b.setConditionType(type);
        b.setConditionValue(value);
        return b;
    }

    private Quest quest(String title, String description, String icon, Quest.Period period, Quest.ConditionType conditionType, int target, int rewardExp) {
        Quest q = new Quest();
        q.setTitle(title);
        q.setDescription(description);
        q.setIcon(icon);
        q.setPeriod(period);
        q.setConditionType(conditionType);
        q.setTargetCount(target);
        q.setRewardExp(rewardExp);
        return q;
    }
}
