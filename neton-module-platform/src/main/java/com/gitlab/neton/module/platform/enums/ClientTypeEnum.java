package com.gitlab.neton.module.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户端类型枚举
 *
 * @author Neton
 */
@Getter
@AllArgsConstructor
public enum ClientTypeEnum {

    ENTERPRISE(1, "企业应用"),
    PERSONAL(2, "个人应用"),
    INTERNAL(3, "内部应用");

    /**
     * 类型值
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

}
