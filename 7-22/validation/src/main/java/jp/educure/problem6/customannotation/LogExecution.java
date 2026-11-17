package jp.educure.problem6.customannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 実行中にアノテーションを読み取れるようにする
@Target(ElementType.METHOD)         // メソッドに付けられるようにする
public @interface LogExecution {
}
