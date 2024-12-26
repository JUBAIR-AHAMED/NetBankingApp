package com.netbanking.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.netbanking.model.Model;

public class PojoValueMapper<T extends Model> {
    public Map<String, Object> getMap(T entity) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();

        Class<?> clazz = entity.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
            	String getMethodString = "get"+capitalizeFirstLetter(field.getName());
            	Method getMethod = clazz.getDeclaredMethod(getMethodString);
            	Object value = getMethod.invoke(entity);
                if (value != null) {
                    if (field.getType().isEnum()) {
                        value = ((Enum<?>) value).name();
                    }
                    valueMap.put(field.getName(), value);
                }
            }

            clazz = clazz.getSuperclass();
        }

        return valueMap;
    }
    
    public Map<String, Object> getMapExcludingParent(T entity) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();
        
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
        	String getMethodString = "get"+capitalizeFirstLetter(field.getName());
        	Method getMethod = clazz.getDeclaredMethod(getMethodString);
        	Object value = getMethod.invoke(entity);
            if (value != null) {
                if (field.getType().isEnum()) {
                    value = ((Enum<?>) value).name();
                }
                valueMap.put(field.getName(), value);
            }
        }

        return valueMap;
    }
    
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the original string if it's null or empty
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
