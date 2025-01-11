package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.netbanking.api.ApiHandler;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class BranchHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	try {
    		// getting required data
			Long userId = (Long) request.getAttribute("userId");
			String role = (String) request.getAttribute("role");
			Long branchId = (Long) request.getAttribute("branchId");
			// Creation process with returning branch id
			ApiHandler apiHandler = new ApiHandler();
			Long createdBranchId = apiHandler.createBranch(request, userId, role, branchId);	
			// response
			Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("branchId", createdBranchId);
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Branch created successfully.", 
            		responseMap);
    	} catch(Exception e) {
            ErrorHandler.handleException(e, response);
    	}
	}
	
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {

            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");

            Map<String, Object> filters = new HashMap<String, Object>();
            JsonObject jsonObject = Parser.getJsonObject(request);
            Parser.storeIfPresent(jsonObject, filters, "branchId", Long.class, "Branch Id", false);
            Parser.storeIfPresent(jsonObject, filters, "employeeId", Long.class, "Employee Id", false);
            Parser.storeIfPresent(jsonObject, filters, "ifsc", String.class, "IFSC", false);
            Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);
		                
            Integer limit=Parser.getValue(jsonObject, "limit", Integer.class), 
            		currentPage=Parser.getValue(jsonObject, "currentPage", Integer.class);
            
            ApiHandler apiHandler = new ApiHandler();
            List<Map<String, Object>> accounts = apiHandler.filteredGetBranch(userId, role, branchId, filters, limit, currentPage);
            
            Long count = ApiHelper.getCount(accounts);
            // if asked to get count
            if(count!=null) {
            	responseMap.put("count", count);
            }
            // if asked to get details
            else {
            	responseMap.put("branch", accounts);
            }
            // response
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Branch fetched successfully.", 
            		responseMap);
            return;
        } catch (Exception e) {
        	ErrorHandler.handleException(e, response);
        }
	}
}
