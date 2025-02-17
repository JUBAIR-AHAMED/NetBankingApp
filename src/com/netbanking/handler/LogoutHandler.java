package com.netbanking.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import com.netbanking.activityLogger.AsyncLoggerUtil;
import com.netbanking.util.ErrorHandler;
import com.netbanking.util.Redis;
import com.netbanking.util.UserDetailsLocal;
import com.netbanking.util.Writer;

public class LogoutHandler {
    public static void handlePut(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	Map<String, Object> responseMap = new HashMap<>();
    	UserDetailsLocal store = UserDetailsLocal.get();
    	Long userId = store.getUserId();
		Redis.delete(userId);
		Writer.responseMapWriter(response, 
        		HttpServletResponse.SC_OK, 
        		HttpServletResponse.SC_OK, 
        		"Logout successful.", 
        		responseMap);
    	AsyncLoggerUtil.log(LoginHandler.class, Level.INFO, "User "+userId+" logged out.");
    }
}
