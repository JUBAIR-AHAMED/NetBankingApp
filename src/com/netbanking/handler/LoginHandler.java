package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.netbanking.exception.CustomException;
import com.netbanking.functions.CustomerFunctions;
import com.netbanking.functions.EmployeeFunctions;
import com.netbanking.functions.UserFunctions;
import com.netbanking.object.Activity;
import com.netbanking.util.Converter;
import com.netbanking.util.Encryption;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.TokenHelper;
import com.netbanking.util.Writer;

public class LoginHandler {
    public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            JsonObject jsonObject = Parser.getJsonObject(request);
            Long userId = Parser.getValue(jsonObject, "userId", Long.class, "User Id", true);
			String password = Parser.getValue(jsonObject, "password", String.class, "Password", true);
			Map<String, Object> userDetails = new UserFunctions().get(userId);
			if(userDetails != null && !userDetails.isEmpty()) {
				Boolean check=Encryption.verifyPassword(password, (String) userDetails.get("password"));
				if(!check) {
					throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid password.");
				}
			} else {
				throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "Invalid customer id.");
			}
			String role = (String) userDetails.get("role");
			Long user_id;
			if(userDetails.get("userId").getClass().getSimpleName().equals("Integer")) {
				user_id = ((Integer) userDetails.get("userId")).longValue();
			} else {
				user_id = (Long) userDetails.get("userId");
			}
			if(role.equals("EMPLOYEE")||role.equals("MANAGER")) {
				userDetails.putAll(new EmployeeFunctions().get(user_id));
			}
			else if(role.equals("CUSTOMER"))
			{
				userDetails.putAll(new CustomerFunctions().get(user_id));
			}
        	new Activity()
        		.setAction("LOGIN")
        		.setTablename("customer")
        		.setUserId(Converter.convertToLong(userDetails.get("userId")))
        		.setDetails("Logged in")
        		.setActionTime(System.currentTimeMillis())
        		.execute();
            String jwt = TokenHelper.generateJwt(userDetails);
            responseMap.put("token", jwt);
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Login successful.", 
            		responseMap);
            return;
        } catch (Exception e) {
            ErrorHandler.handleException(e, response);
        }
    }
}
