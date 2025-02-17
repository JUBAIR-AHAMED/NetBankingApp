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
import com.netbanking.exception.CustomException;
import com.netbanking.functions.BranchFunctions;
import com.netbanking.functions.EmployeeFunctions;
import com.netbanking.functions.UserFunctions;
import com.netbanking.object.Activity;
import com.netbanking.object.Branch;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Converter;
import com.netbanking.util.PasswordUtility;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class BranchHandler {
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
		UserDetailsLocal store = UserDetailsLocal.get();
		Long userId = store.getUserId();
		StringBuilder jsonBody = Parser.getJsonBody(request);
		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
		RequiredFields.BRANCH.validate(data);
		Branch branch = ApiHelper.getPojoFromRequest(data, Branch.class);
		Map<String, Object> userDetails = new EmployeeFunctions().get(branch.getEmployeeId());
		if(userDetails != null && !userDetails.isEmpty()) {	
			throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid employee id.");
		} else if(userDetails.get("role").equals(Role.MANAGER.toString())) {
			throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "The employee is not a manager.");
		}
		Long createdBranchId = new BranchFunctions().createBranch(branch);	
		Long subjectId = Converter.convertToLong(data.get("employeeId"));
		// Activity logger
		new Activity()
    		.setAction("CREATE")
    		.setRecordname("branch")
    		.setActorId(userId)
    		.setSubjectId(subjectId)
    		.setKeyValue(createdBranchId)
    		.setDetails(jsonBody.toString())
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
	}
	
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
    	Map<String, Object> responseMap = new HashMap<>();
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
        Integer limit=Parser.getValue(jsonObject, "limit", Integer.class, "Limit", false);
        Integer currentPage=Parser.getValue(jsonObject, "currentPage", Integer.class, "Current Page", false);
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
	}
}