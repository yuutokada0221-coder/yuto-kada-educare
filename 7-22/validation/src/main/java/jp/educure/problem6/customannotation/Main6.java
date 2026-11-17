package jp.educure.problem6.customannotation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main6 {

    public static void main(String[] args) {
        SpringApplication.run(Main6.class, args);
    }

    @Bean
    public CommandLineRunner run(OrderService orderService) {
        return args -> orderService.processOrder();
    }
}
