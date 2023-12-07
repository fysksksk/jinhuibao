package com.bjpowernode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtTest {

    @Test
    // 5f41c1afdcc0487ca8eb8ea8863e47e3
    public void testCreateJwt() {
        String key = "5f41c1afdcc0487ca8eb8ea8863e47e3";

        // 创建SeceretKey
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        Date curDate = new Date();
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 1001);
        data.put("name", "李四");
        data.put("role", "经理");
        // 创建Jwt，使用Jwts类
        String jwt = Jwts.builder().signWith(secretKey, SignatureAlgorithm.HS256)
                .setExpiration(DateUtils.addMinutes(curDate, 10))
                .setIssuedAt(curDate)
                .setId(UUID.randomUUID().toString())
                .addClaims(data).compact();

        System.out.println("jwt == " + jwt);
    }

    @Test
    public void testReadJwt() {
        String jwt = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTc3OTQ2MzEsImlhdCI6MTY5Nzc5NDAzMSwianRpIjoiMjJjZWFmZTQtOGVjNy00OWE1LTg4ZmItOWZlOTIyZmMyZWU0Iiwicm9sZSI6Iue7j-eQhiIsIm5hbWUiOiLmnY7lm5siLCJ1c2VySWQiOjEwMDF9.t8N0SuzSix5jbMX_Ne9TSVMIHXNmHOFUZoVScwPWfRo";
        String key = "5f41c1afdcc0487ca8eb8ea8863e47e3";

        // 创建SeceretKey
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        // 解析jwt，没有异常，解析成功
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(jwt);

        // 读数据
        Claims body = claimsJws.getBody();
        Integer userId = body.get("userId", Integer.class);
        System.out.println("userId = " + userId);
        Object uid = body.get("userId");
        System.out.println("uid = " + uid);

        Object name = body.get("name");
        if (name != null) {
            String str = (String) name;
            System.out.println("str = " + str);
        }

        String jwtId = body.getId();
        System.out.println("jwtId = " + jwtId);

        Date expiration = body.getExpiration();
        System.out.println("过期时间 = " + expiration);
    }
}
