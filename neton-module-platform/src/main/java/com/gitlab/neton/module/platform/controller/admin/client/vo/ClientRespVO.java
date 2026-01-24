package com.gitlab.neton.module.platform.controller.admin.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.gitlab.neton.framework.excel.core.annotations.DictFormat;
import com.gitlab.neton.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 开放平台客户端 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClientRespVO {

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32243")
    @ExcelProperty("客户端ID")
    private Long id;

    @Schema(description = "客户端唯一标识（公开）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3569")
    @ExcelProperty("客户端唯一标识（公开）")
    private String clientId;

    @Schema(description = "客户端密钥（AES-256 加密存储）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("客户端密钥（AES-256 加密存储）")
    private String clientSecret;

    @Schema(description = "客户端名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("客户端名称")
    private String clientName;

    @Schema(description = "客户端编码（英文标识）")
    @ExcelProperty("客户端编码（英文标识）")
    private String clientCode;

    @Schema(description = "客户端Logo URL")
    @ExcelProperty("客户端Logo URL")
    private String clientLogo;

    @Schema(description = "客户端描述", example = "你猜")
    @ExcelProperty("客户端描述")
    private String description;

    @Schema(description = "公司名称", example = "王五")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "营业执照号")
    @ExcelProperty("营业执照号")
    private String businessLicense;

    @Schema(description = "联系人姓名", example = "王五")
    @ExcelProperty("联系人姓名")
    private String contactName;

    @Schema(description = "联系人邮箱")
    @ExcelProperty("联系人邮箱")
    private String contactEmail;

    @Schema(description = "联系人电话")
    @ExcelProperty("联系人电话")
    private String contactPhone;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("platform_client_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "客户端类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "客户端类型", converter = DictConvert.class)
    @DictFormat("platform_client_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer clientType;

    @Schema(description = "每分钟频率限制（次/分钟）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("每分钟频率限制（次/分钟）")
    private Integer rateLimitPerMin;

    @Schema(description = "每日调用配额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("每日调用配额")
    private Integer rateLimitPerDay;

    @Schema(description = "今日已用次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("今日已用次数")
    private Integer usedCountToday;

    @Schema(description = "累计调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "23481")
    @ExcelProperty("累计调用次数")
    private Long totalUsedCount;

    @Schema(description = "账户余额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("账户余额（分）")
    private Long balance;

    @Schema(description = "累计消费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("累计消费金额（分）")
    private Long totalCharged;

    @Schema(description = "余额不足预警阈值（分，默认100元）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("余额不足预警阈值（分，默认100元）")
    private Long lowBalanceAlert;

    @Schema(description = "允许的IP白名单")
    @ExcelProperty("允许的IP白名单")
    private String allowedIps;

    @Schema(description = "回调地址（接收平台通知）", example = "https://www.iocoder.cn")
    @ExcelProperty("回调地址（接收平台通知）")
    private String webhookUrl;

    @Schema(description = "过期时间（为空表示永久有效）")
    @ExcelProperty("过期时间（为空表示永久有效）")
    private LocalDateTime expiredTime;

    @Schema(description = "最后调用时间")
    @ExcelProperty("最后调用时间")
    private LocalDateTime lastCallTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}