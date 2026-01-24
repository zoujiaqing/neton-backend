package com.gitlab.neton.module.platform.service.stat;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.gitlab.neton.module.platform.controller.admin.stat.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.stat.StatDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.util.object.BeanUtils;

import com.gitlab.neton.module.platform.dal.mysql.stat.StatMapper;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.convertList;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.diffList;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * 开放平台统计 Service 实现类
 *
 * @author Neton
 */
@Service
@Validated
public class StatServiceImpl implements StatService {

    @Resource
    private StatMapper statMapper;

    @Override
    public Long createStat(StatSaveReqVO createReqVO) {
        // 插入
        StatDO stat = BeanUtils.toBean(createReqVO, StatDO.class);
        statMapper.insert(stat);

        // 返回
        return stat.getId();
    }

    @Override
    public void updateStat(StatSaveReqVO updateReqVO) {
        // 校验存在
        validateStatExists(updateReqVO.getId());
        // 更新
        StatDO updateObj = BeanUtils.toBean(updateReqVO, StatDO.class);
        statMapper.updateById(updateObj);
    }

    @Override
    public void deleteStat(Long id) {
        // 校验存在
        validateStatExists(id);
        // 删除
        statMapper.deleteById(id);
    }

    @Override
        public void deleteStatListByIds(List<Long> ids) {
        // 删除
        statMapper.deleteByIds(ids);
        }


    private void validateStatExists(Long id) {
        if (statMapper.selectById(id) == null) {
            throw exception(STAT_NOT_EXISTS);
        }
    }

    @Override
    public StatDO getStat(Long id) {
        return statMapper.selectById(id);
    }

    @Override
    public PageResult<StatDO> getStatPage(StatPageReqVO pageReqVO) {
        return statMapper.selectPage(pageReqVO);
    }

}