package jp.educure.problem2.executiontime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main2 {

    public static void main(String[] args) {
        SpringApplication.run(Main2.class, args);
    }

    @Bean
    public CommandLineRunner run(CalculatorService calculator) {
        return args -> {
            calculator.add(10, 5);
            calculator.subtract(10, 5);
            calculator.multiply(10, 5);
            calculator.divide(10, 2);
            try {
                calculator.divide(10, 0);
            } catch (ArithmeticException e) {
                System.out.println(e.getMessage());
            }
        };
    }
}
