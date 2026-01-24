package com.gitlab.neton.module.platform.service.log;

import java.util.*;
import jakarta.validation.*;
import com.gitlab.neton.module.platform.controller.admin.log.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.log.LogDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;

/**
 * 开放平台调用日志 Service 接口
 *
 * @author Neton
 */
public interface LogService {

    /**
     * 创建开放平台调用日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLog(@Valid LogSaveReqVO createReqVO);

    /**
     * 更新开放平台调用日志
     *
     * @param updateReqVO 更新信息
     */
    void updateLog(@Valid LogSaveReqVO updateReqVO);

    /**
     * 删除开放平台调用日志
     *
     * @param id 编号
     */
    void deleteLog(Long id);

    /**
    * 批量删除开放平台调用日志
    *
    * @param ids 编号
    */
    void deleteLogListByIds(List<Long> ids);

    /**
     * 获得开放平台调用日志
     *
     * @param id 编号
     * @return 开放平台调用日志
     */
    LogDO getLog(Long id);

    /**
     * 获得开放平台调用日志分页
     *
     * @param pageReqVO 分页查询
     * @return 开放平台调用日志分页
     */
    PageResult<LogDO> getLogPage(LogPageReqVO pageReqVO);

}