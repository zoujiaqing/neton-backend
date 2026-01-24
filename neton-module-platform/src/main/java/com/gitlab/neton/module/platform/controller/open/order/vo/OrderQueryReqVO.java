package com.gitlab.neton.module.platform.controller.open.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 开放平台 - 订单查询 Request VO
 *
 * @author Neton
 */
@Schema(description = "开放平台 - 订单查询 Request VO")
@Data
public class OrderQueryReqVO {

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORD20240108001")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

}
