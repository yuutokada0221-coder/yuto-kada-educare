package _18;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows; 

public class DividerTest {

    @Test
    void testNormalDivision() {
        Divider divider = new Divider();

        assertEquals(3.0, divider.divide(6.0, 2.0), 0.0001, "正の数同士の除算（6 ÷ 2）が正しくありません");

        assertEquals(-4.0, divider.divide(-8.0, 2.0), 0.0001, "負の数を含む除算（-8 ÷ 2）が正しくありません");

        assertEquals(2.5, divider.divide(5.0, 2.0), 0.0001, "小数を含む除算（5.0 ÷ 2.0）が正しくありません");
    }

    @Test
    void testExceptionDivision() {
        Divider divider = new Divider();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            divider.divide(5.0, 0.0);
        });
        assertEquals("ゼロで除算はできません", e.getMessage());
    }
}
