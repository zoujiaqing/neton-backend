package com.gitlab.neton.module.platform.controller.admin.clientapi;

import com.gitlab.neton.framework.apilog.core.annotation.ApiAccessLog;
import com.gitlab.neton.framework.common.pojo.CommonResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import com.gitlab.neton.framework.excel.core.util.ExcelUtils;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiCreateAssociationReqVO;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiPageReqVO;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiRespVO;
import com.gitlab.neton.module.platform.controller.admin.clientapi.vo.ClientApiSaveReqVO;
import com.gitlab.neton.module.platform.dal.dataobject.clientapi.ClientApiDO;
import com.gitlab.neton.module.platform.service.clientapi.ClientApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.gitlab.neton.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.gitlab.neton.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 客户端-API授权关系表（含自定义定价）")
@RestController
@RequestMapping("/platform/client-api")
@Validated
public class ClientApiController {

    @Resource
    private ClientApiService clientApiService;

    @PostMapping("/create")
    @Operation(summary = "创建客户端-API授权关系表（含自定义定价）")
    @PreAuthorize("@ss.hasPermission('platform:client-api:create')")
    public CommonResult<Long> createClientApi(@Valid @RequestBody ClientApiSaveReqVO createReqVO) {
        return success(clientApiService.createClientApi(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新客户端-API授权关系表（含自定义定价）")
    @PreAuthorize("@ss.hasPermission('platform:client-api:update')")
    public CommonResult<Boolean> updateClientApi(@Valid @RequestBody ClientApiSaveReqVO updateReqVO) {
        clientApiService.updateClientApi(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除客户端-API授权关系表（含自定义定价）")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('platform:client-api:delete')")
    public CommonResult<Boolean> deleteClientApi(@RequestParam("id") Long id) {
        clientApiService.deleteClientApi(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除客户端-API授权关系表（含自定义定价）")
    @PreAuthorize("@ss.hasPermission('platform:client-api:delete')")
    public CommonResult<Boolean> deleteClientApiList(@RequestParam("ids") List<Long> ids) {
        clientApiService.deleteClientApiListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得客户端-API授权关系表（含自定义定价）")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('platform:client-api:query')")
    public CommonResult<ClientApiRespVO> getClientApi(@RequestParam("id") Long id) {
        ClientApiDO clientApi = clientApiService.getClientApi(id);
        return success(BeanUtils.toBean(clientApi, ClientApiRespVO.class));
    }

    @GetMapping("/byClientIdAndApiId")
    @Operation(summary = "获得客户端-API授权关系表（含自定义定价）")
    @Parameter(name = "clientId", description = "编号", required = true, example = "1024")
    @Parameter(name = "apiId", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('platform:client-api:query')")
    public CommonResult<ClientApiRespVO> byClientIdAndApiId(@RequestParam("clientId") Long clientId, @RequestParam("apiId") Long apiId) {
        ClientApiDO clientApi = clientApiService.byClientIdAndApiId(clientId, apiId);
        return success(BeanUtils.toBean(clientApi, ClientApiRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得客户端-API授权关系表（含自定义定价）分页")
    @PreAuthorize("@ss.hasPermission('platform:client-api:query')")
    public CommonResult<PageResult<ClientApiRespVO>> getClientApiPage(@Valid ClientApiPageReqVO pageReqVO) {
        PageResult<ClientApiDO> pageResult = clientApiService.getClientApiPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ClientApiRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出客户端-API授权关系表（含自定义定价） Excel")
    @PreAuthorize("@ss.hasPermission('platform:client-api:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClientApiExcel(@Valid ClientApiPageReqVO pageReqVO,
                                     HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClientApiDO> list = clientApiService.getClientApiPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "客户端-API授权关系表（含自定义定价）.xls", "数据", ClientApiRespVO.class,
                BeanUtils.toBean(list, ClientApiRespVO.class));
    }


    @PostMapping("/createAssociation")
    @Operation(summary = "创建客户端与API的关系")
    @PreAuthorize("@ss.hasPermission('platform:client-api:create')")
    public CommonResult<String> createAssociation(@Valid @RequestBody ClientApiCreateAssociationReqVO vo) {
        clientApiService.createAssociation(vo);
        return success("成功");
    }

}