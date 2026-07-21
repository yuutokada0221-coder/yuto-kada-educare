package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// ★Duolingo風の「週間リーグ」。全ユーザーを5階層に分け、週間の獲得EXPで毎週昇格・降格させる。
// 実際のDuolingoは「約30人ずつのコホート」に分けるが、このアプリはユーザー数がまだ少ないため
// シンプルに「同じ階層の全ユーザー」を1つのグループとして扱う（人数が増えたら本家同様に
// コホート分割するのが望ましいが、それは将来の課題として明記しておく）。
@Service
public class LeagueService {

    private static final Logger log = LoggerFactory.getLogger(LeagueService.class);

    public static final String[] TIER_NAMES = {"ブロンズ", "シルバー", "ゴールド", "サファイア", "ダイヤモンド"};
    private static final int MAX_TIER = TIER_NAMES.length - 1;
    // 昇格・降格を行うために必要な最低人数（これ未満は「勝負にならない」として現状維持）
    private static final int MIN_GROUP_SIZE_FOR_PROMOTION = 3;

    @Autowired private UserAccountRepository userRepository;

    public static String tierName(int tier) {
        if (tier < 0 || tier >= TIER_NAMES.length) return TIER_NAMES[0];
        return TIER_NAMES[tier];
    }

    public int weeklyExpOf(UserAccount user) {
        return Math.max(0, user.getExp() - user.getWeeklyExpSnapshot());
    }

    /** 自分と同じ階層の全ユーザーを、今週の獲得EXP降順で返す */
    public List<UserAccount> standingsFor(UserAccount user) {
        List<UserAccount> group = userRepository.findByLeagueTier(user.getLeagueTier());
        group.sort(Comparator.comparingInt(this::weeklyExpOf).reversed());
        return group;
    }

    @Scheduled(cron = "0 5 0 * * MON")
    public void weeklyLeagueReset() {
        processPromotionsAndReset();
    }

    // ★@Scheduledのラッパーと分離しておくことで、テストや手動確認から直接呼び出せるようにしてある。
    // 全ユーザーを1回だけfetchして、同じエンティティ集合に対して昇格/降格→スナップショットリセット→
    // 最後に1回だけsaveAll、という順で処理する（このセッションで何度も踏んだ「別クエリで再取得した
    // 未変更のエンティティを誤って保存してしまう」問題を避けるため）。
    @Transactional
    public void processPromotionsAndReset() {
        List<UserAccount> allUsers = userRepository.findAll();

        for (int tier = 0; tier <= MAX_TIER; tier++) {
            final int currentTier = tier;
            List<UserAccount> group = allUsers.stream()
                    .filter(u -> u.getLeagueTier() == currentTier)
                    .collect(Collectors.toList());
            if (group.size() < MIN_GROUP_SIZE_FOR_PROMOTION) {
                continue; // 少人数すぎる階層は今週は昇格・降格なし
            }
            group.sort(Comparator.comparingInt(this::weeklyExpOf).reversed());

            int promoteCount = Math.max(1, (int) Math.ceil(group.size() * 0.3));
            int demoteCount = Math.max(1, (int) Math.ceil(group.size() * 0.3));

            for (int i = 0; i < group.size(); i++) {
                UserAccount u = group.get(i);
                if (i < promoteCount && u.getLeagueTier() < MAX_TIER) {
                    u.setLeagueTier(u.getLeagueTier() + 1);
                } else if (i >= group.size() - demoteCount && u.getLeagueTier() > 0) {
                    u.setLeagueTier(u.getLeagueTier() - 1);
                }
            }
            log.info("リーグ処理: 階層{}（{}人）を昇格{}人・降格{}人で処理しました",
                    tier, group.size(), promoteCount, demoteCount);
        }

        // 昇格・降格を反映した上で、全員のスナップショットを現在のexpにリセットし新しい週を開始する
        for (UserAccount u : allUsers) {
            u.setWeeklyExpSnapshot(u.getExp());
        }
        userRepository.saveAll(allUsers);
    }
}
