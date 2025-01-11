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

public class EmployeeHandler {
	public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long userId = (Long) request.getAttribute("userId");
		String role = (String) request.getAttribute("role");
		Long branchId = (Long) request.getAttribute("branchId");
		
		try {
			Map<String, Object> responseMap = new HashMap<>();
			ApiHandler apiHandler = new ApiHandler();
			Long employeeId = apiHandler.createEmployee(request, userId, role, branchId);	
            responseMap.put("employeeId", employeeId);
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Employee updated successfully.", 
            		responseMap);
		} catch(Exception e) {
			ErrorHandler.handleException(e, response);
    	}
	}
}
