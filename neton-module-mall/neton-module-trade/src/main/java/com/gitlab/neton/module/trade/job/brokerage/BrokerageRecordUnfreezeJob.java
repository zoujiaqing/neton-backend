package com.gitlab.neton.module.trade.job.brokerage;

import cn.hutool.core.util.StrUtil;
import com.gitlab.neton.framework.quartz.core.handler.JobHandler;
import com.gitlab.neton.module.trade.service.brokerage.BrokerageRecordService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 佣金解冻 Job
 *
 * @author owen
 */
@Component
public class BrokerageRecordUnfreezeJob implements JobHandler {

    @Resource
    private BrokerageRecordService brokerageRecordService;

    @Override
    public String execute(String param) {
        int count = brokerageRecordService.unfreezeRecord();
        return StrUtil.format("解冻佣金 {} 个", count);
    }

}
