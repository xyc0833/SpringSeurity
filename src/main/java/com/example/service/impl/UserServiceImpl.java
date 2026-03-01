package com.example.service.impl;

import com.example.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    //@PreAuthorize("hasAnyRole('user')")
    public void test(){
        System.out.println("成功执行");
    }
}
