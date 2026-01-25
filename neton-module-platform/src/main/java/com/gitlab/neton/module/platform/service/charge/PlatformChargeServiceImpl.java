package com.gitlab.neton.module.platform.service.charge;

import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import com.gitlab.neton.module.platform.dal.dataobject.chargerecord.ChargeRecordDO;
import com.gitlab.neton.module.platform.dal.dataobject.client.ClientDO;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import com.gitlab.neton.module.platform.dal.mysql.api.ApiMapper;
import com.gitlab.neton.module.platform.dal.mysql.chargerecord.ChargeRecordMapper;
import com.gitlab.neton.module.platform.dal.mysql.client.ClientMapper;
import com.gitlab.neton.module.platform.dal.mysql.clientapi.ClientApiMapper;
import com.gitlab.neton.module.platform.enums.ChargeStatusEnum;
import com.gitlab.neton.module.platform.enums.ChargeTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 开放平台计费 Service 实现类
 *
 * @author Neton
 */
@Service
@Slf4j
public class PlatformChargeServiceImpl implements PlatformChargeService {

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private ApiMapper apiMapper;

    @Resource
    private ClientApiMapper clientApiMapper;

    @Resource
    private ChargeRecordMapper chargeRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChargeRecordDO chargeForApiCall(String clientId, Long apiId, String traceId, boolean success) {
        // 1. 查询客户端和 API 信息
        ClientDO client = clientMapper.selectByClientId(clientId);
        ApiDO api = apiMapper.selectById(apiId);

        if (client == null || api == null) {
            log.warn("[chargeForApiCall] 客户端或 API 不存在：clientId={}, apiId={}", clientId, apiId);
            return null;
        }

        // 2. 查询计费价格
        Long price = getChargePrice(clientId, apiId);

        // 3. 如果免费或者调用失败，不扣费
        if (price == 0 || !success) {
            log.info("[chargeForApiCall] 免费或失败，不扣费：clientId={}, apiId={}, price={}, success={}",
                    clientId, apiId, price, success);
            return createChargeRecord(clientId, apiId, traceId, price, 0L,
                    client.getBalance(), client.getBalance(), ChargeStatusEnum.SUCCESS, null);
        }

        // 4. 扣减余额
        Long balanceBefore = client.getBalance();
        boolean deducted = deductBalance(clientId, price, traceId);

        if (!deducted) {
            log.warn("[chargeForApiCall] 余额不足：clientId={}, apiId={}, price={}, balance={}",
                    clientId, apiId, price, balanceBefore);
            return createChargeRecord(clientId, apiId, traceId, price, 0L,
                    balanceBefore, balanceBefore, ChargeStatusEnum.FAILED, "余额不足");
        }

        // 5. 扣费成功，更新客户端统计
        clientMapper.updateById(client.setTotalCharged(client.getTotalCharged() + price));

        // 6. 创建计费记录
        Long balanceAfter = balanceBefore - price;
        ChargeRecordDO record = createChargeRecord(clientId, apiId, traceId, price, price,
                balanceBefore, balanceAfter, ChargeStatusEnum.SUCCESS, null);

        log.info("[chargeForApiCall] 扣费成功：clientId={}, apiId={}, price={}, balanceBefore={}, balanceAfter={}",
                clientId, apiId, price, balanceBefore, balanceAfter);

        return record;
    }

    @Override
    public Long getChargePrice(String clientId, Long apiId) {
        // 1. 查询客户端-API 授权关系（自定义价格）
        ClientApiDO clientApi = clientApiMapper.selectByClientIdAndApiId(clientId, apiId);
        if (clientApi != null && Boolean.TRUE.equals(clientApi.getIsCustomPrice())
                && clientApi.getCustomPrice() != null) {
            log.debug("[getChargePrice] 使用自定义价格：clientId={}, apiId={}, price={}",
                    clientId, apiId, clientApi.getCustomPrice());
            return clientApi.getCustomPrice();
        }

        // 2. 使用 API 默认价格
        ApiDO api = apiMapper.selectById(apiId);
        if (api == null || ChargeTypeEnum.FREE.getType().equals(api.getChargeType())) {
            return 0L; // 免费
        }

        log.debug("[getChargePrice] 使用默认价格：clientId={}, apiId={}, price={}",
                clientId, apiId, api.getDefaultPrice());
        return api.getDefaultPrice();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductBalance(String clientId, Long amount, String traceId) {
        // 使用乐观锁更新余额
        ClientDO client = clientMapper.selectByClientId(clientId);
        if (client == null) {
            log.warn("[deductBalance] 客户端不存在：clientId={}", clientId);
            return false;
        }

        Long currentBalance = client.getBalance();
        if (currentBalance < amount) {
            log.warn("[deductBalance] 余额不足：clientId={}, balance={}, amount={}",
                    clientId, currentBalance, amount);
            return false;
        }

        // 更新余额（使用乐观锁：WHERE balance = currentBalance）
        int updated = clientMapper.updateBalanceWithOptimisticLock(clientId, currentBalance, amount);
        if (updated == 0) {
            log.warn("[deductBalance] 乐观锁更新失败（并发冲突）：clientId={}, traceId={}", clientId, traceId);
            return false;
        }

        log.info("[deductBalance] 扣减成功：clientId={}, amount={}, balanceBefore={}, balanceAfter={}",
                clientId, amount, currentBalance, currentBalance - amount);
        return true;
    }

    /**
     * 创建计费记录
     */
    private ChargeRecordDO createChargeRecord(String clientId, Long apiId, String traceId,
                                              Long price, Long actualCharged, Long balanceBefore,
                                              Long balanceAfter, ChargeStatusEnum status, String failureReason) {
        // 查询 API 信息
        ApiDO api = apiMapper.selectById(apiId);

        ChargeRecordDO record = new ChargeRecordDO();
        record.setClientId(clientId);
        record.setApiId(apiId);
        record.setTraceId(traceId);
        record.setChargeType(api != null ? api.getChargeType() : ChargeTypeEnum.FREE.getType());
        record.setPrice(actualCharged);
        record.setIsCustomPrice(false); // TODO: 判断是否使用自定义价格
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setChargeStatus(status.getStatus());
        record.setFailureReason(failureReason);
        record.setChargeTime(LocalDateTime.now());

        chargeRecordMapper.insert(record);
        return record;
    }

}
