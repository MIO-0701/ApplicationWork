package com.mio.andriodwork.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mio.andriodwork.config.Config;
import com.mio.andriodwork.entity.Run;
import com.mio.andriodwork.entity.request.RunRequest;
import com.mio.andriodwork.mapper.RunMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RunService {
    @Autowired
    private RunMapper runMapper;
    public List<Run> getRun(RunRequest runRequest) {
        if (runRequest == null){
            return null;
        }
        log.info("获取运动信息：{}",runRequest);
        List<Run> runs = runMapper.selectList(new LambdaQueryWrapper<>(Run.class).eq(Run::getUserId, runRequest.getUserId()).eq(Run::getUserType, runRequest.getUserType()).eq(Run::getIsDel, Config.NO_DELETE));
        if (runs.size() > 0){
            return runs;
        }
        return null;
    }

    public Boolean addRun(Run run) {
        if (run != null){
            log.info("添加运动信息：{}",run);
            run.setIsDel(Config.NO_DELETE);
            return runMapper.insert(run) > 0;
        }
        return null;
    }
}
