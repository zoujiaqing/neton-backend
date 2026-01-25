package com.gitlab.neton.module.platform.service.api;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gitlab.neton.framework.common.enums.CustomizedStatusEnum;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiListReqVO;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiListRespVO;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiPageReqVO;
import com.gitlab.neton.module.platform.controller.admin.api.vo.ApiSaveReqVO;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import com.gitlab.neton.module.platform.dal.mysql.api.ApiMapper;
import com.gitlab.neton.module.platform.dal.mysql.clientapi.ClientApiMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.gitlab.neton.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.gitlab.neton.module.platform.enums.ErrorCodeConstants.API_NOT_EXISTS;

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

    @Resource
    private ClientApiMapper clientApiMapper;

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

    @Override
    public List<ApiListRespVO> getApiList(ApiListReqVO apiListReqVO) {

        // 获取全量 API 列表
        List<ApiDO> apiDOS = apiMapper.selectList(apiListReqVO);

        List<ApiListRespVO> apiList = BeanUtils.toBean(apiDOS, ApiListRespVO.class);
        // 获取客户端已使用列表
        List<ClientApiDO> clientApiDOS = clientApiMapper.selectList(Wrappers.<ClientApiDO>lambdaQuery()
                .eq(ClientApiDO::getClientId, apiListReqVO.getClientId())
                .eq(ClientApiDO::getSelected, CustomizedStatusEnum.ENABLE.getStatus())
        );
        List<Long> selectedIdList = clientApiDOS.stream().map(ClientApiDO::getApiId).toList();

        // 增加是否选中属性
        apiList.stream().forEach(item -> {
            if (selectedIdList.contains(item.getId())) {
                item.setSelected(CustomizedStatusEnum.ENABLE.getStatus());
            } else {
                item.setSelected(CustomizedStatusEnum.DISABLE.getStatus());
            }
        });

        return apiList;
    }

}