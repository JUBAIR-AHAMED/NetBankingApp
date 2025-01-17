package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.netbanking.functions.UserFunctions;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class ProfileHandler {
    public static void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            UserDetailsLocal store = UserDetailsLocal.get();
            Long userId = store.getUserId();
            Map<String, Object> profile = new UserFunctions().get(userId);
            responseMap.put("profile", profile);
            Writer.responseMapWriter(response, 
            		HttpServletResponse.SC_OK, 
            		HttpServletResponse.SC_OK, 
            		"Profile fetched successfully.", 
            		responseMap);
            return;
        } catch (Exception e) {
        	ErrorHandler.handleException(e, response);
        }
    }
}
