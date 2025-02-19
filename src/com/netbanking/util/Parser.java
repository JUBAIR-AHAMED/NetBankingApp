package com.netbanking.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.netbanking.exception.CustomException;

import javax.servlet.http.HttpServletRequest;

public class Parser {
	public static final Gson gson = new Gson();
	
	public static JsonObject getJsonObject(HttpServletRequest request) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		String line;
		try(BufferedReader reader = request.getReader()){			
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		}
        String jsonString = sb.toString();
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);        
        return jsonObject;
	}
	
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
	
	public static StringBuilder getJsonBody(HttpServletRequest request) {
    	StringBuilder jsonBody = new StringBuilder();
		String line;
		try(BufferedReader reader = request.getReader())
		{
			while((line = reader.readLine()) != null)
			{
				jsonBody.append(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return jsonBody;
    }
	
	public static String getJsonResponse(Object toConvert) {
		return gson.toJson(toConvert);
	}
   
    @SuppressWarnings("unchecked")
    public static <T> T getValue(JsonObject jsonObject, String key, Class<T> type, String fieldName, boolean required) throws CustomException {
        if (jsonObject != null && jsonObject.has(key)) {
            JsonElement element = jsonObject.get(key);

            if (required && (element.isJsonNull() || (element.isJsonPrimitive() && element.getAsString().isEmpty()))) {
                throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, fieldName + " is required and cannot be null or empty.");
            }

            if (element.isJsonNull()) {
                return null;
            }

            try {
                if (element.isJsonPrimitive() && element.getAsString().isEmpty()) {
                    return null; // Treat empty string as null for all types
                }

                if (type == String.class) {
                    return (T) element.getAsString();
                } else if (type == Integer.class) {
                    String value = element.getAsString();
                    if (!value.matches("\\d+")) {
                        throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, fieldName + " can contain only numbers.");
                    }
                    return (T) Integer.valueOf(element.getAsInt());
                } else if (type == Long.class) {
                    String value = element.getAsString();
                    if (!value.matches("\\d+")) {
                        throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, fieldName + " can contain only numbers.");
                    }
                    return (T) Long.valueOf(element.getAsLong());
                } else if (type == Float.class) {
                    String value = element.getAsString();
                    if (!value.matches("[+-]?\\d*\\.?\\d+")) {
                        throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, fieldName + " can contain only numeric values.");
                    }
                    return (T) Float.valueOf(element.getAsFloat());
                } else if (type == Double.class) {
                    String value = element.getAsString();
                    if (!value.matches("[+-]?\\d*\\.?\\d+")) {
                        throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, fieldName + " can contain only numeric values.");
                    }
                    return (T) Double.valueOf(element.getAsDouble());
                } else if (type == Boolean.class) {
                    String value = element.getAsString();
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                        throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, fieldName + " can contain only boolean values (true/false).");
                    }
                    return (T) Boolean.valueOf(element.getAsBoolean());
                } else if (Set.class.isAssignableFrom(type)) {
                    // Handle Set<String> type
                    JsonArray jsonArray = element.getAsJsonArray();
                    Set<String> set = new HashSet<>();

                    for (JsonElement jsonElement : jsonArray) {
                        set.add(jsonElement.getAsString()); 
                    }

                    return (T) set;
                } else {
                    throw new IllegalArgumentException("Unsupported type: " + type.getName());
                }
            } catch (ClassCastException | IllegalStateException e) {
                throw new IllegalArgumentException("Error casting value for key '" + key + "' to type " + type.getName(), e);
            }
        } else if(required) {
        	throw new CustomException(HttpServletResponse.SC_BAD_REQUEST, fieldName + " is required and cannot be null or empty.");
        }
        return null; 
    }



    public static void storeIfPresent(JsonObject jsonObject, Map<String, Object> filters, String key, Class<?> clazz, String fieldName, Boolean required) throws CustomException {
    	Optional.ofNullable(Parser.getValue(jsonObject, key, clazz, fieldName, required))
    	.ifPresent(value -> filters.put(key, value));
    }
}
