package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// ★レベル計算式（Lv1→2は5EXP、Lv2→3は10EXP…と5ずつ増える累積カーブ）の単体テスト。
// Spring不要の純粋なロジックなので、DBやMockMvcを介さず直接検証する。
class LevelingUtilTest {

    @Test
    void level1UntilCumulativeExpReachesFive() {
        assertThat(LevelingUtil.levelOf(0)).isEqualTo(1);
        assertThat(LevelingUtil.levelOf(4)).isEqualTo(1);
    }

    @Test
    void level2StartsAtFiveAndLastsUntilFifteen() {
        assertThat(LevelingUtil.levelOf(5)).isEqualTo(2);
        assertThat(LevelingUtil.levelOf(14)).isEqualTo(2);
        assertThat(LevelingUtil.levelOf(15)).isEqualTo(3);
    }

    @Test
    void level4StartsAtThirty() {
        // 累積境界: Lv2=5, Lv3=5+10=15, Lv4=15+15=30, Lv5=30+20=50
        assertThat(LevelingUtil.levelOf(29)).isEqualTo(3);
        assertThat(LevelingUtil.levelOf(30)).isEqualTo(4);
        assertThat(LevelingUtil.levelOf(49)).isEqualTo(4);
        assertThat(LevelingUtil.levelOf(50)).isEqualTo(5);
    }

    @Test
    void computeReturnsExpIntoLevelAndRequiredForLevel() {
        LevelingUtil.LevelInfo info = LevelingUtil.compute(2);
        assertThat(info.level()).isEqualTo(1);
        assertThat(info.expIntoLevel()).isEqualTo(2);
        assertThat(info.expRequiredForLevel()).isEqualTo(5);
        assertThat(info.progressPercent()).isEqualTo(40); // 2/5 = 40%

        LevelingUtil.LevelInfo justLeveled = LevelingUtil.compute(15);
        assertThat(justLeveled.level()).isEqualTo(3);
        assertThat(justLeveled.expIntoLevel()).isEqualTo(0);
        assertThat(justLeveled.expRequiredForLevel()).isEqualTo(15);
        assertThat(justLeveled.progressPercent()).isEqualTo(0);
    }

    @Test
    void negativeExpIsTreatedAsZero() {
        assertThat(LevelingUtil.levelOf(-5)).isEqualTo(1);
    }
}
