package com.netbanking.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApiHelper {
	private static final Gson gson = new Gson();
	
	public static Map<String, Object> getMapFromRequest(StringBuilder jsonBuilder) {
		Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(jsonBuilder.toString(), mapType);
        return data;
	}
	
    public static <T> T getPojoFromRequest(Map<String, Object> data, Class<T> pojoClass) throws IOException {
        if(data==null) {
        	return null;
        }
        T pojo;
        try {
            pojo = pojoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + pojoClass.getName(), e);
        }
        boolean valuePresence = false;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String setterName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            try {
                Method setter = findSetterMethod(pojoClass, setterName);
                if (setter != null) {
                    Class<?> paramType = setter.getParameterTypes()[0];
                    Object convertedValue = convertValue(value, paramType);
                    setter.invoke(pojo, convertedValue);
                    valuePresence = true;
                }
            } catch (Exception e) {
            	e.printStackTrace();
                System.out.println("Failed to set value for " + key + ": " + e.getMessage());
            }
        }

        if(!valuePresence) {
        	return null;
        }
        return pojo;
    }
    
    public static Long getCount(List<Map<String, Object>> list) {
    	Object value = list.get(0).getOrDefault("count", null);
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

    private static Method findSetterMethod(Class<?> pojoClass, String setterName) {
        for (Method method : pojoClass.getMethods()) {
            if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
    
    public static String dataToString(Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Object> entry:data.entrySet()) {
			sb.append(entry.getKey())
			.append(" : ")
			.append(entry.getValue())
			.append("; ");
		}
		return sb.toString();
	}
}
