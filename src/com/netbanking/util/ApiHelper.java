package com.netbanking.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApiHelper {
	private static final Gson gson = new Gson();

    public static <T> T getPojoFromRequest(StringBuilder jsonBuilder, Class<T> pojoClass) throws IOException {
        // Parse the JSON into a Map
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(jsonBuilder.toString(), mapType);
        System.out.println("the data "+data);
        if(data==null) {
        	return null;
        }
        // Create an instance of the POJO
        T pojo;
        try {
            pojo = pojoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + pojoClass.getName(), e);
        }
        // Use reflection to set values via setters
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String setterName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            boolean valuePresence = false;
            try {
                // Find the setter method
                Method setter = findSetterMethod(pojoClass, setterName);
                if (setter != null) {
                    Class<?> paramType = setter.getParameterTypes()[0];
                    // Convert the value to the expected type
                    Object convertedValue = convertValue(value, paramType);
                    setter.invoke(pojo, convertedValue);
                    valuePresence = true;
                }
                if(!valuePresence) {
                	return null;
                }
                
            } catch (Exception e) {
            	e.printStackTrace();
                System.out.println("Failed to set value for " + key + ": " + e.getMessage());
            }
        }

        return pojo;
    }

    private static Method findSetterMethod(Class<?> pojoClass, String setterName) {
        for (Method method : pojoClass.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }
        
        if (targetType == java.sql.Date.class) {
            if (value instanceof String) {
                String dateString = (String) value;
                try {
                    // Convert String to java.sql.Date
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date utilDate = formatter.parse(dateString); // Parse into util.Date first
                    return new java.sql.Date(utilDate.getTime()); // Convert to sql.Date
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid date format: " + value, e);
                }
            } else if (value instanceof java.util.Date) {
                return new java.sql.Date(((java.util.Date) value).getTime());
            } else {
                throw new IllegalArgumentException("Cannot convert value to java.sql.Date: " + value);
            }
        }

        if(targetType.isEnum()) {
        	if (value instanceof String) {
                try {
                    return Enum.valueOf((Class<? extends Enum>) targetType, (String) value);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid enum value: " + value + " for type " + targetType.getName(), e);
                }
            } else {
                throw new IllegalArgumentException("Cannot convert non-string value to enum: " + targetType.getName());
            }
        }

        // Handle primitive types and their wrappers
        try {
            if (targetType == long.class || targetType == Long.class) {
                return value instanceof String ? Long.parseLong((String) value) : ((Number) value).longValue();
            } else if (targetType == int.class || targetType == Integer.class) {
                return value instanceof String ? Integer.parseInt((String) value) : ((Number) value).intValue();
            } else if (targetType == double.class || targetType == Double.class) {
                return value instanceof String ? Double.parseDouble((String) value) : ((Number) value).doubleValue();
            } else if (targetType == float.class || targetType == Float.class) {
                return value instanceof String ? Float.parseFloat((String) value) : ((Number) value).floatValue();
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return value instanceof String ? Boolean.parseBoolean((String) value) : value;
            } else if (targetType == String.class) {
                return value.toString();
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Failed to convert value: " + value + " to type " + targetType.getName(), e);
        }

        throw new IllegalArgumentException("Unsupported type conversion: " + targetType.getName());
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
}
