package org.example.web.utils;

import org.example.web.common.LoginUser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 自实现 Token（HMAC-SHA256 签名，无第三方依赖）
 * 格式：base64(empId.role.expireAt) . base64(hmacSHA256(payload))
 */
public class JwtUtils {

    private static final String SECRET = "oa-core-system-secret-2026-#01";
    private static final long EXPIRE_MS = 24L * 60 * 60 * 1000;

    private JwtUtils() {}

    public static String generateToken(Long empId, String role) {
        long expireAt = System.currentTimeMillis() + EXPIRE_MS;
        String payload = empId + "." + role + "." + expireAt;
        String encoded = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encoded + "." + sign(encoded);
    }

    /**
     * 校验签名与有效期；合法返回 LoginUser（name 由调用方补充），非法返回 null
     */
    public static LoginUser parseToken(String token) {
        try {
            int idx = token.lastIndexOf('.');
            if (idx <= 0) {
                return null;
            }
            String encoded = token.substring(0, idx);
            String sig = token.substring(idx + 1);
            if (!sign(encoded).equals(sig)) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
            String[] parts = payload.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            long expireAt = Long.parseLong(parts[2]);
            if (System.currentTimeMillis() > expireAt) {
                return null;
            }
            return new LoginUser(Long.parseLong(parts[0]), parts[1], null);
        } catch (Exception e) {
            return null;
        }
    }

    private static String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Token 签名失败", e);
        }
    }
}
