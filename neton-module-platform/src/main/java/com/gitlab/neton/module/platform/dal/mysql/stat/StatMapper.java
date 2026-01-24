package com.gitlab.neton.module.platform.dal.mysql.stat;

import java.util.*;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.gitlab.neton.framework.mybatis.core.mapper.BaseMapperX;
import com.gitlab.neton.module.platform.dal.dataobject.stat.StatDO;
import org.apache.ibatis.annotations.Mapper;
import com.gitlab.neton.module.platform.controller.admin.stat.vo.*;

/**
 * 开放平台统计 Mapper
 *
 * @author Neton
 */
@Mapper
public interface StatMapper extends BaseMapperX<StatDO> {

    default PageResult<StatDO> selectPage(StatPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StatDO>()
                .eqIfPresent(StatDO::getClientId, reqVO.getClientId())
                .eqIfPresent(StatDO::getApiId, reqVO.getApiId())
                .betweenIfPresent(StatDO::getStatDate, reqVO.getStatDate())
                .orderByDesc(StatDO::getId));
    }

}