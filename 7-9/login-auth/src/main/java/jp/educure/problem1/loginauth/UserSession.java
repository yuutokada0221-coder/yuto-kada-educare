package jp.educure.problem1.loginauth;

import org.springframework.stereotype.Component;

@Component
public class UserSession {

    private boolean loggedIn = false;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void login() {
        this.loggedIn = true;
    }

    public void logout() {
        this.loggedIn = false;
    }
}
