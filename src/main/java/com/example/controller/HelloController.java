package com.example.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @Resource
    UserService userService;

    //处理首页或是登录界面跳转
    //@PreAuthorize("hasAnyRole('user','admin')")  //直接使用hasRole方法判断是否包含某个角色
    @GetMapping("/")
    public String index(){
        userService.test();
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/cart")
    public String cart(){
        return "cart";
    }
}
