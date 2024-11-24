package com.netbanking.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;
import com.netbanking.util.ServletHelper;
import com.netbanking.util.Validator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
	
public class AccountsServlet extends HttpServlet {
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
                responseMap.put("message", "Authorization token is required.");
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


            String role = claims.get("role", String.class);
            Long branchId = claims.get("branchId", Long.class);
            Long userId = claims.get("userId", Long.class);
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

            if (("EMPLOYEE".equals(role) || "MANAGER".equals(role)) && branchId == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                responseMap.put("status", false);
                responseMap.put("message", "Branch ID is not found.");
                Parser.writeResponse(response, responseMap);
                return;
            }
            
            List<String> filterFields=new ArrayList<String>();
            List<Object> filterValues=new ArrayList<Object>();
            if ("CUSTOMER".equals(role)) {
                if (request.getParameter("accountNumber") != null || request.getParameter("userId") != null || request.getParameter("branchId") != null) {
                	ServletHelper.responseWriter(response, false, HttpServletResponse.SC_BAD_REQUEST, "No parameters allowed for CUSTOMER role.", responseMap);
                    Parser.writeResponse(response, responseMap);
                    return;
                }
            } else if ("EMPLOYEE".equals(role) || "MANAGER".equals(role)) {
                String accountNumberStr = request.getParameter("accountNumber");
                String userIdStr = request.getParameter("userId");
                String branchIdStr = request.getParameter("branchId");
                
                if(accountNumberStr!=null&&!accountNumberStr.isEmpty()) {
                	filterFields.add("accountNumber");
                	try {
                		filterValues.add(Long.parseLong(accountNumberStr));
                	} catch (NumberFormatException e) {
                		e.printStackTrace();
                	}
                }
                if(userIdStr!=null&&!userIdStr.isEmpty()) {
                	filterFields.add("userId");
                	try {
                		filterValues.add(Long.parseLong(userIdStr));
                	} catch (NumberFormatException e) {
                		e.printStackTrace();
                	}
                }
                if(branchIdStr!=null&&!branchIdStr.isEmpty()) {
                	filterFields.add("branchId");
                	try {
                		filterValues.add(Long.parseLong(branchIdStr));
                	} catch (NumberFormatException e) {
                		e.printStackTrace();
                	}
                }                
                if (accountNumberStr == null && userIdStr == null && branchIdStr == null) {
                	ServletHelper.responseWriter(response, false, HttpServletResponse.SC_BAD_REQUEST, "At least one parameter (accountNumber, customerId, or branchId) must be provided.", responseMap);
                    Parser.writeResponse(response, responseMap);
                    return;
                }
            }
            List<Map<String, Object>> accounts = apiHandler.getUserAccounts(userId, role, branchId, filterFields, filterValues);
            ServletHelper.responseWriter(response, true, HttpServletResponse.SC_OK, "Accounts fetched successfully", responseMap);
            responseMap.put("accounts", accounts);
        } catch (CustomException e) {
        	ServletHelper.responseWriter(response, false, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage(), responseMap);
            e.printStackTrace(); // Log the stack trace for debugging
        } catch (Exception e) {
        	ServletHelper.responseWriter(response, false, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred", responseMap);
            e.printStackTrace(); // Log unexpected exceptions
        }

        Parser.writeResponse(response, responseMap);
    }
}
