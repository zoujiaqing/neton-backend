package com.gitlab.neton.module.platform.dal.dataobject.stat;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitlab.neton.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDate;

/**
 * 开放平台统计 DO
 *
 * @author Neton
 */
@TableName("platform_stat")
@KeySequence("platform_stat_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDO extends BaseDO {

    /**
     * 统计ID
     */
    @TableId
    private Long id;
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * API ID（为空表示客户端维度统计）
     */
    private Long apiId;
    /**
     * 统计日期
     */
    private LocalDate statDate;
    /**
     * 总调用次数
     */
    private Integer totalCount;
    /**
     * 成功次数
     */
    private Integer successCount;
    /**
     * 失败次数
     */
    private Integer failedCount;
    /**
     * 平均耗时（毫秒）
     */
    private Integer avgDurationMs;
    /**
     * 最大耗时（毫秒）
     */
    private Integer maxDurationMs;
    /**
     * 总计费金额（分）
     */
    private Long totalCharge;
    /**
     * 免费调用次数
     */
    private Integer freeCount;
    /**
     * 计费调用次数
     */
    private Integer chargedCount;


}