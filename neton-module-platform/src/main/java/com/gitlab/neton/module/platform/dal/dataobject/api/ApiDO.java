package com.gitlab.neton.module.platform.dal.dataobject.api;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.gitlab.neton.framework.mybatis.core.dataobject.BaseDO;

/**
 * 开放平台API定义 DO
 *
 * @author Neton
 */
@TableName("platform_api")
@KeySequence("platform_api_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDO extends BaseDO {

    /**
     * API ID
     */
    @TableId
    private Long id;
    /**
     * API 编码
     */
    private String apiCode;
    /**
     * API 名称
     */
    private String apiName;
    /**
     * API 路径
     */
    private String apiPath;
    /**
     * HTTP 方法
     */
    private String httpMethod;
    /**
     * API 分类
     */
    private String category;
    /**
     * API 描述
     */
    private String description;
    /**
     * 状态
     *
     * 枚举 {@link TODO platform_client_status 对应的类}
     */
    private Integer status;
    /**
     * 每分钟限流
     */
    private Integer rateLimitPerMin;
    /**
     * 计费类型
     *
     * 枚举 {@link TODO platform_charge_type 对应的类}
     */
    private Integer chargeType;
    /**
     * 默认单价（分）
     */
    private Long defaultPrice;


}