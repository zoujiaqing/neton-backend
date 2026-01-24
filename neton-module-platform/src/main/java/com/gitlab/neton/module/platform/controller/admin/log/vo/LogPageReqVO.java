package com.gitlab.neton.module.platform.controller.admin.log.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.gitlab.neton.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.gitlab.neton.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 开放平台调用日志分页 Request VO")
@Data
public class LogPageReqVO extends PageParam {

    @Schema(description = "客户端ID", example = "30500")
    private String clientId;

    @Schema(description = "API ID（关联 platform_api.id）", example = "24644")
    private Long apiId;

    @Schema(description = "API 编码")
    private String apiCode;

    @Schema(description = "是否成功")
    private Boolean success;

}