package com.mio.andriodwork.controller;

import com.mio.andriodwork.entity.ZhiYuan;
import com.mio.andriodwork.entity.request.*;
import com.mio.andriodwork.entity.response.LoginResponse;
import com.mio.andriodwork.entity.response.YuYueResponse;
import com.mio.andriodwork.entity.response.ZhiYuanResponse;
import com.mio.andriodwork.server.ZhiYuanZheServer;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("zhiYuan")
public class ZhiYuanZheController {
    @Autowired
    ZhiYuanZheServer zhiYuanZheServer;

    @RequestMapping("getZhiYuan")
    public ZhiYuanResponse getZhiYuanById(@NonNull Integer id){
        return zhiYuanZheServer.getZhiYuanById(id);
    }

    @RequestMapping("login")
    public LoginResponse login(@RequestBody LoginRequest zhiYuan){
        return zhiYuanZheServer.login(zhiYuan);
    }

    @RequestMapping("zhuCe")
    public Boolean zhuCe(@RequestBody ZhiYuanZhuCeRequest zhiYuan){
        return zhiYuanZheServer.zhuCe(zhiYuan);
    }

    @RequestMapping("updataZhiYuan")
    public Boolean updataZhiYuan(@RequestBody ZhiYuan zhiYuan){
        return zhiYuanZheServer.updataZhiYuan(zhiYuan);
    }

    @RequestMapping("updataPassword")
    public Boolean updataPassword(@RequestBody PasswordUpdata request){
        return zhiYuanZheServer.updataPassword(request);
    }

    @RequestMapping("getYuYue")
    public List<YuYueResponse> getYuYue(@NonNull Integer id){
        return zhiYuanZheServer.getYuYue(id);
    }

    @RequestMapping("getAllYuYue")
    public List<YuYueResponse> getAllYuYue(){
        return zhiYuanZheServer.getAllYuYue();
    }

    @RequestMapping("delYuYue")
    public Boolean delYuYue(@NonNull Integer yuYueId){
        return zhiYuanZheServer.delYuYue(yuYueId);
    }

}
