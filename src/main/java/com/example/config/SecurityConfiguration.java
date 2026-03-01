package com.example.config;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity   //开启WebSecurity相关功能
//使用注解开启权限控制
@EnableMethodSecurity   //开启方法安全校验
public class SecurityConfiguration {
    //这里将BCryptPasswordEncoder直接注册为Bean，Security会自动进行选择
    @Bean
    public PasswordEncoder passwordEncoder(){
        //System.out.println(new BCryptPasswordEncoder().encode("password"));
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

    //将“记住我”功能中的信息放到数据库里面
    @Bean //该方法会自动建表
    public PersistentTokenRepository tokenRepository(DataSource dataSource){
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        //在启动时自动在数据库中创建存储记住我信息的表，仅第一次需要，后续不需要
        //repository.setCreateTableOnStartup(true);
        repository.setDataSource(dataSource);
        return repository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
        PersistentTokenRepository repository ) throws Exception{
        //如果你学习过SpringSecurity 5.X版本，可能会发现新版本的配置方式完全不一样
        //新版本全部采用lambda形式进行配置，无法再使用之前的and()方法进行连接了
        return http
                //以下是验证请求拦截和放行配置
                //配置页面的拦截规则
                .authorizeHttpRequests(auth ->{
                    //针对前端的静态资源 需要放行
                    //将所有的静态资源放行，一定要添加在全部请求拦截之前
                    auth.requestMatchers("/static/**").permitAll();
                    auth.anyRequest().authenticated();
                    //只有具有以下角色的用户才能访问路径"/"
                    //auth.requestMatchers("/").hasAnyRole("user","admin");
                    //其他的页面只有管理员才能访问
                    //auth.anyRequest().hasAnyRole("admin");

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
                //以下是csrf相关配置
                .csrf(conf ->{
                    conf.disable();
                    //单行lambda 表达式可以转换为 双冒号的方法引用
                    //我希望登录和退出 采用这个 csrf其他的暂时不用
                })
                //“记住我” 的功能
                .rememberMe(conf -> {
                    conf.alwaysRemember(false);  //这里不要开启始终记住，我们需要配置为用户自行勾选
                    conf.rememberMeParameter("remember-me");   //记住我表单字段，默认就是这个，可以不配置
                    //conf.rememberMeCookieName("xxxx");  //记住我设置的Cookie名字，也可以自定义，不过没必要
                    //设置 记住我的 时间
                    conf.tokenValiditySeconds(3600 * 24 * 3);
                    conf.tokenRepository(repository);
                })
                .build();

    }

}
