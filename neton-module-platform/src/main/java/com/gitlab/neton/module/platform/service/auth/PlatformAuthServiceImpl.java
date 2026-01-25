package com.gitlab.neton.module.platform.service.auth;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.gitlab.neton.framework.common.exception.ServiceException;
import com.gitlab.neton.module.platform.controller.admin.client.vo.ClientRespVO;
import com.gitlab.neton.module.platform.convert.client.ClientConvert;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import com.gitlab.neton.module.platform.dal.dataobject.client.ClientDO;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import com.gitlab.neton.module.platform.dal.mysql.api.ApiMapper;
import com.gitlab.neton.module.platform.dal.mysql.client.ClientMapper;
import com.gitlab.neton.module.platform.dal.mysql.clientapi.ClientApiMapper;
import com.gitlab.neton.module.platform.enums.ClientStatusEnum;
import com.gitlab.neton.module.platform.util.SignatureUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * 开放平台鉴权 Service 实现类
 *
 * @author Neton
 */
@Service
@Slf4j
public class PlatformAuthServiceImpl implements PlatformAuthService {

    private static final String REDIS_KEY_REPLAY = "platform:replay:";
    private static final int REPLAY_TTL_SECONDS = 300;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ApiMapper apiMapper;
    @Resource
    private ClientApiMapper clientApiMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ClientRespVO validateSignature(String clientId, Long timestamp, String traceId,
                                          String sign, Map<String, String> allParams) {
        // 1. 验证时间戳
        if (!SignatureUtil.validateTimestamp(timestamp)) {
            log.warn("[validateSignature] 时间戳超出有效窗口：clientId={}, timestamp={}", clientId, timestamp);
            throw new ServiceException(INVALID_TIMESTAMP);
        }

        // 2. 检查 Trace-Id 重复
        if (isTraceIdDuplicated(clientId, traceId)) {
            log.warn("[validateSignature] Trace-Id 重复，疑似重放攻击：clientId={}, traceId={}", clientId, traceId);
            throw new ServiceException(REPLAY_REQUEST);
        }

        // 3. 查询客户端信息
        ClientDO client = clientMapper.selectByClientId(clientId);
        if (client == null) {
            log.warn("[validateSignature] 客户端不存在：clientId={}", clientId);
            throw new ServiceException(CLIENT_NOT_EXISTS);
        }

        // 4. 检查客户端状态
        if (!ClientStatusEnum.NORMAL.getStatus().equals(client.getStatus())) {
            log.warn("[validateSignature] 客户端已停用：clientId={}, status={}", clientId, client.getStatus());
            throw new ServiceException(CLIENT_DISABLED);
        }

        // 5. 检查过期时间
        if (client.getExpiredTime() != null && LocalDateTime.now().isAfter(client.getExpiredTime())) {
            log.warn("[validateSignature] 客户端已过期：clientId={}, expiredTime={}", clientId, client.getExpiredTime());
            throw new ServiceException(CLIENT_EXPIRED);
        }

        // 6. 计算服务端签名
        String serverSign = SignatureUtil.calculateSign(allParams, client.getClientSecret());

        // 7. 验证签名
        if (!SignatureUtil.verifySign(sign, serverSign)) {
            log.warn("[validateSignature] 签名校验失败：clientId={}, clientSign={}, serverSign={}",
                    clientId, sign, serverSign);
            log.debug("[validateSignature] 签名原文参数：{}", allParams);
            throw new ServiceException(INVALID_SIGNATURE);
        }

        // 8. 记录 Trace-Id（防重放）
        recordTraceId(clientId, traceId);

        log.info("[validateSignature] 签名验证成功：clientId={}, traceId={}", clientId, traceId);

        return ClientConvert.INSTANCE.convert(client);
    }

    @Override
    public boolean hasApiPermission(String clientId, Long apiId, String requestIp) {
        // 1. 查询授权关系
        ClientApiDO clientApi = clientApiMapper.selectByClientIdAndApiId(clientId, apiId);
        if (clientApi == null) {
            log.warn("[hasApiPermission] 客户端未授权该 API：clientId={}, apiId={}", clientId, apiId);
            return false;
        }

        // 2. 检查授权状态
        if (clientApi.getStatus() != 1) { // 1=正常
            log.warn("[hasApiPermission] 授权已停用：clientId={}, apiId={}, status={}",
                    clientId, apiId, clientApi.getStatus());
            return false;
        }

        // 3. 检查授权时间范围
        LocalDateTime now = LocalDateTime.now();
        if (clientApi.getStartTime() != null && now.isBefore(clientApi.getStartTime())) {
            log.warn("[hasApiPermission] 授权未开始：clientId={}, apiId={}, startTime={}",
                    clientId, apiId, clientApi.getStartTime());
            return false;
        }
        if (clientApi.getEndTime() != null && now.isAfter(clientApi.getEndTime())) {
            log.warn("[hasApiPermission] 授权已过期：clientId={}, apiId={}, endTime={}",
                    clientId, apiId, clientApi.getEndTime());
            return false;
        }

        // 4. 检查 IP 白名单（如果配置了）
        ClientDO client = clientMapper.selectByClientId(clientId);
        if (client != null && StrUtil.isNotBlank(client.getAllowedIps())) {
            List<String> allowedIps = JSONUtil.toList(client.getAllowedIps(), String.class);
            if (!allowedIps.isEmpty() && !allowedIps.contains(requestIp)) {
                log.warn("[hasApiPermission] IP 不在白名单中：clientId={}, apiId={}, requestIp={}, allowedIps={}",
                        clientId, apiId, requestIp, allowedIps);
                return false;
            }
        }

        log.info("[hasApiPermission] 权限验证通过：clientId={}, apiId={}, requestIp={}", clientId, apiId, requestIp);
        return true;
    }

    @Override
    public ApiDO getApiByPathAndMethod(String apiPath, String httpMethod) {
        ApiDO api = apiMapper.selectByPathAndMethod(apiPath, httpMethod);
        if (api == null) {
            log.warn("[getApiByPathAndMethod] 未找到匹配的 API：apiPath={}, httpMethod={}", apiPath, httpMethod);
            throw new ServiceException(API_NOT_FOUND);
        }

        // 检查 API 状态
        if (api.getStatus() != 1) { // 1=正常
            log.warn("[getApiByPathAndMethod] API 已停用：apiPath={}, httpMethod={}, status={}",
                    apiPath, httpMethod, api.getStatus());
            throw new ServiceException(API_DISABLED);
        }

        return api;
    }

    @Override
    public boolean isTraceIdDuplicated(String clientId, String traceId) {
        String key = REDIS_KEY_REPLAY + clientId + ":" + traceId;
        Boolean exists = stringRedisTemplate.hasKey(key);
        return exists;
    }

    @Override
    public void recordTraceId(String clientId, String traceId) {
        String key = REDIS_KEY_REPLAY + clientId + ":" + traceId;
        stringRedisTemplate.opsForValue().set(key, "1", REPLAY_TTL_SECONDS, TimeUnit.SECONDS);
        log.debug("[recordTraceId] 记录 Trace-Id：key={}, ttl={}", key, REPLAY_TTL_SECONDS);
    }

}
