package com.mio.andriodwork.entity;

import com.mio.andriodwork.config.Config;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Response {
    private int code;
    private String message;
    private Object data;
    public static Response success(Object data){
        return new Response(Config.successCode,null, data);
    }
    public static Response error(String message){
        return new Response(Config.errorCode,message, null);
    }
}
