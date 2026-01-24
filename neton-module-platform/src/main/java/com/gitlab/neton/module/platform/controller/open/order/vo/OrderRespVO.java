package com.gitlab.neton.module.platform.controller.open.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 开放平台 - 订单 Response VO
 *
 * @author Neton
 */
@Schema(description = "开放平台 - 订单 Response VO")
@Data
public class OrderRespVO {

    @Schema(description = "订单号", example = "ORD20240108001")
    private String orderNo;

    @Schema(description = "订单金额（分）", example = "10000")
    private Long amount;

    @Schema(description = "订单状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
