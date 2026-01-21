package com.gitlab.neton.module.trade.job.order;

import com.gitlab.neton.framework.quartz.core.handler.JobHandler;
import com.gitlab.neton.module.trade.service.order.TradeOrderUpdateService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 交易订单的自动收货 Job
 *
 * @author Neton
 */
@Component
public class TradeOrderAutoReceiveJob implements JobHandler {

    @Resource
    private TradeOrderUpdateService tradeOrderUpdateService;

    @Override
    public String execute(String param) {
        int count = tradeOrderUpdateService.receiveOrderBySystem();
        return String.format("自动收货 %s 个", count);
    }

}
