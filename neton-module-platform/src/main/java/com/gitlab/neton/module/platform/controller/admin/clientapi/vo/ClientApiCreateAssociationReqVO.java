package com.gitlab.neton.module.platform.controller.admin.clientapi.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ClientApiCreateAssociationReqVO implements Serializable {
    private static final long serialVersionUID = 5250528435457632773L;

    @Schema(description = "客户端", example = "31650")
    private String clientId;

    @Schema(description = "API ID", example = "31650")
    private List<Long> apiIdList;
}
