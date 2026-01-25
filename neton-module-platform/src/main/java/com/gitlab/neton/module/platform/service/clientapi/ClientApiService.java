package com.gitlab.neton.module.platform.service.clientapi;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiCreateAssociationReqVO;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiPageReqVO;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiSaveReqVO;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 客户端-API授权关系表（含自定义定价） Service 接口
 *
 * @author Neton
 */
public interface ClientApiService {

    /**
     * 创建客户端-API授权关系表（含自定义定价）
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClientApi(@Valid ClientApiSaveReqVO createReqVO);

    /**
     * 更新客户端-API授权关系表（含自定义定价）
     *
     * @param updateReqVO 更新信息
     */
    void updateClientApi(@Valid ClientApiSaveReqVO updateReqVO);

    /**
     * 删除客户端-API授权关系表（含自定义定价）
     *
     * @param id 编号
     */
    void deleteClientApi(Long id);

    /**
     * 批量删除客户端-API授权关系表（含自定义定价）
     *
     * @param ids 编号
     */
    void deleteClientApiListByIds(List<Long> ids);

    /**
     * 获得客户端-API授权关系表（含自定义定价）
     *
     * @param id 编号
     * @return 客户端-API授权关系表（含自定义定价）
     */
    ClientApiDO getClientApi(Long id);

    /**
     * 获得客户端-API授权关系表（含自定义定价）
     *
     * @param clientId 编号
     * @return 客户端-API授权关系表（含自定义定价）
     */
    ClientApiDO byClientIdAndApiId(Long clientId, Long apiId);


    /**
     * 获得客户端-API授权关系表（含自定义定价）分页
     *
     * @param pageReqVO 分页查询
     * @return 客户端-API授权关系表（含自定义定价）分页
     */
    PageResult<ClientApiDO> getClientApiPage(ClientApiPageReqVO pageReqVO);

    /**
     * 创建客户端与 API的关系
     */
    void createAssociation(ClientApiCreateAssociationReqVO vo);

}