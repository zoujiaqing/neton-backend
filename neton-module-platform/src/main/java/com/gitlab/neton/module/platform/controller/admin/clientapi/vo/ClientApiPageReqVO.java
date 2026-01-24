package com.gitlab.neton.module.platform.controller.admin.clientapi.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.gitlab.neton.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.gitlab.neton.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 客户端-API授权关系表（含自定义定价）分页 Request VO")
@Data
public class ClientApiPageReqVO extends PageParam {

    @Schema(description = "客户端", example = "5926")
    private String clientId;

    @Schema(description = "API ID", example = "31650")
    private Long apiId;

    @Schema(description = "是否启用", example = "1")
    private Integer status;

}