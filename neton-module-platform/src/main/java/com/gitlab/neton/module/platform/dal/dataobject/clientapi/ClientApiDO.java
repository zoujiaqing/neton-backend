package com.gitlab.neton.module.platform.dal.dataobject.clientapi;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.gitlab.neton.framework.mybatis.core.dataobject.BaseDO;

/**
 * 客户端-API授权关系表（含自定义定价） DO
 *
 * @author Neton
 */
@TableName("platform_client_api")
@KeySequence("platform_client_api_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientApiDO extends BaseDO {

    /**
     * 关系ID
     */
    @TableId
    private Long id;
    /**
     * 客户端
     */
    private String clientId;
    /**
     * API ID
     */
    private Long apiId;
    /**
     * 是否启用
     *
     * 枚举 {@link TODO platform_bool 对应的类}
     */
    private Integer status;
    /**
     * 每分钟限流（覆盖 API 默认配置）
     */
    private Integer rateLimitPerMin;
    /**
     * 每日配额（覆盖客户端默认配置）
     */
    private Integer rateLimitPerDay;
    /**
     * 是否自定义价格
     *
     * 枚举 {@link TODO platform_bool 对应的类}
     */
    private Boolean isCustomPrice;
    /**
     * 自定义价格（分，仅当 is_custom_price=1 时有效）
     */
    private Long customPrice;
    /**
     * 授权开始时间
     */
    private LocalDateTime startTime;
    /**
     * 授权结束时间（为空表示永久）
     */
    private LocalDateTime endTime;


}