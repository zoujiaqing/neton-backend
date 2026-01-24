package com.gitlab.neton.module.platform.dal.dataobject.client;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import com.gitlab.neton.framework.mybatis.core.dataobject.BaseDO;

/**
 * 开放平台客户端 DO
 *
 * @author Neton
 */
@TableName("platform_client")
@KeySequence("platform_client_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDO extends BaseDO {

    /**
     * 客户端ID
     */
    @TableId
    private Long id;
    /**
     * 客户端唯一标识（公开）
     */
    private String clientId;
    /**
     * 客户端密钥（AES-256 加密存储）
     */
    private String clientSecret;
    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 客户端编码（英文标识）
     */
    private String clientCode;
    /**
     * 客户端Logo URL
     */
    private String clientLogo;
    /**
     * 客户端描述
     */
    private String description;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 营业执照号
     */
    private String businessLicense;
    /**
     * 联系人姓名
     */
    private String contactName;
    /**
     * 联系人邮箱
     */
    private String contactEmail;
    /**
     * 联系人电话
     */
    private String contactPhone;
    /**
     * 状态
     *
     * 枚举 {@link TODO platform_client_status 对应的类}
     */
    private Integer status;
    /**
     * 客户端类型
     *
     * 枚举 {@link TODO platform_client_type 对应的类}
     */
    private Integer clientType;
    /**
     * 每分钟频率限制（次/分钟）
     */
    private Integer rateLimitPerMin;
    /**
     * 每日调用配额
     */
    private Integer rateLimitPerDay;
    /**
     * 今日已用次数
     */
    private Integer usedCountToday;
    /**
     * 累计调用次数
     */
    private Long totalUsedCount;
    /**
     * 账户余额（分）
     */
    private Long balance;
    /**
     * 累计消费金额（分）
     */
    private Long totalCharged;
    /**
     * 余额不足预警阈值（分，默认100元）
     */
    private Long lowBalanceAlert;
    /**
     * 允许的IP白名单
     */
    private String allowedIps;
    /**
     * 回调地址（接收平台通知）
     */
    private String webhookUrl;
    /**
     * 过期时间（为空表示永久有效）
     */
    private LocalDateTime expiredTime;
    /**
     * 最后调用时间
     */
    private LocalDateTime lastCallTime;


}