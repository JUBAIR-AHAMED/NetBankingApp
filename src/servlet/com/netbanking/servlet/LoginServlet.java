package com.netbanking.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.api.ApiHandler;
import com.netbanking.dao.FunctionHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String SECRET_KEY = "018d7a1625d1d217ffde1629409edbdb889f373aaef7032d6a711d2d40848fef";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> responseMap = new HashMap<>();

        try {
            ApiHandler apiHandler = new ApiHandler();
            Map<String, Object> userDetails = apiHandler.loginHandler(request);
            if (userDetails.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseMap.put("message", "Invalid credentials");
                responseMap.put("status", false);
            } else {
                String jwt = generateJwt(userDetails);

                response.setStatus(HttpServletResponse.SC_OK);
                responseMap.put("message", "Login successful");
                responseMap.put("status", true);
                responseMap.put("token", jwt); 
            }
        } catch (CustomException e) {
        	e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("message", "Failed to authenticate user");
            responseMap.put("status", false);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = Parser.getJsonResponse(responseMap);
        response.getWriter().write(jsonResponse);
    }

    private String generateJwt(Map<String, Object> userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("User details cannot be null");
        }

        return Jwts.builder()
                .setClaims(userDetails)
                .setSubject(userDetails.get("userId").toString()) // Include user ID as a subject
                .claim("role", userDetails.get("role")) // Add role to the JWT
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

}
