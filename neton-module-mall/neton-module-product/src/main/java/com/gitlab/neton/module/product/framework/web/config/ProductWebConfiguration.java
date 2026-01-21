package com.gitlab.neton.module.product.framework.web.config;

import com.gitlab.neton.framework.swagger.config.NetonSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * product 模块的 web 组件的 Configuration
 *
 * @author Neton
 */
@Configuration(proxyBeanMethods = false)
public class ProductWebConfiguration {

    /**
     * product 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi productGroupedOpenApi() {
        return NetonSwaggerAutoConfiguration.buildGroupedOpenApi("product");
    }

}
