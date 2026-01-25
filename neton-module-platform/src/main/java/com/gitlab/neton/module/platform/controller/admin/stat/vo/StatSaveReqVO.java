package com.gitlab.neton.module.platform.controller.admin.stat.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "管理后台 - 开放平台统计新增/修改 Request VO")
@Data
public class StatSaveReqVO {

    @Schema(description = "统计ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1984")
    private Long id;

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11086")
    @NotEmpty(message = "客户端ID不能为空")
    private String clientId;

    @Schema(description = "API ID（为空表示客户端维度统计）", example = "13425")
    private Long apiId;

    @Schema(description = "统计日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "统计日期不能为空")
    private LocalDate statDate;

    @Schema(description = "总调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "32388")
    @NotNull(message = "总调用次数不能为空")
    private Integer totalCount;

    @Schema(description = "成功次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25039")
    @NotNull(message = "成功次数不能为空")
    private Integer successCount;

    @Schema(description = "失败次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25747")
    @NotNull(message = "失败次数不能为空")
    private Integer failedCount;

    @Schema(description = "平均耗时（毫秒）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "平均耗时（毫秒）不能为空")
    private Integer avgDurationMs;

    @Schema(description = "最大耗时（毫秒）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "最大耗时（毫秒）不能为空")
    private Integer maxDurationMs;

    @Schema(description = "总计费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总计费金额（分）不能为空")
    private Long totalCharge;

    @Schema(description = "免费调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "24156")
    @NotNull(message = "免费调用次数不能为空")
    private Integer freeCount;

    @Schema(description = "计费调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "29573")
    @NotNull(message = "计费调用次数不能为空")
    private Integer chargedCount;

}