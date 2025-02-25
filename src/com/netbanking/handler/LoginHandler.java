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
import com.netbanking.util.Parser;
import com.netbanking.util.PasswordUtility;
import com.netbanking.util.Redis;
import com.netbanking.util.TokenHelper;
import com.netbanking.util.Writer;

public class LoginHandler {
    public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException, Exception {
    	Map<String, Object> responseMap = new HashMap<>();
        JsonObject jsonObject = Parser.getJsonObject(request);
        Long userId = Parser.getValue(jsonObject, "userId", Long.class, "User Id", true);
		String password = Parser.getValue(jsonObject, "password", String.class, "Password", true);
		Map<String, Object> userDetails = UserFunctions.getInstance().get(userId);
		if(userDetails != null && !userDetails.isEmpty()) {
			Boolean check=PasswordUtility.verifyPassword(password, (String) userDetails.get("password"));
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
			userDetails.putAll(EmployeeFunctions.getInstance().get(userId));
		}
		else if(role.equals(Role.CUSTOMER))
		{
			userDetails.putAll(CustomerFunctions.getInstance().get(userId));
		}
		
        String jwt = TokenHelper.generateJwt(userDetails);
        Redis.setex(userId.toString(), jwt);
        responseMap.put("token", jwt);
        
        new Activity()
        .setAction("LOGIN")
        .setRecordname(Role.CUSTOMER.equals(role)? role.name():Role.EMPLOYEE.name())
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
    }
}
