package com.gitlab.neton.module.platform.framework.security.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.gitlab.neton.framework.common.exception.ServiceException;
import com.gitlab.neton.framework.common.pojo.CommonResult;
import com.gitlab.neton.framework.common.util.servlet.ServletUtils;
import com.gitlab.neton.framework.security.core.authentication.OpenApiAuthentication;
import com.gitlab.neton.module.platform.controller.admin.client.vo.ClientRespVO;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import com.gitlab.neton.module.platform.service.auth.PlatformAuthService;
import com.gitlab.neton.module.platform.util.SignatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * OpenAPI 签名验证过滤器
 * <p>
 * 拦截 /open-api/** 请求，验证签名和权限
 *
 * @author Neton
 */
@Slf4j
public class OpenApiSignatureFilter extends OncePerRequestFilter {

    private final PlatformAuthService platformAuthService;

    public OpenApiSignatureFilter(PlatformAuthService platformAuthService) {
        this.platformAuthService = platformAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 只处理 /open-api/** 路径
        String requestUri = request.getRequestURI();
        if (!requestUri.startsWith("/open-api/")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // 1. 提取并验证 Header
            String clientId = request.getHeader(SignatureUtil.HEADER_CLIENT_ID);
            String timestampStr = request.getHeader(SignatureUtil.HEADER_TIMESTAMP);
            String traceId = request.getHeader(SignatureUtil.HEADER_TRACE_ID);
            String sign = request.getHeader(SignatureUtil.HEADER_SIGN);

            if (StrUtil.isBlank(clientId) || StrUtil.isBlank(timestampStr) 
                    || StrUtil.isBlank(traceId) || StrUtil.isBlank(sign)) {
                log.warn("[OpenApiSignatureFilter] 缺少必填 Header：uri={}", requestUri);
                handleError(response, MISSING_HEADER);
                return;
            }

            Long timestamp = Long.parseLong(timestampStr);

            // 2. 收集所有参与签名的参数
            Map<String, String> allParams = collectSignParams(request, clientId, timestamp, traceId);

            // 3. 验证签名
            ClientRespVO client = platformAuthService.validateSignature(
                    clientId, timestamp, traceId, sign, allParams);

            // 4. 提取业务路径（去除 /open-api 前缀）
            String apiPath = requestUri.substring("/open-api".length());
            String httpMethod = request.getMethod();

            // 5. 查找 API
            ApiDO api = platformAuthService.getApiByPathAndMethod(apiPath, httpMethod);

            // 6. 检查权限（白名单机制）
            String requestIp = ServletUtils.getClientIP(request);
            boolean hasPermission = platformAuthService.hasApiPermission(clientId, api.getId(), requestIp);
            if (!hasPermission) {
                log.warn("[OpenApiSignatureFilter] 权限不足：clientId={}, apiPath={}, apiId={}", 
                        clientId, apiPath, api.getId());
                handleError(response, PERMISSION_DENIED);
                return;
            }

            // 7. 构建认证对象并存入 SecurityContext
            OpenApiAuthentication authentication = new OpenApiAuthentication(clientId, api.getApiCode());
            authentication.setClientName(client.getClientName());
            authentication.setTraceId(traceId);
            authentication.setRequestIp(requestIp);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 8. 将 API 信息存入 request attribute（供后续使用）
            request.setAttribute("PLATFORM_API", api);
            request.setAttribute("PLATFORM_CLIENT", client);
            request.setAttribute("TRACE_ID", traceId);

            log.info("[OpenApiSignatureFilter] 验证通过：clientId={}, apiCode={}, traceId={}", 
                    clientId, api.getApiCode(), traceId);

            // 9. 继续执行
            chain.doFilter(request, response);

        } catch (ServiceException e) {
            log.warn("[OpenApiSignatureFilter] 验证失败：uri={}, error={}", requestUri, e.getMessage());
            handleError(response, e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("[OpenApiSignatureFilter] 处理异常：uri={}", requestUri, e);
            handleError(response, 500, "服务器内部错误");
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 收集所有参与签名的参数
     */
    private Map<String, String> collectSignParams(HttpServletRequest request, 
                                                   String clientId, Long timestamp, String traceId) {
        Map<String, String> params = new TreeMap<>();

        // 1. 添加鉴权 Header（转小写）
        params.put(SignatureUtil.normalizeHeaderName(SignatureUtil.HEADER_CLIENT_ID), clientId);
        params.put(SignatureUtil.normalizeHeaderName(SignatureUtil.HEADER_TIMESTAMP), String.valueOf(timestamp));
        params.put(SignatureUtil.normalizeHeaderName(SignatureUtil.HEADER_TRACE_ID), traceId);

        // 2. 添加 Query 参数
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String value = request.getParameter(name);
            if (StrUtil.isNotBlank(value)) {
                params.put(name, value);
            }
        }

        // 3. 添加 Body 参数（展开 JSON）
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            try {
                String body = ServletUtils.getBody(request);
                if (StrUtil.isNotBlank(body)) {
                    Map<String, Object> bodyMap = JSONUtil.toBean(body, Map.class);
                    SignatureUtil.flattenParams(bodyMap, "", params);
                }
            } catch (Exception e) {
                log.warn("[collectSignParams] 解析 JSON Body 失败", e);
            }
        }

        return params;
    }

    /**
     * 处理错误响应
     */
    private void handleError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        CommonResult<Void> result = CommonResult.error(code, message);
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }

    private void handleError(HttpServletResponse response, com.gitlab.neton.framework.common.exception.ErrorCode errorCode) 
            throws IOException {
        handleError(response, errorCode.getCode(), errorCode.getMsg());
    }

}
