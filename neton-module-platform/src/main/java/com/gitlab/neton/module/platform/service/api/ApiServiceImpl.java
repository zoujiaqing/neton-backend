package com.gitlab.neton.module.platform.service.api;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.gitlab.neton.module.platform.controller.admin.api.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.util.object.BeanUtils;

import com.gitlab.neton.module.platform.dal.mysql.api.ApiMapper;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.convertList;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.diffList;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * 开放平台API定义 Service 实现类
 *
 * @author Neton
 */
@Service
@Validated
public class ApiServiceImpl implements ApiService {

    @Resource
    private ApiMapper apiMapper;

    @Override
    public Long createApi(ApiSaveReqVO createReqVO) {
        // 插入
        ApiDO api = BeanUtils.toBean(createReqVO, ApiDO.class);
        apiMapper.insert(api);

        // 返回
        return api.getId();
    }

    @Override
    public void updateApi(ApiSaveReqVO updateReqVO) {
        // 校验存在
        validateApiExists(updateReqVO.getId());
        // 更新
        ApiDO updateObj = BeanUtils.toBean(updateReqVO, ApiDO.class);
        apiMapper.updateById(updateObj);
    }

    @Override
    public void deleteApi(Long id) {
        // 校验存在
        validateApiExists(id);
        // 删除
        apiMapper.deleteById(id);
    }

    @Override
        public void deleteApiListByIds(List<Long> ids) {
        // 删除
        apiMapper.deleteByIds(ids);
        }


    private void validateApiExists(Long id) {
        if (apiMapper.selectById(id) == null) {
            throw exception(API_NOT_EXISTS);
        }
    }

    @Override
    public ApiDO getApi(Long id) {
        return apiMapper.selectById(id);
    }

    @Override
    public PageResult<ApiDO> getApiPage(ApiPageReqVO pageReqVO) {
        return apiMapper.selectPage(pageReqVO);
    }

}