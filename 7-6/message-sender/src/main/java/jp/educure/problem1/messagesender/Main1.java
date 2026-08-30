package jp.educure.problem1.messagesender;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main1 {

    public static void main(String[] args) {
        SpringApplication.run(Main1.class, args);
    }

    @Bean
    public CommandLineRunner run(NotificationService notificationService) {
        return args -> notificationService.sendNotification("Hello, Spring!");
    }
}