package com.gitlab.neton.module.platform.controller.admin.chargerecord;

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

import com.gitlab.neton.module.platform.controller.admin.chargerecord.vo.*;
import com.gitlab.neton.module.platform.dal.dataobject.chargerecord.ChargeRecordDO;
import com.gitlab.neton.module.platform.service.chargerecord.ChargeRecordService;

@Tag(name = "管理后台 - 开放平台计费记录")
@RestController
@RequestMapping("/platform/charge-record")
@Validated
public class ChargeRecordController {

    @Resource
    private ChargeRecordService chargeRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建开放平台计费记录")
    @PreAuthorize("@ss.hasPermission('platform:charge-record:create')")
    public CommonResult<Long> createChargeRecord(@Valid @RequestBody ChargeRecordSaveReqVO createReqVO) {
        return success(chargeRecordService.createChargeRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新开放平台计费记录")
    @PreAuthorize("@ss.hasPermission('platform:charge-record:update')")
    public CommonResult<Boolean> updateChargeRecord(@Valid @RequestBody ChargeRecordSaveReqVO updateReqVO) {
        chargeRecordService.updateChargeRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除开放平台计费记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('platform:charge-record:delete')")
    public CommonResult<Boolean> deleteChargeRecord(@RequestParam("id") Long id) {
        chargeRecordService.deleteChargeRecord(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除开放平台计费记录")
                @PreAuthorize("@ss.hasPermission('platform:charge-record:delete')")
    public CommonResult<Boolean> deleteChargeRecordList(@RequestParam("ids") List<Long> ids) {
        chargeRecordService.deleteChargeRecordListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得开放平台计费记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('platform:charge-record:query')")
    public CommonResult<ChargeRecordRespVO> getChargeRecord(@RequestParam("id") Long id) {
        ChargeRecordDO chargeRecord = chargeRecordService.getChargeRecord(id);
        return success(BeanUtils.toBean(chargeRecord, ChargeRecordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得开放平台计费记录分页")
    @PreAuthorize("@ss.hasPermission('platform:charge-record:query')")
    public CommonResult<PageResult<ChargeRecordRespVO>> getChargeRecordPage(@Valid ChargeRecordPageReqVO pageReqVO) {
        PageResult<ChargeRecordDO> pageResult = chargeRecordService.getChargeRecordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ChargeRecordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出开放平台计费记录 Excel")
    @PreAuthorize("@ss.hasPermission('platform:charge-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportChargeRecordExcel(@Valid ChargeRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ChargeRecordDO> list = chargeRecordService.getChargeRecordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "开放平台计费记录.xls", "数据", ChargeRecordRespVO.class,
                        BeanUtils.toBean(list, ChargeRecordRespVO.class));
    }

}