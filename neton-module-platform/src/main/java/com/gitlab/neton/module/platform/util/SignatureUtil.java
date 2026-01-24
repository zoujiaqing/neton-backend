package com.gitlab.neton.module.platform.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * OpenAPI 签名工具类
 * <p>
 * 实现 openapi-signature-spec-v1.1 规范
 *
 * @author Neton
 */
@Slf4j
public class SignatureUtil {

    private static final String HMAC_SHA256 = "HmacSHA256";
    
    /**
     * OpenAPI Header 常量
     */
    public static final String HEADER_CLIENT_ID = "X-Client-Id";
    public static final String HEADER_TIMESTAMP = "X-Timestamp";
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String HEADER_SIGN = "X-Sign";

    /**
     * 计算签名
     *
     * @param params 参与签名的参数（已展开并转小写）
     * @param clientSecret 客户端密钥
     * @return 签名字符串（Hex 小写）
     */
    public static String calculateSign(Map<String, String> params, String clientSecret) {
        // 1. 按 ASCII 字典序排序
        TreeMap<String, String> sortedParams = new TreeMap<>(params);

        // 2. 拼接签名原文
        StringBuilder signString = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (signString.length() > 0) {
                signString.append("&");
            }
            signString.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 3. HMAC-SHA256 计算
        String sign = hmacSha256(signString.toString(), clientSecret);
        
        log.debug("[SignatureUtil] Sign String: {}", signString);
        log.debug("[SignatureUtil] Sign Result: {}", sign);
        
        return sign;
    }

    /**
     * 验证签名
     *
     * @param clientSign 客户端签名
     * @param serverSign 服务端计算的签名
     * @return true=验证通过
     */
    public static boolean verifySign(String clientSign, String serverSign) {
        if (StrUtil.isBlank(clientSign) || StrUtil.isBlank(serverSign)) {
            return false;
        }
        // 使用常量时间比较，防止时序攻击
        return MessageDigest.isEqual(
                clientSign.getBytes(StandardCharsets.UTF_8),
                serverSign.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * HMAC-SHA256 加密
     *
     * @param data 待加密数据
     * @param secret 密钥
     * @return Hex 小写字符串
     */
    private static String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("HMAC-SHA256 计算失败", e);
        }
    }

    /**
     * 字节数组转 Hex 小写字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 递归展开 JSON 参数
     * <p>
     * 规则：
     * - 嵌套对象使用 "." 连接：user.name
     * - 数组使用 "[index]"：tags[0]
     * - null 和空字符串不参与签名
     *
     * @param obj 待展开对象
     * @param prefix 前缀
     * @param result 结果集
     */
    public static void flattenParams(Object obj, String prefix, Map<String, String> result) {
        if (obj == null || "".equals(obj)) {
            return; // 跳过空值
        }

        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = StrUtil.isEmpty(prefix) ? entry.getKey() : prefix + "." + entry.getKey();
                flattenParams(entry.getValue(), key, result);
            }
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            for (int i = 0; i < list.size(); i++) {
                String key = prefix + "[" + i + "]";
                flattenParams(list.get(i), key, result);
            }
        } else {
            result.put(prefix, String.valueOf(obj));
        }
    }

    /**
     * 验证时间戳是否在有效窗口内（±300 秒）
     *
     * @param timestamp 请求时间戳（秒）
     * @return true=有效
     */
    public static boolean validateTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis() / 1000;
        long timeDiff = Math.abs(currentTime - timestamp);
        return timeDiff <= 300; // 5 分钟窗口
    }

    /**
     * Header 名称转小写（规范化）
     *
     * @param headerName Header 名称
     * @return 小写 Header 名称
     */
    public static String normalizeHeaderName(String headerName) {
        return StrUtil.isBlank(headerName) ? "" : headerName.toLowerCase();
    }

}
