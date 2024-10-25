package com.netbanking.util;

import java.io.BufferedReader;
import java.io.IOException;

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
}
