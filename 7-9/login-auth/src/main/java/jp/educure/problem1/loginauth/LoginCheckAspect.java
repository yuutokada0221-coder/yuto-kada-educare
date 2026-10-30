package jp.educure.problem1.loginauth;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoginCheckAspect {

    private final UserSession userSession;

    public LoginCheckAspect(UserSession userSession) {
        this.userSession = userSession;
    }

    @Before("execution(* jp.educure.problem1.loginauth.UserService.getUserDetails(..))")
    public void checkLogin() {
        if (!userSession.isLoggedIn()) {
            throw new IllegalStateException("User must be logged in to access this method.");
        }
    }
}
