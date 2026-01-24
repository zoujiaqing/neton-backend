package com.gitlab.neton.module.platform.service.auth;

import com.gitlab.neton.module.platform.controller.admin.client.vo.ClientRespVO;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;

import java.util.Map;

/**
 * 开放平台鉴权 Service 接口
 *
 * @author Neton
 */
public interface PlatformAuthService {

    /**
     * 验证客户端签名
     *
     * @param clientId 客户端 ID
     * @param timestamp 时间戳（秒）
     * @param traceId 请求跟踪 ID
     * @param sign 客户端签名
     * @param allParams 所有参与签名的参数（已展开）
     * @return 验证通过返回客户端信息
     */
    ClientRespVO validateSignature(String clientId, Long timestamp, String traceId, 
                                    String sign, Map<String, String> allParams);

    /**
     * 检查客户端是否有权限访问指定 API
     *
     * @param clientId 客户端 ID
     * @param apiId API ID
     * @param requestIp 请求 IP
     * @return true=有权限
     */
    boolean hasApiPermission(String clientId, Long apiId, String requestIp);

    /**
     * 根据路径和方法查找 API
     *
     * @param apiPath API 路径（业务路径，如 /order/create）
     * @param httpMethod HTTP 方法
     * @return API 对象
     */
    ApiDO getApiByPathAndMethod(String apiPath, String httpMethod);

    /**
     * 检查 Trace-Id 是否重复（防重放）
     *
     * @param clientId 客户端 ID
     * @param traceId 请求跟踪 ID
     * @return true=重复
     */
    boolean isTraceIdDuplicated(String clientId, String traceId);

    /**
     * 记录 Trace-Id 到 Redis（TTL=300秒）
     *
     * @param clientId 客户端 ID
     * @param traceId 请求跟踪 ID
     */
    void recordTraceId(String clientId, String traceId);

}
