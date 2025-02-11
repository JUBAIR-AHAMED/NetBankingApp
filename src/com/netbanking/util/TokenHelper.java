package com.netbanking.util;

import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenHelper {
	private static final String SECRET_KEY = System.getenv("JWT_SECRET");
	public static String generateJwt(Map<String, Object> userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User details cannot be null");
        }

        return Jwts.builder()
                .setSubject(userDetails.get("userId").toString())
                .claim("userId", userDetails.get("userId"))
                .claim("role", userDetails.get("role"))
                .claim("branchId", userDetails.getOrDefault("branchId", null))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
