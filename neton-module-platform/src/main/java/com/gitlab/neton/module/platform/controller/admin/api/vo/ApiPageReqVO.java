package com.gitlab.neton.module.platform.controller.admin.api.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.gitlab.neton.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.gitlab.neton.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 开放平台API定义分页 Request VO")
@Data
public class ApiPageReqVO extends PageParam {

    @Schema(description = "API 编码")
    private String apiCode;

    @Schema(description = "API 名称", example = "张三")
    private String apiName;

    @Schema(description = "API 分类")
    private String category;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "是否公开")
    private Boolean isPublic;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}