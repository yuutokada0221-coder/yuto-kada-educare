package jp.educure.problem5.multipleaspects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Main5Tests {

    @Autowired
    private PaymentService paymentService;

    @Test
    void processPaymentRuns() {
        assertDoesNotThrow(() -> paymentService.processPayment());
    }
}
