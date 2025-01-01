package com.netbanking.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletHelper {
	public static void responseWriter(HttpServletResponse response, boolean status, int code, String message, Map<String, Object> responseMap) {
		response.setStatus(code);
        responseMap.put("status", status);
        responseMap.put("message", message);
	}
	
	public static Map<String, Object> getQueryParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap(); // Get all parameters as Map<String, String[]>
        Map<String, Object> queryParams = new HashMap<>();

        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                queryParams.put(key, values[0]);
            }
        }
        return queryParams;
    }
	
	public static <T> void convertValue(String param, Map<String, Object> filters, Function<String, T> parser) {
        String paramValue = (String) filters.getOrDefault(param, null);
        if(paramValue!=null) {
        	filters.put(param, parser.apply(paramValue));
        }
    }
	
	public static void handleException(HttpServletResponse response, Exception e, Map<String, Object> responseMap) throws IOException {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ServletHelper.responseWriter(response, false, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred.", responseMap);
    }
	
	public static Long convertToLong(Object value) {
		if(value==null) {
			return null;
		}
		
		if (value instanceof Integer) {
			return ((Integer) value).longValue();
		} else if (value instanceof Long) {
			return (Long) value;
		}
		
		return null;
	}
}
