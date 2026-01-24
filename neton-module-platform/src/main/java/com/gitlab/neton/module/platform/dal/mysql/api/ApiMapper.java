package com.gitlab.neton.module.platform.dal.mysql.api;

import java.util.*;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.gitlab.neton.framework.mybatis.core.mapper.BaseMapperX;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import org.apache.ibatis.annotations.Mapper;
import com.gitlab.neton.module.platform.controller.admin.api.vo.*;

/**
 * 开放平台API定义 Mapper
 *
 * @author Neton
 */
@Mapper
public interface ApiMapper extends BaseMapperX<ApiDO> {

    default PageResult<ApiDO> selectPage(ApiPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ApiDO>()
                .eqIfPresent(ApiDO::getApiCode, reqVO.getApiCode())
                .likeIfPresent(ApiDO::getApiName, reqVO.getApiName())
                .eqIfPresent(ApiDO::getCategory, reqVO.getCategory())
                .eqIfPresent(ApiDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ApiDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ApiDO::getId));
    }

    /**
     * 根据 API 路径和方法查询
     */
    default ApiDO selectByPathAndMethod(String apiPath, String httpMethod) {
        return selectOne(new LambdaQueryWrapperX<ApiDO>()
                .eq(ApiDO::getApiPath, apiPath)
                .eq(ApiDO::getHttpMethod, httpMethod));
    }

}