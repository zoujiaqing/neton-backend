package com.gitlab.neton.module.platform.dal.dataobject.chargerecord;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.gitlab.neton.framework.mybatis.core.dataobject.BaseDO;

/**
 * 开放平台计费记录 DO
 *
 * @author Neton
 */
@TableName("platform_charge_record")
@KeySequence("platform_charge_record_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRecordDO extends BaseDO {

    /**
     * 计费ID
     */
    @TableId
    private Long id;
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * API ID
     */
    private Long apiId;
    /**
     * 请求跟踪ID（关联日志）
     */
    private String traceId;
    /**
     * 计费类型
     *
     * 枚举 {@link TODO platform_charge_type 对应的类}
     */
    private Integer chargeType;
    /**
     * 本次计费金额（分）
     */
    private Long price;
    /**
     * 是否使用自定义价格
     *
     * 枚举 {@link TODO platform_bool 对应的类}
     */
    private Boolean isCustomPrice;
    /**
     * 扣费前余额（分）
     */
    private Long balanceBefore;
    /**
     * 扣费后余额（分）
     */
    private Long balanceAfter;
    /**
     * 是否扣费成功
     *
     * 枚举 {@link TODO platform_bool 对应的类}
     */
    private Integer chargeStatus;
    /**
     * 失败原因
     */
    private String failureReason;
    /**
     * 扣费时间
     */
    private LocalDateTime chargeTime;


}