package jp.educure.problem3.eventperiod;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = EventPeriodValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventPeriod {

    String message() default "イベント申し込み期間が正しくありません";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
