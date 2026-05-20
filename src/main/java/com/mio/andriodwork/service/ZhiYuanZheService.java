package com.mio.andriodwork.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mio.andriodwork.config.Config;
import com.mio.andriodwork.entity.LoginException;
import com.mio.andriodwork.entity.YuYue;
import com.mio.andriodwork.entity.ZhiYuan;
import com.mio.andriodwork.entity.request.LoginRequest;
import com.mio.andriodwork.entity.request.PasswordUpdata;
import com.mio.andriodwork.entity.request.YuYueXuanZeRequest;
import com.mio.andriodwork.entity.request.ZhiYuanZhuCeRequest;
import com.mio.andriodwork.entity.response.LoginResponse;
import com.mio.andriodwork.entity.response.YuYueResponse;
import com.mio.andriodwork.entity.response.ZhiYuanResponse;
import com.mio.andriodwork.mapper.YuYueMapper;
import com.mio.andriodwork.mapper.ZhiYuanZheMapper;
import com.mio.andriodwork.until.BeanUntil;
import com.mio.andriodwork.until.JwtUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ZhiYuanZheService {
    @Autowired
    ZhiYuanZheMapper zhiYuanZheMapper;
    @Autowired
    private YuYueMapper yuYueMapper;

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
        if(zhiYuan == null){
            throw new LoginException("请输入内容");
        }
        log.info("用户注册：{}",zhiYuan);
        ZhiYuan zhiYuan1 = zhiYuanZheMapper.selectOne(new LambdaQueryWrapper<>(ZhiYuan.class).eq(ZhiYuan::getZhangHao, zhiYuan.getZhangHao()).eq(ZhiYuan::getIsDel, Config.NO_DELETE));
        if(zhiYuan1 != null){
            throw new LoginException("用户已存在");
        }
        ZhiYuan zhiYuan2 = BeanUntil.getZhiYuan(zhiYuan);
        zhiYuan2.setIsRenZhen(1);
        zhiYuan2.setIsDel(Config.NO_DELETE);
        zhiYuan2.setPingFen(0);
        int insert = zhiYuanZheMapper.insert(zhiYuan2);
        if(insert > 0){
            return true;
        }
        return false;
    }

    public Boolean updataZhiYuan(ZhiYuan zhiYuan) {
        if (zhiYuan == null){
            return false;
        }
        log.info("修改志愿者信息：{}",zhiYuan);
        int update = zhiYuanZheMapper.update(zhiYuan, new LambdaQueryWrapper<>(ZhiYuan.class).eq(ZhiYuan::getId, zhiYuan.getId()).eq(ZhiYuan::getIsDel, Config.NO_DELETE));
        if (update>0){
            return true;
        }
        return false;
    }

    public Boolean updataPassword(PasswordUpdata request) {
        if (request == null){
            return false;
        }
        log.info("修改密码：{}",request);
        ZhiYuan zhiYuan = new ZhiYuan();
        zhiYuan.setPassword(request.getPassword());
        int update = zhiYuanZheMapper.update(zhiYuan, new LambdaQueryWrapper<>(ZhiYuan.class).eq(ZhiYuan::getId, request.getId()).eq(ZhiYuan::getIsDel, Config.NO_DELETE));

        if (update>0){
            return true;
        }
        return false;
    }

    public List<YuYueResponse> getYuYue(int id) {
        log.info("获取预约用户id：{}",id);
        List<YuYue> yuYues = yuYueMapper.selectList(new LambdaQueryWrapper<>(YuYue.class).eq(YuYue::getZhiYuanId, id).eq(YuYue::getIsDel, Config.NO_DELETE));
        if(yuYues==null){
            return null;
        }
        List<YuYueResponse> yuYueResponses = new ArrayList<>();
        for (YuYue yuYue : yuYues){
            yuYueResponses.add(BeanUntil.getYuYueResponse(yuYue));
        }
        return yuYueResponses;
    }

    public Boolean delYuYue(int yuYueId) {
        log.info("删除预约：{}",yuYueId);
        YuYue yuYue = yuYueMapper.selectOne(new LambdaQueryWrapper<>(YuYue.class).eq(YuYue::getId, yuYueId).eq(YuYue::getIsDel, Config.NO_DELETE));
        if(yuYue==null){
            return false;
        }
        yuYue.setIsDel(Config.DELETED);
        int update = yuYueMapper.update(yuYue, new LambdaQueryWrapper<>(YuYue.class).eq(YuYue::getId, yuYueId));
        if(update>0){
            return true;
        }
        return false;
    }

    public List<YuYueResponse> getAllYuYue() {
        log.info("获取所有预约");
        List<YuYue> yuYues = yuYueMapper.selectList(new LambdaQueryWrapper<>(YuYue.class).eq(YuYue::getIsDel, Config.NO_DELETE));

        if(yuYues==null||yuYues.size()<1){
            return null;
        }
        List<YuYueResponse> yuYueResponses = new ArrayList<>();
        for (YuYue yuYue : yuYues){
            yuYueResponses.add(BeanUntil.getYuYueResponse(yuYue));
        }

        return yuYueResponses;
    }


    public Boolean YuYue(YuYueXuanZeRequest yuYue) {
        log.info("选择预约：{}",yuYue);
        YuYue yuYue1 = yuYueMapper.selectOne(new LambdaQueryWrapper<>(YuYue.class).eq(YuYue::getId, yuYue.getYuYueId()).eq(YuYue::getIsDel, Config.NO_DELETE));

        if(yuYue1.getId()==-1){
            return false;
        }
        yuYue1.setZhiYuanId(yuYue.getId());
        int update = yuYueMapper.updateById(yuYue1);
        if(update>0){
            return true;
        }
        return false;
    }
}
