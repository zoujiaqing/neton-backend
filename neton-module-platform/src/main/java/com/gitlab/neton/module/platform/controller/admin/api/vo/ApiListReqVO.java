package com.gitlab.neton.module.platform.controller.admin.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApiListReqVO implements Serializable {
    private static final long serialVersionUID = -27486015088509254L;

    @Schema(description = "客户端ID")
    @NotNull(message = "客户端ID不能为空")
    private Long clientId;

    @Schema(description = "API名称", example = "张三")
    private String apiName;
}
