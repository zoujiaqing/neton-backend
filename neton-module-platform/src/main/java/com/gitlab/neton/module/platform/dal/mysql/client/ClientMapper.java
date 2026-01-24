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

}