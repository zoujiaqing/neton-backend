package com.gitlab.neton.module.platform.controller.open.order;

import com.gitlab.neton.framework.common.pojo.CommonResult;
import com.gitlab.neton.module.platform.controller.open.order.vo.OrderQueryReqVO;
import com.gitlab.neton.module.platform.controller.open.order.vo.OrderRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.gitlab.neton.framework.common.pojo.CommonResult.success;

/**
 * 开放平台 - 订单接口
 * <p>
 * 示例接口，展示如何使用 @PreAuthorize 结合 api_code 进行权限控制
 *
 * @author Neton
 */
@Tag(name = "开放平台 - 订单接口")
@RestController
@RequestMapping("/open-api/order")
@Validated
@Slf4j
public class PlatformOrderController {

    /**
     * 查询订单
     * <p>
     * 权限：platform:order:query
     */
    @GetMapping("/query")
    @Operation(summary = "查询订单")
    @PreAuthorize("@ss.hasPermission('platform:order:query')")
    public CommonResult<OrderRespVO> queryOrder(@Validated OrderQueryReqVO reqVO) {
        log.info("[queryOrder] 查询订单：orderNo={}", reqVO.getOrderNo());
        
        // TODO: 实际业务逻辑（查询数据库）
        OrderRespVO resp = new OrderRespVO();
        resp.setOrderNo(reqVO.getOrderNo());
        resp.setAmount(10000L);
        resp.setStatus(1);
        resp.setCreateTime(LocalDateTime.now());
        
        return success(resp);
    }

    /**
     * 创建订单
     * <p>
     * 权限：platform:order:create
     */
    @PostMapping("/create")
    @Operation(summary = "创建订单")
    @PreAuthorize("@ss.hasPermission('platform:order:create')")
    public CommonResult<Long> createOrder(@RequestBody @Validated Object reqVO) {
        log.info("[createOrder] 创建订单：reqVO={}", reqVO);
        
        // TODO: 实际业务逻辑（创建订单）
        return success(System.currentTimeMillis());
    }

}
