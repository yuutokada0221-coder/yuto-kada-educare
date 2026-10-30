package jp.educure.problem3.exceptionhandling;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandlingAspect {

    @AfterThrowing(
            pointcut = "execution(* jp.educure.problem3.exceptionhandling.UsermanagementService.createUser(..))",
            throwing = "ex")
    public void handleException(Exception ex) {
        System.out.println("Exception while saving user: " + ex.getMessage());
    }
}
