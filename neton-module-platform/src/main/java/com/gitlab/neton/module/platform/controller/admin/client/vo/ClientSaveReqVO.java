package com.gitlab.neton.module.platform.controller.admin.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 开放平台客户端新增/修改 Request VO")
@Data
public class ClientSaveReqVO {

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32243")
    private Long id;

    @Schema(description = "客户端唯一标识（公开）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3569")
    @NotEmpty(message = "客户端唯一标识（公开）不能为空")
    private String clientId;

    @Schema(description = "客户端密钥（AES-256 加密存储）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "客户端密钥（AES-256 加密存储）不能为空")
    private String clientSecret;

    @Schema(description = "客户端名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "客户端名称不能为空")
    private String clientName;

    @Schema(description = "客户端编码（英文标识）")
    private String clientCode;

    @Schema(description = "客户端Logo URL")
    private String clientLogo;

    @Schema(description = "客户端描述", example = "你猜")
    private String description;

    @Schema(description = "公司名称", example = "王五")
    private String companyName;

    @Schema(description = "营业执照号")
    private String businessLicense;

    @Schema(description = "联系人姓名", example = "王五")
    private String contactName;

    @Schema(description = "联系人邮箱")
    private String contactEmail;

    @Schema(description = "联系人电话")
    private String contactPhone;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "客户端类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "客户端类型不能为空")
    private Integer clientType;

    @Schema(description = "每分钟频率限制（次/分钟）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "每分钟频率限制（次/分钟）不能为空")
    private Integer rateLimitPerMin;

    @Schema(description = "每日调用配额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "每日调用配额不能为空")
    private Integer rateLimitPerDay;

    @Schema(description = "今日已用次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "今日已用次数不能为空")
    private Integer usedCountToday;

    @Schema(description = "累计调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "23481")
    @NotNull(message = "累计调用次数不能为空")
    private Long totalUsedCount;

    @Schema(description = "账户余额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "账户余额（分）不能为空")
    private Long balance;

    @Schema(description = "累计消费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "累计消费金额（分）不能为空")
    private Long totalCharged;

    @Schema(description = "余额不足预警阈值（分，默认100元）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "余额不足预警阈值（分，默认100元）不能为空")
    private Long lowBalanceAlert;

    @Schema(description = "允许的IP白名单")
    private String allowedIps;

    @Schema(description = "回调地址（接收平台通知）", example = "https://www.iocoder.cn")
    private String webhookUrl;

    @Schema(description = "过期时间（为空表示永久有效）")
    private LocalDateTime expiredTime;

    @Schema(description = "最后调用时间")
    private LocalDateTime lastCallTime;

}