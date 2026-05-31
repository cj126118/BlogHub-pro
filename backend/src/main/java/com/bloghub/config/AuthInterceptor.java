package com.bloghub.config;

import com.bloghub.common.Result;
import com.bloghub.common.enums.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证拦截器
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 白名单路径（完全公开，无需任何认证） */
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/api/auth/**",
            "/api/upload/**",
            "/api/tags",
            "/api/categories",
            "/api/hot-topics/**",
            "/uploads/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    /** 只读公开路径（GET 放行，其他方法需登录） */
    private static final List<String> PUBLIC_READ_PATHS = Arrays.asList(
            "/api/posts",
            "/api/posts/*",
            "/api/posts/ranking/likes",
            "/api/posts/archive",
            "/api/posts/*/like",
            "/api/posts/*/comments"
    );

    /** 需要登录的公开 GET 路径（虽然公开但需注入 userId） */
    private static final List<String> AUTH_READ_PATHS = Arrays.asList(
            "/api/posts/my"
    );

    /** 互动公开路径（GET + POST 均放行，用于点赞、评论等互动） */
    private static final List<String> INTERACT_PATHS = Arrays.asList(
            "/api/posts/*/like",
            "/api/posts/*/comments"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();

        // 检查白名单（完全公开）
        for (String pattern : EXCLUDE_PATHS) {
            if (pathMatcher.match(pattern, requestURI)) {
                return true;
            }
        }

        // 检查只读公开路径（GET 放行，写操作需登录）
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            // 特例：/api/posts/my 需要登录，不属于公开路径
            if (pathMatcher.match("/api/posts/my", requestURI)) {
                // 不在此处放行，继续往下走 Token 校验
            } else {
                for (String pattern : PUBLIC_READ_PATHS) {
                    if (pathMatcher.match(pattern, requestURI)) {
                        return true;
                    }
                }
            }
        }

        // 检查互动公开路径（GET + POST 均放行）
        for (String pattern : INTERACT_PATHS) {
            if (pathMatcher.match(pattern, requestURI)) {
                return true;
            }
        }

        // 获取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(
                    Result.unauthorized("未登录或 Token 已过期")));
            return false;
        }

        String token = authHeader.substring(7);

        try {
            // 校验 Token
            if (!jwtUtil.validateToken(token)) {
                throw new Exception("Token 无效");
            }

            // 将用户信息存入 request 属性
            request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
            request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
            request.setAttribute("role", jwtUtil.getRoleFromToken(token));

            // 管理后台路径需要 admin 角色
            if (requestURI.startsWith("/api/admin/")) {
                String role = jwtUtil.getRoleFromToken(token);
                if (!"admin".equals(role)) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    ObjectMapper mapper = new ObjectMapper();
                    response.getWriter().write(mapper.writeValueAsString(
                            Result.fail(403, "无权访问，需要管理员权限")));
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(
                    Result.unauthorized("Token 无效或已过期")));
            return false;
        }
    }
}
