package jp.educure.problem6.customannotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@SpringBootTest(classes = Main6.class)
@ExtendWith(OutputCaptureExtension.class)
class Main6Tests {

    private static final String SIG = "void jp.educure.problem6.customannotation.OrderService.processOrder()";

    @Autowired
    private OrderService orderService;

    @Test
    void logsInOrder(CapturedOutput output) {
        orderService.processOrder();
        String expected = "[LOG] 開始: " + SIG + System.lineSeparator()
                + "注文処理中..." + System.lineSeparator()
                + "注文処理完了" + System.lineSeparator()
                + "[LOG] 終了: " + SIG + System.lineSeparator();
        assertThat(output.getOut()).endsWith(expected);
    }

    @Test
    void threeConsecutiveRuns(CapturedOutput output) {
        int before = output.getOut().split("\\R").length;
        for (int i = 0; i < 3; i++) {
            orderService.processOrder();
        }
        String[] lines = output.getOut().split("\\R");
        assertThat(lines.length - before).isEqualTo(12);
        for (int i = lines.length - 12; i < lines.length; i += 4) {
            assertThat(lines[i]).isEqualTo("[LOG] 開始: " + SIG);
            assertThat(lines[i + 1]).isEqualTo("注文処理中...");
            assertThat(lines[i + 2]).isEqualTo("注文処理完了");
            assertThat(lines[i + 3]).isEqualTo("[LOG] 終了: " + SIG);
        }
    }
}
