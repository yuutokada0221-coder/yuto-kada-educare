package jp.educure.problem6.customannotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogExecutionAspect {

    @Around("@annotation(jp.educure.problem6.customannotation.LogExecution)")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toString();
        System.out.println("[LOG] 開始: " + signature);
        Object result = joinPoint.proceed();
        System.out.println("[LOG] 終了: " + signature);
        return result;
    }
}
