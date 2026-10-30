package jp.educure.problem3.exceptionhandling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main3 {

    public static void main(String[] args) {
        SpringApplication.run(Main3.class, args);
    }

    @Bean
    public CommandLineRunner run(UsermanagementService service) {
        return args -> {
            service.createUser("John Doe", "john.doe@example.com");
            try {
                service.createUser("Error User", "error@example.com");
            } catch (RuntimeException e) {
                // エラーメッセージはアスペクト(@AfterThrowing)が出力済み
            }
        };
    }
}
