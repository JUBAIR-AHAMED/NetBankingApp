package com.netbanking.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SECRET_KEY = "018d7a1625d1d217ffde1629409edbdb889f373aaef7032d6a711d2d40848fef";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();

            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseMap.put("status", false);
                responseMap.put("message", ".Authorization token is required.");
                Parser.writeResponse(response, responseMap);
                return;
            }

            token = token.replace("Bearer ", "");
            Claims claims;
            try {
                claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseMap.put("status", false);
                responseMap.put("message", "Invalid or expired token.");
                Parser.writeResponse(response, responseMap);
                return;
            }


            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);
            if (userId == null) {
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            	responseMap.put("status", false);
            	responseMap.put("message", "User ID is missing.");
            	Parser.writeResponse(response, responseMap);
            	return;
            }
            
            if (!"EMPLOYEE".equals(role) && !"MANAGER".equals(role) && !"CUSTOMER".equals(role) ) {
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            	responseMap.put("status", false);
            	responseMap.put("message", "Role is missing or inappropriate.");
            	Parser.writeResponse(response, responseMap);
            	return;
            }
            
            Map<String, Object> profile = apiHandler.getProfile(userId, role);
            response.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("status", true);
            responseMap.put("message", "Profile fetched successfully");
            responseMap.put("profile", profile);
        } catch (CustomException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", false);
            responseMap.put("message", "Failed to fetch profile due to a server error.");
            e.printStackTrace(); // Log the stack trace for debugging
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", false);
            responseMap.put("message", "An unexpected error occurred.");
            e.printStackTrace(); // Log unexpected exceptions
        }

        Parser.writeResponse(response, responseMap);
    }
}
