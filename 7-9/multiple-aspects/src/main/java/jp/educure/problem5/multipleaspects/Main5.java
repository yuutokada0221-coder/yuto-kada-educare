package jp.educure.problem5.multipleaspects;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main5 {

    public static void main(String[] args) {
        SpringApplication.run(Main5.class, args);
    }

    @Bean
    public CommandLineRunner run(PaymentService paymentService) {
        return args -> paymentService.processPayment();
    }
}
