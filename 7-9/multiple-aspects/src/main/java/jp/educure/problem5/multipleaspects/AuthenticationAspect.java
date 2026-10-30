package jp.educure.problem5.multipleaspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class AuthenticationAspect {

    @Before("execution(* jp.educure.problem5.multipleaspects.PaymentService.*(..))")
    public void checkAuthentication(JoinPoint joinPoint) {
        System.out.println("[認証チェック] " + joinPoint.getSignature().getName() + " を実行する前に認証を確認中...");
    }
}
