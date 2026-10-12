package org.example.web.common;

/**
 * 登录用户上下文（ThreadLocal），拦截器写入、请求结束清理
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long currentEmpId() {
        LoginUser user = HOLDER.get();
        if (user == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return user.getEmpId();
    }

    public static void remove() {
        HOLDER.remove();
    }
}
