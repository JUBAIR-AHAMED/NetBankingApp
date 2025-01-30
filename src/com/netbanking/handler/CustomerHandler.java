package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.enumHelper.EditableFields;
import com.netbanking.enumHelper.RequiredFields;
import com.netbanking.enums.Status;
import com.netbanking.exception.CustomException;
import com.netbanking.functions.CustomerFunctions;
import com.netbanking.functions.UserFunctions;
import com.netbanking.object.Activity;
import com.netbanking.object.Customer;
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class CustomerHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
    	try {
    		// fetching data from body, validating, preparing object
    		Map<String, Object> responseMap = new HashMap<>();
    		StringBuilder jsonBody = Parser.getJsonBody(request);
    		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
    		RequiredFields.validate("CUSTOMER", data);
    		Customer customer = ApiHelper.getPojoFromRequest(data, Customer.class);
    		// initiating creation
			Long createdCustomerId = new CustomerFunctions().createCustomer(customer, userId);	
			// Activity logger
			new Activity()
        		.setAction("CREATE")
        		.setRecordname("customer")
        		.setActorId(userId)
        		.setSubjectId(createdCustomerId)
        		.setKeyValue(createdCustomerId)
        		.setDetails(jsonBody.toString())
        		.setActionTime(System.currentTimeMillis())
        		.execute();
			
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
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
        try {
        	// fetching data from body, validating, preparing object
        	Map<String, Object> responseMap = new HashMap<>();
            StringBuilder jsonBody = Parser.getJsonBody(request);
            Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
            Function<String, Object> parseLong = Long::parseLong;
            Long key = (Long) parseLong.apply((String) data.remove("key"));
            EditableFields.validateEditableFields(Customer.class, data);
            Map<String, Object>  userData = new UserFunctions().get(key);
            if(userData.get("status").equals(Status.INACTIVE.toString())) {
            	throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "User is inactive.");
            }
            // updating the user table
    		User user = ApiHelper.getPojoFromRequest(data, User.class);
            new UserFunctions().updateUser(user, key);
            // updating the employee table
            Customer customer = ApiHelper.getPojoFromRequest(data, Customer.class);
            new CustomerFunctions().updateCustomer(customer, userId, key);
            // Activity logger
            new Activity()
        		.setAction("UPDATE")
        		.setRecordname("customer")
        		.setActorId(userId)
        		.setSubjectId(key)
        		.setKeyValue(key)
        		.setDetails(jsonBody.toString())
        		.setActionTime(System.currentTimeMillis())
        		.execute();
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
