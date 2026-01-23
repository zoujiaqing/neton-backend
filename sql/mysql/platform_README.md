# 开放平台模块数据库表结构

## 📋 文件说明

| 文件 | 说明 |
|-----|------|
| `platform.sql` | 核心表结构（6 张表） |
| `platform_test_data.sql` | 测试数据（可选） |

---

## 📊 表结构说明

### 核心表（6 张）

| 表名 | 说明 | 关键字段 |
|-----|------|---------|
| **platform_client** | 客户端表 | client_id, client_secret, balance |
| **platform_api** | API 定义表 | api_code, api_path, default_price |
| **platform_client_api** | 授权关系表 ⭐ | client_id, api_id, custom_price |
| **platform_charge_record** | 计费记录表 | trace_id, price, balance_before/after |
| **platform_log** | 调用日志表 | trace_id, client_id, api_path |
| **platform_stat** | 统计表（可选） | client_id, api_id, stat_date |

---

## 🚀 执行顺序

### 1. 创建表结构

```bash
mysql -u root -p ruoyi-vue-pro < platform.sql
```

### 2. 导入测试数据（可选）

```bash
mysql -u root -p ruoyi-vue-pro < platform_test_data.sql
```

---

## ✅ 验证安装

### 检查表是否创建成功

```sql
USE ruoyi-vue-pro;

-- 查看所有平台表
SHOW TABLES LIKE 'platform_%';

-- 应该返回 6 张表：
-- platform_api
-- platform_charge_record
-- platform_client
-- platform_client_api
-- platform_log
-- platform_stat
```

### 验证测试数据

```sql
-- 1. 查看客户端数量
SELECT COUNT(*) FROM platform_client;
-- 应返回：2

-- 2. 查看 API 数量
SELECT COUNT(*) FROM platform_api;
-- 应返回：4

-- 3. 查看授权关系数量
SELECT COUNT(*) FROM platform_client_api;
-- 应返回：6
```

---

## 🔑 核心设计特性

### 1. 白名单机制 ⭐

```
请求 API → 
  ① 检查 API 是否在 platform_api 中 → 
  ② 检查客户端是否在 platform_client_api 中被授权 → 
  ③ 允许访问
```

**示例**：
- ✅ `client_demo_001` 被授权访问 4 个 API → 可以访问
- ❌ `client_demo_002` 只被授权访问 2 个 API → 其他 API 拒绝访问
- ❌ `client_demo_003` 未授权任何 API → 全部拒绝访问

### 2. 自定义定价

| 客户端 | API | 价格 | 说明 |
|-------|-----|------|------|
| client_demo_001 | /open-api/order/create | 5 分 | 自定义价格（VIP） |
| client_demo_002 | /open-api/order/create | 10 分 | 默认价格（普通） |

### 3. IP 白名单

```sql
-- client_demo_001 配置了 IP 白名单
allowed_ips = '["192.168.1.100","192.168.1.101"]'

-- 只有这两个 IP 可以访问，其他 IP 拒绝
```

---

## 📝 测试数据说明

### 客户端

| client_id | 名称 | 余额 | 授权 API 数量 |
|-----------|------|------|------------|
| client_demo_001 | 演示客户端A | 100 万分（10000 元） | 4 个 |
| client_demo_002 | 演示客户端B | 50 万分（5000 元） | 2 个 |

### API 列表

| api_code (权限标识) | API 路径 | 方法 | 默认价格 |
|-------------------|---------|------|---------|
| platform:order:create | /order/create | POST | 10 分 |
| platform:order:query | /order/query | GET | 2 分 |
| platform:product:list | /product/list | GET | 1 分 |
| platform:user:info | /user/info | GET | 0 分（免费） |

**API 编码格式**：
- 采用权限标识格式：`platform:资源:操作`
- 与现有权限系统保持一致（如 `system:user:create`）
- 可直接用于权限检查：`@PreAuthorize("@ss.hasPermission('platform:order:create')")`

**访问方式**：
- **推荐**：域名映射 `POST api.xxx.com/order/create`
- **也可以**：路由前缀 `POST xxx.com/open-api/order/create`（需配置路由映射）

### 授权关系

**client_demo_001（VIP）**：
- ✅ order.create - 5 分（自定义）
- ✅ order.query - 2 分（默认）
- ✅ product.list - 1 分（默认）
- ✅ user.info - 0 分（免费）

**client_demo_002（普通）**：
- ✅ order.create - 10 分（默认）
- ✅ order.query - 2 分（默认）
- ❌ product.list - 未授权
- ❌ user.info - 未授权

---

## 🔍 常用查询

### 查询客户端的授权列表

```sql
SELECT 
    c.client_id,
    c.client_name,
    a.api_code,
    a.api_name,
    a.api_path,
    CASE 
        WHEN ca.is_custom_price = b'1' THEN ca.custom_price 
        ELSE a.default_price 
    END AS final_price,
    ca.status
FROM platform_client_api ca
JOIN platform_client c ON ca.client_id = c.client_id
JOIN platform_api a ON ca.api_id = a.id
WHERE ca.client_id = 'client_demo_001'
  AND ca.status = 1
  AND a.status = 1;
```

### 查询 API 的授权客户端列表

```sql
SELECT 
    a.api_code,
    a.api_name,
    c.client_id,
    c.client_name,
    CASE 
        WHEN ca.is_custom_price = b'1' THEN ca.custom_price 
        ELSE a.default_price 
    END AS final_price
FROM platform_api a
JOIN platform_client_api ca ON a.id = ca.api_id
JOIN platform_client c ON ca.client_id = c.client_id
WHERE a.api_code = 'order.create'
  AND ca.status = 1
  AND a.status = 1;
```

---

## ⚠️ 注意事项

### 1. client_secret 安全

```sql
-- ⚠️ 测试数据中的 client_secret 是明文，实际使用时必须加密存储
-- 建议使用 AES-256 加密
client_secret = 'secret_encrypted_demo_001'  -- 这是加密后的值
```

### 2. 余额单位

```sql
-- 所有金额字段单位都是"分"
balance = 1000000  -- 表示 10000 元（1000000 分）
```

### 3. 删除测试数据

```sql
-- 如需清空测试数据
DELETE FROM platform_client_api WHERE client_id LIKE 'client_demo_%';
DELETE FROM platform_api WHERE api_code LIKE '%.%';
DELETE FROM platform_client WHERE client_id LIKE 'client_demo_%';
```

---

## 📚 相关文档

- 设计文档：`backend/docs/PLATFORM_MODULE_DESIGN.md`
- 签名规范：`backend/docs/openapi-signature-spec-v1.1.md`

---

**创建日期**：2024-01-23  
**版本**：v4.1（白名单机制）
