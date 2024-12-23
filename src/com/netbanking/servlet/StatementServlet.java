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
import com.netbanking.exception.CustomException;
import com.netbanking.util.Parser;

public class StatementServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            responseMap.put("statement", statement);
            Parser.writeResponse(response, responseMap);
            return;
		} catch(CustomException e) {			
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			responseMap.put("status", false);
			responseMap.put("message", e.getMessage());
			Parser.writeResponse(response, responseMap);
		} catch(Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			responseMap.put("status", false);
			Parser.writeResponse(response, responseMap);	
		}
	}
}
