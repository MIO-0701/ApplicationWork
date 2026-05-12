package com.mio.andriodwork.controller;

import com.mio.andriodwork.entity.MangRen;
import com.mio.andriodwork.entity.request.LoginRequest;
import com.mio.andriodwork.entity.request.MangRenUpdataRequest;
import com.mio.andriodwork.entity.request.PasswordUpdata;
import com.mio.andriodwork.entity.request.YuYueRequest;
import com.mio.andriodwork.entity.response.LoginResponse;
import com.mio.andriodwork.entity.response.MangRenResponse;
import com.mio.andriodwork.entity.response.YuYueResponse;
import com.mio.andriodwork.server.MangRenServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("mangRen")
@Slf4j
public class MangRenController {
    @Autowired
    MangRenServer mangRenServer;

    @RequestMapping("getuser")
    public MangRenResponse getMangRenById(int id){
        return mangRenServer.getMangRenById(id);
    }

    @RequestMapping("login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return mangRenServer.login(request);
    }
    @RequestMapping("zhuCe")
    public Boolean zhuCe(@RequestBody MangRen  request){
        return mangRenServer.zhuCe(request);
    }
    @RequestMapping("updataUser")
    public Boolean updataUser(@RequestBody MangRenUpdataRequest request){
        return mangRenServer.updataUser(request);
    }
    @RequestMapping("updataPassword")
    public Boolean updataPassword(@RequestBody PasswordUpdata request){
        return mangRenServer.updataPassword(request);
    }

    @RequestMapping("createYuYue")
    public int createYuYue(@RequestBody YuYueRequest request){
        return mangRenServer.createYuYue(request);
    }

    @RequestMapping("getYuYue")
    public List<YuYueResponse> getYuYue(int id){
        return mangRenServer.getYuYue(id);
    }

    @RequestMapping("delYuYue")
    public Boolean delYuYue(int yuYueId){
        return mangRenServer.delYuYue(yuYueId);
    }

}
