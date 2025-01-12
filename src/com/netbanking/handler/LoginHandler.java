package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.netbanking.api.ApiHandler;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.TokenHelper;
import com.netbanking.util.Writer;

public class LoginHandler {
    public static void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
        try {
            ApiHandler apiHandler = new ApiHandler();
            Map<String, Object> userDetails = apiHandler.loginHandler(request);
            if (userDetails.isEmpty()) {
                throw new Exception();
            } else {
                String jwt = TokenHelper.generateJwt(userDetails);
                responseMap.put("token", jwt);
                Writer.responseMapWriter(response, 
                		HttpServletResponse.SC_OK, 
                		HttpServletResponse.SC_OK, 
                		"Login successful.", 
                		responseMap);
                return;
            }
        } catch (Exception e) {
            ErrorHandler.handleException(e, response);
        }
    }
}
