package com.bloghub.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 请求日志过滤器 — DEBUG 级别打印每个 API 请求的方法、路径、参数
 */
@Component
public class RequestLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        // 只记录 API 请求
        if (uri.startsWith("/api/")) {
            long start = System.currentTimeMillis();
            String method = request.getMethod();
            String qs = request.getQueryString();
            String params = (qs != null) ? "?" + qs : "";
            String auth = request.getHeader("Authorization");
            String visitorId = request.getHeader("X-Visitor-Id");

            chain.doFilter(request, response);

            long elapsed = System.currentTimeMillis() - start;
            int status = response.getStatus();

            if (log.isDebugEnabled()) {
                log.debug("[{}] {} {}{} → {} ({}ms) auth={} visitor={}",
                        method, uri, params, status, elapsed,
                        auth != null ? "Bearer ***" : "none",
                        visitorId != null ? visitorId : "none");
            } else {
                // INFO 级别只记录慢请求和错误
                if (status >= 400 || elapsed > 1000) {
                    log.warn("[{}] {} {}{} → {} ({}ms)", method, uri, params, status, elapsed);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
