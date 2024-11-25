package com.netbanking.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

public class ApiHelper {
	private static final Gson gson = new Gson();

    public static <T> T getPojoFromRequest(StringBuilder jsonBuilder, Class<T> pojoClass) throws IOException {
        // Parse the JSON into a Map
        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(jsonBuilder.toString(), mapType);

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

            try {
                // Find the setter method
                Method setter = findSetterMethod(pojoClass, setterName);
                if (setter != null) {
                    Class<?> paramType = setter.getParameterTypes()[0];
                    // Convert the value to the expected type
                    Object convertedValue = convertValue(value, paramType);
                    setter.invoke(pojo, convertedValue);
                }
            } catch (Exception e) {
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

    private static Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
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

        // Add additional conversions if needed
        throw new IllegalArgumentException("Unsupported type conversion: " + targetType.getName());
    }


}
