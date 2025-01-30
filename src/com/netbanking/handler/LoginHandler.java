package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;
import com.netbanking.enums.Role;
import com.netbanking.enums.Status;
import com.netbanking.exception.CustomException;
import com.netbanking.functions.CustomerFunctions;
import com.netbanking.functions.EmployeeFunctions;
import com.netbanking.functions.UserFunctions;
import com.netbanking.object.Activity;
import com.netbanking.util.Encryption;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Parser;
import com.netbanking.util.Redis;
import com.netbanking.util.TokenHelper;
import com.netbanking.util.UserDetailsLocal;
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
			Status status =Status.valueOf((String) userDetails.get("status"));
			if(!status.equals(Status.ACTIVE)) {
				throw new CustomException(HttpServletResponse.SC_UNAUTHORIZED, "User is "+status.toString());
			}
			Role role = Role.valueOf((String) userDetails.get("role"));
			if(role.equals(Role.EMPLOYEE)||role.equals(Role.MANAGER)) {
				userDetails.putAll(new EmployeeFunctions().get(userId));
			}
			else if(role.equals(Role.CUSTOMER))
			{
				userDetails.putAll(new CustomerFunctions().get(userId));
			}
			
            String jwt = TokenHelper.generateJwt(userDetails);
            Redis.setex(userId.toString(), jwt);
            responseMap.put("token", jwt);
            
            new Activity()
            .setAction("LOGIN")
            .setRecordname("customer")
            .setActorId(userId)
            .setSubjectId(userId)
            .setKeyValue(userId)
            .setDetails("Logged in")
            .setActionTime(System.currentTimeMillis())
            .execute();
            
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
    
    public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
    	UserDetailsLocal store = UserDetailsLocal.get();
    	Long userId = store.getUserId();
    	try {
    		Redis.delete(userId);
    		Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Logout successful.", 
            		responseMap);
    	} catch (Exception e) {
    		ErrorHandler.handleException(e, response);
		}
    }
}
