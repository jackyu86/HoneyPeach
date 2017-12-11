package org.learn.open.web.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Aspect
@Service
@Order(-5)
public class APILogAspect {
	private Logger logger =  LoggerFactory.getLogger(this.getClass());
	ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	/**
     * 通配符说明
     *  第一个 * 代表任意修饰符及任意返回值.
     *  第二个 * 任意包名
     *  第三个 * 代表任意方法.
     *  第四个 * 定义在web包或者子包
     *  第五个 * 任意方法
     *  .. 匹配任意数量的参数.
     */
    @Pointcut("execution(public * org.osidocker.open.*.controller..*.*(..))")
    public void webLog(){}
     
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        startTime.set(System.currentTimeMillis());
    }
     
    @AfterReturning("webLog()")
    public void doAfterReturning(JoinPoint joinPoint){
       // 处理完请求，返回内容
        logger.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName()+"->"+"ARGS : " + Arrays.toString(joinPoint.getArgs())+"耗时（毫秒） : " + (System.currentTimeMillis() - startTime.get()));
    }
}
