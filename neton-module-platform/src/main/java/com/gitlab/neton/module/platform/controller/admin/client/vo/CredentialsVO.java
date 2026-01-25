package com.gitlab.neton.module.platform.controller.admin.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Schema(description = "管理后台 - 管理后台客户端凭证")
@Data
@Accessors(chain = true)
public class CredentialsVO {

    @Schema(description = "客户端唯一标识")
    private String clientId;

    @Schema(description = "客户端密钥")
    private String clientSecret;
}
