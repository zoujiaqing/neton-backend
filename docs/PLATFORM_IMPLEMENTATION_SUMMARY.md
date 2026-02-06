# 开放平台（Platform）模块实现总结

## 📊 实现进度

### ✅ 已完成（100%）

| 阶段 | 任务 | 状态 | 说明 |
|------|------|------|------|
| **Phase 1** | **签名验证与鉴权** | ✅ | **核心功能** |
| 1.1 | 枚举类 | ✅ | ClientStatusEnum, ClientTypeEnum, ChargeTypeEnum, ChargeStatusEnum, ErrorCodeConstants |
| 1.2 | 签名工具类 | ✅ | SignatureUtil（HMAC-SHA256、参数展开、时间戳验证） |
| 1.3 | 鉴权服务 | ✅ | PlatformAuthService + Impl（签名验证、权限检查、防重放） |
| 1.4 | 认证对象 | ✅ | PlatformApiAuthentication（Spring Security 集成） |
| 1.5 | 签名过滤器 | ✅ | PlatformApiSignatureFilter（拦截 `/platform-api/**`） |
| 1.6 | Security 配置 | ✅ | PlatformSecurityConfiguration（注册过滤器） |
| **Phase 2** | **计费系统** | ✅ | **完整计费** |
| 2.1 | 计费服务 | ✅ | PlatformChargeService + Impl |
| 2.2 | 余额管理 | ✅ | 乐观锁扣减、自定义定价支持 |
| 2.3 | 计费记录 | ✅ | 自动记录扣费明细 |
| **Phase 3** | **示例接口** | ✅ | **演示用法** |
| 3.1 | 订单接口 | ✅ | PlatformOrderController（查询、创建） |

---

## 🏗️ 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                      第三方客户端请求                              │
│         POST /platform-api/order/create                              │
│         Headers: X-Client-Id, X-Timestamp, X-Trace-Id, X-Sign    │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ↓
        ┌──────────────────────────────────────────────┐
        │   PlatformApiSignatureFilter（签名验证过滤器）     │
        │   1. 验证时间戳（±300秒）                     │
        │   2. 检查 Trace-Id 重复（Redis）              │
        │   3. 查询客户端信息                           │
        │   4. 计算并验证签名（HMAC-SHA256）            │
        │   5. 查找 API 信息                            │
        │   6. 检查权限（白名单机制）                   │
        │   7. 检查 IP 白名单                           │
        │   8. 构建 PlatformApiAuthentication              │
        └──────────────────────────────────────────────┘
                           │
                           ↓
        ┌──────────────────────────────────────────────┐
        │   PlatformOrderController（业务接口）         │
        │   @PreAuthorize("@ss.hasPermission('...')")  │
        │   执行业务逻辑                                 │
        └──────────────────────────────────────────────┘
                           │
                           ↓
        ┌──────────────────────────────────────────────┐
        │   PlatformChargeService（计费服务）           │
        │   1. 查询价格（自定义 > 默认）                │
        │   2. 乐观锁扣减余额                           │
        │   3. 记录计费明细                             │
        └──────────────────────────────────────────────┘
                           │
                           ↓
                      返回业务结果
```

---

## 📁 文件清单

### 核心功能层

| 文件路径 | 说明 |
|---------|------|
| `neton-module-platform/enums/` | 枚举类（状态、类型、错误码） |
| `neton-module-platform/util/SignatureUtil.java` | 签名工具类 |
| `neton-module-platform/service/auth/PlatformAuthService.java` | 鉴权服务接口 |
| `neton-module-platform/service/auth/PlatformAuthServiceImpl.java` | 鉴权服务实现 |
| `neton-module-platform/service/charge/PlatformChargeService.java` | 计费服务接口 |
| `neton-module-platform/service/charge/PlatformChargeServiceImpl.java` | 计费服务实现 |

### 框架集成层

| 文件路径 | 说明 |
|---------|------|
| `neton-framework/.../PlatformApiAuthentication.java` | Platform API 认证对象（纯数据类） |
| `neton-module-platform/.../PlatformApiSignatureFilter.java` | 签名验证过滤器 |
| `neton-module-platform/.../PlatformSecurityConfiguration.java` | Security 配置 |

### 业务接口层

| 文件路径 | 说明 |
|---------|------|
| `neton-module-platform/controller/open/order/PlatformOrderController.java` | 订单接口示例 |

### 数据访问层

| 文件路径 | 说明 |
|---------|------|
| `neton-module-platform/dal/mysql/client/ClientMapper.java` | 客户端 Mapper（新增乐观锁方法） |
| `neton-module-platform/dal/mysql/api/ApiMapper.java` | API Mapper（新增路径查询） |
| `neton-module-platform/dal/mysql/clientapi/ClientApiMapper.java` | 授权关系 Mapper |

---

## 🚀 快速开始

### 1. 导入数据库表

```bash
# 导入表结构
mysql -h 127.0.0.1 -P 3306 -u root -p123456 livestreaming < backend/sql/mysql/platform.sql

# 导入测试数据
mysql -h 127.0.0.1 -P 3306 -u root -p123456 livestreaming < backend/sql/mysql/platform_test_data.sql
```

### 2. 启动应用

```bash
cd backend/neton-server
mvn spring-boot:run
```

### 3. 测试 Platform API 接口

#### 3.1 准备测试数据

数据库中已有测试客户端：
- **client_id**: `test_client_001`
- **client_secret**: `test_secret_001`（实际存储已加密）

已授权的 API：
- `platform:order:query` - `/order/query` (GET)
- `platform:order:create` - `/order/create` (POST)

#### 3.2 使用 Java SDK 调用

```java
import com.gitlab.neton.module.platform.util.SignatureUtil;
import java.util.*;

public class PlatformApiClient {
    public static void main(String[] args) throws Exception {
        String clientId = "test_client_001";
        String clientSecret = "test_secret_001";
        String apiUrl = "http://localhost:8080/platform-api/order/query";
        
        // 1. 准备参数
        Map<String, String> params = new TreeMap<>();
        params.put("x-client-id", clientId);
        params.put("x-timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("x-trace-id", UUID.randomUUID().toString());
        params.put("orderNo", "ORD20240108001");
        
        // 2. 计算签名
        String sign = SignatureUtil.calculateSign(params, clientSecret);
        
        // 3. 发送请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(apiUrl + "?orderNo=ORD20240108001"))
                .header("X-Client-Id", clientId)
                .header("X-Timestamp", params.get("x-timestamp"))
                .header("X-Trace-Id", params.get("x-trace-id"))
                .header("X-Sign", sign)
                .GET()
                .build();
        
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Response: " + response.body());
    }
}
```

#### 3.3 使用 cURL 测试

```bash
# 查询订单
curl -X GET "http://localhost:8080/platform-api/order/query?orderNo=ORD20240108001" \
  -H "X-Client-Id: test_client_001" \
  -H "X-Timestamp: $(date +%s)" \
  -H "X-Trace-Id: $(uuidgen)" \
  -H "X-Sign: <计算的签名>"

# 创建订单
curl -X POST "http://localhost:8080/platform-api/order/create" \
  -H "Content-Type: application/json" \
  -H "X-Client-Id: test_client_001" \
  -H "X-Timestamp: $(date +%s)" \
  -H "X-Trace-Id: $(uuidgen)" \
  -H "X-Sign: <计算的签名>" \
  -d '{"orderNo":"ORD001","amount":100}'
```

---

## 🎯 核心特性

### 1. 签名验证（符合 v1.1 规范）

✅ HMAC-SHA256 签名算法  
✅ UTC 时间戳（±300秒窗口）  
✅ Trace-Id 防重放（Redis TTL=300秒）  
✅ Header 小写规范化  
✅ JSON Body 递归展开  
✅ 常量时间签名比对（防时序攻击）

### 2. 权限控制（白名单机制）

✅ 两层白名单：API 定义 + 客户端授权  
✅ IP 白名单支持  
✅ 授权时间范围控制  
✅ 与 Spring Security `@PreAuthorize` 集成

### 3. 计费系统

✅ 自定义定价优先  
✅ 乐观锁余额扣减  
✅ 计费记录详细日志  
✅ 余额不足自动拒绝

### 4. 安全防护

✅ 时间戳防重放（±300秒）  
✅ Trace-Id 去重（Redis）  
✅ 客户端状态校验  
✅ 过期时间检查  
✅ IP 白名单

---

## 📝 后续开发建议

### 高优先级

1. **实现日志记录**：创建 `PlatformLogService` 异步记录请求日志
2. **实现限流**：基于 Redis 的客户端级和 API 级限流
3. **错误响应标准化**：统一 Platform API 错误响应格式
4. **余额预警**：当余额低于阈值时发送通知

### 中优先级

5. **统计任务**：创建定时任务，每日汇总统计数据到 `platform_stat`
6. **管理后台**：完善客户端、API、授权管理界面
7. **SDK 封装**：提供 Java/Python/Go 官方 SDK

### 低优先级

8. **Webhook 通知**：余额不足、授权过期等事件通知
9. **API 文档生成**：基于 `platform_api` 表自动生成 Platform API 文档
10. **多版本支持**：支持签名算法版本升级（v2.0: RSA）

---

## 🔍 故障排查

### 签名验证失败

1. **检查时间戳**：确保服务器时间同步（NTP）
2. **检查参数展开**：打印客户端和服务端的签名原文对比
3. **检查 Header 大小写**：确保转为小写参与签名
4. **检查 client_secret**：确认密钥一致

### 权限不足

1. **检查 API 定义**：确认 `platform_api` 表中存在该 API
2. **检查授权关系**：确认 `platform_client_api` 表中存在授权记录
3. **检查授权状态**：status=1 且在有效时间范围内
4. **检查 IP 白名单**：如果配置了白名单，确认 IP 在列表中

### 余额不足

1. **检查客户端余额**：查询 `platform_client.balance`
2. **检查 API 价格**：确认 `platform_api.default_price` 或自定义价格
3. **充值余额**：`UPDATE platform_client SET balance = balance + 100000 WHERE client_id = 'xxx'`

---

## 📚 相关文档

- [开放平台模块设计方案](PLATFORM_MODULE_DESIGN.md)
- [Platform API 签名规范 v1.1](openapi-signature-spec-v1.1.md)
- [数据库表结构](../sql/mysql/platform.sql)
- [测试数据](../sql/mysql/platform_test_data.sql)

---

## 🎉 总结

已完成开放平台（Platform）模块的**核心功能**实现，包括：

1. ✅ **签名验证与鉴权**（Phase 1）- 符合 openapi-signature-spec-v1.1 规范
2. ✅ **计费系统**（Phase 2）- 支持自定义定价、乐观锁扣减
3. ✅ **示例业务接口**（Phase 3）- 演示如何使用 `@PreAuthorize` 集成

**现在可以启动应用并测试 Platform API 功能！** 🚀

---

**版本**: v1.0  
**日期**: 2026-01-24  
**作者**: Neton Platform Team
