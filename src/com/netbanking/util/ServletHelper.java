package com.netbanking.util;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public class ServletHelper {
	public static void responseWriter(HttpServletResponse response, boolean status, int code, String message, Map<String, Object> responseMap) {
		response.setStatus(code);
        responseMap.put("status", status);
        responseMap.put("message", message);
	}
}
