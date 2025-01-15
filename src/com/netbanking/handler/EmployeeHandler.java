package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.enumHelper.RequiredFields;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Activity;
import com.netbanking.object.Customer;
import com.netbanking.object.Employee;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class EmployeeHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Map<String, Object> responseMap = new HashMap<>();
		Long userId = (Long) request.getAttribute("userId");
    	try {
    		StringBuilder jsonBody = Parser.getJsonBody(request);
    		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
    		RequiredFields.validate("EMPLOYEE", data);
    		Employee employee = ApiHelper.getPojoFromRequest(data, Employee.class);
    		
    		ApiHandler apiHandler = new ApiHandler();
			Long createdEmployeeId = apiHandler.createEmployee(employee, userId);	
			new Activity()
        		.setAction("CREATE")
        		.setTablename("employee")
        		.setUserId(userId)
        		.setDetails(ApiHelper.dataToString(data))
        		.setActionTime(System.currentTimeMillis())
        		.execute();
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
}
