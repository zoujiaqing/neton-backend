package com.gitlab.neton.framework.security.core.authentication;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * OpenAPI 认证对象
 * <p>
 * 用于存储开放平台客户端认证信息，包括 client_id 和 api_code
 *
 * @author Neton
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenApiAuthentication extends AbstractAuthenticationToken {

    /**
     * 客户端 ID（对应 principal）
     */
    private final String clientId;

    /**
     * API 编码（权限标识，如：platform:order:create）
     */
    private final String apiCode;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * Trace ID（请求跟踪）
     */
    private String traceId;

    /**
     * 请求 IP
     */
    private String requestIp;

    /**
     * 构造函数
     *
     * @param clientId 客户端 ID
     * @param apiCode API 编码
     */
    public OpenApiAuthentication(String clientId, String apiCode) {
        super(null);
        this.clientId = clientId;
        this.apiCode = apiCode;
        setAuthenticated(true); // 由 Filter 验证后设置为已认证
    }

    /**
     * 构造函数（带权限）
     *
     * @param clientId 客户端 ID
     * @param apiCode API 编码
     * @param authorities 权限集合
     */
    public OpenApiAuthentication(String clientId, String apiCode, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.clientId = clientId;
        this.apiCode = apiCode;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // OpenAPI 不使用密码凭证
    }

    @Override
    public Object getPrincipal() {
        return this.clientId;
    }

    @Override
    public String getName() {
        return this.clientId;
    }

}
