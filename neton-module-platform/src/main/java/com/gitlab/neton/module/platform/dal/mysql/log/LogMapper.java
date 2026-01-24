package com.gitlab.neton.module.platform.dal.mysql.log;

import java.util.*;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.gitlab.neton.framework.mybatis.core.mapper.BaseMapperX;
import com.gitlab.neton.module.platform.dal.dataobject.log.LogDO;
import org.apache.ibatis.annotations.Mapper;
import com.gitlab.neton.module.platform.controller.admin.log.vo.*;

/**
 * 开放平台调用日志 Mapper
 *
 * @author Neton
 */
@Mapper
public interface LogMapper extends BaseMapperX<LogDO> {

    default PageResult<LogDO> selectPage(LogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LogDO>()
                .eqIfPresent(LogDO::getClientId, reqVO.getClientId())
                .eqIfPresent(LogDO::getApiId, reqVO.getApiId())
                .eqIfPresent(LogDO::getApiCode, reqVO.getApiCode())
                .eqIfPresent(LogDO::getSuccess, reqVO.getSuccess())
                .orderByDesc(LogDO::getId));
    }

}