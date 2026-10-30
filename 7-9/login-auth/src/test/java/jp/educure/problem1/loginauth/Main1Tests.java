package jp.educure.problem1.loginauth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Main1Tests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSession userSession;

    @Test
    void throwsWhenNotLoggedIn() {
        userSession.logout();
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> userService.getUserDetails());
        assertEquals("User must be logged in to access this method.", e.getMessage());
    }

    @Test
    void worksWhenLoggedIn() {
        userSession.login();
        assertDoesNotThrow(() -> userService.getUserDetails());
    }
}
