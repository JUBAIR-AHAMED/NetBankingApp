package com.netbanking.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.ServletHelper;

public class UserServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();

            Long userId = (Long) request.getAttribute("userId");
            String role = (String) request.getAttribute("role");
            Long branchId = (Long) request.getAttribute("branchId");
            Map<String, Object> filters = null;
        	filters = ServletHelper.getQueryParams(request);
        	ServletHelper.convertValue("userId", filters, Long::parseLong);
        	// Name & email are already a string
        	// ServletHelper.convertValue("name", filters, );
        	// ServletHelper.convertValue("email", filters, );
        	// ServletHelper.convertValue("userType", filters, );
        	 ServletHelper.convertValue("moreDetails", filters, Boolean::parseBoolean);
        	ServletHelper.convertValue("count", filters, Boolean::parseBoolean);
        	ServletHelper.convertValue("limit", filters, Integer::parseInt);
        	ServletHelper.convertValue("currentPage", filters, Integer::parseInt);
        	if(!filters.containsKey("userType")||!filters.containsKey("moreDetails")) {
        		ServletHelper.responseWriter(response,
        				false, HttpServletResponse.SC_BAD_REQUEST,
        				"User Type and/or More details not found.", 
        				responseMap);
        		Parser.writeResponse(response, responseMap);
        		return;
        	}
            Integer limit = filters.containsKey("limit") ? Integer.valueOf(filters.remove("limit").toString()) : null;
            Integer currentPage = filters.containsKey("currentPage") ? Integer.valueOf(filters.remove("currentPage").toString()) : null;
            List<Map<String, Object>> accounts = apiHandler.filteredGetCustomerOrEmployee(userId, role, branchId, filters, limit, currentPage);
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
            	responseMap.put("users", accounts);
            }
            Parser.writeResponse(response, responseMap);
        } catch (Exception e) {
        	ServletHelper.handleException(response, e, responseMap);
        }
    }
}
