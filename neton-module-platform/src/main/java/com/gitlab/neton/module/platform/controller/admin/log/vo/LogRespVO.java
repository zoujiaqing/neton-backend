package com.gitlab.neton.module.platform.controller.admin.log.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 开放平台调用日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class LogRespVO {

    @Schema(description = "日志ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11096")
    @ExcelProperty("日志ID")
    private Long id;

    @Schema(description = "请求跟踪ID（对应 X-Trace-Id）", requiredMode = Schema.RequiredMode.REQUIRED, example = "21114")
    @ExcelProperty("请求跟踪ID（对应 X-Trace-Id）")
    private String traceId;

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30500")
    @ExcelProperty("客户端ID")
    private String clientId;

    @Schema(description = "API ID（关联 platform_api.id）", example = "24644")
    @ExcelProperty("API ID（关联 platform_api.id）")
    private Long apiId;

    @Schema(description = "API 编码")
    @ExcelProperty("API 编码")
    private String apiCode;

    @Schema(description = "API 路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("API 路径")
    private String apiPath;

    @Schema(description = "HTTP 方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("HTTP 方法")
    private String httpMethod;

    @Schema(description = "请求头（JSON）")
    @ExcelProperty("请求头（JSON）")
    private String requestHeaders;

    @Schema(description = "请求参数（JSON）")
    @ExcelProperty("请求参数（JSON）")
    private String requestParams;

    @Schema(description = "请求体（JSON）")
    @ExcelProperty("请求体（JSON）")
    private String requestBody;

    @Schema(description = "请求IP")
    @ExcelProperty("请求IP")
    private String requestIp;

    @Schema(description = "User-Agent")
    @ExcelProperty("User-Agent")
    private String requestUserAgent;

    @Schema(description = "HTTP 状态码", example = "2")
    @ExcelProperty("HTTP 状态码")
    private Integer responseStatus;

    @Schema(description = "响应内容（截断，保留前 10KB）")
    @ExcelProperty("响应内容（截断，保留前 10KB）")
    private String responseBody;

    @Schema(description = "耗时（毫秒）")
    @ExcelProperty("耗时（毫秒）")
    private Integer durationMs;

    @Schema(description = "是否成功", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否成功")
    private Boolean success;

    @Schema(description = "错误码")
    @ExcelProperty("错误码")
    private String errorCode;

    @Schema(description = "错误信息")
    @ExcelProperty("错误信息")
    private String errorMsg;

    @Schema(description = "本次计费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "25044")
    @ExcelProperty("本次计费金额（分）")
    private Long chargePrice;

    @Schema(description = "扣费状态：1=成功 2=失败（余额不足）", example = "2")
    @ExcelProperty("扣费状态：1=成功 2=失败（余额不足）")
    private Integer chargeStatus;

    @Schema(description = "请求时间（UTC）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("请求时间（UTC）")
    private LocalDateTime requestTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}