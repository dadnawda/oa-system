package org.example.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.web.common.LoginUser;
import org.example.web.common.UserContext;
import org.example.web.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录校验拦截器：
 * 放行 /api/login 与 CORS 预检请求；
 * 其余 /api/** 需携带有效 token；
 * /api/admin/** 额外要求 admin 角色。
 */
@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 CORS 预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (path.equals("/api/login") || path.equals("/api/register")) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isBlank()) {
            return reject(response, 401, "未登录，请先登录");
        }
        LoginUser loginUser = JwtUtils.parseToken(token);
        if (loginUser == null) {
            return reject(response, 401, "登录已过期，请重新登录");
        }
        if (path.startsWith("/api/admin/") && !"admin".equals(loginUser.getRole())) {
            return reject(response, 403, "无权限，该操作仅管理员可用");
        }
        UserContext.set(loginUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }

    private boolean reject(HttpServletResponse response, int code, String msg) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"msg\":\"" + msg + "\",\"data\":null}");
        return false;
    }
}
