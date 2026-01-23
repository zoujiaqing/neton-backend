# 开放平台模块设计方案（独立模块 + 计费系统）v4.0

## 一、模块架构

### 1.1 模块定位

**neton-module-platform**：独立的开放平台模块

- 与 system/member/pay 等模块平级
- 独立的数据库表（platform_ 前缀）
- 独立的业务逻辑
- 支持客户端授权管理
- 支持 API 计费系统 ⭐
- 可独立部署（未来微服务化）

---

### 1.2 核心命名规范 ⭐

**避免与 app-api 路由混淆**：

| 命名 | 说明 |
|------|------|
| **platform_client** | 客户端应用表 |
| **client_id** | 客户端唯一标识（符合 OAuth2 标准） |
| **client_secret** | 客户端密钥 |
| **api_code** | API 编码（权限标识：`platform:资源:操作`） |
| **PlatformClientDO** | 客户端实体类 |

**设计原则**：
- 采用 `client_id` + `client_secret` 命名（符合 OAuth2/OpenID Connect 标准），避免与系统内的 `app-api` 路由组产生混淆
- `api_code` 采用权限标识格式（`platform:资源:操作`），与现有权限系统保持一致（如 `system:user:create`）

**api_code 命名规范** ⭐：

| 格式 | 示例 | 说明 |
|-----|------|------|
| `platform:资源:操作` | `platform:order:create` | 创建订单 |
| `platform:资源:操作` | `platform:order:query` | 查询订单 |
| `platform:资源:操作` | `platform:product:list` | 商品列表 |
| `platform:资源:操作` | `platform:user:info` | 用户信息 |

**与现有权限系统对比**：
- 系统模块：`system:user:create`、`system:role:update`
- 平台模块：`platform:order:create`、`platform:product:list`
- 格式统一，便于权限管理和检查

---

### 1.3 模块结构

```
backend/
├── neton-module-system/      # 系统管理模块
├── neton-module-member/      # 会员模块
├── neton-module-pay/         # 支付模块
├── neton-module-platform/    # ⭐ 开放平台模块（新增）
│   ├── pom.xml
│   └── src/main/java/com/gitlab/neton/module/platform/
│       ├── controller/
│       │   ├── admin/                    # 管理端（平台管理员使用）
│       │   │   ├── client/
│       │   │   │   └── PlatformClientController.java
│       │   │   ├── api/
│       │   │   │   └── PlatformApiController.java
│       │   │   ├── charge/
│       │   │   │   └── PlatformChargeController.java
│       │   │   └── log/
│       │   │       └── PlatformLogController.java
│       │   └── open/                     # 开放端（第三方调用）
│       │       └── [业务接口可选择放在各自模块的 open 包下]
│       ├── dal/
│       │   ├── dataobject/
│       │   │   ├── client/
│       │   │   │   └── PlatformClientDO.java
│       │   │   ├── api/
│       │   │   │   ├── PlatformApiDO.java
│       │   │   │   └── PlatformClientApiDO.java
│       │   │   ├── charge/
│       │   │   │   └── PlatformChargeRecordDO.java
│       │   │   └── log/
│       │   │       └── PlatformLogDO.java
│       │   └── mapper/
│       │       ├── PlatformClientMapper.java
│       │       ├── PlatformApiMapper.java
│       │       ├── PlatformClientApiMapper.java
│       │       ├── PlatformChargeRecordMapper.java
│       │       └── PlatformLogMapper.java
│       ├── service/
│       │   ├── client/
│       │   │   ├── PlatformClientService.java
│       │   │   └── PlatformClientServiceImpl.java
│       │   ├── api/
│       │   │   ├── PlatformApiService.java
│       │   │   └── PlatformApiServiceImpl.java
│       │   ├── auth/
│       │   │   ├── PlatformAuthService.java
│       │   │   └── PlatformAuthServiceImpl.java
│       │   ├── charge/
│       │   │   ├── PlatformChargeService.java
│       │   │   └── PlatformChargeServiceImpl.java
│       │   └── log/
│       │       ├── PlatformLogService.java
│       │       └── PlatformLogServiceImpl.java
│       ├── api/
│       │   ├── PlatformAuthApi.java
│       │   └── PlatformChargeApi.java
│       └── enums/
│           ├── PlatformClientStatusEnum.java
│           ├── PlatformApiStatusEnum.java
│           └── PlatformChargeTypeEnum.java
└── neton-server/
    └── pom.xml
```

---

## 二、数据库设计（完整版 + 标准通用字段）

### 2.1 平台客户端表（platform_client）⭐ 核心

**职责**：存储第三方客户端应用信息

```sql
DROP TABLE IF EXISTS `platform_client`;
CREATE TABLE `platform_client` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户端ID',
    
    -- 客户端凭证（OAuth2 标准命名）⭐
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
    
    -- 计费与余额 ⭐
    `balance` bigint NOT NULL DEFAULT 0 COMMENT '账户余额（分）',
    `total_charged` bigint NOT NULL DEFAULT 0 COMMENT '累计消费金额（分）',
    `low_balance_alert` bigint NOT NULL DEFAULT 10000 COMMENT '余额不足预警阈值（分，默认100元）',
    
    -- 安全配置
    `allowed_ips` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '允许的IP白名单（JSON数组）',
    `webhook_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '回调地址（接收平台通知）',
    
    -- 时间字段
    `expired_time` datetime NULL DEFAULT NULL COMMENT '过期时间（为空表示永久有效）',
    `last_call_time` datetime NULL DEFAULT NULL COMMENT '最后调用时间',
    
    -- 标准通用字段 ⭐
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
```

---

### 2.2 平台API定义表（platform_api）

**职责**：定义所有可被开放平台调用的 API + 默认计费规则

**设计原则**：⭐
- 只有在此表中定义的 API 才能被调用（白名单第一层）
- 客户端必须在 `platform_client_api` 中被明确授权才能访问（白名单第二层）
- 不再需要 `is_public` 字段，所有 API 必须明确授权
- **API 路径直接存储业务路径**：如 `/order/create`，适配多种访问方式（域名映射、路由前缀等）
- **API 编码采用权限标识格式**：`platform:资源:操作`（如 `platform:order:create`），与权限系统保持一致

**API 编码格式说明**：⭐

```
格式：platform:资源:操作
示例：
  - platform:order:create   (创建订单)
  - platform:order:query    (查询订单)
  - platform:product:list   (商品列表)
  - platform:user:info      (用户信息)

与现有权限系统对比：
  - system:user:create    (系统用户创建)
  - platform:order:create (平台订单创建)
```

**优势**：
1. ✅ 与现有权限系统格式统一（`模块:资源:操作`）
2. ✅ 可以直接用于权限检查 `@PreAuthorize("@ss.hasPermission('platform:order:create')")`
3. ✅ 语义清晰，易于理解和管理
4. ✅ 支持权限分组和批量管理

```sql
DROP TABLE IF EXISTS `platform_api`;
CREATE TABLE `platform_api` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'API ID',
    
    -- API 标识
    `api_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API 编码（权限标识格式：platform:资源:操作，如：platform:order:create）',
    `api_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API 名称',
    `api_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API 路径（业务路径，如：/order/create，不包含 /open-api 前缀）',
    `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'HTTP 方法（GET/POST/PUT/DELETE）',
    
    -- 分类与描述
    `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API 分类（order/user/product等）',
    `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'API 描述',
    
    -- 状态
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=停用 1=正常',
    
    -- 限流配置（API 级别）
    `rate_limit_per_min` int NOT NULL DEFAULT 100 COMMENT '每分钟限流（次/分钟/客户端）',
    
    -- 计费配置（默认定价）⭐
    `charge_type` tinyint NOT NULL DEFAULT 0 COMMENT '计费类型：0=免费 1=按次计费 2=按量计费',
    `default_price` bigint NOT NULL DEFAULT 0 COMMENT '默认单价（分）',
    
    -- 标准通用字段 ⭐
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
```

---

### 2.3 客户端-API授权关系表（platform_client_api）⭐ 核心

**职责**：客户端和 API 的多对多关系（权限控制 + 自定义定价核心）

**白名单机制**：⭐
- 只有在此表中存在授权记录的客户端才能访问对应的 API
- 未授权的客户端一律拒绝访问（默认拒绝原则）
- 授权记录必须满足：状态正常 + 在有效时间范围内

```sql
DROP TABLE IF EXISTS `platform_client_api`;
CREATE TABLE `platform_client_api` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    
    -- 关联关系 ⭐
    `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ID（关联 platform_client.client_id）',
    `api_id` bigint NOT NULL COMMENT 'API ID（关联 platform_api.id）',
    
    -- 状态
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0=停用 1=正常',
    
    -- 配额（客户端级别对该 API 的配额）
    `rate_limit_per_min` int NULL DEFAULT NULL COMMENT '每分钟限流（覆盖 API 默认配置）',
    `rate_limit_per_day` int NULL DEFAULT NULL COMMENT '每日配额（覆盖客户端默认配置）',
    
    -- 自定义定价（核心字段）⭐
    `is_custom_price` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否自定义价格：0=使用默认价格 1=使用自定义价格',
    `custom_price` bigint NULL DEFAULT NULL COMMENT '自定义价格（分，仅当 is_custom_price=1 时有效）',
    
    -- 时间范围
    `start_time` datetime NULL DEFAULT NULL COMMENT '授权开始时间',
    `end_time` datetime NULL DEFAULT NULL COMMENT '授权结束时间（为空表示永久）',
    
    -- 标准通用字段 ⭐
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
```

---

### 2.4 平台计费记录表（platform_charge_record）⭐

**职责**：记录每次 API 调用的计费明细

```sql
DROP TABLE IF EXISTS `platform_charge_record`;
CREATE TABLE `platform_charge_record` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '计费ID',
    
    -- 关联信息 ⭐
    `client_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '客户端ID',
    `api_id` bigint NOT NULL COMMENT 'API ID',
    `trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '请求跟踪ID（关联日志）',
    
    -- 计费信息 ⭐
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
```

---

### 2.5 平台调用日志表（platform_log）

**职责**：记录所有 Open-API 调用日志

```sql
DROP TABLE IF EXISTS `platform_log`;
CREATE TABLE `platform_log` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    
    -- 请求标识 ⭐
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
    
    -- 计费关联 ⭐
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
```

---

### 2.6 平台统计表（platform_stat）（可选）

**职责**：按日汇总统计数据，加速查询

```sql
DROP TABLE IF EXISTS `platform_stat`;
CREATE TABLE `platform_stat` (
    -- 主键
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    
    -- 统计维度 ⭐
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
    
    -- 计费统计 ⭐
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
```

---

## 三、核心业务逻辑（含计费流程）

### 3.1 权限检查与 api_code ⭐

**与现有权限系统集成**：

```java
// Controller 示例：可以直接使用 api_code 进行权限检查
@RestController
@RequestMapping("/open-api/order")
public class PlatformOrderController {
    
    /**
     * 创建订单（需要平台权限：platform:order:create）
     */
    @PostMapping("/create")
    @PreAuthorize("@ss.hasPermission('platform:order:create')")
    public CommonResult<Long> createOrder(@RequestBody OrderCreateReqVO reqVO) {
        // 业务逻辑
        return success(orderService.createOrder(reqVO));
    }
    
    /**
     * 查询订单（需要平台权限：platform:order:query）
     */
    @GetMapping("/query")
    @PreAuthorize("@ss.hasPermission('platform:order:query')")
    public CommonResult<OrderVO> queryOrder(@RequestParam String orderNo) {
        // 业务逻辑
        return success(orderService.queryOrder(orderNo));
    }
}
```

**权限校验流程**：

```java
// OpenApiSignatureFilter 中
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
    // 1. 验证签名（client_id + client_secret）
    validateSignature(request);
    
    // 2. 获取 API 信息
    String apiPath = request.getRequestURI();
    PlatformApiDO api = platformApiMapper.selectByPathAndMethod(apiPath, httpMethod);
    
    // 3. 检查客户端是否被授权（白名单机制）
    boolean hasPermission = platformAuthService.hasApiPermission(clientId, api.getId());
    if (!hasPermission) {
        throw new ServiceException(PERMISSION_DENIED, "未授权访问 API: " + api.getApiCode());
    }
    
    // 4. 将 api_code 存入 SecurityContext，供 @PreAuthorize 使用
    // 这样可以统一使用 @PreAuthorize("@ss.hasPermission('platform:order:create')")
    SecurityContextHolder.getContext().setAuthentication(
        new OpenApiAuthentication(clientId, api.getApiCode())
    );
    
    // 5. 计费（如需要）
    chargeService.chargeForApiCall(clientId, api.getId(), traceId);
    
    chain.doFilter(request, response);
}
```

**优势**：
1. ✅ 与现有权限系统格式完全一致
2. ✅ 可以使用 `@PreAuthorize` 注解进行权限控制
3. ✅ 权限标识清晰易懂（`platform:order:create`）
4. ✅ 支持权限分组管理（按模块、资源、操作）

---

### 3.2 计费服务（PlatformChargeService）⭐

**核心方法**：

```java
/**
 * 为 API 调用计费
 * 
 * @param clientId 客户端ID
 * @param apiId API ID
 * @param traceId 请求跟踪ID
 * @param success 是否调用成功
 * @return 计费记录
 */
PlatformChargeRecordDO chargeForApiCall(String clientId, Long apiId, String traceId, boolean success);

/**
 * 获取计费价格（自定义价格优先）
 * 
 * @param clientId 客户端ID
 * @param apiId API ID
 * @param api API 对象
 * @return 价格（分）
 */
Long getChargePrice(String clientId, Long apiId, PlatformApiDO api);
```

---

### 3.3 签名验证服务（PlatformAuthService）

**请求 Header**：

| Header | 说明 |
|--------|------|
| X-Client-Id | 客户端ID（对应 client_id）⭐ |
| X-Timestamp | Unix Epoch 时间戳（秒） |
| X-Trace-Id | UUID v4 |
| X-Sign | HMAC-SHA256 签名 |

**签名原文构造**：

```java
// 参与签名的参数（转小写）
x-client-id=client_123456
x-timestamp=1704700000
x-trace-id=550e8400-e29b-41d4-a716-446655440000
order_no=ORD001
amount=100

// 拼接签名原文（字典序）
amount=100&order_no=ORD001&x-client-id=client_123456&x-timestamp=1704700000&x-trace-id=550e8400-e29b-41d4-a716-446655440000

// HMAC-SHA256
sign = hmac_sha256(签名原文, client_secret)
```

---

### 3.4 权限控制逻辑 ⭐

**两层检查机制（白名单模式）**：

```java
/**
 * API 权限校验流程（默认拒绝原则）
 */
public boolean hasApiPermission(String clientId, String apiPath, String httpMethod) {
    // 第一层：API 是否在平台中定义
    // apiPath 直接从请求中获取（如 /order/create）
    PlatformApiDO api = platformApiMapper.selectByPathAndMethod(apiPath, httpMethod);
    if (api == null) {
        // API 未定义，拒绝访问
        return false;
    }
    
    // 检查 API 状态
    if (api.getStatus() != 1) {
        // API 已停用，拒绝访问
        return false;
    }
    
    // 第二层：客户端是否被授权访问该 API
    PlatformClientApiDO clientApi = platformClientApiMapper.selectByClientIdAndApiId(clientId, api.getId());
    if (clientApi == null) {
        // 未授权，拒绝访问
        return false;
    }
    
    // 检查授权状态
    if (clientApi.getStatus() != 1) {
        // 授权已停用，拒绝访问
        return false;
    }
    
    // 检查授权时间范围
    LocalDateTime now = LocalDateTime.now();
    if (clientApi.getStartTime() != null && now.isBefore(clientApi.getStartTime())) {
        // 授权未生效，拒绝访问
        return false;
    }
    if (clientApi.getEndTime() != null && now.isAfter(clientApi.getEndTime())) {
        // 授权已过期，拒绝访问
        return false;
    }
    
    // 通过所有检查，允许访问
    return true;
}
```

**权限控制原则**：

| 场景 | 结果 | 说明 |
|-----|------|------|
| API 不在 `platform_api` 表中 | ❌ 拒绝 | API 未定义 |
| API 状态为停用 | ❌ 拒绝 | API 已关闭 |
| 客户端未在 `platform_client_api` 中授权 | ❌ 拒绝 | 未授权访问 ⭐ |
| 授权状态为停用 | ❌ 拒绝 | 授权已撤销 |
| 授权未到生效时间 | ❌ 拒绝 | 授权未生效 |
| 授权已过期 | ❌ 拒绝 | 授权已过期 |
| 通过所有检查 | ✅ 允许 | 正常访问 |

**路径匹配逻辑**：⭐

```java
// 接收请求路径（可能来自多种方式）
// 方式1: 域名映射 - api.xxx.com/order/create
// 方式2: 路由前缀 - xxx.com/open-api/order/create
String requestPath = request.getRequestURI();  // 例如: "/order/create"

// 直接使用请求路径在 platform_api 表中查询
PlatformApiDO api = platformApiMapper.selectByPathAndMethod(requestPath, "POST");
```

**为什么这样设计**：
1. ✅ **适配多种访问方式**：域名映射（`api.xxx.com`）或路由前缀（`/open-api`）
2. ✅ **数据库数据简洁**：只存储业务路径
3. ✅ **灵活性更高**：部署方式改变不影响数据

**访问方式示例**：

| 访问方式 | 客户端请求 | 路径匹配 |
|---------|----------|---------|
| 域名映射 | `POST api.xxx.com/order/create` | `/order/create` ✅ |
| 路由前缀 | `POST xxx.com/open-api/order/create` | 需去除前缀 `/open-api` |
| 子路径 | `POST xxx.com/v1/order/create` | 需去除前缀 `/v1` |

**推荐部署方式**：域名映射，客户端直接请求 `api.xxx.com/order/create`，服务端无需处理前缀。

---

**权限校验流程图**：

```
请求 /order/create (或 /open-api/order/create)
    ↓
[1] API 是否在 platform_api 中？
    ├─ 否 → ❌ 返回 404 API_NOT_FOUND
    └─ 是 → 继续
    ↓
[2] API 状态是否正常（status=1）？
    ├─ 否 → ❌ 返回 403 API_DISABLED
    └─ 是 → 继续
    ↓
[3] client_id 是否在 platform_client_api 中授权？
    ├─ 否 → ❌ 返回 403 PERMISSION_DENIED（未授权）⭐
    └─ 是 → 继续
    ↓
[4] 授权状态是否正常（status=1）？
    ├─ 否 → ❌ 返回 403 PERMISSION_REVOKED
    └─ 是 → 继续
    ↓
[5] 授权是否在有效时间范围内？
    ├─ 否 → ❌ 返回 403 PERMISSION_EXPIRED
    └─ 是 → 继续
    ↓
✅ 权限校验通过，允许访问
```

**设计优势**：

1. **默认拒绝**：未明确授权的请求一律拒绝（安全第一）⭐
2. **双重验证**：API 定义 + 客户端授权，两层保障
3. **细粒度控制**：可以精确控制每个客户端对每个 API 的访问权限
4. **灵活管理**：可以随时启用/停用 API 或授权关系
5. **时间控制**：支持临时授权（开始时间 + 结束时间）
6. **安全第一**：白名单机制，不存在"公开 API"的概念

---

## 四、请求示例

### 4.1 完整的 HTTP 请求

**方式1：域名映射（推荐）** ⭐

```http
POST /order/create HTTP/1.1
Host: api.example.com
Content-Type: application/json
X-Client-Id: client_123456
X-Timestamp: 1704700000
X-Trace-Id: 550e8400-e29b-41d4-a716-446655440000
X-Sign: 3a8f5e7d9b2c1a4f6e8d7c5b3a9f1e2d4c6b8a7f5e3d1c9b7a5f3e1d9c7b5a3f

{
  "order_no": "ORD20240108001",
  "amount": 100
}
```

**方式2：路由前缀（可选）**

```http
POST /open-api/order/create HTTP/1.1
Host: www.example.com
Content-Type: application/json
X-Client-Id: client_123456
X-Timestamp: 1704700000
X-Trace-Id: 550e8400-e29b-41d4-a716-446655440000
X-Sign: 3a8f5e7d9b2c1a4f6e8d7c5b3a9f1e2d4c6b8a7f5e3d1c9b7a5f3e1d9c7b5a3f

{
  "order_no": "ORD20240108001",
  "amount": 100
}
```

**说明**：
- 推荐使用域名映射方式（`api.example.com`），客户端调用更简洁
- 使用路由前缀方式需要在框架层配置路由映射

---

## 五、计费定价逻辑

### 5.1 定价优先级

```
1. platform_client_api.is_custom_price = 1
   → 使用 platform_client_api.custom_price（自定义价格）⭐

2. platform_client_api.is_custom_price = 0 或 未授权
   → 使用 platform_api.default_price（默认价格）
```

### 5.2 定价示例

| API | 默认价格 | 客户端A（VIP） | 客户端B（普通） |
|-----|---------|--------------|---------------|
| /order/create | 10分/次 | **5分/次** ✅ | 10分/次 |
| /product/list | 2分/次 | **1分/次** ✅ | 2分/次 |
| /user/query | 免费 | 免费 | 免费 |

### 5.3 数据库配置示例

```sql
-- 1. 定义 API（默认价格）
INSERT INTO platform_api (api_code, api_name, api_path, http_method, default_price, status) 
VALUES ('platform:order:create', '创建订单', '/order/create', 'POST', 10, 1);

-- 2. 授权给客户端A（自定义价格 - VIP）⭐
INSERT INTO platform_client_api (client_id, api_id, is_custom_price, custom_price, status)
VALUES ('client_A', 1, 1, 5, 1);  -- ✅ 自定义5分，状态正常

-- 3. 授权给客户端B（使用默认价格 - 普通）⭐
INSERT INTO platform_client_api (client_id, api_id, is_custom_price, custom_price, status)
VALUES ('client_B', 1, 0, NULL, 1);  -- 使用默认10分，状态正常

-- 4. 未授权的客户端C ❌
-- 客户端C 没有 platform_client_api 记录，因此无法调用任何 API
```

**权限效果**：

| 客户端 | 请求 API | 价格 | 说明 |
|-------|---------|------|------|
| client_A | /order/create | 5分/次 | ✅ 已授权，自定义价格 |
| client_B | /order/create | 10分/次 | ✅ 已授权，默认价格 |
| client_C | /order/create | - | ❌ 未授权（白名单机制）⭐ |
| client_D | /order/create | - | ❌ 未授权（白名单机制）⭐ |

**说明**：
- API 路径存储格式：`/order/create`（业务路径）
- 客户端请求可以是：`api.xxx.com/order/create` 或 `xxx.com/open-api/order/create`
- 推荐使用域名映射方式，简化客户端调用

---

## 六、标准通用字段说明 ⭐

### 6.1 所有表必须包含的字段

```sql
-- 审计字段（标准格式）
`creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除'
```

### 6.2 字段说明

| 字段 | 类型 | 默认值 | 说明 |
|-----|------|--------|------|
| creator | varchar(64) | '' | 创建者（用户名或系统标识） |
| create_time | datetime | CURRENT_TIMESTAMP | 创建时间（自动填充） |
| updater | varchar(64) | '' | 更新者 |
| update_time | datetime | CURRENT_TIMESTAMP ON UPDATE | 更新时间（自动更新） |
| deleted | bit(1) | b'0' | 逻辑删除标记（0=未删除 1=已删除） |

---

## 七、实施步骤

### Phase 1：创建模块骨架（1 小时）

1. 创建 `neton-module-platform` 目录
2. 创建 `pom.xml`
3. 创建包结构（client/api/charge/log）
4. 父 pom 引入模块
5. Server 引入依赖

---

### Phase 2：数据库设计（2 小时）

1. 创建 SQL 脚本：`sql/mysql/platform.sql`
2. 创建 5 张核心表：
   - `platform_client`（客户端表）
   - `platform_api`（API 定义表）
   - `platform_client_api`（授权关系表）
   - `platform_charge_record`（计费记录表）
   - `platform_log`（调用日志表）
3. 所有表包含标准通用字段
4. 执行 SQL 脚本

---

### Phase 3：框架层扩展（1 小时）

1. 扩展 `UserTypeEnum`（增加 OPEN）
2. 扩展 `WebProperties`（增加 openApi 配置）
3. 扩展 `WebFrameworkUtils`（增加 /open-api 识别）
4. 扩展 `NetonWebAutoConfiguration`（注册路由前缀）

---

### Phase 4：Security 层实现（2 小时）

1. 创建 `OpenApiSignatureFilter`
2. 实现签名验证（使用 client_id + client_secret）
3. **实现 API 权限校验（白名单机制）** ⭐
   - 检查 API 是否在 platform_api 中定义
   - 检查客户端是否在 platform_client_api 中被授权
   - 检查授权状态和时间范围
4. 注册 Filter

---

### Phase 5：业务层实现（4 小时）

1. 创建 DO/Mapper
2. 实现客户端管理 Service
3. 实现 API 管理 Service
4. 实现计费服务 Service
5. 实现日志记录 Service

---

### Phase 6：Controller 层（2 小时）

1. 实现 `PlatformClientController`（客户端管理）
2. 实现 `PlatformApiController`（API 管理）
3. 实现 `PlatformChargeController`（计费管理）
4. 实现 `PlatformLogController`（日志查询）

---

### Phase 7：测试与优化（2 小时）

1. 单元测试
2. 集成测试
3. 性能测试

**预计总工作量：14-16 小时**

---

## 八、架构优势总结

### 8.1 命名优势 ⭐

| 维度 | 优势 |
|-----|------|
| **避免混淆** | platform_client 不会和 app-api 路由混淆 |
| **符合标准** | client_id + client_secret 符合 OAuth2 标准 |
| **语义清晰** | 一眼看出是"客户端"而非"应用" |
| **专业性** | 与行业惯例一致（OAuth2/OpenID Connect） |

### 8.2 权限控制优势 ⭐

| 维度 | 优势 |
|-----|------|
| **白名单机制** | 默认拒绝，只有明确授权才能访问 |
| **两层检查** | API 定义检查 + 客户端授权检查 |
| **细粒度控制** | 精确控制每个客户端对每个 API 的访问 |
| **时间维度** | 支持临时授权（开始时间 + 结束时间） |
| **状态管理** | 可随时启用/停用 API 或授权关系 |

### 8.3 标准通用字段优势

| 维度 | 优势 |
|-----|------|
| **统一规范** | 与现有模块完全一致 |
| **审计完整** | creator/updater 记录操作人 |
| **逻辑删除** | deleted 字段支持软删除 |
| **时间追溯** | create_time/update_time 完整记录 |

---

## 九、总结

### 核心设计亮点 ⭐

1. **规范的命名**
   - `platform_client`（避免和 app-api 混淆）
   - `client_id` + `client_secret`（符合 OAuth2 标准）

2. **标准的通用字段**
   - 完全对齐现有模块的字段格式
   - 支持审计、逻辑删除

3. **完整的授权体系**
   - 客户端 ↔ API 多对多关系
   - **白名单机制**：默认拒绝，只有明确授权才能访问 ⭐
   - **两层检查**：API 定义检查 + 客户端授权检查
   - 支持时间范围授权
   - 支持自定义配额

4. **灵活的计费系统**
   - 默认价格（API 级别）
   - 自定义价格（客户端-API 级别）
   - 所有金额单位：分
   - 完整的计费审计

5. **生产级设计**
   - 余额管理 + 预警
   - 并发安全（乐观锁）
   - 计费失败隔离
   - 完整的日志审计

---

**文档版本**: v4.1（待实现的设计方案 - 白名单机制）  
**最后更新**: 2024-01-23  
**文档状态**: 规划设计阶段（尚未实施）  
**核心特性**: 规范命名 + 客户端管理 + **白名单授权** + 灵活定价 + 计费系统  
**设计说明**: 本文档是开放平台模块的完整设计方案，采用**白名单机制**（默认拒绝，明确授权才允许访问）

---

**END OF DOCUMENT**
