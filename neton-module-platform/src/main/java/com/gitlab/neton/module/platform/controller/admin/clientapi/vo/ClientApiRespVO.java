package com.gitlab.neton.module.platform.controller.admin.clientapi.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.gitlab.neton.framework.excel.core.annotations.DictFormat;
import com.gitlab.neton.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 客户端-API授权关系表（含自定义定价） Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClientApiRespVO {

    @Schema(description = "关系ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24294")
    @ExcelProperty("关系ID")
    private Long id;

    @Schema(description = "客户端", requiredMode = Schema.RequiredMode.REQUIRED, example = "5926")
    @ExcelProperty("客户端")
    private String clientId;

    @Schema(description = "API ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31650")
    @ExcelProperty("API ID")
    private Long apiId;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "是否启用", converter = DictConvert.class)
    @DictFormat("platform_bool") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "每分钟限流（覆盖 API 默认配置）")
    @ExcelProperty("每分钟限流（覆盖 API 默认配置）")
    private Integer rateLimitPerMin;

    @Schema(description = "每日配额（覆盖客户端默认配置）")
    @ExcelProperty("每日配额（覆盖客户端默认配置）")
    private Integer rateLimitPerDay;

    @Schema(description = "是否自定义价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "1715")
    @ExcelProperty(value = "是否自定义价格", converter = DictConvert.class)
    @DictFormat("platform_bool") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Boolean isCustomPrice;

    @Schema(description = "自定义价格（分，仅当 is_custom_price=1 时有效）", example = "25762")
    @ExcelProperty("自定义价格（分，仅当 is_custom_price=1 时有效）")
    private Long customPrice;

    @Schema(description = "授权开始时间")
    @ExcelProperty("授权开始时间")
    private LocalDateTime startTime;

    @Schema(description = "授权结束时间（为空表示永久）")
    @ExcelProperty("授权结束时间（为空表示永久）")
    private LocalDateTime endTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}