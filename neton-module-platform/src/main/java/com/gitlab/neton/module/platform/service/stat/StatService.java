package com.gitlab.neton.module.platform.service.stat;

import java.util.*;
import jakarta.validation.*;
import com.gitlab.neton.module.platform.controller.admin.stat.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.stat.StatDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;

/**
 * 开放平台统计 Service 接口
 *
 * @author Neton
 */
public interface StatService {

    /**
     * 创建开放平台统计
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStat(@Valid StatSaveReqVO createReqVO);

    /**
     * 更新开放平台统计
     *
     * @param updateReqVO 更新信息
     */
    void updateStat(@Valid StatSaveReqVO updateReqVO);

    /**
     * 删除开放平台统计
     *
     * @param id 编号
     */
    void deleteStat(Long id);

    /**
    * 批量删除开放平台统计
    *
    * @param ids 编号
    */
    void deleteStatListByIds(List<Long> ids);

    /**
     * 获得开放平台统计
     *
     * @param id 编号
     * @return 开放平台统计
     */
    StatDO getStat(Long id);

    /**
     * 获得开放平台统计分页
     *
     * @param pageReqVO 分页查询
     * @return 开放平台统计分页
     */
    PageResult<StatDO> getStatPage(StatPageReqVO pageReqVO);

}