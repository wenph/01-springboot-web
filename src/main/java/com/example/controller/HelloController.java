package com.example.controller;

import com.example.lock.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HelloController {
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("hello")
    @ResponseBody
    private String hello() {
        return "hello";
    }

    @GetMapping("/redis/get/{key}")
    @ResponseBody
    public String get(@PathVariable("key") String key) {
        try {
            Object o = redisUtil.get(key);
            return String.valueOf(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @GetMapping("/redis/set/{key}/{value}")
    @ResponseBody
    public String set(@PathVariable("key") String key, @PathVariable("value") String value) {
        try {
            redisUtil.set(key, value);
            return "set success";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
