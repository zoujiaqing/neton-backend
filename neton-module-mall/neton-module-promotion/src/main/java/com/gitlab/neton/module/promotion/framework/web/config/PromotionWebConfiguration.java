package com.gitlab.neton.module.promotion.framework.web.config;

import com.gitlab.neton.framework.swagger.config.NetonSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * promotion 模块的 web 组件的 Configuration
 *
 * @author Neton
 */
@Configuration(proxyBeanMethods = false)
public class PromotionWebConfiguration {

    /**
     * promotion 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi promotionGroupedOpenApi() {
        return NetonSwaggerAutoConfiguration.buildGroupedOpenApi("promotion");
    }

}
