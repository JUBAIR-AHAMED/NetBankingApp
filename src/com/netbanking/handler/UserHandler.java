package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.enumHelper.EditableFields;
import com.netbanking.functions.CustomerFunctions;
import com.netbanking.functions.EmployeeFunctions;
import com.netbanking.functions.UserFunctions;
import com.netbanking.object.Activity;
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class UserHandler {
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            Map<String, Object> filters = new HashMap<String, Object>();
            JsonObject jsonObject = Parser.getJsonObject(request);
            
            Parser.storeIfPresent(jsonObject, filters, "userId", Long.class, "User Id", false);
            Parser.storeIfPresent(jsonObject, filters, "name", String.class, "Name", false);
            Parser.storeIfPresent(jsonObject, filters, "email", String.class, "Email", false);
            Parser.storeIfPresent(jsonObject, filters, "moreDetails", Boolean.class, "More Details", false);
            Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);
            Parser.storeIfPresent(jsonObject, filters, "searchSimilar", Boolean.class, "Type of search", false);
            
	        if(!filters.containsKey("moreDetails")) {
        		Writer.responseWriter(response,
        				false, HttpServletResponse.SC_BAD_REQUEST,
        				"User Type and/or More details not found.", 
        				responseMap);
        		Writer.writeResponse(response, responseMap);
        		return;
        	}
            Integer limit = Parser.getValue(jsonObject, "limit", Integer.class);
            Integer currentPage = Parser.getValue(jsonObject, "currentPage", Integer.class);
            String userType = Parser.getValue(jsonObject, "userType", String.class, "User Type", true);
            List<Map<String, Object>> users = null;
            if(userType.equals("customer")) {
            	users = new CustomerFunctions().getCustomers(filters, limit, currentPage);
            } else if(userType.equals("employee")) {
            	Parser.storeIfPresent(jsonObject, filters, "branchId", Long.class, "Branch Id", false);
            	users = new EmployeeFunctions().getEmployees(filters, limit, currentPage);
            }
            Writer.responseWriter(response, true, HttpServletResponse.SC_OK, "Users fetched successfully", responseMap);
            Long count = null;
            if (users.size() >= 1) {
                Object countValue = users.get(0).getOrDefault("count", null);
                if (countValue instanceof Integer) {
                    count = ((Integer) countValue).longValue();
                } else if (countValue instanceof Long) {
                    count = (Long) countValue;
                }
            }

            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("users", users);
            }
            Writer.writeResponse(response, responseMap);
        } catch (Exception e) {
        	ErrorHandler.handleException(e, response);
        }
	}
	
	public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
        	UserDetailsLocal store = UserDetailsLocal.get();
        	Long userId = store.getUserId();
            StringBuilder jsonBody = Parser.getJsonBody(request);
            Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
            EditableFields.validateEditableFields(User.class, data);
            User user = ApiHelper.getPojoFromRequest(data, User.class);            
            new UserFunctions().updateUser(user, userId);
            
            new Activity()
        		.setAction("UPDATE")
        		.setTablename("user")
        		.setUserId(userId)
        		.setDetails(ApiHelper.dataToString(data))
        		.setActionTime(System.currentTimeMillis())
        		.execute();

            // apiHandler.updateCustomer(jsonBody, userId);
            Map<String, Object> responseMap = new HashMap<>();
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Profile updated successfully.", 
            		responseMap);
        } catch (Exception e) {
            ErrorHandler.handleException(e, response);
        }
    }
}
