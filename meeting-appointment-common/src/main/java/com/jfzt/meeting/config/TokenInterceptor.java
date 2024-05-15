package com.jfzt.meeting.config;

import com.jfzt.meeting.exception.ErrorCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 拦截判断是否有token
 *
 * @author zhenxing.lu
 * @since 2024-04-30 10.13:51
 */
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 从HTTP头信息中取得token
        String token = request.getHeader("Authorization");

        // 如果token为空，则返回false，表示拦截请求
        if (token == null || token.isEmpty()) {
            response.sendError(Integer.parseInt(ErrorCodeEnum.SERVICE_ERROR_A0230.getCode()),ErrorCodeEnum.SERVICE_ERROR_A0230.getDescription());
            return false;
        }

        // 如果token不为空，则继续处理请求
        return true;
    }
}