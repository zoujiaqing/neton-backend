package com.gitlab.neton.module.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 扣费状态枚举
 *
 * @author Neton
 */
@Getter
@AllArgsConstructor
public enum ChargeStatusEnum {

    SUCCESS(1, "成功"),
    FAILED(2, "失败（余额不足）");

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

}
