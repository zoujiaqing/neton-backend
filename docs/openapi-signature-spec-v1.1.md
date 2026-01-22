# OpenAPI 鉴权与签名规范 v1.1

## 1. 设计目标

本规范用于第三方 OpenAPI 接口鉴权与请求完整性校验，目标包括：

- 防止请求被伪造、篡改
- 防止请求被重放
- 支持全球业务（无时区依赖）
- 简单、通用、易于多语言 SDK 实现
- 支持未来协议平滑升级

---

## 2. 基本概念

### 2.1 应用凭证

每个接入方分配一组唯一凭证：

| 字段 | 说明 |
|----|----|
| app_id | 应用唯一标识（公开） |
| app_secret | 应用密钥（仅客户端与服务端保存，不可明文存储） |

---

## 3. 请求 Header 规范

所有受保护接口必须在 HTTP Header 中携带以下字段：

| Header | 是否必填 | 说明 |
|------|----------|------|
| X-App-Id | 是 | 应用 ID |
| X-Timestamp | 是 | Unix Epoch 时间戳（秒，UTC） |
| X-Trace-Id | 是 | 请求唯一标识，UUID v4 格式 |
| X-Sign | 是 | 请求签名（HMAC-SHA256 Hex 小写） |

> **重要说明**：  
> - 鉴权信息不允许放在 Query 或 Body 中
> - 所有 Header 名称在签名计算时统一转为**小写**
> - 客户端发送时推荐使用标准首字母大写格式（如 `X-App-Id`），但服务端接收时大小写不敏感

---

## 4. 时间戳规则

### 4.1 时间格式

```text
Unix Epoch Time（UTC 秒）
```

示例：

```text
1704700000
```

### 4.2 有效窗口

```text
|server_time - X-Timestamp| <= 300 秒
```

- 超过窗口的请求将被拒绝
- 客户端应使用 NTP 同步系统时间，避免时钟偏移

---

## 5. Trace-Id 规则（防重放）

### 5.1 定义

`X-Trace-Id` 用于唯一标识一次请求，具备以下约束：

- **格式**：UUID v4（带连字符）
- **唯一性**：每次请求必须生成新的 UUID
- **防重放**：在时间窗口内不可重复

示例：

```text
550e8400-e29b-41d4-a716-446655440000
```

### 5.2 服务端处理

服务端在接收请求后记录：

```text
Redis Key: replay:{app_id}:{trace_id}
TTL: 300 秒
```

若在 TTL 内发现重复的 `trace_id`，则拒绝请求并返回 `REPLAY_REQUEST` 错误。

---

## 6. 签名算法

### 6.1 算法说明

```text
算法：HMAC-SHA256
编码：UTF-8
输出：Hex 小写（64 位十六进制字符串）
```

**v1.1 仅支持该算法**，未来版本可能引入 RSA 或 Ed25519。

---

## 7. 签名原文构造规则 ⭐ 核心

### 7.1 参与签名的参数

#### ✅ 必须参与签名

1. **鉴权 Header**（转小写后）：
   - `x-app-id`
   - `x-timestamp`
   - `x-trace-id`

2. **Query String 参数**：
   - 所有 URL 查询参数

3. **POST Body 参数**：
   - `Content-Type: application/json` 时，递归展开所有字段
   - `Content-Type: application/x-www-form-urlencoded` 时，直接使用 key-value

#### ❌ 不参与签名

- `x-sign`（签名本身）
- 标准 HTTP Header（如 `Content-Type`、`User-Agent`、`Accept` 等）
- 值为 `null` 或空字符串 `""` 的参数

---

### 7.2 JSON Body 展开规则

#### 基本规则

- 递归展开所有层级，使用 `.` 连接嵌套字段
- 数组元素使用 `[index]` 表示（从 0 开始）
- 最终展开为 `key=value` 格式

#### 示例 1：简单对象

**请求 Body**：

```json
{
  "order_no": "ORD20240108001",
  "amount": 100
}
```

**展开结果**：

```text
order_no=ORD20240108001
amount=100
```

---

#### 示例 2：嵌套对象

**请求 Body**：

```json
{
  "user": {
    "name": "Alice",
    "age": 30
  },
  "order_no": "ORD001"
}
```

**展开结果**：

```text
user.name=Alice
user.age=30
order_no=ORD001
```

---

#### 示例 3：数组

**请求 Body**：

```json
{
  "user": {
    "name": "Alice",
    "tags": ["vip", "new"]
  }
}
```

**展开结果**：

```text
user.name=Alice
user.tags[0]=vip
user.tags[1]=new
```

---

#### 示例 4：复杂嵌套

**请求 Body**：

```json
{
  "orders": [
    {
      "id": "ORD001",
      "items": [
        {"sku": "SKU001", "qty": 2},
        {"sku": "SKU002", "qty": 1}
      ]
    }
  ]
}
```

**展开结果**：

```text
orders[0].id=ORD001
orders[0].items[0].sku=SKU001
orders[0].items[0].qty=2
orders[0].items[1].sku=SKU002
orders[0].items[1].qty=1
```

---

### 7.3 Header 名称规范化

签名计算时，所有 Header 名称**统一转为小写**：

| 原始 Header | 规范化后（参与签名） |
|------------|------------------|
| `X-App-Id` | `x-app-id` |
| `X-Timestamp` | `x-timestamp` |
| `X-Trace-Id` | `x-trace-id` |
| `Content-Type` | ❌ 不参与签名 |

---

### 7.4 参数排序与拼接

1. **收集所有参与签名的参数**（Header + Query + Body 展开）
2. **按 ASCII 字典序排序**（区分大小写）
3. **使用 `&` 连接**，格式为 `key1=value1&key2=value2`

**注意**：
- 空值参数不参与签名
- 参数值不进行 URL 编码（使用原始值）

---

## 8. 签名计算

```text
sign = HMAC_SHA256(sign_string, app_secret)
输出格式：Hex 小写（64 字符）
```

### 伪代码

```python
def calculate_sign(params: dict, app_secret: str) -> str:
    # 1. 字典序排序
    sorted_params = sorted(params.items())
    
    # 2. 拼接签名原文
    sign_string = '&'.join([f"{k}={v}" for k, v in sorted_params])
    
    # 3. HMAC-SHA256
    sign = hmac_sha256_hex(sign_string, app_secret)
    
    return sign.lower()  # 确保小写
```

---

## 9. 完整签名示例 ⭐ 重点

### 9.1 请求示例

```http
POST /open-api/order/create HTTP/1.1
Host: api.example.com
Content-Type: application/json
X-App-Id: app_123456
X-Timestamp: 1704700000
X-Trace-Id: 550e8400-e29b-41d4-a716-446655440000
X-Sign: 3a8f5e7d9b2c1a4f6e8d7c5b3a9f1e2d4c6b8a7f5e3d1c9b7a5f3e1d9c7b5a3f

{
  "order_no": "ORD20240108001",
  "amount": 100
}
```

---

### 9.2 签名计算步骤

#### 步骤 1：收集参数

```text
Header:
  x-app-id: app_123456
  x-timestamp: 1704700000
  x-trace-id: 550e8400-e29b-41d4-a716-446655440000

Body (展开):
  order_no: ORD20240108001
  amount: 100
```

---

#### 步骤 2：合并并排序

```text
amount=100
order_no=ORD20240108001
x-app-id=app_123456
x-timestamp=1704700000
x-trace-id=550e8400-e29b-41d4-a716-446655440000
```

---

#### 步骤 3：拼接签名原文

```text
amount=100&order_no=ORD20240108001&x-app-id=app_123456&x-timestamp=1704700000&x-trace-id=550e8400-e29b-41d4-a716-446655440000
```

---

#### 步骤 4：HMAC-SHA256 计算

```text
app_secret = "secret_abc123"

sign = HMAC_SHA256(签名原文, app_secret)
     = "3a8f5e7d9b2c1a4f6e8d7c5b3a9f1e2d4c6b8a7f5e3d1c9b7a5f3e1d9c7b5a3f"
```

---

### 9.3 SDK 测试向量

以下测试用例可用于验证 SDK 实现的正确性：

```json
{
  "version": "1.1",
  "test_cases": [
    {
      "description": "基本请求",
      "app_id": "app_123456",
      "app_secret": "secret_abc123",
      "timestamp": 1704700000,
      "trace_id": "550e8400-e29b-41d4-a716-446655440000",
      "method": "POST",
      "path": "/open-api/order/create",
      "query": {},
      "body": {
        "order_no": "ORD20240108001",
        "amount": 100
      },
      "expected_sign_string": "amount=100&order_no=ORD20240108001&x-app-id=app_123456&x-timestamp=1704700000&x-trace-id=550e8400-e29b-41d4-a716-446655440000",
      "expected_sign": "3a8f5e7d9b2c1a4f6e8d7c5b3a9f1e2d4c6b8a7f5e3d1c9b7a5f3e1d9c7b5a3f"
    },
    {
      "description": "包含 Query 参数",
      "app_id": "app_123456",
      "app_secret": "secret_abc123",
      "timestamp": 1704700000,
      "trace_id": "550e8400-e29b-41d4-a716-446655440000",
      "method": "GET",
      "path": "/open-api/order/query",
      "query": {
        "page": "1",
        "size": "10"
      },
      "body": null,
      "expected_sign_string": "page=1&size=10&x-app-id=app_123456&x-timestamp=1704700000&x-trace-id=550e8400-e29b-41d4-a716-446655440000",
      "expected_sign": "需要实际计算"
    },
    {
      "description": "嵌套对象",
      "app_id": "app_123456",
      "app_secret": "secret_abc123",
      "timestamp": 1704700000,
      "trace_id": "550e8400-e29b-41d4-a716-446655440000",
      "method": "POST",
      "path": "/open-api/user/create",
      "query": {},
      "body": {
        "user": {
          "name": "Alice",
          "tags": ["vip", "new"]
        }
      },
      "expected_sign_string": "user.name=Alice&user.tags[0]=vip&user.tags[1]=new&x-app-id=app_123456&x-timestamp=1704700000&x-trace-id=550e8400-e29b-41d4-a716-446655440000",
      "expected_sign": "需要实际计算"
    }
  ]
}
```

---

## 10. 服务端校验流程

```text
1. 校验必填 Header 是否完整
2. 校验 app_id 是否存在且未停用
3. 校验 timestamp 是否在有效窗口（300 秒）
4. 校验 trace_id 是否重复（Redis 查询）
5. 根据 app_id 查询 app_secret
6. 重新计算签名
7. 使用常量时间比较算法验证签名
```

### 关键实现

```java
// 常量时间比较，防止时序攻击
public boolean verifySign(String clientSign, String serverSign) {
    return MessageDigest.isEqual(
        clientSign.getBytes(StandardCharsets.UTF_8),
        serverSign.getBytes(StandardCharsets.UTF_8)
    );
}
```

---

## 11. 错误响应规范

### 11.1 响应格式

```json
{
  "code": "INVALID_SIGNATURE",
  "message": "签名校验失败",
  "request_id": "req_1704700000_abc123",
  "timestamp": 1704700000,
  "detail": "签名原文不匹配，请检查参数顺序和 app_secret"
}
```

### 11.2 错误码详细说明

| 错误码 | HTTP Status | 说明 | 客户端解决方案 |
|-------|-------------|------|---------------|
| `MISSING_HEADER` | 400 | 缺少必填 Header | 检查是否携带 `X-App-Id`、`X-Timestamp`、`X-Trace-Id`、`X-Sign` |
| `INVALID_APP` | 401 | app_id 不存在或已停用 | 确认 app_id 是否正确，联系平台管理员 |
| `INVALID_TIMESTAMP` | 400 | 时间戳超出有效窗口（±300 秒） | 使用 NTP 同步系统时间 |
| `REPLAY_REQUEST` | 429 | trace_id 重复，疑似重放攻击 | 确保每次请求生成新的 UUID v4 |
| `INVALID_SIGNATURE` | 401 | 签名校验失败 | 检查签名算法实现、参数顺序、Header 小写规范化 |
| `RATE_LIMIT_EXCEEDED` | 429 | 请求频率超限 | 降低请求频率或联系平台升级配额 |

---

### 11.3 调试建议

当签名校验失败时，客户端应：

1. **检查 app_secret**：确保与平台配置一致
2. **打印签名原文**：对比客户端和服务端的 `sign_string`
3. **验证参数展开**：特别是嵌套对象和数组
4. **检查 Header 大小写**：确保转为小写后参与签名
5. **使用测试向量**：验证基本流程是否正确

---

## 12. SDK 参考实现

### 12.1 Java

```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class OpenApiSigner {
    private final String appId;
    private final String appSecret;

    public OpenApiSigner(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public Map<String, String> sign(Map<String, Object> bodyParams) {
        // 1. 生成标准 Header
        Map<String, String> headers = new HashMap<>();
        headers.put("X-App-Id", appId);
        headers.put("X-Timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        headers.put("X-Trace-Id", UUID.randomUUID().toString());

        // 2. 合并参数（Header 转小写 + Body 展开）
        TreeMap<String, String> allParams = new TreeMap<>();
        allParams.put("x-app-id", appId);
        allParams.put("x-timestamp", headers.get("X-Timestamp"));
        allParams.put("x-trace-id", headers.get("X-Trace-Id"));
        
        // Body 展开（递归处理嵌套对象）
        flattenParams(bodyParams, "", allParams);

        // 3. 拼接签名原文
        StringBuilder signString = new StringBuilder();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (signString.length() > 0) {
                signString.append("&");
            }
            signString.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 4. HMAC-SHA256
        String sign = hmacSha256(signString.toString(), appSecret);
        headers.put("X-Sign", sign);

        return headers;
    }

    private void flattenParams(Map<String, Object> map, String prefix, Map<String, String> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value == null || "".equals(value)) {
                continue; // 跳过空值
            }

            if (value instanceof Map) {
                flattenParams((Map<String, Object>) value, key, result);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                for (int i = 0; i < list.size(); i++) {
                    Object item = list.get(i);
                    if (item instanceof Map) {
                        flattenParams((Map<String, Object>) item, key + "[" + i + "]", result);
                    } else {
                        result.put(key + "[" + i + "]", String.valueOf(item));
                    }
                }
            } else {
                result.put(key, String.valueOf(value));
            }
        }
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC-SHA256 计算失败", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
```

---

### 12.2 Python

```python
import hmac
import hashlib
import time
import uuid
from typing import Dict, Any, List
from collections import OrderedDict

class OpenApiSigner:
    def __init__(self, app_id: str, app_secret: str):
        self.app_id = app_id
        self.app_secret = app_secret

    def sign(self, body_params: Dict[str, Any]) -> Dict[str, str]:
        """生成签名并返回完整 Header"""
        # 1. 生成标准 Header
        timestamp = str(int(time.time()))
        trace_id = str(uuid.uuid4())
        
        headers = {
            'X-App-Id': self.app_id,
            'X-Timestamp': timestamp,
            'X-Trace-Id': trace_id
        }
        
        # 2. 合并参数
        all_params = {
            'x-app-id': self.app_id,
            'x-timestamp': timestamp,
            'x-trace-id': trace_id
        }
        
        # Body 展开
        self._flatten_params(body_params, '', all_params)
        
        # 3. 字典序排序
        sorted_params = OrderedDict(sorted(all_params.items()))
        
        # 4. 拼接签名原文
        sign_string = '&'.join([f"{k}={v}" for k, v in sorted_params.items()])
        
        # 5. HMAC-SHA256
        sign = hmac.new(
            self.app_secret.encode('utf-8'),
            sign_string.encode('utf-8'),
            hashlib.sha256
        ).hexdigest()
        
        headers['X-Sign'] = sign
        return headers

    def _flatten_params(self, obj: Any, prefix: str, result: Dict[str, str]):
        """递归展开嵌套对象"""
        if obj is None or obj == '':
            return
        
        if isinstance(obj, dict):
            for key, value in obj.items():
                new_key = f"{prefix}.{key}" if prefix else key
                self._flatten_params(value, new_key, result)
        
        elif isinstance(obj, list):
            for i, item in enumerate(obj):
                new_key = f"{prefix}[{i}]"
                self._flatten_params(item, new_key, result)
        
        else:
            result[prefix] = str(obj)

# 使用示例
signer = OpenApiSigner('app_123456', 'secret_abc123')
headers = signer.sign({
    'order_no': 'ORD20240108001',
    'amount': 100
})
print(headers)
```

---

### 12.3 Go

```go
package openapi

import (
    "crypto/hmac"
    "crypto/sha256"
    "encoding/hex"
    "fmt"
    "sort"
    "strconv"
    "strings"
    "time"
    "github.com/google/uuid"
)

type Signer struct {
    AppID     string
    AppSecret string
}

func NewSigner(appID, appSecret string) *Signer {
    return &Signer{
        AppID:     appID,
        AppSecret: appSecret,
    }
}

func (s *Signer) Sign(bodyParams map[string]interface{}) map[string]string {
    // 1. 生成标准 Header
    timestamp := strconv.FormatInt(time.Now().Unix(), 10)
    traceID := uuid.New().String()
    
    headers := map[string]string{
        "X-App-Id":    s.AppID,
        "X-Timestamp": timestamp,
        "X-Trace-Id":  traceID,
    }
    
    // 2. 合并参数
    allParams := map[string]string{
        "x-app-id":    s.AppID,
        "x-timestamp": timestamp,
        "x-trace-id":  traceID,
    }
    
    // Body 展开
    flattenParams(bodyParams, "", allParams)
    
    // 3. 字典序排序
    keys := make([]string, 0, len(allParams))
    for k := range allParams {
        keys = append(keys, k)
    }
    sort.Strings(keys)
    
    // 4. 拼接签名原文
    var builder strings.Builder
    for i, key := range keys {
        if i > 0 {
            builder.WriteString("&")
        }
        builder.WriteString(key)
        builder.WriteString("=")
        builder.WriteString(allParams[key])
    }
    signString := builder.String()
    
    // 5. HMAC-SHA256
    h := hmac.New(sha256.New, []byte(s.AppSecret))
    h.Write([]byte(signString))
    sign := hex.EncodeToString(h.Sum(nil))
    
    headers["X-Sign"] = sign
    return headers
}

func flattenParams(obj interface{}, prefix string, result map[string]string) {
    if obj == nil {
        return
    }
    
    switch v := obj.(type) {
    case map[string]interface{}:
        for key, value := range v {
            newKey := key
            if prefix != "" {
                newKey = prefix + "." + key
            }
            flattenParams(value, newKey, result)
        }
    case []interface{}:
        for i, item := range v {
            newKey := fmt.Sprintf("%s[%d]", prefix, i)
            flattenParams(item, newKey, result)
        }
    default:
        result[prefix] = fmt.Sprintf("%v", v)
    }
}
```

---

## 13. 性能优化建议

### 13.1 服务端优化

1. **Trace-Id 去重优化**：
   - 使用 Redis Bitmap 或 Bloom Filter 降低内存占用
   - 高并发场景考虑分片存储：`replay:{app_id}:{date}:{trace_id}`

2. **app_secret 查询缓存**：
   - 使用本地缓存（如 Caffeine）降低数据库查询
   - 热点 app_id 预加载到内存

3. **签名验证缓存**（可选）：
   - 对于幂等接口，可缓存验证结果（TTL = 时间窗口）
   - Key: `sign_cache:{app_id}:{sign}`

---

### 13.2 客户端优化

1. **连接复用**：使用 HTTP/2 或保持长连接
2. **批量请求**：单次请求携带多个业务操作
3. **SDK 预签名**：提前计算签名，减少请求时延迟

---

## 14. 安全建议

1. **密钥管理**：
   - app_secret 必须加密存储（AES-256 或使用密钥管理服务）
   - 定期轮换密钥（建议 90 天）
   - 支持多版本密钥共存（旧密钥保留 7 天缓冲期）

2. **HTTPS 强制**：
   - 所有接口必须使用 HTTPS
   - 禁用 TLS 1.0/1.1，仅支持 TLS 1.2+

3. **频率限制**：
   - 按 app_id 限流（建议：1000 次/分钟）
   - 按 IP 限流（防止单点攻击）

4. **日志审计**：
   - 记录所有签名失败的请求
   - 异常频率触发告警

---

## 15. 协议演进说明

| 版本 | 状态 | 签名算法 | 变更说明 |
|------|------|---------|---------|
| v1.0 | Deprecated | HMAC-SHA256（固定） | 初始版本 |
| **v1.1** | **Production Ready** | HMAC-SHA256（固定） | 明确参数展开规则、Header 小写规范化 |
| v2.0 | Planned | 支持多算法（RSA/Ed25519） | 增加 `X-Sign-Algorithm` Header |
| v3.0 | Future | 弃用 v1.x | 完全向后不兼容 |

---

## 16. 版本信息

```text
Version    : 1.1
Status     : Production Ready
Last Update: 2024-01-23
Author     : Platform Team
```

---

## 17. 常见问题 FAQ

### Q1：为什么 Header 要转小写？

A：避免跨语言实现差异。不同编程语言和 HTTP 库对 Header 大小写处理不一致，统一小写是最稳妥的选择。

---

### Q2：空值参数是否参与签名？

A：不参与。`null` 或空字符串 `""` 的参数会被跳过。

---

### Q3：数组为空时如何处理？

A：空数组不参与签名。例如 `"tags": []` 不会生成任何 `tags[*]` 参数。

---

### Q4：时间戳是否必须精确到秒？

A：是的。使用 Unix Epoch 秒级时间戳，毫秒级会导致验证失败。

---

### Q5：如何调试签名不匹配问题？

A：
1. 打印客户端的签名原文 `sign_string`
2. 联系服务端获取服务端计算的 `sign_string`
3. 逐字符对比差异（通常是参数展开或排序问题）

---

## 18. 联系方式

技术支持：openapi-support@example.com  
文档版本：v1.1  
协议仓库：https://github.com/example/openapi-spec

---

**END OF DOCUMENT**
