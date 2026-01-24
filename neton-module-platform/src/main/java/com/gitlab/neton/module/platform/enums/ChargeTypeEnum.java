package com.gitlab.neton.module.platform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计费类型枚举
 *
 * @author Neton
 */
@Getter
@AllArgsConstructor
public enum ChargeTypeEnum {

    FREE(0, "免费"),
    PER_CALL(1, "按次计费"),
    PER_VOLUME(2, "按量计费");

    /**
     * 类型值
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

}
