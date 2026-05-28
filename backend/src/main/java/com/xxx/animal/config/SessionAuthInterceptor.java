package com.xxx.animal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxx.animal.common.Result;
import com.xxx.animal.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();
        User loginUser = (User) request.getSession().getAttribute("loginUser");

        boolean requiresLogin = false;
        boolean requiresAdmin = false;

        if (uri.startsWith("/api/admin/")) {
            requiresLogin = true;
            requiresAdmin = true;
        } else if ("/api/adoption-applications".equals(uri) && HttpMethod.POST.matches(method)) {
            requiresLogin = true;
        } else if ("/api/donation/claims".equals(uri) && HttpMethod.POST.matches(method)) {
            requiresLogin = true;
        } else if ("/api/file/upload".equals(uri) && HttpMethod.POST.matches(method)) {
            requiresLogin = true;
        } else if ("/api/community/posts".equals(uri) && HttpMethod.POST.matches(method)) {
            requiresLogin = true;
        } else if ("/api/community/comments".equals(uri) && HttpMethod.POST.matches(method)) {
            requiresLogin = true;
        } else if ("/api/user/profile".equals(uri) || "/api/user/password".equals(uri)) {
            requiresLogin = true;
        } else if (uri.startsWith("/api/animals") && !HttpMethod.GET.matches(method)) {
            requiresLogin = true;
            requiresAdmin = true;
        } else if ((uri.startsWith("/api/community/posts/") || uri.startsWith("/api/community/comments/")) && !HttpMethod.GET.matches(method)) {
            requiresLogin = true;
            requiresAdmin = true;
        }

        if (requiresLogin && loginUser == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "请先登录");
            return false;
        }
        if (requiresAdmin && (loginUser == null || !"ADMIN".equalsIgnoreCase(loginUser.getRole()))) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "无管理员权限");
            return false;
        }
        return true;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(message)));
    }
}
