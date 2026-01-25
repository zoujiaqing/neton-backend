package com.gitlab.neton.module.platform.util;

import java.security.SecureRandom;

public class AppCredentialGeneratorUtil {
    private static final String ALPHANUMERIC_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private AppCredentialGeneratorUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 生成 AppID（默认16位字母数字）
     */
    public static String generateAppId() {
        return generateAlphanumericString(18);
    }

    /**
     * 生成指定长度的 AppID
     */
    public static String generateAppId(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("AppID 长度至少为 8");
        }
        return generateAlphanumericString(length);
    }

    /**
     * 生成带前缀的 AppID（前缀自动清洗为字母数字）
     */
    public static String generateAppId(String prefix, int randomPartLength) {
        if (randomPartLength < 8) {
            throw new IllegalArgumentException("随机部分长度至少为 8");
        }
        String cleanPrefix = (prefix == null) ? "" : prefix.replaceAll("[^a-zA-Z0-9]", "");
        return cleanPrefix + generateAlphanumericString(randomPartLength);
    }

    /**
     * 生成 AppSecret（默认48位字母数字）
     */
    public static String generateAppSecret() {
        return generateAppSecret(48);
    }

    /**
     * 生成指定长度的 AppSecret（仅字母数字）
     */
    public static String generateAppSecret(int length) {
        if (length < 32) {
            throw new IllegalArgumentException("AppSecret 长度建议不小于 32");
        }
        return generateAlphanumericString(length);
    }

    // 内部：生成指定长度的纯字母数字字符串
    private static String generateAlphanumericString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARS.length());
            sb.append(ALPHANUMERIC_CHARS.charAt(index));
        }
        return sb.toString();
    }

}
