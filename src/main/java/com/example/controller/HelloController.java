package com.example.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
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
    //处理登录操作并跳转
//    @PostMapping("/login")
//    public String login(@RequestParam String username,
//                        @RequestParam String password,
//                        HttpSession session,
//                        Model model){
//        if("test".equals(username) && "123456".equals(password)) {
//            session.setAttribute("login", true);
//            return "redirect:/";
//        } else {
//            model.addAttribute("status", true);
//            return "login";
//        }
//    }
    @Resource
    UserDetailsManager manager;

    @Resource
    PasswordEncoder encoder;

    //修改密码的接口
    @ResponseBody
    @PostMapping("/change-password")
    public JSONObject changePassword(@RequestParam String oldPassword,
                                     @RequestParam String newPassword) {
        manager.changePassword(oldPassword, encoder.encode(newPassword));
        JSONObject object = new JSONObject();
        object.put("success", true);
        return object;
    }

    //处理首页或是登录界面跳转
    @GetMapping("/")
    public String index(){
        return "index";
    }

    //转账操作
    @ResponseBody
    @PostMapping("/pay")
    public JSONObject pay(@RequestParam String account){
        JSONObject object = new JSONObject();
        System.out.println("转账给"+account+"成功，交易已完成！");
        object.put("success", true);
        return object;
    }
}
