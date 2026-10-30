package jp.educure.problem5.multipleaspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(3)
public class PaymentExecutionTimeAspect {

    @Around("execution(* jp.educure.problem5.multipleaspects.PaymentService.*(..))")
    public Object measure(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("[実行時間] " + joinPoint.getSignature().getName() + ": " + elapsed + "ms");
        }
    }
}
