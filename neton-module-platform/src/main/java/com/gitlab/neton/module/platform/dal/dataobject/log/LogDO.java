package com.gitlab.neton.module.platform.dal.dataobject.log;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.gitlab.neton.framework.mybatis.core.dataobject.BaseDO;

/**
 * 开放平台调用日志 DO
 *
 * @author Neton
 */
@TableName("platform_log")
@KeySequence("platform_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogDO extends BaseDO {

    /**
     * 日志ID
     */
    @TableId
    private Long id;
    /**
     * 请求跟踪ID（对应 X-Trace-Id）
     */
    private String traceId;
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * API ID（关联 platform_api.id）
     */
    private Long apiId;
    /**
     * API 编码
     */
    private String apiCode;
    /**
     * API 路径
     */
    private String apiPath;
    /**
     * HTTP 方法
     */
    private String httpMethod;
    /**
     * 请求头（JSON）
     */
    private String requestHeaders;
    /**
     * 请求参数（JSON）
     */
    private String requestParams;
    /**
     * 请求体（JSON）
     */
    private String requestBody;
    /**
     * 请求IP
     */
    private String requestIp;
    /**
     * User-Agent
     */
    private String requestUserAgent;
    /**
     * HTTP 状态码
     */
    private Integer responseStatus;
    /**
     * 响应内容（截断，保留前 10KB）
     */
    private String responseBody;
    /**
     * 耗时（毫秒）
     */
    private Integer durationMs;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 本次计费金额（分）
     */
    private Long chargePrice;
    /**
     * 扣费状态：1=成功 2=失败（余额不足）
     */
    private Integer chargeStatus;
    /**
     * 请求时间（UTC）
     */
    private LocalDateTime requestTime;


}