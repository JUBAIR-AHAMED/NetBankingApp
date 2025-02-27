package com.netbanking.util;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class TokenHelper {
	private static final String SECRET_KEY_STRING = System.getenv("JWT_SECRET");
	public static String generateJwt(Map<String, Object> userDetails) {
        if (Validator.isNull(userDetails)) {
            throw new IllegalArgumentException("User details cannot be null");
        }
        SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userDetails.get("userId").toString())
                .claim("userId", userDetails.get("userId"))
                .claim("role", userDetails.get("role"))
                .claim("branchId", userDetails.getOrDefault("branchId", null))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L))
                .signWith(SECRET_KEY, Jwts.SIG.HS256)
                .compact();
    }
	
	public static Claims getClaims(String token, Claims claims) throws CustomException {
    	if (Validator.isNull(token) || !token.startsWith("Bearer ")) {
    		throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token is required.");
        }
    	SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    	try {
        	token = token.replace("Bearer ", "");
        	claims = Jwts.parser()
        			.verifyWith(SECRET_KEY)
        			.build()
        			.parseSignedClaims(token)
        			.getPayload();
        } catch (ExpiredJwtException ex) {
        	throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Expired token.");
        } catch (Exception e) {
        	throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
		}
    	return claims;
    }
}
