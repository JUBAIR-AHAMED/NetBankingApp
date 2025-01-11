package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Writer;

public class TransactionHandler {
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			Long branchId = (Long) request.getAttribute("branchId");
			
			ApiHandler apiHandler = new ApiHandler();			
            List<Map<String, Object>> statement = apiHandler.getStatement(request, userId, role, branchId);
            Long count = ApiHelper.getCount(statement);
            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("statement", statement);
            }
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Statement fetched successfully.", 
            		responseMap);
            return;
		} catch(Exception e) {
			ErrorHandler.handleException(e, response);
		}
	}
	
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			Long branchId = (Long) request.getAttribute("branchId");
			ApiHandler apiHandler = new ApiHandler();
			apiHandler.initiateTransaction(request, userId, role, branchId);	
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Transaction success.", 
            		responseMap);
		} catch (Exception e) {
			ErrorHandler.handleException(e, response);
		}
	}
}
