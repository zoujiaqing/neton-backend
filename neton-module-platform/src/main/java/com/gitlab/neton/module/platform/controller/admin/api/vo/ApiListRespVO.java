package com.gitlab.neton.module.platform.controller.admin.api.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class ApiListRespVO implements Serializable {
    private static final long serialVersionUID = 1831253633202934800L;

    @Schema(description = "API ID")
    private Long id;

    @Schema(description = "API 编码")
    private String apiCode;

    @Schema(description = "API 名称")
    private String apiName;

    @Schema(description = "是否选中")
    private Integer selected;
}
