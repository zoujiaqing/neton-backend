/*
 开放平台模块数据库表结构
 
 Database: ruoyi-vue-pro
 Module: neton-module-platform
 Version: v4.1
 Date: 2024-01-23
 
 说明：
 1. 所有表采用 platform_ 前缀
 2. 采用白名单机制（默认拒绝，明确授权才允许访问）
 3. 客户端命名：client_id + client_secret（符合 OAuth2 标准）
 4. 所有金额单位：分
 5. API 路径只存储业务路径（如 /order/create），不含域名或路由前缀
    - 推荐访问方式：api.xxx.com/order/create（域名映射）
    - 可选访问方式：xxx.com/open-api/order/create（路由前缀）
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for platform_client
-- ----------------------------
DROP TABLE IF EXISTS `platform_client`;
CREATE TABLE `platform_client` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户端ID',
    
    -- 客户端凭证（OAuth2 标准命名）
    `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端唯一标识（公开）',
    `client_secret` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端密钥（AES-256 加密存储）',
    
    -- 基本信息
    `client_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端名称',
    `client_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端编码（英文标识）',
    `client_logo` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端Logo URL',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端描述',
    
    -- 商务信息
    `company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '公司名称',
    `business_license` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '营业执照号',
    `contact_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人姓名',
    `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人邮箱',
    `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人电话',
    
    -- 状态与类型
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=停用 1=正常 2=待审核',
    `client_type` tinyint NOT NULL DEFAULT 1 COMMENT '客户端类型：1=企业应用 2=个人应用 3=内部应用',
    
    -- 配额与限流
    `rate_limit_per_min` int NOT NULL DEFAULT 1000 COMMENT '每分钟频率限制（次/分钟）',
    `rate_limit_per_day` int NOT NULL DEFAULT 100000 COMMENT '每日调用配额',
    `used_count_today` int NOT NULL DEFAULT 0 COMMENT '今日已用次数',
    `total_used_count` bigint NOT NULL DEFAULT 0 COMMENT '累计调用次数',
    
    -- 计费与余额
    `balance` bigint NOT NULL DEFAULT 0 COMMENT '账户余额（分）',
    `total_charged` bigint NOT NULL DEFAULT 0 COMMENT '累计消费金额（分）',
    `low_balance_alert` bigint NOT NULL DEFAULT 10000 COMMENT '余额不足预警阈值（分，默认100元）',
    
    -- 安全配置
    `allowed_ips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '允许的IP白名单（JSON数组）',
    `webhook_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回调地址（接收平台通知）',
    
    -- 时间字段
    `expired_time` datetime NULL DEFAULT NULL COMMENT '过期时间（为空表示永久有效）',
    `last_call_time` datetime NULL DEFAULT NULL COMMENT '最后调用时间',
    
    -- 标准通用字段
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_client_id` (`client_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开放平台客户端表';

-- ----------------------------
-- Table structure for platform_api
-- ----------------------------
DROP TABLE IF EXISTS `platform_api`;
CREATE TABLE `platform_api` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'API ID',
    
    -- API 标识
    `api_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API 编码（权限标识格式：platform:资源:操作，如：platform:order:create）',
    `api_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API 名称',
    `api_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API 路径（业务路径，如：/order/create，不包含路由组前缀）',
    `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HTTP 方法（GET/POST/PUT/DELETE）',
    
    -- 分类与描述
    `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API 分类（order/user/product等）',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API 描述',
    
    -- 状态
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=停用 1=正常',
    
    -- 限流配置（API 级别）
    `rate_limit_per_min` int NOT NULL DEFAULT 100 COMMENT '每分钟限流（次/分钟/客户端）',
    
    -- 计费配置（默认定价）
    `charge_type` tinyint NOT NULL DEFAULT 0 COMMENT '计费类型：0=免费 1=按次计费 2=按量计费',
    `default_price` bigint NOT NULL DEFAULT 0 COMMENT '默认单价（分）',
    
    -- 标准通用字段
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_api_code` (`api_code`) USING BTREE,
    INDEX `idx_api_path` (`api_path`(255)) USING BTREE,
    INDEX `idx_category` (`category`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_charge_type` (`charge_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开放平台API定义表';

-- ----------------------------
-- Table structure for platform_client_api
-- ----------------------------
DROP TABLE IF EXISTS `platform_client_api`;
CREATE TABLE `platform_client_api` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    
    -- 关联关系
    `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ID（关联 platform_client.client_id）',
    `api_id` bigint NOT NULL COMMENT 'API ID（关联 platform_api.id）',
    
    -- 状态
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=停用 1=正常',
    
    -- 配额（客户端级别对该 API 的配额）
    `rate_limit_per_min` int NULL DEFAULT NULL COMMENT '每分钟限流（覆盖 API 默认配置）',
    `rate_limit_per_day` int NULL DEFAULT NULL COMMENT '每日配额（覆盖客户端默认配置）',
    
    -- 自定义定价（核心字段）
    `is_custom_price` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否自定义价格：0=使用默认价格 1=使用自定义价格',
    `custom_price` bigint NULL DEFAULT NULL COMMENT '自定义价格（分，仅当 is_custom_price=1 时有效）',
    
    -- 时间范围
    `start_time` datetime NULL DEFAULT NULL COMMENT '授权开始时间',
    `end_time` datetime NULL DEFAULT NULL COMMENT '授权结束时间（为空表示永久）',
    
    -- 标准通用字段
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_client_api` (`client_id`, `api_id`) USING BTREE,
    INDEX `idx_client_id` (`client_id`) USING BTREE,
    INDEX `idx_api_id` (`api_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_is_custom_price` (`is_custom_price`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户端-API授权关系表（含自定义定价）';

-- ----------------------------
-- Table structure for platform_charge_record
-- ----------------------------
DROP TABLE IF EXISTS `platform_charge_record`;
CREATE TABLE `platform_charge_record` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计费ID',
    
    -- 关联信息
    `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ID',
    `api_id` bigint NOT NULL COMMENT 'API ID',
    `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求跟踪ID（关联日志）',
    
    -- 计费信息
    `charge_type` tinyint NOT NULL COMMENT '计费类型：0=免费 1=按次 2=按量',
    `price` bigint NOT NULL COMMENT '本次计费金额（分）',
    `is_custom_price` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否使用自定义价格',
    
    -- 余额变动
    `balance_before` bigint NOT NULL COMMENT '扣费前余额（分）',
    `balance_after` bigint NOT NULL COMMENT '扣费后余额（分）',
    
    -- 结果
    `charge_status` tinyint NOT NULL DEFAULT 1 COMMENT '扣费状态：1=成功 2=失败（余额不足）',
    `failure_reason` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '失败原因',
    
    -- 时间
    `charge_time` datetime NOT NULL COMMENT '扣费时间',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_client_id_charge_time` (`client_id`, `charge_time`) USING BTREE,
    INDEX `idx_trace_id` (`trace_id`) USING BTREE,
    INDEX `idx_api_id` (`api_id`) USING BTREE,
    INDEX `idx_charge_status` (`charge_status`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开放平台计费记录表';

-- ----------------------------
-- Table structure for platform_log
-- ----------------------------
DROP TABLE IF EXISTS `platform_log`;
CREATE TABLE `platform_log` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    
    -- 请求标识
    `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求跟踪ID（对应 X-Trace-Id）',
    `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ID',
    
    -- API 信息
    `api_id` bigint NULL DEFAULT NULL COMMENT 'API ID（关联 platform_api.id）',
    `api_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API 编码',
    `api_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API 路径',
    `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HTTP 方法',
    
    -- 请求信息
    `request_headers` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求头（JSON）',
    `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数（JSON）',
    `request_body` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求体（JSON）',
    `request_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求IP',
    `request_user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'User-Agent',
    
    -- 响应信息
    `response_status` int NULL DEFAULT NULL COMMENT 'HTTP 状态码',
    `response_body` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '响应内容（截断，保留前 10KB）',
    
    -- 性能
    `duration_ms` int NULL DEFAULT NULL COMMENT '耗时（毫秒）',
    
    -- 结果
    `success` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否成功',
    `error_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误码',
    `error_msg` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '错误信息',
    
    -- 计费关联
    `charge_price` bigint NOT NULL DEFAULT 0 COMMENT '本次计费金额（分）',
    `charge_status` tinyint NULL DEFAULT NULL COMMENT '扣费状态：1=成功 2=失败（余额不足）',
    
    -- 时间
    `request_time` datetime NOT NULL COMMENT '请求时间（UTC）',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_client_id_request_time` (`client_id`, `request_time`) USING BTREE,
    INDEX `idx_trace_id` (`trace_id`) USING BTREE,
    INDEX `idx_api_id` (`api_id`) USING BTREE,
    INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开放平台调用日志表';

-- ----------------------------
-- Table structure for platform_stat
-- ----------------------------
DROP TABLE IF EXISTS `platform_stat`;
CREATE TABLE `platform_stat` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    
    -- 统计维度
    `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ID',
    `api_id` bigint NULL DEFAULT NULL COMMENT 'API ID（为空表示客户端维度统计）',
    `stat_date` date NOT NULL COMMENT '统计日期',
    
    -- 调用统计
    `total_count` int NOT NULL DEFAULT 0 COMMENT '总调用次数',
    `success_count` int NOT NULL DEFAULT 0 COMMENT '成功次数',
    `failed_count` int NOT NULL DEFAULT 0 COMMENT '失败次数',
    
    -- 性能统计
    `avg_duration_ms` int NOT NULL DEFAULT 0 COMMENT '平均耗时（毫秒）',
    `max_duration_ms` int NOT NULL DEFAULT 0 COMMENT '最大耗时（毫秒）',
    
    -- 计费统计
    `total_charge` bigint NOT NULL DEFAULT 0 COMMENT '总计费金额（分）',
    `free_count` int NOT NULL DEFAULT 0 COMMENT '免费调用次数',
    `charged_count` int NOT NULL DEFAULT 0 COMMENT '计费调用次数',
    
    -- 时间
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_client_api_date` (`client_id`, `api_id`, `stat_date`) USING BTREE,
    INDEX `idx_stat_date` (`stat_date`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开放平台统计表';

SET FOREIGN_KEY_CHECKS = 1;
