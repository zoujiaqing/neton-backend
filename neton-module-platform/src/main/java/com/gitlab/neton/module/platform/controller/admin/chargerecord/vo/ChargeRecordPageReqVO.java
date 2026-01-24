package com.gitlab.neton.module.platform.controller.admin.chargerecord.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import com.gitlab.neton.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static com.gitlab.neton.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 开放平台计费记录分页 Request VO")
@Data
public class ChargeRecordPageReqVO extends PageParam {

    @Schema(description = "客户端ID", example = "24891")
    private String clientId;

    @Schema(description = "API ID", example = "19106")
    private Long apiId;

    @Schema(description = "请求跟踪ID（关联日志）", example = "14861")
    private String traceId;

    @Schema(description = "是否扣费成功", example = "1")
    private Integer chargeStatus;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}