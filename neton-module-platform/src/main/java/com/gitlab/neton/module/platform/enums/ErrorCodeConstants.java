package com.gitlab.neton.module.platform.enums;

import com.gitlab.neton.framework.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 * <p>
 * system 系统，使用 2-001-000-000
 */
public interface ErrorCodeConstants {

    // ========== 开放平台客户端 TODO 补充编号 ==========
    ErrorCode CLIENT_NOT_EXISTS = new ErrorCode(2_001_000_000, "开放平台客户端不存在");

    // ========== 开放平台API定义 TODO 补充编号 ==========
    ErrorCode API_NOT_EXISTS = new ErrorCode(2_002_000_000, "开放平台API定义不存在");

    // ========== 开放平台计费记录 TODO 补充编号 ==========
    ErrorCode CHARGE_RECORD_NOT_EXISTS = new ErrorCode(2_003_000_000, "开放平台计费记录不存在");

    // ========== 客户端-API授权关系表（含自定义定价） TODO 补充编号 ==========
    ErrorCode CLIENT_API_NOT_EXISTS = new ErrorCode(2_004_000_000, "客户端-API授权关系表（含自定义定价）不存在");

    // ========== 开放平台调用日志 TODO 补充编号 ==========
    ErrorCode LOG_NOT_EXISTS = new ErrorCode(2_005_000_000, "开放平台调用日志不存在");

    // ========== 开放平台统计 TODO 补充编号 ==========
    ErrorCode STAT_NOT_EXISTS = new ErrorCode(2_006_000_000, "开放平台统计不存在");
}
