package com.tongji.exam.annotation;

import java.lang.annotation.*;

/**
 * created by kz on
 * @author kk
 */

@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented
public @interface ApiCallMonitor {
    String value() default "普通接口";
    int limit() default -1;
    String type() default "user";
    int expire() default 300;
    String specialHandle() default "no";
}
