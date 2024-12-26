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
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Parser;

public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
            
            Map<String, Object> profile = apiHandler.get(userId, User.class);

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
    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
            StringBuilder jsonBody = ApiHelper.getJsonBody(request);
            apiHandler.updateUser(jsonBody, userId);
//            apiHandler.updateCustomer(jsonBody, userId);
            
            response.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("status", true);
            responseMap.put("message", "Updated successfully");
        } catch (CustomException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", false);
            responseMap.put("message", "Update failed due to a server error.");
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
