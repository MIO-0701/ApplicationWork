package com.mio.andriodwork.server;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mio.andriodwork.config.Config;
import com.mio.andriodwork.entity.LoginException;
import com.mio.andriodwork.entity.ZhiYuan;
import com.mio.andriodwork.entity.request.LoginRequest;
import com.mio.andriodwork.entity.request.PasswordUpdata;
import com.mio.andriodwork.entity.request.YuYueXuanZeRequest;
import com.mio.andriodwork.entity.request.ZhiYuanZhuCeRequest;
import com.mio.andriodwork.entity.response.LoginResponse;
import com.mio.andriodwork.entity.response.YuYueResponse;
import com.mio.andriodwork.entity.response.ZhiYuanResponse;
import com.mio.andriodwork.mapper.ZhiYuanZheMapper;
import com.mio.andriodwork.until.BeanUntil;
import com.mio.andriodwork.until.JwtUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ZhiYuanZheServer {
    @Autowired
    ZhiYuanZheMapper zhiYuanZheMapper;

    public ZhiYuanResponse getZhiYuanById(int id) {
        log.info("通过id获取 志愿者信息：{}",id);
        ZhiYuan zhiYuan = zhiYuanZheMapper.selectOne(new LambdaQueryWrapper<>(ZhiYuan.class).eq(ZhiYuan::getId, id).eq(ZhiYuan::getIsDel, Config.NO_DELETE));
        if(zhiYuan== null){
            throw null;
        }
        return BeanUntil.getZhiYuanResponse(zhiYuan);

    }

    public LoginResponse login(LoginRequest zhiYuan) {
        if(zhiYuan== null){
            throw new LoginException("请输入内容");
        }
        log.info("用户登录：{}",zhiYuan);
        ZhiYuan zhiYuan1 = zhiYuanZheMapper.selectOne(new LambdaQueryWrapper<>(ZhiYuan.class)
                .eq(ZhiYuan::getZhangHao, zhiYuan.getZhangHao())
                .eq(ZhiYuan::getPassword, zhiYuan.getPassword())
                .eq(ZhiYuan::getIsDel, Config.NO_DELETE));
        if (zhiYuan1== null){
            throw new LoginException("用户名或密码错误");
        }
        return new LoginResponse(zhiYuan1.getId(), JwtUntil.createJwt(BeanUntil.getZhiYuanMap(zhiYuan1)));
    }

    public Boolean zhuCe(ZhiYuanZhuCeRequest zhiYuan) {
        return null;
    }

    public Boolean updataZhiYuan(ZhiYuan zhiYuan) {
        return null;
    }

    public Boolean updataPassword(PasswordUpdata request) {
        return null;
    }

    public List<YuYueResponse> getYuYue(int id) {
        return null;
    }

    public Boolean delYuYue(int yuYueId) {
        return null;
    }

    public Boolean YuYue(YuYueXuanZeRequest yuYue) {
        return null;
    }
}
