package com.gitlab.neton.module.platform.service.clientapi;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gitlab.neton.framework.common.enums.CustomizedStatusEnum;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiCreateAssociationReqVO;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiPageReqVO;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiSaveReqVO;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import com.gitlab.neton.module.platform.dal.mysql.api.ApiMapper;
import com.gitlab.neton.module.platform.dal.mysql.clientapi.ClientApiMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.CLIENT_API_NOT_EXISTS;

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

    @Resource
    private ApiMapper apiMapper;


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
    public ClientApiDO byClientIdAndApiId(Long clientId, Long apiId) {
        return clientApiMapper.selectOne(Wrappers.<ClientApiDO>lambdaQuery()
                .eq(ClientApiDO::getClientId, clientId)
                .eq(ClientApiDO::getApiId, apiId)
                .last("limit 1")
        );
    }

    @Override
    public PageResult<ClientApiDO> getClientApiPage(ClientApiPageReqVO pageReqVO) {
        return clientApiMapper.selectPage(pageReqVO);
    }

    @Override
    public void createAssociation(ClientApiCreateAssociationReqVO vo) {

        if (vo == null || vo.getClientId() == null || vo.getApiIdList() == null) {
            throw exception(CLIENT_API_NOT_EXISTS);
        }

        // 把对应客户端的 api 先定义为都删除状态
        clientApiMapper.update(null, Wrappers.<ClientApiDO>lambdaUpdate()
                .eq(ClientApiDO::getClientId, vo.getClientId())
                .set(ClientApiDO::getSelected, CustomizedStatusEnum.DISABLE.getStatus())
        );

        // 循环更改状态或者添加 api 关系
        for (Long apiId : vo.getApiIdList()) {
            ClientApiDO clientApiDO = clientApiMapper.selectOne(Wrappers.<ClientApiDO>lambdaQuery()
                    .eq(ClientApiDO::getClientId, vo.getClientId())
                    .eq(ClientApiDO::getApiId, apiId)
            );

            if (clientApiDO != null) {
                clientApiMapper.update(null, Wrappers.<ClientApiDO>lambdaUpdate()
                        .eq(ClientApiDO::getClientId, vo.getClientId())
                        .eq(ClientApiDO::getApiId, apiId)
                        .set(ClientApiDO::getSelected, CustomizedStatusEnum.ENABLE.getStatus())
                );
            } else {

                ApiDO apiDO = apiMapper.selectById(apiId);
                if (apiDO != null) {
                    ClientApiDO addData = new ClientApiDO();
                    addData.setClientId(vo.getClientId());  // 客户端ID
                    addData.setApiId(apiId);    // APIID
                    addData.setStatus(CustomizedStatusEnum.ENABLE.getStatus()); // 启用状态
                    addData.setSelected(CustomizedStatusEnum.ENABLE.getStatus()); // 选中状态
                    addData.setRateLimitPerMin(apiDO.getRateLimitPerMin()); // 每分钟限流
                    addData.setIsCustomPrice(false); // 是否自定义价格
                    addData.setCustomPrice(apiDO.getDefaultPrice()); // 价格

                    clientApiMapper.insert(addData);
                }
            }
        }
    }

}