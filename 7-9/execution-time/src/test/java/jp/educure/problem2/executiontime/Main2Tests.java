package jp.educure.problem2.executiontime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Main2Tests {

    @Autowired
    private CalculatorService calculator;

    @Test
    void calculates() {
        assertEquals(15, calculator.add(10, 5));
        assertEquals(5, calculator.subtract(10, 5));
        assertEquals(50, calculator.multiply(10, 5));
        assertEquals(5, calculator.divide(10, 2));
    }

    @Test
    void divideByZeroThrows() {
        ArithmeticException e = assertThrows(ArithmeticException.class, () -> calculator.divide(10, 0));
        assertEquals("Division by zero", e.getMessage());
    }
}
