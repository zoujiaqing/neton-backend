package com.gitlab.neton.module.platform.service.charge;

import com.gitlab.neton.module.platform.dal.dataobject.chargerecord.ChargeRecordDO;

/**
 * 开放平台计费 Service 接口
 *
 * @author Neton
 */
public interface PlatformChargeService {

    /**
     * 为 API 调用计费
     *
     * @param clientId 客户端 ID
     * @param apiId API ID
     * @param traceId 请求跟踪 ID
     * @param success 调用是否成功
     * @return 计费记录
     */
    ChargeRecordDO chargeForApiCall(String clientId, Long apiId, String traceId, boolean success);

    /**
     * 获取计费价格（自定义价格优先）
     *
     * @param clientId 客户端 ID
     * @param apiId API ID
     * @return 价格（分），0 表示免费
     */
    Long getChargePrice(String clientId, Long apiId);

    /**
     * 扣减客户端余额（使用乐观锁）
     *
     * @param clientId 客户端 ID
     * @param amount 扣减金额（分）
     * @param traceId 请求跟踪 ID
     * @return true=扣减成功，false=余额不足
     */
    boolean deductBalance(String clientId, Long amount, String traceId);

}
