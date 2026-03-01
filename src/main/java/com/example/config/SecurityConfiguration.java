package com.example.config;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity   //开启WebSecurity相关功能
public class SecurityConfiguration {
    //这里将BCryptPasswordEncoder直接注册为Bean，Security会自动进行选择
    @Bean
    public PasswordEncoder passwordEncoder(){
        System.out.println(new BCryptPasswordEncoder().encode("password"));
        return new BCryptPasswordEncoder();
    }

    //数据库相关配置
    @Bean
    public DataSource dataSource(){
        //数据源配置
        return new PooledDataSource("com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://localhost:3306/sql_inventory", "root", "xuyong612");
    }

    //Spring 容器中配置 MyBatis 的 SqlSessionFactory
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        // 直接参数得到Bean对象
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //如果你学习过SpringSecurity 5.X版本，可能会发现新版本的配置方式完全不一样
        //新版本全部采用lambda形式进行配置，无法再使用之前的and()方法进行连接了
        return http
                //以下是验证请求拦截和放行配置
                //配置页面的拦截规则
                .authorizeHttpRequests(auth ->{
                    //针对前端的静态资源 需要放行
                    //将所有的静态资源放行，一定要添加在全部请求拦截之前
                    auth.requestMatchers("/static/**").permitAll();
                    //将所有请求全部拦截，一律需要验证
                    auth.anyRequest().authenticated();
                })
                //以下是表单登录相关配置
                .formLogin(conf ->{
                    conf.loginPage("/login");//将登录页设置为我们自己的登录页面
                    conf.loginProcessingUrl("/doLogin");//登录表单提交的地址，可以自定义
                    conf.defaultSuccessUrl("/");//登录成功后跳转的页面
                    conf.permitAll();//全部放行
                    //将登录相关的地址放行，否则未登录的用户连登录界面都进不去
                    //用户名和密码的表单字段名称，不过默认就是这个，可以不配置，除非有特殊需求

                })
                //以下是退出登录相关配置
                .logout(conf -> {
                    conf.logoutUrl("/doLogout");   //退出登录地址，跟上面一样可自定义
                    conf.logoutSuccessUrl("/login");  //退出登录成功后跳转的地址，这里设置为登录界面
                    conf.permitAll();
                })
                .build();

    }

}
