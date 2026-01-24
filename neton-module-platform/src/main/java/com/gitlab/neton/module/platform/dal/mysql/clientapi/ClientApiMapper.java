package com.gitlab.neton.module.platform.dal.mysql.clientapi;

import java.util.*;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.gitlab.neton.framework.mybatis.core.mapper.BaseMapperX;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import org.apache.ibatis.annotations.Mapper;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.*;

/**
 * 客户端-API授权关系表（含自定义定价） Mapper
 *
 * @author Neton
 */
@Mapper
public interface ClientApiMapper extends BaseMapperX<ClientApiDO> {

    default PageResult<ClientApiDO> selectPage(ClientApiPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ClientApiDO>()
                .eqIfPresent(ClientApiDO::getClientId, reqVO.getClientId())
                .eqIfPresent(ClientApiDO::getApiId, reqVO.getApiId())
                .eqIfPresent(ClientApiDO::getStatus, reqVO.getStatus())
                .orderByDesc(ClientApiDO::getId));
    }

}