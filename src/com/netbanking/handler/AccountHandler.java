package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.api.ApiHandler;
import com.netbanking.enumHelper.EditableFields;
import com.netbanking.enumHelper.RequiredFields;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Account;
import com.netbanking.object.Activity;
import com.netbanking.util.ActivityLogger;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Converter;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class AccountHandler {
	public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");
            Map<String, Object> filters = new HashMap<String, Object>();
            JsonObject jsonObject = Parser.getJsonObject(request);
            Parser.storeIfPresent(jsonObject, filters, "accountNumber", Long.class, "Account Number", false);
            Parser.storeIfPresent(jsonObject, filters, "userId", Long.class, "User Id", false);
            Parser.storeIfPresent(jsonObject, filters, "branchId", Long.class, "Branch Id", false);
            Parser.storeIfPresent(jsonObject, filters, "count", Boolean.class, "Count", false);
            Parser.storeIfPresent(jsonObject, filters, "searchSimilar", Boolean.class, "Type of search", false);
            Boolean countReq = (Boolean) filters.get("count");
		    if ("CUSTOMER".equals(role) && !filters.isEmpty()) {
                throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "No parameters allowed for CUSTOMER role.");
            }
            
            Integer limit=Parser.getValue(jsonObject, "limit", Integer.class);
            Integer currentPage=Parser.getValue(jsonObject, "currentPage", Integer.class);
            
            List<Map<String, Object>> accounts = apiHandler
            									.filteredGetAccount(
            											userId, role, branchId, filters, limit, currentPage);
            
            // Sending the count or account data as requested
            if(countReq!=null&&countReq) {
            	Long count = ApiHelper.getCount(accounts);
            	responseMap.put("count", count);
            } else {
            	responseMap.put("accounts", accounts);
            }

            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Accounts fetched successfully", 
            		responseMap);
        } catch (Exception e) {
        	ErrorHandler.handleException(e, response);
    	}
	}
	
	public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
    	Map<String, Object> responseMap = new HashMap<>();
		ApiHandler apiHandler = new ApiHandler();
	    Long userId = (Long) request.getAttribute("userId");
	    StringBuilder jsonBody = Parser.getJsonBody(request);
		Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
		RequiredFields.validate("ACCOUNT", data);
		Account account = ApiHelper.getPojoFromRequest(data, Account.class);
		Long createdBranchId = apiHandler.createAccount(account, userId);
		new Activity()
    		.setAction("CREATE")
    		.setTablename("account")
    		.setUserId(userId)
    		.setDetails(ApiHelper.dataToString(data))
    		.setActionTime(System.currentTimeMillis())
    		.execute();
		responseMap.put("accountNumber", createdBranchId);
		Writer.responseMapWriter(response, 
        		HttpServletResponse.SC_OK, 
        		HttpServletResponse.SC_OK, 
        		"Accounts created successfully", 
        		responseMap);
		} catch (Exception e) {
			ErrorHandler.handleException(e, response);
		}
	}
	
	public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");
            Function<String, Object> parseLong = Long::parseLong;
            StringBuilder jsonBody = Parser.getJsonBody(request);
            Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
            Long key = (Long) parseLong.apply((String) data.remove("accountNumber"));
            Map<String, Object> accountData = apiHandler.get(key, Account.class);
            
            // Block the employee from editing the details of the account in another branch
            if(role.equals("EMPLOYEE")) {
            	Long accountBranchId = Converter.convertToLong(accountData.get("branchId"));
            	if(accountBranchId != branchId) {
            		throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "Operation failed. Employee belongs to a different branch.");
            	}
            }
            if(accountData.get("status").equals("INACTIVE"))
    		{
    			throw new CustomException(HttpServletResponse.SC_FORBIDDEN, "This account can't be updated because it is an inactive/deleted account.");
    		}
            EditableFields.validateEditableFields(Account.class, data);
            apiHandler.updateAccount(data, userId, key);

            new Activity()
            		.setAction("UPDATE")
            		.setTablename("account")
            		.setUserId(userId)
            		.setDetails(ApiHelper.dataToString(data))
            		.setActionTime(System.currentTimeMillis())
            		.execute();;

            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Account updated successfully", 
            		responseMap);
            return;
        } catch (Exception e) {
        	ErrorHandler.handleException(e, response);
        }
	}
}