package com.netbanking.util;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.exception.CustomException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenHelper {
	private static final String SECRET_KEY = System.getenv("JWT_SECRET");
	public static String generateJwt(Map<String, Object> userDetails) {
        if (Validator.isNull(userDetails)) {
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
	
	public static Claims getClaims(String token, Claims claims) throws CustomException {
    	if (Validator.isNull(token) || !token.startsWith("Bearer ")) {
    		throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token is required.");
        }
    	try {
        	token = token.replace("Bearer ", "");
        	claims = Jwts.parser()
        			.setSigningKey(SECRET_KEY)
        			.parseClaimsJws(token)
        			.getBody();
        } catch (ExpiredJwtException ex) {
        	throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Expired token.");
        } catch (Exception e) {
        	throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
		}
    	return claims;
    }
}
