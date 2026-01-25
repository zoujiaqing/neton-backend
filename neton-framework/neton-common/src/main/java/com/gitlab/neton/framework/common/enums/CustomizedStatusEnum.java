package com.gitlab.neton.framework.common.enums;

import cn.hutool.core.util.ObjUtil;
import com.gitlab.neton.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum CustomizedStatusEnum implements ArrayValuable<Integer> {

    ENABLE(1, "开启"),
    DISABLE(0, "关闭");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(CustomizedStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    public static boolean isEnable(Integer status) {
        return ObjUtil.equal(ENABLE.status, status);
    }

    public static boolean isDisable(Integer status) {
        return ObjUtil.equal(DISABLE.status, status);
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
