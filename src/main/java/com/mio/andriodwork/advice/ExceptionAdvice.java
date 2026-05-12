package com.mio.andriodwork.advice;

import com.mio.andriodwork.entity.LoginException;
import com.mio.andriodwork.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(LoginException.class)
    public Response handleLoginException(LoginException e) {
        log.error(e.toString());
        return Response.error(e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public Response handleException(Exception e) {
        log.error(e.toString());
        return Response.error("服务器异常");
    }

}
