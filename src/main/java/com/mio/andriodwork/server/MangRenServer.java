package com.mio.andriodwork.server;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mio.andriodwork.config.Config;
import com.mio.andriodwork.entity.LoginException;
import com.mio.andriodwork.entity.MangRen;
import com.mio.andriodwork.entity.request.LoginRequest;
import com.mio.andriodwork.entity.request.MangRenUpdataRequest;
import com.mio.andriodwork.entity.request.PasswordUpdata;
import com.mio.andriodwork.entity.request.YuYueRequest;
import com.mio.andriodwork.entity.response.LoginResponse;
import com.mio.andriodwork.entity.response.MangRenResponse;
import com.mio.andriodwork.entity.response.YuYueResponse;
import com.mio.andriodwork.mapper.MangRenMapper;
import com.mio.andriodwork.until.BeanUntil;
import com.mio.andriodwork.until.JwtUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MangRenServer {
    @Autowired
    MangRenMapper mangRenMapper;

    public MangRenResponse getMangRenById(int id) {// 通过盲人id获取盲人信息
        log.info("通过id获取用户信息：{}",id);
        MangRen mangRen = mangRenMapper.selectOne(new LambdaQueryWrapper<>(MangRen.class).eq(MangRen::getId, id).eq(MangRen::getIsDel, Config.NO_DELETE));
        return BeanUntil.getMangRenResponse(mangRen);
    }

    public LoginResponse login(LoginRequest request) {// 登录 需要生成合法token
        if (request== null){
            throw new LoginException("用户名或密码错误");
        }
        log.info("用户登录：{}",request);
        MangRen mangRen =null;
        mangRen = mangRenMapper.selectOne(new LambdaQueryWrapper<>(MangRen.class)
                    .eq(MangRen::getZhangHao, request.getZhangHao())
                    .eq(MangRen::getPassword, request.getPassword())
                    .eq(MangRen::getIsDel, Config.NO_DELETE));

        if(mangRen== null){
            log.error("用户名或密码错误");
            throw new LoginException("用户名或密码错误");
        }
        LoginResponse response = new LoginResponse();
        response.setId(mangRen.getId());
        response.setToken(JwtUntil.createJwt(BeanUntil.getMangRenMap(mangRen)));
        return response;
    }

    public Boolean zhuCe(MangRen request) {// 注册
        if(request== null||"".equals(request.getZhangHao())){
            return false;
        }
        log.info("用户注册：{}",request);
        mangRenMapper.selectList(new LambdaQueryWrapper<>(MangRen.class)
                .eq(MangRen::getZhangHao,request.getZhangHao())
                .eq(MangRen::getIsDel,Config.NO_DELETE));
        request.setIsDel(Config.NO_DELETE);
//        request.setId(null);
        int insert = mangRenMapper.insert(request);
        if(insert>0){
            return true;
        }
        return false;
    }

    public Boolean updataUser(MangRenUpdataRequest request) {// 更新用户信息
        if(request== null){
            return false;
        }
        log.info("更新用户信息：{}",request);
        MangRen mangRen = BeanUntil.getMangRen(request);
        mangRen.setId(null);
        int update = mangRenMapper.update(mangRen, new LambdaQueryWrapper<>(MangRen.class)
                .eq(MangRen::getId, request.getId())
                .eq(MangRen::getIsDel, Config.NO_DELETE));
        if(update>0){
            return true;
        }
        return false;
    }

    public Boolean updataPassword(PasswordUpdata request) {// 更新密码
        if (request== null){
            return false;
        }
        log.info("更新密码：{}",request);
        MangRen mangRen = new MangRen();
        mangRen.setPassword(request.getPassword());
        int update = mangRenMapper.update(mangRen, new LambdaQueryWrapper<>(MangRen.class)
                .eq(MangRen::getId, request.getId()).eq(MangRen::getIsDel, Config.NO_DELETE));
        if(update>0){
            return true;
        }
        return false;
    }

    public int createYuYue(YuYueRequest request) {// 创建预约
        return 1;
    }

    public List<YuYueResponse> getYuYue(int id) {// 通过用户id获取预约
        return null;
    }

    public Boolean delYuYue(int yuYueId) {// 删除预约
        return null;
    }
}
