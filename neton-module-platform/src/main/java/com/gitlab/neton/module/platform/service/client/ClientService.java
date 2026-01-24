package com.gitlab.neton.module.platform.service.client;

import java.util.*;
import jakarta.validation.*;
import com.gitlab.neton.module.platform.controller.admin.client.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.client.ClientDO;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.PageParam;

/**
 * 开放平台客户端 Service 接口
 *
 * @author Neton
 */
public interface ClientService {

    /**
     * 创建开放平台客户端
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClient(@Valid ClientSaveReqVO createReqVO);

    /**
     * 更新开放平台客户端
     *
     * @param updateReqVO 更新信息
     */
    void updateClient(@Valid ClientSaveReqVO updateReqVO);

    /**
     * 删除开放平台客户端
     *
     * @param id 编号
     */
    void deleteClient(Long id);

    /**
    * 批量删除开放平台客户端
    *
    * @param ids 编号
    */
    void deleteClientListByIds(List<Long> ids);

    /**
     * 获得开放平台客户端
     *
     * @param id 编号
     * @return 开放平台客户端
     */
    ClientDO getClient(Long id);

    /**
     * 获得开放平台客户端分页
     *
     * @param pageReqVO 分页查询
     * @return 开放平台客户端分页
     */
    PageResult<ClientDO> getClientPage(ClientPageReqVO pageReqVO);

}