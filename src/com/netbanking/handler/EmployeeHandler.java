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
import com.netbanking.functions.EmployeeFunctions;
import com.netbanking.functions.UserFunctions;
import com.netbanking.object.Activity;
import com.netbanking.object.Employee;
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class EmployeeHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
    	try {
    		StringBuilder jsonBody = Parser.getJsonBody(request);
    		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
    		RequiredFields.validate("EMPLOYEE", data);
    		Employee employee = ApiHelper.getPojoFromRequest(data, Employee.class);
    		// initiating creation
			Long createdEmployeeId = new EmployeeFunctions().createEmployee(employee, userId);
			// Activity Logger
			new Activity()
        		.setAction("CREATE")
        		.setRecordname("employee")
        		.setActorId(userId)
        		.setSubjectId(createdEmployeeId)
        		.setKeyValue(createdEmployeeId)
        		.setDetails(jsonBody.toString())
        		.setActionTime(System.currentTimeMillis())
        		.execute();

			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("employeeId", createdEmployeeId);
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Employee created successfully.", 
            		responseMap);
		} catch(Exception e) {
			ErrorHandler.handleException(e, response);
    	}
	}
	
	public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            Long userId = (Long) request.getAttribute("userId");
            StringBuilder jsonBody = Parser.getJsonBody(request);
            Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
            Function<String, Object> parseLong = Long::parseLong;
            Long key = (Long) parseLong.apply((String) data.remove("key"));
            EditableFields.validateEditableFields(Employee.class, data);
    		// generating the pojo object 
            User user = ApiHelper.getPojoFromRequest(data, User.class);
            Employee employee = ApiHelper.getPojoFromRequest(data, Employee.class);
            Map<String, Object>  userData = new UserFunctions().get(key);
            if(userData.get("status").equals(Status.INACTIVE.toString())) {
            	throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, "User is inactive.");
            }
            // updating using the object
            new UserFunctions().updateUser(user, key);
            new EmployeeFunctions().updateEmployee(employee, userId, key);
            // activity logger
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
