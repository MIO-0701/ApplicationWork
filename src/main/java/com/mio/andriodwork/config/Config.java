package com.mio.andriodwork.config;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

public class Config {
    public static final int successCode = 200;
    public static final int errorCode = 0;
    public final static String SECRECT = "mio-64-byte-secret-key-here-requires-at-least-64-characters-for-hs512-algorithm-security";
    public final static SecretKey KEY= Keys.hmacShaKeyFor(SECRECT.getBytes());//密钥
    public final static long EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;//7天
    public final static int NO_DELETE = 0;
    public final static int DELETED = 1;
}
