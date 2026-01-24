package com.gitlab.neton.module.platform.service.log;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.gitlab.neton.module.platform.controller.admin.log.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.log.LogDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.util.object.BeanUtils;

import com.gitlab.neton.module.platform.dal.mysql.log.LogMapper;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.convertList;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.diffList;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * 开放平台调用日志 Service 实现类
 *
 * @author Neton
 */
@Service
@Validated
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    @Override
    public Long createLog(LogSaveReqVO createReqVO) {
        // 插入
        LogDO log = BeanUtils.toBean(createReqVO, LogDO.class);
        logMapper.insert(log);

        // 返回
        return log.getId();
    }

    @Override
    public void updateLog(LogSaveReqVO updateReqVO) {
        // 校验存在
        validateLogExists(updateReqVO.getId());
        // 更新
        LogDO updateObj = BeanUtils.toBean(updateReqVO, LogDO.class);
        logMapper.updateById(updateObj);
    }

    @Override
    public void deleteLog(Long id) {
        // 校验存在
        validateLogExists(id);
        // 删除
        logMapper.deleteById(id);
    }

    @Override
        public void deleteLogListByIds(List<Long> ids) {
        // 删除
        logMapper.deleteByIds(ids);
        }


    private void validateLogExists(Long id) {
        if (logMapper.selectById(id) == null) {
            throw exception(LOG_NOT_EXISTS);
        }
    }

    @Override
    public LogDO getLog(Long id) {
        return logMapper.selectById(id);
    }

    @Override
    public PageResult<LogDO> getLogPage(LogPageReqVO pageReqVO) {
        return logMapper.selectPage(pageReqVO);
    }

}