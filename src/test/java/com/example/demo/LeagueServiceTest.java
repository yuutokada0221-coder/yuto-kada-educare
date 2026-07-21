package com.example.demo;

import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// ★週間リーグ（LeagueService）のロジックを検証する単体テスト。
// H2インメモリDBを使うため、実際のPostgres（habit_app）には触れない。
@SpringBootTest
@Transactional
class LeagueServiceTest {

    @Autowired
    private LeagueService leagueService;

    @Autowired
    private UserAccountRepository userRepository;

    private UserAccount makeUser(String username, int tier, int exp, int weeklyExpSnapshot) {
        UserAccount u = new UserAccount();
        u.setUsername(username);
        u.setPassword("dummy");
        u.setLeagueTier(tier);
        u.setExp(exp);
        u.setWeeklyExpSnapshot(weeklyExpSnapshot);
        return userRepository.save(u);
    }

    @Test
    void promotesTopAndDemotesBottomOfTierThenResetsSnapshot() {
        // 同じ階層（tier=1）に5人。週間EXP（exp - snapshot）で 50,40,30,20,10 の順位になる想定
        makeUser("league_top1", 1, 150, 100);    // 週間+50 → 上位30%(2人)に入り昇格
        makeUser("league_top2", 1, 140, 100);    // 週間+40 → 上位30%(2人)に入り昇格
        makeUser("league_mid", 1, 130, 100);     // 週間+30 → 現状維持
        makeUser("league_bot1", 1, 120, 100);    // 週間+20 → 下位30%(2人)に入り降格
        makeUser("league_bot2", 1, 110, 100);    // 週間+10 → 下位30%(2人)に入り降格

        leagueService.processPromotionsAndReset();

        assertThat(userRepository.findByUsername("league_top1").getLeagueTier()).isEqualTo(2);
        assertThat(userRepository.findByUsername("league_top2").getLeagueTier()).isEqualTo(2);
        assertThat(userRepository.findByUsername("league_mid").getLeagueTier()).isEqualTo(1);
        assertThat(userRepository.findByUsername("league_bot1").getLeagueTier()).isEqualTo(0);
        assertThat(userRepository.findByUsername("league_bot2").getLeagueTier()).isEqualTo(0);

        // 昇格・降格の判定後、全員の週間スナップショットが現在のexpにリセットされていること
        UserAccount top1 = userRepository.findByUsername("league_top1");
        assertThat(top1.getWeeklyExpSnapshot()).isEqualTo(top1.getExp());
    }

    @Test
    void tooFewUsersInTierAreLeftUnchanged() {
        // 同じ階層に2人しかいない場合は、最低人数(3人)未満なので昇格・降格なし
        makeUser("league_small_a", 3, 200, 100);
        makeUser("league_small_b", 3, 100, 100);

        leagueService.processPromotionsAndReset();

        assertThat(userRepository.findByUsername("league_small_a").getLeagueTier()).isEqualTo(3);
        assertThat(userRepository.findByUsername("league_small_b").getLeagueTier()).isEqualTo(3);
    }

    @Test
    void topTierCannotPromoteFurtherAndBottomTierCannotDemoteFurther() {
        int maxTier = LeagueService.TIER_NAMES.length - 1;
        makeUser("league_max1", maxTier, 150, 100);
        makeUser("league_max2", maxTier, 140, 100);
        makeUser("league_max3", maxTier, 130, 100);

        makeUser("league_min1", 0, 150, 100);
        makeUser("league_min2", 0, 140, 100);
        makeUser("league_min3", 0, 130, 100);

        leagueService.processPromotionsAndReset();

        assertThat(userRepository.findByUsername("league_max1").getLeagueTier()).isEqualTo(maxTier);
        assertThat(userRepository.findByUsername("league_min3").getLeagueTier()).isEqualTo(0);
    }
}
