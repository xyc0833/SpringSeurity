package com.example.service.impl;

import com.example.entity.Account;
import com.example.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeService implements UserDetailsService {

    @Resource
    UserMapper userMapper;

    /**
     * 根据用户名加载用户详情（Spring Security 核心方法）
     * 该方法会被 Spring Security 认证管理器调用，完成用户信息的查询和封装
     *
     * @param username 前端传入的登录用户名
     * @return UserDetails Spring Security 标准的用户详情对象，包含用户名、密码等认证信息
     * @throws UsernameNotFoundException 用户名不存在时抛出的异常，由认证管理器捕获处理
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库中根据用户名查询用户信息（⚠️ 原代码bug："username"是字符串常量，应改为变量username）
        Account account = userMapper.findUserByName(username);

        // 2. 校验用户是否存在：若查询结果为空，抛出用户名不存在异常
        //    异常信息提示"用户名或密码错误"而非"用户名不存在"，是为了避免信息泄露，提高安全性
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 3. 封装 UserDetails 对象返回给 Spring Security
        //    Spring Security 会自动使用配置的密码编码器比对该密码与前端传入的密码
        return User
                // 设置认证的用户名（与查询条件一致）
                .withUsername(username)
                // 设置用户密码（⚠️ 注意：数据库中必须存储密文密码，且项目需配置对应的密码编码器）
                .password(account.getPassword())
                // 构建 UserDetails 对象（如需添加权限/角色，可在此处链式调用.roles()/.authorities()）
                .roles(account.getRole())
                .build();
    }
}
