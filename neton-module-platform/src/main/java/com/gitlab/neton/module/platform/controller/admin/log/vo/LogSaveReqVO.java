package com.gitlab.neton.module.platform.controller.admin.log.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 开放平台调用日志新增/修改 Request VO")
@Data
public class LogSaveReqVO {

    @Schema(description = "日志ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11096")
    private Long id;

    @Schema(description = "请求跟踪ID（对应 X-Trace-Id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "21114")
    @NotEmpty(message = "请求跟踪ID（对应 X-Trace-Id）不能为空")
    private String traceId;

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30500")
    @NotEmpty(message = "客户端ID不能为空")
    private String clientId;

    @Schema(description = "API ID（关联 platform_api.id）", example = "24644")
    private Long apiId;

    @Schema(description = "API 编码")
    private String apiCode;

    @Schema(description = "API 路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "API 路径不能为空")
    private String apiPath;

    @Schema(description = "HTTP 方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "HTTP 方法不能为空")
    private String httpMethod;

    @Schema(description = "请求头（JSON）")
    private String requestHeaders;

    @Schema(description = "请求参数（JSON）")
    private String requestParams;

    @Schema(description = "请求体（JSON）")
    private String requestBody;

    @Schema(description = "请求IP")
    private String requestIp;

    @Schema(description = "User-Agent")
    private String requestUserAgent;

    @Schema(description = "HTTP 状态码", example = "2")
    private Integer responseStatus;

    @Schema(description = "响应内容（截断，保留前 10KB）")
    private String responseBody;

    @Schema(description = "耗时（毫秒）")
    private Integer durationMs;

    @Schema(description = "是否成功", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否成功不能为空")
    private Boolean success;

    @Schema(description = "错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "本次计费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25044")
    @NotNull(message = "本次计费金额（分）不能为空")
    private Long chargePrice;

    @Schema(description = "扣费状态：1=成功 2=失败（余额不足）", example = "2")
    private Integer chargeStatus;

    @Schema(description = "请求时间（UTC）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请求时间（UTC）不能为空")
    private LocalDateTime requestTime;

}