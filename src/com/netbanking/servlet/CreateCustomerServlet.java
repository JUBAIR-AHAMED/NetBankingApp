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

public class CreateCustomerServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Map<String, Object> responseMap = new HashMap<>();
			ApiHandler apiHandler = new ApiHandler();
			
		try {
			Long createdCustomerId = apiHandler.createCustomer(request);	
			response.setStatus(HttpServletResponse.SC_OK);
			responseMap.put("status", true);
            responseMap.put("message", "Customer created successfully.");
            responseMap.put("customerId", createdCustomerId);
		} catch(CustomException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			responseMap.put("status", false);
            responseMap.put("message", e.getMessage());
		} catch(Exception e) {
    		e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMap.put("status", false);
            responseMap.put("message", "Action failed.");
    	}
    	Parser.writeResponse(response, responseMap);
    }
}