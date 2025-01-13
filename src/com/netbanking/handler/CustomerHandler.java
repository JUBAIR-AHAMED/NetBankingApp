package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.api.ApiHandler;
import com.netbanking.enumHelper.EditableFields;
import com.netbanking.object.Activity;
import com.netbanking.object.Customer;
import com.netbanking.object.User;
import com.netbanking.util.ActivityLogger;
import com.netbanking.util.ApiHelper;
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
	
	public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
            StringBuilder jsonBody = Parser.getJsonBody(request);
            Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
            Function<String, Object> parseLong = Long::parseLong;
            Long key = (Long) parseLong.apply((String) data.remove("key"));
            EditableFields.validateEditableFields(Customer.class, data);
    		User user = ApiHelper.getPojoFromRequest(data, User.class);
            apiHandler.updateUser(user, userId, key);
            Customer customer = ApiHelper.getPojoFromRequest(data, Customer.class);
            apiHandler.updateCustomer(customer, userId, key);
            Activity activity = new Activity()
            		.setAction("UPDATE")
            		.setTablename("customer")
            		.setUserId(userId)
            		.setDetails(ApiHelper.dataToString(data))
            		.setActionTime(System.currentTimeMillis());
			ActivityLogger activityLogger = new ActivityLogger();
			activityLogger.log(activity);
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Customer update successfully.", 
            		responseMap);
        } catch (Exception e) {
        	ErrorHandler.handleException(e, response);
        }
	}
}
