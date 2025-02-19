package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.enumHelper.EditableFields;
import com.netbanking.enums.Role;
import com.netbanking.functions.CustomerFunctions;
import com.netbanking.functions.EmployeeFunctions;
import com.netbanking.functions.UserFunctions;
import com.netbanking.object.Activity;
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Parser;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class UserHandler {
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
		Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> filters = new HashMap<String, Object>();
        JsonObject jsonObject = Parser.getJsonObject(request);
        
        Parser.storeIfPresent(jsonObject, filters, "userId", Long.class, "User Id", false);
        Parser.storeIfPresent(jsonObject, filters, "name", String.class, "Name", false);
        Parser.storeIfPresent(jsonObject, filters, "email", String.class, "Email", false);
        Parser.storeIfPresent(jsonObject, filters, "moreDetails", Boolean.class, "More Details", false);
        Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);
        Parser.storeIfPresent(jsonObject, filters, "searchSimilar", Boolean.class, "Type of search", false);
        Boolean countReq = (Boolean)filters.get("count");
        if(!filters.containsKey("moreDetails")) {
    		Writer.responseMapWriter(response,
    				HttpServletResponse.SC_OK, HttpServletResponse.SC_BAD_REQUEST,
    				"User Type and/or More details not found.", 
    				responseMap);
    		return;
    	}
        Integer limit = Parser.getValue(jsonObject, "limit", Integer.class, "Limit", false);
        Integer currentPage = Parser.getValue(jsonObject, "currentPage", Integer.class, "Current Page", false);
        String userType = Parser.getValue(jsonObject, "userType", String.class, "User Type", true);
        List<Map<String, Object>> users = null;
        if(userType.equalsIgnoreCase(Role.CUSTOMER.toString())) {
        	users = CustomerFunctions.getInstance().getCustomers(filters, limit, currentPage);
        } else if(userType.equalsIgnoreCase(Role.EMPLOYEE.toString())) {
        	Parser.storeIfPresent(jsonObject, filters, "branchId", Long.class, "Branch Id", false);
        	users = EmployeeFunctions.getInstance().getEmployees(filters, limit, currentPage);
        }
        Long count = ApiHelper.getCount(users);

        if(countReq!=null&&countReq) {
        	responseMap.put("count", count);
        } else {
        	responseMap.put("users", users);
        }
        Writer.responseMapWriter(response, HttpServletResponse.SC_OK, HttpServletResponse.SC_OK, "Users fetched successfully", responseMap);
	}
	
	public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
    	UserDetailsLocal store = UserDetailsLocal.get();
    	Long userId = store.getUserId();
        StringBuilder jsonBody = Parser.getJsonBody(request);
        Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
        EditableFields.validateEditableFields(User.class, data);
        User user = ApiHelper.getPojoFromRequest(data, User.class);            
        UserFunctions.getInstance().updateUser(user, userId);
        
        new Activity()
    		.setAction("UPDATE")
    		.setRecordname("user")
    		.setActorId(userId)
    		.setSubjectId(userId)
    		.setKeyValue(userId)
    		.setDetails(jsonBody.toString())
    		.setActionTime(System.currentTimeMillis())
    		.execute();

        // apiHandler.updateCustomer(jsonBody, userId);
        Map<String, Object> responseMap = new HashMap<>();
        Writer.responseMapWriter(response, 
        		HttpServletResponse.SC_OK, 
        		HttpServletResponse.SC_OK, 
        		"Profile updated successfully.", 
        		responseMap);
    }
}
