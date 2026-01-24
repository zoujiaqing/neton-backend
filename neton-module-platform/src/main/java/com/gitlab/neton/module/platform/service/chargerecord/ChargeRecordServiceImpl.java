package com.gitlab.neton.module.platform.service.chargerecord;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.gitlab.neton.module.platform.controller.admin.chargerecord.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.chargerecord.ChargeRecordDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.util.object.BeanUtils;

import com.gitlab.neton.module.platform.dal.mysql.chargerecord.ChargeRecordMapper;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.convertList;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.diffList;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * 开放平台计费记录 Service 实现类
 *
 * @author Neton
 */
@Service
@Validated
public class ChargeRecordServiceImpl implements ChargeRecordService {

    @Resource
    private ChargeRecordMapper chargeRecordMapper;

    @Override
    public Long createChargeRecord(ChargeRecordSaveReqVO createReqVO) {
        // 插入
        ChargeRecordDO chargeRecord = BeanUtils.toBean(createReqVO, ChargeRecordDO.class);
        chargeRecordMapper.insert(chargeRecord);

        // 返回
        return chargeRecord.getId();
    }

    @Override
    public void updateChargeRecord(ChargeRecordSaveReqVO updateReqVO) {
        // 校验存在
        validateChargeRecordExists(updateReqVO.getId());
        // 更新
        ChargeRecordDO updateObj = BeanUtils.toBean(updateReqVO, ChargeRecordDO.class);
        chargeRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteChargeRecord(Long id) {
        // 校验存在
        validateChargeRecordExists(id);
        // 删除
        chargeRecordMapper.deleteById(id);
    }

    @Override
        public void deleteChargeRecordListByIds(List<Long> ids) {
        // 删除
        chargeRecordMapper.deleteByIds(ids);
        }


    private void validateChargeRecordExists(Long id) {
        if (chargeRecordMapper.selectById(id) == null) {
            throw exception(CHARGE_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public ChargeRecordDO getChargeRecord(Long id) {
        return chargeRecordMapper.selectById(id);
    }

    @Override
    public PageResult<ChargeRecordDO> getChargeRecordPage(ChargeRecordPageReqVO pageReqVO) {
        return chargeRecordMapper.selectPage(pageReqVO);
    }

}