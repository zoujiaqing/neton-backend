package com.gitlab.neton.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * @author Neton
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${neton.info.base-package}
@SpringBootApplication(scanBasePackages = {"${neton.info.base-package}.server", "${neton.info.base-package}.module"})
public class NetonServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetonServerApplication.class, args);
    }

}
