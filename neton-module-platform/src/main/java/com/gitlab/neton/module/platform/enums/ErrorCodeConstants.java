package com.gitlab.neton.module.platform.enums;

import com.gitlab.neton.framework.common.exception.ErrorCode;

/**
 * Platform 错误码枚举类
 * <p>
 * platform 开放平台，使用 2-020-000-000
 */
public interface ErrorCodeConstants {

    // ========== 开放平台客户端 2-020-001-000 ==========
    ErrorCode CLIENT_NOT_EXISTS = new ErrorCode(2_020_001_000, "客户端不存在");
    ErrorCode CLIENT_DISABLED = new ErrorCode(2_020_001_001, "客户端已停用");
    ErrorCode CLIENT_EXPIRED = new ErrorCode(2_020_001_002, "客户端已过期");
    ErrorCode CLIENT_SECRET_ERROR = new ErrorCode(2_020_001_003, "客户端密钥错误");
    ErrorCode CLIENT_BALANCE_INSUFFICIENT = new ErrorCode(2_020_001_004, "客户端余额不足");

    // ========== 开放平台API定义 2-020-002-000 ==========
    ErrorCode API_NOT_EXISTS = new ErrorCode(2_020_002_000, "API 不存在");
    ErrorCode API_DISABLED = new ErrorCode(2_020_002_001, "API 已停用");
    ErrorCode API_NOT_FOUND = new ErrorCode(2_020_002_002, "未找到匹配的 API 路径");

    // ========== 签名验证 2-020-003-000 ==========
    ErrorCode MISSING_HEADER = new ErrorCode(2_020_003_000, "缺少必填 Header");
    ErrorCode INVALID_CLIENT_ID = new ErrorCode(2_020_003_001, "client_id 不存在或已停用");
    ErrorCode INVALID_TIMESTAMP = new ErrorCode(2_020_003_002, "时间戳超出有效窗口（±300秒）");
    ErrorCode REPLAY_REQUEST = new ErrorCode(2_020_003_003, "trace_id 重复，疑似重放攻击");
    ErrorCode INVALID_SIGNATURE = new ErrorCode(2_020_003_004, "签名校验失败");
    ErrorCode RATE_LIMIT_EXCEEDED = new ErrorCode(2_020_003_005, "请求频率超限");
    
    // ========== 权限控制 2-020-004-000 ==========
    ErrorCode PERMISSION_DENIED = new ErrorCode(2_020_004_000, "未授权访问该 API");
    ErrorCode PERMISSION_EXPIRED = new ErrorCode(2_020_004_001, "API 授权已过期");
    ErrorCode IP_NOT_ALLOWED = new ErrorCode(2_020_004_002, "IP 不在白名单中");

    // ========== 计费相关 2-020-005-000 ==========
    ErrorCode CHARGE_RECORD_NOT_EXISTS = new ErrorCode(2_020_005_000, "计费记录不存在");
    ErrorCode CHARGE_FAILED = new ErrorCode(2_020_005_001, "扣费失败");
    ErrorCode BALANCE_INSUFFICIENT = new ErrorCode(2_020_005_002, "余额不足，无法完成请求");

    // ========== 客户端-API授权关系 2-020-006-000 ==========
    ErrorCode CLIENT_API_NOT_EXISTS = new ErrorCode(2_020_006_000, "客户端-API 授权关系不存在");
    ErrorCode CLIENT_API_ALREADY_EXISTS = new ErrorCode(2_020_006_001, "客户端-API 授权关系已存在");

    // ========== 日志和统计 2-020-007-000 ==========
    ErrorCode LOG_NOT_EXISTS = new ErrorCode(2_020_007_000, "调用日志不存在");
    ErrorCode STAT_NOT_EXISTS = new ErrorCode(2_020_007_001, "统计数据不存在");
}
