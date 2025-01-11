package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class CustomerHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		ApiHandler apiHandler = new ApiHandler();
		Long userId = (Long) request.getAttribute("userId");
    	try {
			Long createdCustomerId = apiHandler.createCustomer(request, userId);	
            responseMap.put("customerId", createdCustomerId);
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Customer created successfully.", 
            		responseMap);
		} catch(Exception e) {
			ErrorHandler.handleException(e, response);
    	}
	}
}
