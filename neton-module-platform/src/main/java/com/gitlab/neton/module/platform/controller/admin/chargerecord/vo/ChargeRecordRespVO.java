package com.gitlab.neton.module.platform.controller.admin.chargerecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.gitlab.neton.framework.excel.core.annotations.DictFormat;
import com.gitlab.neton.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 开放平台计费记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ChargeRecordRespVO {

    @Schema(description = "计费ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17551")
    @ExcelProperty("计费ID")
    private Long id;

    @Schema(description = "客户端ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24891")
    @ExcelProperty("客户端ID")
    private String clientId;

    @Schema(description = "API ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19106")
    @ExcelProperty("API ID")
    private Long apiId;

    @Schema(description = "请求跟踪ID（关联日志）", requiredMode = Schema.RequiredMode.REQUIRED, example = "14861")
    @ExcelProperty("请求跟踪ID（关联日志）")
    private String traceId;

    @Schema(description = "计费类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "计费类型", converter = DictConvert.class)
    @DictFormat("platform_charge_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer chargeType;

    @Schema(description = "本次计费金额（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "24292")
    @ExcelProperty("本次计费金额（分）")
    private Long price;

    @Schema(description = "是否使用自定义价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "10292")
    @ExcelProperty(value = "是否使用自定义价格", converter = DictConvert.class)
    @DictFormat("platform_bool") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Boolean isCustomPrice;

    @Schema(description = "扣费前余额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("扣费前余额（分）")
    private Long balanceBefore;

    @Schema(description = "扣费后余额（分）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("扣费后余额（分）")
    private Long balanceAfter;

    @Schema(description = "是否扣费成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "是否扣费成功", converter = DictConvert.class)
    @DictFormat("platform_bool") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer chargeStatus;

    @Schema(description = "失败原因", example = "不对")
    @ExcelProperty("失败原因")
    private String failureReason;

    @Schema(description = "扣费时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("扣费时间")
    private LocalDateTime chargeTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}