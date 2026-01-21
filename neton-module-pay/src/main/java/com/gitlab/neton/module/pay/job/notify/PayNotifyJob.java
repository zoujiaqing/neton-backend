package com.gitlab.neton.module.pay.job.notify;

import com.gitlab.neton.framework.quartz.core.handler.JobHandler;
import com.gitlab.neton.module.pay.service.notify.PayNotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 支付通知 Job
 * 通过不断扫描待通知的 PayNotifyTaskDO 记录，回调业务线的回调接口
 *
 * @author Neton
 */
@Component
@Slf4j
public class PayNotifyJob implements JobHandler {

    @Resource
    private PayNotifyService payNotifyService;

    @Override
    public String execute(String param) throws Exception {
        int notifyCount = payNotifyService.executeNotify();
        return String.format("执行支付通知 %s 个", notifyCount);
    }

}
