package com.mio.andriodwork.controller;

import com.mio.andriodwork.entity.Run;
import com.mio.andriodwork.entity.request.RunRequest;
import com.mio.andriodwork.service.RunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("user")
public class RunController {
    @Autowired
    private RunService runService;
    @RequestMapping("getRun")
    public List<Run> getRun(@RequestBody RunRequest runRequest){
        return runService.getRun(runRequest);
    }
}
