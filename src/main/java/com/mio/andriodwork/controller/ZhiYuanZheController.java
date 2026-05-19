package com.mio.andriodwork.controller;

import com.mio.andriodwork.entity.ZhiYuan;
import com.mio.andriodwork.entity.request.*;
import com.mio.andriodwork.entity.response.LoginResponse;
import com.mio.andriodwork.entity.response.YuYueResponse;
import com.mio.andriodwork.entity.response.ZhiYuanResponse;
import com.mio.andriodwork.service.ZhiYuanZheService;
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
    ZhiYuanZheService zhiYuanZheService;

    @RequestMapping("getuser")
    public ZhiYuanResponse getZhiYuanById(@NonNull Integer id){
        return zhiYuanZheService.getZhiYuanById(id);
    }

    @RequestMapping("login")
    public LoginResponse login(@RequestBody LoginRequest zhiYuan){
        return zhiYuanZheService.login(zhiYuan);
    }

    @RequestMapping("zhuCe")
    public Boolean zhuCe(@RequestBody ZhiYuanZhuCeRequest zhiYuan){
        return zhiYuanZheService.zhuCe(zhiYuan);
    }

    @RequestMapping("updataZhiYuan")
    public Boolean updataZhiYuan(@RequestBody ZhiYuan zhiYuan){
        return zhiYuanZheService.updataZhiYuan(zhiYuan);
    }

    @RequestMapping("updataPassword")
    public Boolean updataPassword(@RequestBody PasswordUpdata request){
        return zhiYuanZheService.updataPassword(request);
    }

    @RequestMapping("getYuYue")
    public List<YuYueResponse> getYuYue(@NonNull Integer id){
        return zhiYuanZheService.getYuYue(id);
    }

    @RequestMapping("getAllYuYue")
    public List<YuYueResponse> getAllYuYue(){
        return zhiYuanZheService.getAllYuYue();
    }

    @RequestMapping("delYuYue")
    public Boolean delYuYue(@NonNull Integer yuYueId){
        return zhiYuanZheService.delYuYue(yuYueId);
    }

    @RequestMapping("YuYue")
    public Boolean YuYue(@RequestBody YuYueXuanZeRequest yuYue){ //选择预约
        return zhiYuanZheService.YuYue(yuYue);
    }
}
