package _18;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class RPGCharacterTest {

    @Test
    void testInitialStatus() {
        RPGCharacter character = new RPGCharacter();

        // 初期状態のテスト
        assertEquals(1, character.getLevel(), "初期レベルが正しくありません");
        assertEquals(100, character.getHP(), "初期HPが正しくありません");
        assertEquals(50, character.getMP(), "初期MPが正しくありません");
    }

    @Test
    void testNormalLevelUp() {
        RPGCharacter character = new RPGCharacter();

        // 1回レベルアップさせる
        character.levelUp();

        // 通常のレベルアップテスト
        assertEquals(2, character.getLevel(), "レベルアップ後のレベルが正しくありません");
        assertEquals(110, character.getHP(), "レベルアップ後のHPが正しくありません");
        assertEquals(55, character.getMP(), "レベルアップ後のMPが正しくありません");
    }

    @Test
    void testMaxLevel() {
        RPGCharacter character = new RPGCharacter();

        // レベル1からスタートするので、97回レベルアップさせるとレベル98になる
        for (int i = 0; i < 97; i++) {
            character.levelUp();
        }

        // レベル98から99への正常なレベルアップ
        character.levelUp();
        assertEquals(99, character.getLevel(), "レベル99へのレベルアップが正しくありません");

        // レベル99からのレベルアップ試行で例外発生を確認
        try {
            character.levelUp();
            fail("最大レベル時のレベルアップで例外が発生するはずです");
        } catch (IllegalStateException e) {
            assertEquals("最大レベルに達しています", e.getMessage());
        }
    }
}
