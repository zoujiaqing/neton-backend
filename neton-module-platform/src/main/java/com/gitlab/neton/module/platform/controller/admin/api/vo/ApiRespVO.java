package com.gitlab.neton.module.platform.controller.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import com.gitlab.neton.framework.excel.core.annotations.DictFormat;
import com.gitlab.neton.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 开放平台API定义 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ApiRespVO {

    @Schema(description = "API ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17141")
    @ExcelProperty("API ID")
    private Long id;

    @Schema(description = "API 编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("API 编码")
    private String apiCode;

    @Schema(description = "API 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("API 名称")
    private String apiName;

    @Schema(description = "API 路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("API 路径")
    private String apiPath;

    @Schema(description = "HTTP 方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("HTTP 方法")
    private String httpMethod;

    @Schema(description = "API 分类")
    @ExcelProperty("API 分类")
    private String category;

    @Schema(description = "API 描述", example = "你猜")
    @ExcelProperty("API 描述")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("platform_client_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "是否公开", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "是否公开", converter = DictConvert.class)
    @DictFormat("platform_bool") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Boolean isPublic;

    @Schema(description = "每分钟限流", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("每分钟限流")
    private Integer rateLimitPerMin;

    @Schema(description = "计费类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "计费类型", converter = DictConvert.class)
    @DictFormat("platform_charge_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer chargeType;

    @Schema(description = "默认单价（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "19967")
    @ExcelProperty("默认单价（分）")
    private Long defaultPrice;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}