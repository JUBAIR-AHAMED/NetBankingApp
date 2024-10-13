package com.netbanking.mapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReflectionMapper<T> implements GenericMapper<T> {

	@Override
	public Map<String, Object> toMap(T entity, ResultSet rs) {
	    Map<String, Object> fieldMap = new HashMap<>();
	    
	    // Get the class of the entity
	    Class<?> clazz = entity.getClass();
	    
	    // Iterate through all fields in the class
	    Field[] fields = clazz.getDeclaredFields();
//	    YamlMapper ymap = new YamlMapper();
	    ResultSetMetaData metaData=null;
	    Boolean rsState = true;
	    
		try {
			metaData = rs.getMetaData();
			if(!rs.next())
			{
				rsState = false;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
	    try {
	        for (int i = 1; i <= metaData.getColumnCount(); i++) {
	            String columnName = metaData.getColumnName(i);
	            String tableName = clazz.getSimpleName().toLowerCase();
	            String dbColName = YamlMapper.getFieldName(tableName, columnName);
	            
	            // Try to match with class fields
	            for (Field field : fields) {
	                field.setAccessible(true); // Allow access to private fields
	                
	                // Use a case-insensitive match for column names
	                if (field.getName().equals(dbColName)) {
	                    // Get the value of the field from the entity
	                    Object value = field.get(entity);
	                    
	                    // Check if the field is an Enum and convert to String if necessary
	                    if (value != null) {
	                    	if(field.getType().isEnum())
	                    	{
	                    		value = ((Enum<?>) value).name(); // Convert enum to its name
	                    	}
	                    	if(!rsState||!value.equals(rs.getObject(columnName)))
	                    	{
	                    		fieldMap.put(columnName, value);
	                    	}
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace(); // Handle exceptions as needed
	    }
	    
	    return fieldMap;
	}

    
    public Long getId(T entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id"); // Adjust field name as needed
            idField.setAccessible(true);
            return (Long) idField.get(entity); // Cast to Long or adjust based on your ID type
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions as needed
            return null;
        }
    }
}
