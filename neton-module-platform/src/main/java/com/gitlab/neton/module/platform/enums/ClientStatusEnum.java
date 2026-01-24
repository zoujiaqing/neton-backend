package com.gitlab.neton.module.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客户端状态枚举
 *
 * @author Neton
 */
@Getter
@AllArgsConstructor
public enum ClientStatusEnum {

    DISABLED(0, "停用"),
    NORMAL(1, "正常"),
    PENDING(2, "待审核");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

}
