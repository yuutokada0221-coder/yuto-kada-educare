package _18;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DividerTest {

    @Test
    void testNormalDivision() {
        Divider divider = new Divider();

        // 正の数の除算テスト (例：6 ÷ 2)
        // ※小数の比較では、誤差を許容するために第3引数に「0.0001」のようなデルタ値（許容範囲）を指定します
        assertEquals(3.0, divider.divide(6.0, 2.0), 0.0001, "正の数同士の除算（6 ÷ 2）が正しくありません");

        // 負の数を含む除算 (例：-8 ÷ 2)
        assertEquals(-4.0, divider.divide(-8.0, 2.0), 0.0001, "負の数を含む除算（-8 ÷ 2）が正しくありません");

        // 小数を含む除算 (例：5.0 ÷ 2.0)
        assertEquals(2.5, divider.divide(5.0, 2.0), 0.0001, "小数を含む除算（5.0 ÷ 2.0）が正しくありません");
    }

    @Test
    void testExceptionDivision() {
        Divider divider = new Divider();

        try {
            // ゼロによる除算 (例：5 ÷ 0)
            divider.divide(5.0, 0.0);
            
            // もし上の行でエラー（例外）が起きず、ここまで進んでしまったらテストを強制的に失敗させる
            fail("ゼロ除算で例外が発生するはずです");
            
        } catch (IllegalArgumentException e) {
            // 期待通りに例外が発生した場合、そのエラーメッセージが正しいかを確認する
            assertEquals("ゼロで除算はできません", e.getMessage());
        }
    }
}
