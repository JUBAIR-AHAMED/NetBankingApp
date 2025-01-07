package com.netbanking.handler;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class LoginHandler {

    private static final String SECRET_KEY = "018d7a1625d1d217ffde1629409edbdb889f373aaef7032d6a711d2d40848fef";

    public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Login Servlet - Handle Post");
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Map<String, Object> userDetails = apiHandler.loginHandler(request);
            if (userDetails.isEmpty()) {
                throw new Exception();
            } else {
                String jwt = generateJwt(userDetails);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                responseMap.put("message", "Login successful");
                responseMap.put("status", true);
                responseMap.put("token", jwt); 
                String jsonResponse = Parser.getJsonResponse(responseMap);
                response.getWriter().write(jsonResponse);
                return;
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, response);
        }
    }

    private static String generateJwt(Map<String, Object> userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User details cannot be null");
        }

        return Jwts.builder()
                .setSubject(userDetails.get("userId").toString())
                .claim("userId", userDetails.get("userId"))
                .claim("role", userDetails.get("role"))
                .claim("branchId", userDetails.getOrDefault("branchId", null))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
