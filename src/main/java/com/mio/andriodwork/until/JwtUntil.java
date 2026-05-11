package com.mio.andriodwork.until;

import com.mio.andriodwork.config.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Map;

public class JwtUntil {
    public static String createJwt(Map<String,Object> map){
        return Jwts.builder().
                setClaims(map).
                signWith(Config.KEY, SignatureAlgorithm.HS512).
                setExpiration(new java.util.Date(System.currentTimeMillis() + Config.EXPIRE_TIME)).
                compact();
    }

    public static Boolean isJwtTrue(String token) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(Config.KEY).build();
        try {
            Claims body = jwtParser.parseClaimsJws(token).getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
