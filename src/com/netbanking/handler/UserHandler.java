package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Customer;
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class UserHandler {
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");
            Map<String, Object> filters = new HashMap<String, Object>();
            JsonObject jsonObject = Parser.getJsonObject(request);
            
            Parser.storeIfPresent(jsonObject, filters, "userId", Long.class, "User Id", false);
            Parser.storeIfPresent(jsonObject, filters, "name", String.class, "Name", false);
            Parser.storeIfPresent(jsonObject, filters, "email", String.class, "Email", false);
            Parser.storeIfPresent(jsonObject, filters, "userType", String.class, "User Type", false);
            Parser.storeIfPresent(jsonObject, filters, "moreDetails", Boolean.class, "More Details", false);
            Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);

	        if(!filters.containsKey("userType")||!filters.containsKey("moreDetails")) {
        		Writer.responseWriter(response,
        				false, HttpServletResponse.SC_BAD_REQUEST,
        				"User Type and/or More details not found.", 
        				responseMap);
        		Writer.writeResponse(response, responseMap);
        		return;
        	}
            Integer limit = Parser.getValue(jsonObject, "limit", Integer.class);
            Integer currentPage = Parser.getValue(jsonObject, "currentPage", Integer.class);
            List<Map<String, Object>> accounts = apiHandler.filteredGetCustomerOrEmployee(userId, role, branchId, filters, limit, currentPage);
            Writer.responseWriter(response, true, HttpServletResponse.SC_OK, "Accounts fetched successfully", responseMap);
            Long count = null;

            if (accounts.size() >= 1) {
                Object countValue = accounts.get(0).getOrDefault("count", null);
                if (countValue instanceof Integer) {
                    count = ((Integer) countValue).longValue();
                } else if (countValue instanceof Long) {
                    count = (Long) countValue;
                }
            }

            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("users", accounts);
            }
            Writer.writeResponse(response, responseMap);
        } catch (Exception e) {
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
            Long key = (Long) parseLong.apply((String) data.get("key"));
    		User user = ApiHelper.getPojoFromRequest(data, User.class);
            apiHandler.updateUser(user, userId, key);
            Customer customer = ApiHelper.getPojoFromRequest(data, Customer.class);
            apiHandler.updateCustomer(customer, userId, key);            
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
            e.printStackTrace();
        }

        Writer.writeResponse(response, responseMap);
	}
}
