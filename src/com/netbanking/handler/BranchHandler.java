package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.util.Converter;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class BranchHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
    	try {
			ApiHandler apiHandler = new ApiHandler();
			Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			Long branchId = (Long) request.getAttribute("branchId");
			
			try {
				Long createdBranchId = apiHandler.createBranch(request, userId, role, branchId);	
				response.setStatus(HttpServletResponse.SC_OK);
				responseMap.put("status", true);
                responseMap.put("message", "Action completed successfully.");
                responseMap.put("branchId", createdBranchId);
			} catch(CustomException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				responseMap.put("status", false);
                responseMap.put("message", e.getMessage());
			}
    	} catch(Exception e) {
            ErrorHandler.handleException(e, response);
    	}
	}
	
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();

            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");

            Map<String, Object> filters = new HashMap<String, Object>();
            JsonObject jsonObject = Parser.getJsonObject(request);
            Parser.storeIfPresent(jsonObject, filters, "branchId", Long.class, "Branch Id", false);
            Parser.storeIfPresent(jsonObject, filters, "employeeId", Long.class, "Employee Id", false);
            Parser.storeIfPresent(jsonObject, filters, "ifsc", String.class, "IFSC", false);
            Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);
		    
            // Role-based logic
            if ("CUSTOMER".equals(role) && !filters.isEmpty()) {
            	Writer.responseWriter(response, false, HttpServletResponse.SC_BAD_REQUEST, "No parameters allowed for CUSTOMER role.", responseMap);
                return;
            }
            
            Integer limit=Parser.getValue(jsonObject, "limit", Integer.class), 
            		currentPage=Parser.getValue(jsonObject, "currentPage", Integer.class);
            
            List<Map<String, Object>> accounts = apiHandler.filteredGetBranch(userId, role, branchId, filters, limit, currentPage);
            Writer.responseWriter(response, true, HttpServletResponse.SC_OK, "Branchs fetched successfully", responseMap);
            
            Long count = accounts.size()==0? null : Converter.convertToLong(accounts.get(0).getOrDefault("count", null)) ;
            
            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("branch", accounts);
            }
            Writer.writeResponse(response, responseMap);
        } catch (Exception e) {
        	ErrorHandler.handleException(e, response);
        }
	}
}
