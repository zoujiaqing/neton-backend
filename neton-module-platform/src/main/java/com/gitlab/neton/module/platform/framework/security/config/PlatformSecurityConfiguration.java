package com.gitlab.neton.module.platform.framework.security.config;

import com.gitlab.neton.framework.security.config.AuthorizeRequestsCustomizer;
import com.gitlab.neton.module.platform.framework.security.filter.PlatformApiSignatureFilter;
import com.gitlab.neton.module.platform.service.auth.PlatformAuthService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


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
     * 创建 Platform API 签名验证过滤器
     */
    @Bean
    public PlatformApiSignatureFilter platformApiSignatureFilter() {
        return new PlatformApiSignatureFilter(platformAuthService);
    }

    /**
     * 配置 /platform-api/** 路径的权限
     */
    @Bean("platformAuthorizeRequestsCustomizer")
    public AuthorizeRequestsCustomizer authorizeRequestsCustomizer(PlatformApiSignatureFilter platformApiSignatureFilter) {
        return new AuthorizeRequestsCustomizer() {

            @Override
            public void customize(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
                // /platform-api/** 路径：允许匿名访问，但会被 PlatformApiSignatureFilter 拦截验证
                registry.requestMatchers("/platform-api/**").permitAll();
            }

            //            @Override
            public void customize(HttpSecurity httpSecurity) {
                // 在 UsernamePasswordAuthenticationFilter 之前添加 PlatformApiSignatureFilter
                httpSecurity.addFilterBefore(platformApiSignatureFilter, UsernamePasswordAuthenticationFilter.class);
            }
        };
    }

}
