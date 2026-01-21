package com.gitlab.neton.module.promotion.job.coupon;

import cn.hutool.core.util.StrUtil;
import com.gitlab.neton.framework.quartz.core.handler.JobHandler;
import com.gitlab.neton.module.promotion.service.coupon.CouponService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 优惠券过期 Job
 *
 * @author owen
 */
@Component
public class CouponExpireJob implements JobHandler {

    @Resource
    private CouponService couponService;

    @Override
    public String execute(String param) {
        int count = couponService.expireCoupon();
        return StrUtil.format("过期优惠券 {} 个", count);
    }

}
