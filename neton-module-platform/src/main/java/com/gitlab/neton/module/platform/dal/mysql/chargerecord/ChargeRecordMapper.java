package com.gitlab.neton.module.platform.dal.mysql.chargerecord;

import java.util.*;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.gitlab.neton.framework.mybatis.core.mapper.BaseMapperX;
import com.gitlab.neton.module.platform.dal.dataobject.chargerecord.ChargeRecordDO;
import org.apache.ibatis.annotations.Mapper;
import com.gitlab.neton.module.platform.controller.admin.chargerecord.vo.*;

/**
 * 开放平台计费记录 Mapper
 *
 * @author Neton
 */
@Mapper
public interface ChargeRecordMapper extends BaseMapperX<ChargeRecordDO> {

    default PageResult<ChargeRecordDO> selectPage(ChargeRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ChargeRecordDO>()
                .eqIfPresent(ChargeRecordDO::getClientId, reqVO.getClientId())
                .eqIfPresent(ChargeRecordDO::getApiId, reqVO.getApiId())
                .eqIfPresent(ChargeRecordDO::getTraceId, reqVO.getTraceId())
                .eqIfPresent(ChargeRecordDO::getChargeStatus, reqVO.getChargeStatus())
                .betweenIfPresent(ChargeRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ChargeRecordDO::getId));
    }

}