package jp.educure.problem4.customannotation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Main4Tests {

    @Autowired
    private OrderService orderService;

    @Test
    void processOrderRuns() {
        assertDoesNotThrow(() -> orderService.processOrder());
    }
}
