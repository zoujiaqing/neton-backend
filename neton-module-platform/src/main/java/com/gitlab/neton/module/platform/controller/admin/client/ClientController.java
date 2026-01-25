package com.gitlab.neton.module.platform.controller.admin.client;

import com.gitlab.neton.framework.apilog.core.annotation.ApiAccessLog;
import com.gitlab.neton.framework.common.pojo.CommonResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import com.gitlab.neton.framework.excel.core.util.ExcelUtils;
import com.gitlab.neton.module.platform.controller.admin.client.vo.ClientPageReqVO;
import com.gitlab.neton.module.platform.controller.admin.client.vo.ClientRespVO;
import com.gitlab.neton.module.platform.controller.admin.client.vo.ClientSaveReqVO;
import com.gitlab.neton.module.platform.dal.dataobject.client.ClientDO;
import com.gitlab.neton.module.platform.service.client.ClientService;
import com.gitlab.neton.module.platform.util.AppCredentialGeneratorUtil;
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

@Tag(name = "管理后台 - 开放平台客户端")
@RestController
@RequestMapping("/platform/client")
@Validated
public class ClientController {
    
    @Resource
    private ClientService clientService;

    @PostMapping("/create")
    @Operation(summary = "创建开放平台客户端")
    @PreAuthorize("@ss.hasPermission('platform:client:create')")
    public CommonResult<Long> createClient(@Valid @RequestBody ClientSaveReqVO createReqVO) {
        return success(clientService.createClient(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新开放平台客户端")
    @PreAuthorize("@ss.hasPermission('platform:client:update')")
    public CommonResult<Boolean> updateClient(@Valid @RequestBody ClientSaveReqVO updateReqVO) {
        clientService.updateClient(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开放平台客户端")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('platform:client:delete')")
    public CommonResult<Boolean> deleteClient(@RequestParam("id") Long id) {
        clientService.deleteClient(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除开放平台客户端")
    @PreAuthorize("@ss.hasPermission('platform:client:delete')")
    public CommonResult<Boolean> deleteClientList(@RequestParam("ids") List<Long> ids) {
        clientService.deleteClientListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得开放平台客户端")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('platform:client:query')")
    public CommonResult<ClientRespVO> getClient(@RequestParam("id") Long id) {
        ClientDO client = clientService.getClient(id);
        return success(BeanUtils.toBean(client, ClientRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得开放平台客户端分页")
    @PreAuthorize("@ss.hasPermission('platform:client:query')")
    public CommonResult<PageResult<ClientRespVO>> getClientPage(@Valid ClientPageReqVO pageReqVO) {
        PageResult<ClientDO> pageResult = clientService.getClientPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ClientRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出开放平台客户端 Excel")
    @PreAuthorize("@ss.hasPermission('platform:client:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClientExcel(@Valid ClientPageReqVO pageReqVO,
                                  HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClientDO> list = clientService.getClientPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "开放平台客户端.xls", "数据", ClientRespVO.class,
                BeanUtils.toBean(list, ClientRespVO.class));
    }


    @GetMapping("/generateAppId")
    @Operation(summary = "生成APP凭证")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<String> generateAppId() {

        String appId = "";
        while (true) {
            appId = AppCredentialGeneratorUtil.generateAppId();
            ClientDO clientByAppid = clientService.getClientByAppid(appId);
            if (clientByAppid == null) {
                break;
            }
        }

        return success(appId);
    }

    @GetMapping("/generateAppSecret")
    @Operation(summary = "生成APP凭证")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<String> generateAppSecret() {
        return success(AppCredentialGeneratorUtil.generateAppSecret());
    }


}