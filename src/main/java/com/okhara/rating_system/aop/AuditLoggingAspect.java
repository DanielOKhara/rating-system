package com.okhara.rating_system.aop;

import com.okhara.rating_system.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLoggingAspect {

    private final AuthenticationFacade authenticationFacade;

    @Before("@annotation(AuditLoggable)")
    public void logBefore(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        String params = args.length > 0 ? " with params: " + java.util.Arrays.toString(args) : "";
        log.info("Admin \"{}\" execute {} method{}", authenticationFacade.getCurrentUser().getNickname(),
                joinPoint.getSignature().getName(), params);
    }

    @AfterThrowing(pointcut = "@annotation(AuditLoggable)", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        log.error("Admin \"{}\" failed to execute {} method. Error: {}",
                authenticationFacade.getCurrentUser().getNickname(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }
}
