package com.gitlab.neton.module.platform.service.api;

import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiListReqVO;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiListRespVO;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiPageReqVO;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiSaveReqVO;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 开放平台API定义 Service 接口
 *
 * @author Neton
 */
public interface ApiService {

    /**
     * 创建开放平台API定义
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApi(@Valid ApiSaveReqVO createReqVO);

    /**
     * 更新开放平台API定义
     *
     * @param updateReqVO 更新信息
     */
    void updateApi(@Valid ApiSaveReqVO updateReqVO);

    /**
     * 删除开放平台API定义
     *
     * @param id 编号
     */
    void deleteApi(Long id);

    /**
     * 批量删除开放平台API定义
     *
     * @param ids 编号
     */
    void deleteApiListByIds(List<Long> ids);

    /**
     * 获得开放平台API定义
     *
     * @param id 编号
     * @return 开放平台API定义
     */
    ApiDO getApi(Long id);

    /**
     * 获得开放平台API定义分页
     *
     * @param pageReqVO 分页查询
     * @return 开放平台API定义分页
     */
    PageResult<ApiDO> getApiPage(ApiPageReqVO pageReqVO);

    /**
     * 获得开放平台API定义分页
     *
     * @return 开放平台API定义列表
     */
    List<ApiListRespVO> getApiList(ApiListReqVO apiListReqVO);

}