package org.learn.open.monitor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @Author: jack-yu
 * @Description:  线程池监控注解
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ThreadPoolMonitorAnnotation {

     String threadPoolName() default "threadPoolName";

}
