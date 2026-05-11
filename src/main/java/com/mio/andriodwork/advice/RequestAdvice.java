package com.mio.andriodwork.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mio.andriodwork.entity.Response;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class RequestAdvice implements ResponseBodyAdvice {

    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Response){
            return body;
        }
        if (body instanceof String){
            return mapper.valueToTree(Response.success(body));
        }
        return Response.success(body);
    }
}
