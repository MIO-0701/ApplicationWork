package com.mio.andriodwork.config;

import com.mio.andriodwork.entity.LoginException;
import com.mio.andriodwork.until.JwtUntil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(Config.TOKEN_NAME);
        Boolean isLogin = JwtUntil.isJwtTrue(token);
        if(isLogin){
            return true;
        }
        log.error("未登录");
        throw new LoginException("请先登录");
    }
}
