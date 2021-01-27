package com.ticket.support.constants;

public class Constants {
    /**
     * requestHeader中token的key
     */
    public static final String HEADER = "Authorization";
    /**
     * requestHeader中token的value的前缀
     */
    public static final String TOKEN_SPLIT = "Bearer ";

    /**
     * token过期时间(小时)
     */
    public static final int TOKEN_EXPIRE_TIME = 2;

    /**
     * 登录用户信息缓存key前缀
     */
    public static final String REDIS_USER_LOGIN_PREFIX ="USER_LOGIN:";
}
