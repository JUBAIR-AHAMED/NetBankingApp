package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class TransactionHandler {
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ApiHandler apiHandler = new ApiHandler();			
			Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			Long branchId = (Long) request.getAttribute("branchId");
			
            List<Map<String, Object>> statement = apiHandler.getStatement(request, userId, role, branchId);
            response.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("status", true);
            responseMap.put("message", "Statement fetched successfully");
            Object countObj = statement.get(0).getOrDefault("count", null);
            Long count = null;
            if(countObj!=null) {
            	if (countObj instanceof Integer) {
            		count = ((Integer) countObj).longValue();
            	} else if (countObj instanceof Long) {
            		count = (Long) countObj;
            	}
            }

            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("statement", statement);
            }
            Writer.writeResponse(response, responseMap);
            return;
		} catch(CustomException e) {			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			responseMap.put("status", false);
			responseMap.put("message", e.getMessage());
			Writer.writeResponse(response, responseMap);
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			responseMap.put("status", false);
			Writer.writeResponse(response, responseMap);	
		}
	}
	
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
		try {
			ApiHandler apiHandler = new ApiHandler();
			Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			Long branchId = (Long) request.getAttribute("branchId");
			try {
				apiHandler.initiateTransaction(request, userId, role, branchId);	
				response.setStatus(HttpServletResponse.SC_OK);
				responseMap.put("status", true);
                responseMap.put("message", "Transaction success.");
			} catch(CustomException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				responseMap.put("status", false);
                responseMap.put("message", e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMap.put("status", false);
            responseMap.put("message", "Transaction failed.");
		}
		Writer.writeResponse(response, responseMap);
	}
}
