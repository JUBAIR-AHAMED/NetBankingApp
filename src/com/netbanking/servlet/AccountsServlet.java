package com.netbanking.servlet;

import java.io.IOException;
import java.util.ArrayList;
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
import com.netbanking.object.Account;
import com.netbanking.util.Parser;
import com.netbanking.util.ServletHelper;
	
public class AccountsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();

            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");
            Map<String, Object> filters = null;
            if ("CUSTOMER".equals(role)) {
                if (request.getParameter("accountNumber") != null || request.getParameter("userId") != null || request.getParameter("branchId") != null) {
                    ServletHelper.responseWriter(response, false, HttpServletResponse.SC_BAD_REQUEST, "No parameters allowed for CUSTOMER role.", responseMap);
                    return;
                }
            } else if ("EMPLOYEE".equals(role) || "MANAGER".equals(role)) {
            	filters = ServletHelper.getQueryParams(request);
            	ServletHelper.convertValue("accountNumber", filters, Long::parseLong);
            	ServletHelper.convertValue("userId", filters, Long::parseLong);
            	ServletHelper.convertValue("branchId", filters, Long::parseLong);
            	ServletHelper.convertValue("count", filters, Boolean::parseBoolean);
            	ServletHelper.convertValue("limit", filters, Integer::parseInt);
            	ServletHelper.convertValue("currentPage", filters, Integer::parseInt);
            }
            Integer limit=null, currentPage=null;
            System.out.println(role);
            if(!role.equals("CUSTOMER")) {
            	limit = filters.containsKey("limit") ? Integer.valueOf(filters.remove("limit").toString()) : null;
            	currentPage = filters.containsKey("currentPage") ? Integer.valueOf(filters.remove("currentPage").toString()) : null;
            }
            List<Map<String, Object>> accounts = apiHandler.filteredGetAccount(userId, role, branchId, filters, limit, currentPage);
            ServletHelper.responseWriter(response, true, HttpServletResponse.SC_OK, "Accounts fetched successfully", responseMap);
            Long count = null;

            if (accounts.size() >= 1) {
                Object countValue = accounts.get(0).getOrDefault("count", null);
                if (countValue instanceof Integer) {
                    count = ((Integer) countValue).longValue();
                } else if (countValue instanceof Long) {
                    count = (Long) countValue;
                }
            }

            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("accounts", accounts);
            }
            Parser.writeResponse(response, responseMap);
        } catch (Exception e) {
        	ServletHelper.handleException(response, e, responseMap);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Map<String, Object> responseMap = new HashMap<>();
    	try {
    		ApiHandler apiHandler = new ApiHandler();

            Long userId = (Long) request.getAttribute("userId");

			try {
				Long createdBranchId = apiHandler.createAccount(request, userId);	
				response.setStatus(HttpServletResponse.SC_OK);
				responseMap.put("status", true);
                responseMap.put("message", "Action completed successfully.");
                responseMap.put("accountNumber", createdBranchId);
			} catch(CustomException e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				responseMap.put("status", false);
                responseMap.put("message", e.getMessage());
			}
    	} catch(Exception e) {
    		e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMap.put("status", false);
            responseMap.put("message", "Action failed.");
    	}
    	Parser.writeResponse(response, responseMap);
    }
}
