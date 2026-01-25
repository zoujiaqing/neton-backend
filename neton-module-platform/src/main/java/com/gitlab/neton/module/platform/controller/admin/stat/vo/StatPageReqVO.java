package com.gitlab.neton.module.platform.controller.admin.stat.vo;

import com.gitlab.neton.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.gitlab.neton.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 开放平台统计分页 Request VO")
@Data
public class StatPageReqVO extends PageParam {

    @Schema(description = "客户端ID", example = "11086")
    private String clientId;

    @Schema(description = "API ID（为空表示客户端维度统计）", example = "13425")
    private Long apiId;

    @Schema(description = "统计日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] statDate;

}