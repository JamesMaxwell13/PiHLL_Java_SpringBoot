package com.sharesapp.backend.aspect;

import com.sharesapp.backend.service.RequestCounterService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class RequestCountingAspect {
  RequestCounterService requestCounterService;

  RequestCountingAspect(RequestCounterService requestCounterService) {
    this.requestCounterService = requestCounterService;
  }

  @Around(
      "@within(com.sharesapp.backend.aspect.annotation.RequestCounting) ||"
          + "@annotation(com.sharesapp.backend.aspect.annotation.RequestCounting)")
  public Object incrementRequestCounter(ProceedingJoinPoint joinPoint) throws Throwable {
    requestCounterService.increment();
    return joinPoint.proceed();
  }
}