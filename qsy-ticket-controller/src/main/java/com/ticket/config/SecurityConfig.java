package com.ticket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.security.TokenAuthenticationFilter;
import com.ticket.security.LoginFilter;
import com.ticket.support.constants.Constants;
import com.ticket.support.dto.base.RestResult;
import com.ticket.support.dto.base.LoginUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {




    @Autowired
    @Qualifier("userServiceImpl")
    private UserDetailsService userDetailsService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }


    @Bean
    LoginFilter loginFilter() throws Exception {
        LoginFilter authenticationFilter = new LoginFilter();
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        authenticationFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                LoginUserInfo principal = (LoginUserInfo) authentication.getPrincipal();
                String token = UUID.randomUUID().toString();
                principal.setToken(token);
                //TODO:REDIS缓存权限，考虑如何刷新？
                stringRedisTemplate.opsForValue().set(Constants.REDIS_USER_LOGIN_PREFIX+token,new ObjectMapper().writeValueAsString(principal), Constants.TOKEN_EXPIRE_TIME, TimeUnit.HOURS);

                httpServletResponse.setContentType("application/json;charset=utf-8");
                PrintWriter writer = httpServletResponse.getWriter();
                writer.write(new ObjectMapper().writeValueAsString(RestResult.success(authentication.getPrincipal())));
            }
        });
        authenticationFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                httpServletResponse.setContentType("application/json;charset=utf-8");
                PrintWriter writer = httpServletResponse.getWriter();
                writer.write(new ObjectMapper().writeValueAsString(RestResult.fail(e.getMessage())));
            }
        });
        return authenticationFilter;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用 CSRF  防止iframe 造成跨域
        http.csrf().disable()
                .headers()
                .frameOptions()
                .disable()
                .and()
                //开启跨域
                .cors()
                .and()
                // 不创建会话
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests()
                 //处理跨域请求中的Preflight请求
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .anyRequest().authenticated()
                .and().
                //TOKEN过滤
                addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class).addFilter(new TokenAuthenticationFilter(authenticationManager(),stringRedisTemplate));
    }

    /**
     * 用于静态资源放行
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //    web.ignoring().antMatchers("/**");
    }
}
