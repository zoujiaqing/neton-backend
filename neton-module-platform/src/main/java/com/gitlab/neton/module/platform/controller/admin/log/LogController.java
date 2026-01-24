package com.gitlab.neton.module.platform.controller.admin.log;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.gitlab.neton.framework.common.pojo.PageParam;
import com.gitlab.neton.framework.common.pojo.PageResult;
import com.gitlab.neton.framework.common.pojo.CommonResult;
import com.gitlab.neton.framework.common.util.object.BeanUtils;
import static com.gitlab.neton.framework.common.pojo.CommonResult.success;

import com.gitlab.neton.framework.excel.core.util.ExcelUtils;

import com.gitlab.neton.framework.apilog.core.annotation.ApiAccessLog;
import static com.gitlab.neton.framework.apilog.core.enums.OperateTypeEnum.*;

import com.gitlab.neton.module.platform.controller.admin.log.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.log.LogDO;
import com.gitlab.neton.module.platform.service.log.LogService;

@Tag(name = "管理后台 - 开放平台调用日志")
@RestController
@RequestMapping("/platform/log")
@Validated
public class LogController {

    @Resource
    private LogService logService;

    @PostMapping("/create")
    @Operation(summary = "创建开放平台调用日志")
    @PreAuthorize("@ss.hasPermission('platform:log:create')")
    public CommonResult<Long> createLog(@Valid @RequestBody LogSaveReqVO createReqVO) {
        return success(logService.createLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新开放平台调用日志")
    @PreAuthorize("@ss.hasPermission('platform:log:update')")
    public CommonResult<Boolean> updateLog(@Valid @RequestBody LogSaveReqVO updateReqVO) {
        logService.updateLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开放平台调用日志")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('platform:log:delete')")
    public CommonResult<Boolean> deleteLog(@RequestParam("id") Long id) {
        logService.deleteLog(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除开放平台调用日志")
                @PreAuthorize("@ss.hasPermission('platform:log:delete')")
    public CommonResult<Boolean> deleteLogList(@RequestParam("ids") List<Long> ids) {
        logService.deleteLogListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得开放平台调用日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('platform:log:query')")
    public CommonResult<LogRespVO> getLog(@RequestParam("id") Long id) {
        LogDO log = logService.getLog(id);
        return success(BeanUtils.toBean(log, LogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得开放平台调用日志分页")
    @PreAuthorize("@ss.hasPermission('platform:log:query')")
    public CommonResult<PageResult<LogRespVO>> getLogPage(@Valid LogPageReqVO pageReqVO) {
        PageResult<LogDO> pageResult = logService.getLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出开放平台调用日志 Excel")
    @PreAuthorize("@ss.hasPermission('platform:log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportLogExcel(@Valid LogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<LogDO> list = logService.getLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "开放平台调用日志.xls", "数据", LogRespVO.class,
                        BeanUtils.toBean(list, LogRespVO.class));
    }

}