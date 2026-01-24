package com.gitlab.neton.module.platform.service.chargerecord;

import java.util.*;
import jakarta.validation.*;
import com.gitlab.neton.module.platform.controller.admin.chargerecord.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.chargerecord.ChargeRecordDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;

/**
 * 开放平台计费记录 Service 接口
 *
 * @author Neton
 */
public interface ChargeRecordService {

    /**
     * 创建开放平台计费记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createChargeRecord(@Valid ChargeRecordSaveReqVO createReqVO);

    /**
     * 更新开放平台计费记录
     *
     * @param updateReqVO 更新信息
     */
    void updateChargeRecord(@Valid ChargeRecordSaveReqVO updateReqVO);

    /**
     * 删除开放平台计费记录
     *
     * @param id 编号
     */
    void deleteChargeRecord(Long id);

    /**
    * 批量删除开放平台计费记录
    *
    * @param ids 编号
    */
    void deleteChargeRecordListByIds(List<Long> ids);

    /**
     * 获得开放平台计费记录
     *
     * @param id 编号
     * @return 开放平台计费记录
     */
    ChargeRecordDO getChargeRecord(Long id);

    /**
     * 获得开放平台计费记录分页
     *
     * @param pageReqVO 分页查询
     * @return 开放平台计费记录分页
     */
    PageResult<ChargeRecordDO> getChargeRecordPage(ChargeRecordPageReqVO pageReqVO);

}