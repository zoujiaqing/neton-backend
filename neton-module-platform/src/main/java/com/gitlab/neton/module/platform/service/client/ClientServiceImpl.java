package com.gitlab.neton.module.platform.service.client;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import com.gitlab.neton.module.platform.controller.admin.client.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.client.ClientDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.util.object.BeanUtils;

import com.gitlab.neton.module.platform.dal.mysql.client.ClientMapper;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.convertList;
import static com.gitlab.neton.framework.common.util.collection.CollectionUtils.diffList;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.*;

/**
 * 开放平台客户端 Service 实现类
 *
 * @author Neton
 */
@Service
@Validated
public class ClientServiceImpl implements ClientService {

    @Resource
    private ClientMapper clientMapper;

    @Override
    public Long createClient(ClientSaveReqVO createReqVO) {
        // 插入
        ClientDO client = BeanUtils.toBean(createReqVO, ClientDO.class);
        clientMapper.insert(client);

        // 返回
        return client.getId();
    }

    @Override
    public void updateClient(ClientSaveReqVO updateReqVO) {
        // 校验存在
        validateClientExists(updateReqVO.getId());
        // 更新
        ClientDO updateObj = BeanUtils.toBean(updateReqVO, ClientDO.class);
        clientMapper.updateById(updateObj);
    }

    @Override
    public void deleteClient(Long id) {
        // 校验存在
        validateClientExists(id);
        // 删除
        clientMapper.deleteById(id);
    }

    @Override
        public void deleteClientListByIds(List<Long> ids) {
        // 删除
        clientMapper.deleteByIds(ids);
        }


    private void validateClientExists(Long id) {
        if (clientMapper.selectById(id) == null) {
            throw exception(CLIENT_NOT_EXISTS);
        }
    }

    @Override
    public ClientDO getClient(Long id) {
        return clientMapper.selectById(id);
    }

    @Override
    public PageResult<ClientDO> getClientPage(ClientPageReqVO pageReqVO) {
        return clientMapper.selectPage(pageReqVO);
    }

}