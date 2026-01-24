package com.gitlab.neton.module.platform.service.clientapi;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.util.object.BeanUtils;

import com.gitlab.neton.module.platform.dal.mysql.clientapi.ClientApiMapper;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.convertList;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.diffList;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * 客户端-API授权关系表（含自定义定价） Service 实现类
 *
 * @author Neton
 */
@Service
@Validated
public class ClientApiServiceImpl implements ClientApiService {

    @Resource
    private ClientApiMapper clientApiMapper;

    @Override
    public Long createClientApi(ClientApiSaveReqVO createReqVO) {
        // 插入
        ClientApiDO clientApi = BeanUtils.toBean(createReqVO, ClientApiDO.class);
        clientApiMapper.insert(clientApi);

        // 返回
        return clientApi.getId();
    }

    @Override
    public void updateClientApi(ClientApiSaveReqVO updateReqVO) {
        // 校验存在
        validateClientApiExists(updateReqVO.getId());
        // 更新
        ClientApiDO updateObj = BeanUtils.toBean(updateReqVO, ClientApiDO.class);
        clientApiMapper.updateById(updateObj);
    }

    @Override
    public void deleteClientApi(Long id) {
        // 校验存在
        validateClientApiExists(id);
        // 删除
        clientApiMapper.deleteById(id);
    }

    @Override
        public void deleteClientApiListByIds(List<Long> ids) {
        // 删除
        clientApiMapper.deleteByIds(ids);
        }


    private void validateClientApiExists(Long id) {
        if (clientApiMapper.selectById(id) == null) {
            throw exception(CLIENT_API_NOT_EXISTS);
        }
    }

    @Override
    public ClientApiDO getClientApi(Long id) {
        return clientApiMapper.selectById(id);
    }

    @Override
    public PageResult<ClientApiDO> getClientApiPage(ClientApiPageReqVO pageReqVO) {
        return clientApiMapper.selectPage(pageReqVO);
    }

}