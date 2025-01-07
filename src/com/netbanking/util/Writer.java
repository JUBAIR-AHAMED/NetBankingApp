package com.netbanking.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

public class Writer {
	public static void writeResponse(HttpServletResponse httpResponse, Map<String, Object> responseMap) throws IOException {
		httpResponse.setContentType("application/json");
		httpResponse.setCharacterEncoding("UTF-8");
        String jsonResponse = Parser.getJsonResponse(responseMap);
        PrintWriter out = httpResponse.getWriter();
        out.write(jsonResponse);
        out.flush();
    }
	
	public static void responseWriter(HttpServletResponse response, boolean status, int code, String message, Map<String, Object> responseMap) {
		response.setStatus(code);
        responseMap.put("status", status);
        responseMap.put("message", message);
	}
	
	public static void responseMapWriter(HttpServletResponse httpResponse, int status, int code, String message, Map<String, Object> responseMap) throws IOException {
		httpResponse.setContentType("application/json");
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setStatus(status);
        responseMap.put("status", code);
        responseMap.put("message", message);
        String jsonResponse = Parser.getJsonResponse(responseMap);
        PrintWriter out = httpResponse.getWriter();
        out.write(jsonResponse);
        out.flush();
	}
	
	public static void setResponse(HttpServletResponse httpResponse, int statusCode, String message) throws IOException {
        httpResponse.setStatus(statusCode);
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        PrintWriter out = httpResponse.getWriter();
        out.write("{\"status\":" + statusCode + ", \"message\":\"" + message + "\"}");
        out.flush();
    }
    
    public static void setResponse(HttpServletResponse httpResponse, int serverStatus, int actionStatus, String message) throws IOException {
    	httpResponse.setStatus(serverStatus);
    	httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        PrintWriter out = httpResponse.getWriter();
        out.write("{\"status\":"+actionStatus+",\"message\":\""+message+"\"}");
        out.flush();
    }
}
