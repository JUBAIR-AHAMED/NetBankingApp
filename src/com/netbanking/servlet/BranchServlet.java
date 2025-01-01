package com.netbanking.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.object.Branch;
import com.netbanking.object.User;
import com.netbanking.util.Parser;
import com.netbanking.util.ServletHelper;

public class BranchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            ServletHelper.handleException(response, e, responseMap);
    	}
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();

            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");

            // Role-based logic
            Map<String, Object> filters = null;
            if ("CUSTOMER".equals(role)) {
                if (request.getParameter("accountNumber") != null || request.getParameter("userId") != null || request.getParameter("branchId") != null) {
                    ServletHelper.responseWriter(response, false, HttpServletResponse.SC_BAD_REQUEST, "No parameters allowed for CUSTOMER role.", responseMap);
                    return;
                }
            } else if ("EMPLOYEE".equals(role) || "MANAGER".equals(role)) {
            	filters = ServletHelper.getQueryParams(request);
            	ServletHelper.convertValue("branchId", filters, Long::parseLong);
            	ServletHelper.convertValue("employeeId", filters, Long::parseLong);
            	ServletHelper.convertValue("count", filters, Boolean::parseBoolean);
            	ServletHelper.convertValue("limit", filters, Integer::parseInt);
            	ServletHelper.convertValue("currentPage", filters, Integer::parseInt);
            }
            Integer limit = filters.containsKey("limit") ? Integer.valueOf(filters.remove("limit").toString()) : null;
            Integer currentPage = filters.containsKey("currentPage") ? Integer.valueOf(filters.remove("currentPage").toString()) : null;
            List<Map<String, Object>> accounts = apiHandler.filteredGetBranch(userId, role, branchId, filters, limit, currentPage);
            ServletHelper.responseWriter(response, true, HttpServletResponse.SC_OK, "Branchs fetched successfully", responseMap);
            
            Long count = accounts.size()==0? null : ServletHelper.convertToLong(accounts.get(0).getOrDefault("count", null)) ;
            
            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("branch", accounts);
            }
            Parser.writeResponse(response, responseMap);
        } catch (Exception e) {
            ServletHelper.handleException(response, e, responseMap);
        }
    }
}
