package com.jfzt.meeting.controller;

import com.jfzt.meeting.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zilong.deng
 * @date 2024/04/25
 */
@RestController
@RequestMapping("/meeting")
public class TestController {
    /**
     * @return {@code Result}
     * @description 测试模块导入
     */
    @GetMapping("/test")
    @ResponseBody
    public Result test () {
        return Result.ok("HelloWorld!");
    }
}
