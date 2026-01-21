package com.gitlab.neton.framework.ratelimiter.core.keyresolver;

import com.gitlab.neton.framework.ratelimiter.core.annotation.RateLimiter;
import org.aspectj.lang.JoinPoint;

/**
 * 限流 Key 解析器接口
 *
 * @author Neton
 */
public interface RateLimiterKeyResolver {

    /**
     * 解析一个 Key
     *
     * @param rateLimiter 限流注解
     * @param joinPoint  AOP 切面
     * @return Key
     */
    String resolver(JoinPoint joinPoint, RateLimiter rateLimiter);

}
