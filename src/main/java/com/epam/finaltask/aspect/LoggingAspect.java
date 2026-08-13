package com.epam.finaltask.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //Interception Point: Intercepting Calls in All Services and REST Controllers
    @Pointcut("within(com.epam.finaltask.service..*) || within(com.epam.finaltask.restcontroller..*)")
    public void applicationPackagePointcut() {
    }

    // Logging before method execution (level DEBUG)
    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        if (log.isDebugEnabled()) {
            log.debug("Enter: {}.{}() with argument[s] = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    Arrays.toString(joinPoint.getArgs()));
        }
    }

    // Logging the successful completion of a method (level DEBUG)
    @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("Exit: {}.{}() with result = {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    result);
        }
    }

    // Logging of Thrown Exceptions (level ERROR)
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = '{}' and exception = '{}'",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage());
    }
}