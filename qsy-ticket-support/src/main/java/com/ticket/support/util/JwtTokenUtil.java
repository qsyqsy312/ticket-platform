package com.ticket.support.util;

import com.google.common.collect.Maps;
import com.ticket.support.dto.base.LoginUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;

import static com.ticket.support.constants.Constants.TOKEN_EXPIRE_TIME;

public class JwtTokenUtil {

    /**
     * 密钥
     */
    private static final String SECRET = "qsyqsy";


    /**
     * 从数据声明生成令牌
     *
     * @param userDetails 数据声明
     * @return 令牌
     */
    public static String generateToken(LoginUserInfo userDetails) {
        Date expirationDate = new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME*60*60*1000L);
        HashMap<String, Object> claims = Maps.newHashMap();
        claims.put("sub",userDetails.getId());
        claims.put("username", userDetails.getUsername());
        claims.put("createTime", new Date());
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS512, SECRET).compact();
    }


    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    public static Jws<Claims> getClaimsFromToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
        return claims;
    }


    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        String username;
        Claims claims = getClaimsFromToken(token).getBody();
        username = (String) claims.get("username");
        return username;
    }




    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public static Boolean validateToken(String token, UserDetails userDetails) {
        LoginUserInfo user = (LoginUserInfo) userDetails;
        String username = getUsernameFromToken(token);
        return username.equals(user.getUsername());
    }

}
