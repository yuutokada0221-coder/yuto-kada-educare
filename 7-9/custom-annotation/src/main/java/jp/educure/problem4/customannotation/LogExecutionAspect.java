package jp.educure.problem4.customannotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionAspect {

    @Around("@annotation(jp.educure.problem4.customannotation.LogExecution)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("[LOG] 開始: " + joinPoint.getSignature());
        try {
            return joinPoint.proceed();
        } finally {
            System.out.println("[LOG] 終了: " + joinPoint.getSignature());
        }
    }
}
