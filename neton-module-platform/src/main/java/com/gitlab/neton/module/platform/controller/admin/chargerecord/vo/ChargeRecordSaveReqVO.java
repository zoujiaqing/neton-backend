package com.gitlab.neton.module.platform.controller.admin.chargerecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 开放平台计费记录新增/修改 Request VO")
@Data
public class ChargeRecordSaveReqVO {

    @Schema(description = "计费ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17551")
    private Long id;

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24891")
    @NotEmpty(message = "客户端ID不能为空")
    private String clientId;

    @Schema(description = "API ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19106")
    @NotNull(message = "API ID不能为空")
    private Long apiId;

    @Schema(description = "请求跟踪ID（关联日志）", requiredMode = Schema.RequiredMode.REQUIRED, example = "14861")
    @NotEmpty(message = "请求跟踪ID（关联日志）不能为空")
    private String traceId;

    @Schema(description = "计费类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "计费类型不能为空")
    private Integer chargeType;

    @Schema(description = "本次计费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "24292")
    @NotNull(message = "本次计费金额（分）不能为空")
    private Long price;

    @Schema(description = "是否使用自定义价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "10292")
    @NotNull(message = "是否使用自定义价格不能为空")
    private Boolean isCustomPrice;

    @Schema(description = "扣费前余额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "扣费前余额（分）不能为空")
    private Long balanceBefore;

    @Schema(description = "扣费后余额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "扣费后余额（分）不能为空")
    private Long balanceAfter;

    @Schema(description = "是否扣费成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "是否扣费成功不能为空")
    private Integer chargeStatus;

    @Schema(description = "失败原因", example = "不对")
    private String failureReason;

    @Schema(description = "扣费时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "扣费时间不能为空")
    private LocalDateTime chargeTime;

}