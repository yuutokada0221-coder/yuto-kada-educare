package jp.educure.problem3.exceptionhandling;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Main3Tests {

    @Autowired
    private UsermanagementService service;

    @Test
    void savesUser() {
        assertDoesNotThrow(() -> service.createUser("John Doe", "john.doe@example.com"));
    }

    @Test
    void throwsOnError() {
        assertThrows(RuntimeException.class, () -> service.createUser("Error User", "error@example.com"));
    }
}
