package com.netbanking.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.api.ApiHandler;
import com.netbanking.util.Parser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class StatementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SECRET_KEY = "018d7a1625d1d217ffde1629409edbdb889f373aaef7032d6a711d2d40848fef";
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ApiHandler apiHandler = new ApiHandler();
			String token = request.getHeader("Authorization");
			System.out.println(token);
			if(token == null || !token.startsWith("Bearer ")) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				responseMap.put("status", false);
				responseMap.put("message", "Authorization token is failed.");
				Parser.writeResponse(response, responseMap);
				return;
			}
			token = token.substring(7);
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
			Long branchId = claims.get("branchId", Long.class);
			String role = claims.get("role", String.class);
			
			if (userId == null) {
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            	responseMap.put("status", false);
            	responseMap.put("message", "User ID is missing.");
            	Parser.writeResponse(response, responseMap);
            	return;
            }

            if (("EMPLOYEE".equals(role) || "MANAGER".equals(role)) && branchId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("status", false);
                responseMap.put("message", "Branch ID is required for employees.");
                Parser.writeResponse(response, responseMap);
                return;
            }
            List<Map<String, Object>> statement = apiHandler.getStatement(request);
            response.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("status", true);
            responseMap.put("message", "Accounts fetched successfully");
            responseMap.put("statement", statement);
            Parser.writeResponse(response, responseMap);
            return;
		} catch(Exception e) {
			
		}
	}
}
