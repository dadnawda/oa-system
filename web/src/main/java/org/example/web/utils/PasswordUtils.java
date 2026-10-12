package org.example.web.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 密码工具：MD5 32 位小写，与数据库测试数据（123456）一致
 */
public class PasswordUtils {

    private PasswordUtils() {}

    public static String md5(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("MD5 计算失败", e);
        }
    }
}
