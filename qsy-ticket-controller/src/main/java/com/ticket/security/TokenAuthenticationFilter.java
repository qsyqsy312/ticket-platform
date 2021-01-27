package com.ticket.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.support.constants.Constants;
import com.ticket.support.dto.base.LoginUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class TokenAuthenticationFilter extends BasicAuthenticationFilter {


    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationFilter.class);


    private StringRedisTemplate stringRedisTemplate;


    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, StringRedisTemplate stringRedisTemplate) {
        super(authenticationManager);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint, StringRedisTemplate stringRedisTemplate) {
        super(authenticationManager, authenticationEntryPoint);
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //header里url里都取一遍
        String token = request.getHeader(Constants.HEADER);
        if (StringUtils.isEmpty(token)) {
            token = request.getParameter(Constants.HEADER);
        }
        if (!StringUtils.isEmpty(token) && token.startsWith(Constants.TOKEN_SPLIT)) {

            try {
                token = Constants.REDIS_USER_LOGIN_PREFIX + token.replace(Constants.TOKEN_SPLIT, "");
                if (!stringRedisTemplate.hasKey(token)) {
                    chain.doFilter(request, response);
                }
                //如果快到期了就给token续期
                String userJson = stringRedisTemplate.opsForValue().get(token);
                LoginUserInfo userInfo = new ObjectMapper().readValue(userJson, LoginUserInfo.class);
                Long expire = stringRedisTemplate.getExpire(token, TimeUnit.MINUTES);
                if (expire <= 30L) {
                    stringRedisTemplate.expire(token, Constants.TOKEN_EXPIRE_TIME, TimeUnit.HOURS);
                }
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities()));
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}
