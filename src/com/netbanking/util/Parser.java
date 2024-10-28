package com.netbanking.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Parser {
	public static final Gson gson = new Gson();
	public static JsonObject getJsonObject(BufferedReader reader) throws IOException
	{
		StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonString = sb.toString();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        
        return jsonObject;
	}
	
	public static String getJsonResponse(Object toConvert) {
		return gson.toJson(toConvert);
	}
	
    public static void writeResponse(HttpServletResponse response, Map<String, Object> responseMap) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = Parser.getJsonResponse(responseMap);
        response.getWriter().write(jsonResponse);
    }
}
