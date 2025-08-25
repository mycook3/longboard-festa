package com.example.trx.support.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ValidationLoggingAspect {

    // pointcut: DynamicValidator.validate(..) 호출 지점
    @Around("execution(* com.tem.chain.support.valid.DynamicValidator.validate(..))")
    public Object aroundValidate(ProceedingJoinPoint pjp) throws Throwable {
        log.info("Enter validate");
        Object result = pjp.proceed();
        log.info("Exit validate");
        return result;
    }
}