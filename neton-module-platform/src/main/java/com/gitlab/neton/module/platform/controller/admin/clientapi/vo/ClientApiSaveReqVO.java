package com.gitlab.neton.module.platform.controller.admin.clientapi.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 客户端-API授权关系表（含自定义定价）新增/修改 Request VO")
@Data
public class ClientApiSaveReqVO {

    @Schema(description = "关系ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24294")
    private Long id;

    @Schema(description = "客户端", requiredMode = Schema.RequiredMode.REQUIRED, example = "5926")
    @NotEmpty(message = "客户端不能为空")
    private String clientId;

    @Schema(description = "API ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31650")
    @NotNull(message = "API ID不能为空")
    private Long apiId;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "是否启用不能为空")
    private Integer status;

    @Schema(description = "每分钟限流（覆盖 API 默认配置）")
    private Integer rateLimitPerMin;

    @Schema(description = "每日配额（覆盖客户端默认配置）")
    private Integer rateLimitPerDay;

    @Schema(description = "是否自定义价格", requiredMode = Schema.RequiredMode.REQUIRED, example = "1715")
    @NotNull(message = "是否自定义价格不能为空")
    private Boolean isCustomPrice;

    @Schema(description = "自定义价格（分，仅当 is_custom_price=1 时有效）", example = "25762")
    private Long customPrice;

    @Schema(description = "授权开始时间")
    private LocalDateTime startTime;

    @Schema(description = "授权结束时间（为空表示永久）")
    private LocalDateTime endTime;

}