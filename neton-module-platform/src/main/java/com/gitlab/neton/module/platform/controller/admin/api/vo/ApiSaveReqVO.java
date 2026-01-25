package com.gitlab.neton.module.platform.controller.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 开放平台API定义新增/修改 Request VO")
@Data
public class ApiSaveReqVO {

    @Schema(description = "API ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17141")
    private Long id;

    @Schema(description = "API 编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "API 编码不能为空")
    private String apiCode;

    @Schema(description = "API 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "API 名称不能为空")
    private String apiName;

    @Schema(description = "API 路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "API 路径不能为空")
    private String apiPath;

    @Schema(description = "HTTP 方法", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "HTTP 方法不能为空")
    private String httpMethod;

    @Schema(description = "API 分类")
    private String category;

    @Schema(description = "API 描述", example = "你猜")
    private String description;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "每分钟限流", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "每分钟限流不能为空")
    private Integer rateLimitPerMin;

    @Schema(description = "计费类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "计费类型不能为空")
    private Integer chargeType;

    @Schema(description = "默认单价（分）", requiredMode = Schema.RequiredMode.REQUIRED, example = "19967")
    @NotNull(message = "默认单价（分）不能为空")
    private Long defaultPrice;

}