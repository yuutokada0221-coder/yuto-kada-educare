package _18;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {

    @Test
    void testAdd() {
        // Calculatorクラスのインスタンスを作成
        Calculator calculator = new Calculator();

        // 正の数同士の計算 (例：2 + 3)
        assertEquals(5, calculator.add(2, 3), "正の数同士の足し算（2 + 3）が正しくありません");

        // 負の数を含む計算 (例：-1 + 5)
        assertEquals(4, calculator.add(-1, 5), "負の数を含む足し算（-1 + 5）が正しくありません");

        // ゼロを含む計算 (例：0 + 4)
        assertEquals(4, calculator.add(0, 4), "ゼロを含む足し算（0 + 4）が正しくありません");
    }

    @Test
    void testSubtract() {
        // Calculatorクラスのインスタンスを作成
        Calculator calculator = new Calculator();

        // 正の数同士の計算 (例：5 - 3)
        assertEquals(2, calculator.subtract(5, 3), "正の数同士の引き算（5 - 3）が正しくありません");

        // 負の数を含む計算 (例：-1 - 2)
        assertEquals(-3, calculator.subtract(-1, 2), "負の数を含む引き算（-1 - 2）が正しくありません");

        // ゼロを含む計算 (例：4 - 0)
        assertEquals(4, calculator.subtract(4, 0), "ゼロを含む引き算（4 - 0）が正しくありません");
    }
}
