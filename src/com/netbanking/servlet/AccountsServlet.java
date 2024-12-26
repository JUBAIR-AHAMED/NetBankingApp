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

            // Role-based logic
            List<String> filterFields = new ArrayList<>();
            List<Object> filterValues = new ArrayList<>();

            if ("CUSTOMER".equals(role)) {
                if (request.getParameter("accountNumber") != null || request.getParameter("userId") != null || request.getParameter("branchId") != null) {
                    ServletHelper.responseWriter(response, false, HttpServletResponse.SC_BAD_REQUEST, "No parameters allowed for CUSTOMER role.", responseMap);
                    return;
                }
            } else if ("EMPLOYEE".equals(role) || "MANAGER".equals(role)) {
                handleEmployeeOrManagerFilters(request, filterFields, filterValues);
            }
            Function<String, Integer> parser = Integer::parseInt;
            String limitParam = request.getParameter("limit");
            Integer limit = limitParam!=null? parser.apply(request.getParameter("limit")):null;
            String currentPageString = request.getParameter("currentPage");
            Integer currentPage = currentPageString!=null && !currentPageString.isEmpty()? parser.apply(currentPageString):null;
            List<Map<String, Object>> accounts = apiHandler.getUserAccounts(userId, role, branchId, filterFields, filterValues, limit, currentPage);
            ServletHelper.responseWriter(response, true, HttpServletResponse.SC_OK, "Accounts fetched successfully", responseMap);
            Long count = (Long) accounts.get(0).getOrDefault("count", null);
            if(count!=null) {
            	responseMap.put("count", count);
            } else {
            	responseMap.put("accounts", accounts);
            }
            Parser.writeResponse(response, responseMap);
        } catch (Exception e) {
            handleException(response, e, responseMap);
        }
    }

    private void handleEmployeeOrManagerFilters(HttpServletRequest request, List<String> filterFields, List<Object> filterValues) {
        addFilterIfPresent(request, "accountNumber", filterFields, filterValues, Long::parseLong);
        addFilterIfPresent(request, "userId", filterFields, filterValues, Long::parseLong);
        addFilterIfPresent(request, "branchId", filterFields, filterValues, Long::parseLong);
        addFilterIfPresent(request, "count", filterFields, filterValues, Boolean::parseBoolean);
    }

    private <T> void addFilterIfPresent(HttpServletRequest request, String param, List<String> fields, List<Object> values, Function<String, T> parser) {
        String paramValue = request.getParameter(param);
        if (paramValue != null && !paramValue.isEmpty()) {
            fields.add(param);
            values.add(parser.apply(paramValue));
        }
    }

    private void handleException(HttpServletResponse response, Exception e, Map<String, Object> responseMap) throws IOException {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ServletHelper.responseWriter(response, false, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.", responseMap);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("came");
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
