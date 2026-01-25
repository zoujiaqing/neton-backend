package com.gitlab.neton.module.platform.controller.admin.api;

import com.gitlab.neton.framework.apilog.core.annotation.ApiAccessLog;
import com.gitlab.neton.framework.common.pojo.CommonResult;
import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import com.gitlab.neton.framework.excel.core.util.ExcelUtils;
import com.gitlab.neton.module.platform.controller.admin.api.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.api.ApiDO;
import com.gitlab.neton.module.platform.service.api.ApiService;
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

@Tag(name = "管理后台 - 开放平台API定义")
@RestController
@RequestMapping("/platform/api")
@Validated
public class ApiController {

    @Resource
    private ApiService apiService;

    @PostMapping("/create")
    @Operation(summary = "创建开放平台API定义")
    @PreAuthorize("@ss.hasPermission('platform:api:create')")
    public CommonResult<Long> createApi(@Valid @RequestBody ApiSaveReqVO createReqVO) {
        return success(apiService.createApi(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新开放平台API定义")
    @PreAuthorize("@ss.hasPermission('platform:api:update')")
    public CommonResult<Boolean> updateApi(@Valid @RequestBody ApiSaveReqVO updateReqVO) {
        apiService.updateApi(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开放平台API定义")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('platform:api:delete')")
    public CommonResult<Boolean> deleteApi(@RequestParam("id") Long id) {
        apiService.deleteApi(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除开放平台API定义")
    @PreAuthorize("@ss.hasPermission('platform:api:delete')")
    public CommonResult<Boolean> deleteApiList(@RequestParam("ids") List<Long> ids) {
        apiService.deleteApiListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得开放平台API定义")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('platform:api:query')")
    public CommonResult<ApiRespVO> getApi(@RequestParam("id") Long id) {
        ApiDO api = apiService.getApi(id);
        return success(BeanUtils.toBean(api, ApiRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得开放平台API定义分页")
    @PreAuthorize("@ss.hasPermission('platform:api:query')")
    public CommonResult<PageResult<ApiRespVO>> getApiPage(@Valid ApiPageReqVO pageReqVO) {
        PageResult<ApiDO> pageResult = apiService.getApiPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ApiRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得开放平台API定义分页")
    @PreAuthorize("@ss.hasPermission('platform:api:query')")
    public CommonResult<List<ApiListRespVO>> getList(@Valid ApiListReqVO apiListReqVO) {
        List<ApiListRespVO> apiList = apiService.getApiList(apiListReqVO);

        return success(apiList);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出开放平台API定义 Excel")
    @PreAuthorize("@ss.hasPermission('platform:api:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportApiExcel(@Valid ApiPageReqVO pageReqVO,
                               HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ApiDO> list = apiService.getApiPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "开放平台API定义.xls", "数据", ApiRespVO.class,
                BeanUtils.toBean(list, ApiRespVO.class));
    }

}