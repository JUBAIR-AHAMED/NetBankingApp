package com.netbanking.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

//@WebServlet("/createEmployee")
public class CreateEmployeeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	Map<String, Object> responseMap = new HashMap<>();
    	try {
			ApiHandler apiHandler = new ApiHandler();
			Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			Long branchId = (Long) request.getAttribute("branchId");
			
			if(!role.equals("MANAGER")) {
				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				responseMap.put("status", false);
				responseMap.put("message", "Permission Denied.");
				Parser.writeResponse(response, responseMap);
				return;
			}
			
			try {
				Long employeeId = apiHandler.createEmployee(request, userId, role, branchId);	
				response.setStatus(HttpServletResponse.SC_OK);
				responseMap.put("status", true);
                responseMap.put("message", "Action completed successfully.");
                responseMap.put("employeeId", employeeId);
			} catch(CustomException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				responseMap.put("status", false);
                responseMap.put("message", e.getMessage());
			}
    	} catch(Exception e) {
    		e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMap.put("status", false);
            responseMap.put("message", "Action failed.");
    	}
    	Parser.writeResponse(response, responseMap);
    }
}
