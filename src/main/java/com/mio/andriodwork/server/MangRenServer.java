package com.mio.andriodwork.server;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mio.andriodwork.config.Config;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MangRenServer {
    @Autowired
    MangRenMapper mangRenMapper;

    public MangRenResponse getMangRenById(int id) {// 通过盲人id获取盲人信息
        MangRen mangRen = mangRenMapper.selectOne(new LambdaQueryWrapper<>(MangRen.class).eq(MangRen::getId, id).eq(MangRen::getIsDel, Config.NO_DELETE));
        return BeanUntil.getMangRenResponse(mangRen);
    }

    public LoginResponse login(LoginRequest request) {// 登录 需要生成合法token
        return null;
    }

    public Boolean zhuCe(MangRen request) {// 注册
        return null;
    }

    public Boolean updataUser(MangRenUpdataRequest request) {// 更新用户信息
        return null;
    }

    public Boolean updataPassword(PasswordUpdata request) {// 更新密码
        return null;
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
