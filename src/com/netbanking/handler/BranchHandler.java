package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.enumHelper.RequiredFields;
import com.netbanking.enums.Role;
import com.netbanking.functions.BranchFunctions;
import com.netbanking.object.Activity;
import com.netbanking.object.Branch;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class BranchHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
    	try {			
			StringBuilder jsonBody = Parser.getJsonBody(request);
			Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
			
			RequiredFields.validate("BRANCH", data);
			Branch branch = ApiHelper.getPojoFromRequest(data, Branch.class);
			Long createdBranchId = new BranchFunctions().createBranch(branch);	
			
			// Activity logger
			new Activity()
        		.setAction("CREATE")
        		.setTablename("branch")
        		.setUserId(userId)
        		.setDetails(ApiHelper.dataToString(data))
        		.setActionTime(System.currentTimeMillis())
        		.execute();
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
        	UserDetailsLocal store = UserDetailsLocal.get();
			Role role = store.getRole();
            
			Map<String, Object> filters = new HashMap<String, Object>();
            JsonObject jsonObject = Parser.getJsonObject(request);
            Parser.storeIfPresent(jsonObject, filters, "branchId", Long.class, "Branch Id", false);
            if(role.equals(Role.MANAGER)) {
            	Parser.storeIfPresent(jsonObject, filters, "employeeId", Long.class, "Employee Id", false);
            	Parser.storeIfPresent(jsonObject, filters, "ifsc", String.class, "IFSC", false);
            	Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);
            	Parser.storeIfPresent(jsonObject, filters, "searchSimilar", Boolean.class, "Type of search", false);
            }
            Boolean countReq = (Boolean) filters.get("count");
            Integer limit=Parser.getValue(jsonObject, "limit", Integer.class);
            Integer currentPage=Parser.getValue(jsonObject, "currentPage", Integer.class);
            // fetching branch data
            List<Map<String, Object>> branch =new BranchFunctions().filteredGetBranch(filters, limit, currentPage);            
            // if asked to get count
            if(countReq!=null&&countReq) {
            	Long count = ApiHelper.getCount(branch);
            	responseMap.put("count", count);
            }
            // if asked to get details
            else {
            	responseMap.put("branch", branch);
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