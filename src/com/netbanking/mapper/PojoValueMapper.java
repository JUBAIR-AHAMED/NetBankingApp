package com.netbanking.mapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import com.netbanking.model.Model;

public class PojoValueMapper<T> implements GenericMapper<T> {

    @Override
    public Map<String, Object> getMap(T entity) throws Exception {
        Map<String, Object> valueMap = new HashMap<>();

        Class<?> clazz = entity.getClass();

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = null;

                try {
                    value = field.get(entity);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new Exception(e.toString());
                }

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
}
