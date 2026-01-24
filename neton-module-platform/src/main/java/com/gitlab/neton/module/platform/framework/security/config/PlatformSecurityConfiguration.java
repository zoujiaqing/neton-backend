package com.gitlab.neton.module.platform.framework.security.config;

import com.gitlab.neton.framework.security.config.AuthorizeRequestsCustomizer;
import com.gitlab.neton.framework.security.core.filter.OpenApiSignatureFilter;
import com.gitlab.neton.module.platform.service.auth.PlatformAuthService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Platform 模块的 Security 配置
 *
 * @author Neton
 */
@AutoConfiguration
public class PlatformSecurityConfiguration {

    @Resource
    private PlatformAuthService platformAuthService;

    /**
     * 创建 OpenAPI 签名验证过滤器
     */
    @Bean
    public OpenApiSignatureFilter openApiSignatureFilter() {
        return new OpenApiSignatureFilter(platformAuthService);
    }

    /**
     * 配置 /open-api/** 路径的权限
     */
    @Bean("platformAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer(OpenApiSignatureFilter openApiSignatureFilter) {
        return new AuthorizeRequestsCustomizer() {

            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // /open-api/** 路径：允许匿名访问，但会被 OpenApiSignatureFilter 拦截验证
                registry.requestMatchers("/open-api/**").permitAll();
            }

            @Override
            public void customize(HttpSecurity httpSecurity) {
                // 在 UsernamePasswordAuthenticationFilter 之前添加 OpenApiSignatureFilter
                httpSecurity.addFilterBefore(openApiSignatureFilter, UsernamePasswordAuthenticationFilter.class);
            }
        };
    }

}
