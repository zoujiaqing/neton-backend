package com.gitlab.neton.module.platform.dal.mysql.client;

import java.util.*;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.gitlab.neton.framework.mybatis.core.mapper.BaseMapperX;
import com.gitlab.neton.module.platform.dal.dataobject.client.ClientDO;
import org.apache.ibatis.annotations.Mapper;
import com.gitlab.neton.module.platform.controller.admin.client.vo.*;

/**
 * 开放平台客户端 Mapper
 *
 * @author Neton
 */
@Mapper
public interface ClientMapper extends BaseMapperX<ClientDO> {

    default PageResult<ClientDO> selectPage(ClientPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ClientDO>()
                .eqIfPresent(ClientDO::getClientId, reqVO.getClientId())
                .likeIfPresent(ClientDO::getClientName, reqVO.getClientName())
                .eqIfPresent(ClientDO::getClientCode, reqVO.getClientCode())
                .likeIfPresent(ClientDO::getCompanyName, reqVO.getCompanyName())
                .eqIfPresent(ClientDO::getBusinessLicense, reqVO.getBusinessLicense())
                .likeIfPresent(ClientDO::getContactName, reqVO.getContactName())
                .eqIfPresent(ClientDO::getContactEmail, reqVO.getContactEmail())
                .eqIfPresent(ClientDO::getContactPhone, reqVO.getContactPhone())
                .eqIfPresent(ClientDO::getStatus, reqVO.getStatus())
                .orderByDesc(ClientDO::getId));
    }

    /**
     * 根据 client_id 查询客户端
     */
    default ClientDO selectByClientId(String clientId) {
        return selectOne(ClientDO::getClientId, clientId);
    }

    /**
     * 使用乐观锁更新余额
     *
     * @param clientId 客户端 ID
     * @param currentBalance 当前余额
     * @param amount 扣减金额
     * @return 更新行数
     */
    @org.apache.ibatis.annotations.Update("UPDATE platform_client SET balance = balance - #{amount} " +
            "WHERE client_id = #{clientId} AND balance = #{currentBalance}")
    int updateBalanceWithOptimisticLock(@org.apache.ibatis.annotations.Param("clientId") String clientId,
                                        @org.apache.ibatis.annotations.Param("currentBalance") Long currentBalance,
                                        @org.apache.ibatis.annotations.Param("amount") Long amount);

}