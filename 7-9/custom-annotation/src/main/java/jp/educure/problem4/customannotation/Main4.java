package jp.educure.problem4.customannotation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main4 {

    public static void main(String[] args) {
        SpringApplication.run(Main4.class, args);
    }

    @Bean
    public CommandLineRunner run(OrderService orderService) {
        return args -> orderService.processOrder();
    }
}
