package com.gitlab.neton.module.platform.controller.admin.stat.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 开放平台统计 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StatRespVO {

    @Schema(description = "统计ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1984")
    @ExcelProperty("统计ID")
    private Long id;

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11086")
    @ExcelProperty("客户端ID")
    private String clientId;

    @Schema(description = "API ID（为空表示客户端维度统计）", example = "13425")
    @ExcelProperty("API ID（为空表示客户端维度统计）")
    private Long apiId;

    @Schema(description = "统计日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("统计日期")
    private LocalDate statDate;

    @Schema(description = "总调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "32388")
    @ExcelProperty("总调用次数")
    private Integer totalCount;

    @Schema(description = "成功次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25039")
    @ExcelProperty("成功次数")
    private Integer successCount;

    @Schema(description = "失败次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "25747")
    @ExcelProperty("失败次数")
    private Integer failedCount;

    @Schema(description = "平均耗时（毫秒）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("平均耗时（毫秒）")
    private Integer avgDurationMs;

    @Schema(description = "最大耗时（毫秒）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("最大耗时（毫秒）")
    private Integer maxDurationMs;

    @Schema(description = "总计费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("总计费金额（分）")
    private Long totalCharge;

    @Schema(description = "免费调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "24156")
    @ExcelProperty("免费调用次数")
    private Integer freeCount;

    @Schema(description = "计费调用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "29573")
    @ExcelProperty("计费调用次数")
    private Integer chargedCount;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}