package com.gitlab.neton.module.infra.framework.file.config;

import com.gitlab.neton.module.infra.framework.file.core.client.FileClientFactory;
import com.gitlab.neton.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author Neton
 */
@Configuration(proxyBeanMethods = false)
public class NetonFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
