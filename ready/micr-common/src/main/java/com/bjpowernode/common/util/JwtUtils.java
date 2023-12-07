package com.bjpowernode.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {

    private String selfKey;

    public JwtUtils(String selfKey) {
        this.selfKey = selfKey;
    }

    // 创建jwt(token)
    public String createJwt(Map<String, Object> data, Integer minute) throws Exception {
        Date curDate = new Date();
        SecretKey secretKey = Keys.hmacShaKeyFor(selfKey.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder().signWith(secretKey, SignatureAlgorithm.HS256)
                    .setExpiration(DateUtils.addMinutes(curDate, minute))
                    .setIssuedAt(curDate)
                    .setId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase())
                    .addClaims(data)
                    .compact();
        return jwt;
    }

    // 读取jwt(token)
    public Claims readJwt(String jwt) throws Exception {
        SecretKey secretKey = Keys.hmacShaKeyFor(selfKey.getBytes(StandardCharsets.UTF_8));
        Claims body = Jwts.parserBuilder().setSigningKey(secretKey)
                        .build().parseClaimsJws(jwt).getBody();
        return body;
    }
}
