package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.exception.CustomException;
import com.netbanking.object.User;
import com.netbanking.util.ApiHelper;
import com.netbanking.util.Parser;
import com.netbanking.util.Writer;

public class ProfileHandler {
    public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
            StringBuilder jsonBody = Parser.getJsonBody(request);
            Map<String, Object> data = ApiHelper.getMapFromRequest(jsonBody);
    		User user = ApiHelper.getPojoFromRequest(data, User.class);
            apiHandler.updateUser(user, userId, userId);
//            apiHandler.updateCustomer(jsonBody, userId);
            
            response.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("status", true);
            responseMap.put("message", "Updated successfully");
        } catch (CustomException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", false);
            responseMap.put("message", "Update failed due to a server error.");
            e.printStackTrace(); // Log the stack trace for debugging
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", false);
            responseMap.put("message", "An unexpected error occurred.");
            e.printStackTrace(); // Log unexpected exceptions
        }

        Writer.writeResponse(response, responseMap);
    }

    public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Long userId = (Long) request.getAttribute("userId");
//			String role = (String) request.getAttribute("role");
            
            Map<String, Object> profile = apiHandler.get(userId, User.class);

            response.setStatus(HttpServletResponse.SC_OK);
            responseMap.put("status", true);
            responseMap.put("message", "Profile fetched successfully");
            responseMap.put("profile", profile);
        } catch (CustomException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", false);
            responseMap.put("message", "Failed to fetch profile due to a server error.");
            e.printStackTrace(); // Log the stack trace for debugging
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            responseMap.put("status", false);
            responseMap.put("message", "An unexpected error occurred.");
            e.printStackTrace(); // Log unexpected exceptions
        }

        Writer.writeResponse(response, responseMap);
    }
}
