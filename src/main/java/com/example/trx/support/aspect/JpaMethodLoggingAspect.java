package com.example.trx.support.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class JpaMethodLoggingAspect {
    // 1) 전체 Repository 메소드
    @Pointcut("execution(* com.tem.chain.repository..*(..))")
    public void allRepositoryMethods() {}

    // 2) 제외할 클래스의 모든 메소드
    @Pointcut("within(com.tem.chain.repository.sol.DevSolRepository)")
    public void ignoredRepositoryMethods() {}

    // 3) 제외한 실제 적용 대상
    @Pointcut("allRepositoryMethods() && !ignoredRepositoryMethods()")
    public void repositoryMethodsToLog() {}

    @Around("repositoryMethodsToLog()")
    public Object logRepositoryMethod(ProceedingJoinPoint pjp) throws Throwable {
        Class<?> targetClass = AopUtils.getTargetClass(pjp.getTarget());
        String implName = targetClass.getSimpleName();
        String methodName = pjp.getSignature().getName();
        log.info("JPA 호출: {}.{} ", implName, methodName);
        Object result=pjp.proceed();
        String returnType = (result==null ? "null" : result.getClass().getSimpleName());
        log.info("JPA Response:  {}.{} return={}", implName, methodName, returnType);
        return result;
    }
}