package com.gitlab.neton.module.platform.controller.admin.stat;

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

import com.gitlab.neton.module.platform.controller.admin.stat.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.stat.StatDO;
import com.gitlab.neton.module.platform.service.stat.StatService;

@Tag(name = "管理后台 - 开放平台统计")
@RestController
@RequestMapping("/platform/stat")
@Validated
public class StatController {

    @Resource
    private StatService statService;

    @PostMapping("/create")
    @Operation(summary = "创建开放平台统计")
    @PreAuthorize("@ss.hasPermission('platform:stat:create')")
    public CommonResult<Long> createStat(@Valid @RequestBody StatSaveReqVO createReqVO) {
        return success(statService.createStat(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新开放平台统计")
    @PreAuthorize("@ss.hasPermission('platform:stat:update')")
    public CommonResult<Boolean> updateStat(@Valid @RequestBody StatSaveReqVO updateReqVO) {
        statService.updateStat(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开放平台统计")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('platform:stat:delete')")
    public CommonResult<Boolean> deleteStat(@RequestParam("id") Long id) {
        statService.deleteStat(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除开放平台统计")
                @PreAuthorize("@ss.hasPermission('platform:stat:delete')")
    public CommonResult<Boolean> deleteStatList(@RequestParam("ids") List<Long> ids) {
        statService.deleteStatListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得开放平台统计")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('platform:stat:query')")
    public CommonResult<StatRespVO> getStat(@RequestParam("id") Long id) {
        StatDO stat = statService.getStat(id);
        return success(BeanUtils.toBean(stat, StatRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得开放平台统计分页")
    @PreAuthorize("@ss.hasPermission('platform:stat:query')")
    public CommonResult<PageResult<StatRespVO>> getStatPage(@Valid StatPageReqVO pageReqVO) {
        PageResult<StatDO> pageResult = statService.getStatPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StatRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出开放平台统计 Excel")
    @PreAuthorize("@ss.hasPermission('platform:stat:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStatExcel(@Valid StatPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StatDO> list = statService.getStatPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "开放平台统计.xls", "数据", StatRespVO.class,
                        BeanUtils.toBean(list, StatRespVO.class));
    }

}