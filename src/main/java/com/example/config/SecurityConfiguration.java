package com.example.config;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

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

//    //手动创建一个AuthenticationManager用于处理密码校验  用于原密码的校验
//    private AuthenticationManager authenticationManager(UserDetailsManager manager,
//                                                        PasswordEncoder encoder){
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(manager);
//        provider.setPasswordEncoder(encoder);
//        return new ProviderManager(provider);
//    }
//
//    @Bean
//    public UserDetailsManager userDetailsService(DataSource dataSource,
//                                                 PasswordEncoder encoder) throws Exception {
//        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
//        //为UserDetailsManager设置AuthenticationManager即可开启重置密码的时的校验
//        manager.setAuthenticationManager(authenticationManager(manager, encoder));
//        return manager;
//    }

}
