package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class CustomerHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		ApiHandler apiHandler = new ApiHandler();
    	try {
    		Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			if(role.equals("CUSTOMER")) {
				response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
				responseMap.put("status", false);
				responseMap.put("message", "Permission Denied.");
				Writer.writeResponse(response, responseMap);
				return;
			}
			
			try {
				Long createdCustomerId = apiHandler.createCustomer(request, userId);	
				response.setStatus(HttpServletResponse.SC_OK);
				responseMap.put("status", true);
	            responseMap.put("message", "Customer created successfully.");
	            responseMap.put("customerId", createdCustomerId);
			} catch(CustomException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				responseMap.put("status", false);
	            responseMap.put("message", e.getMessage());
			} }catch(Exception e) {
	    		e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				responseMap.put("status", false);
	            responseMap.put("message", "Action failed.");
	    	}
    	Writer.writeResponse(response, responseMap);
	}
}
