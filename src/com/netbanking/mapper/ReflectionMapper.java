package com.netbanking.mapper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netbanking.model.Model;
import com.netbanking.util.DBConnection;
import com.netbanking.util.TableHelper;
import com.netbanking.util.TableMetaData;

public class ReflectionMapper<T extends Model> implements GenericMapper<T> {
	@Override
	public Map<String, Object> toMap(T entity) {
		Map<String, Object> fieldMap = new HashMap<>();
		
		try(Connection connection = DBConnection.getConnection())
		{			
			String tableName = TableHelper.getTableName(entity);
			try(Statement metaStmt = connection.createStatement();
				ResultSet rs = metaStmt.executeQuery("SELECT * FROM " + tableName + " WHERE "+ entity.getId() + " = "+ entity.getIdField()))
			{				
				
				
				// Get the class of the entity
				Class<?> clazz = entity.getClass();
				
				// Iterate through all fields in the class
				Field[] fields = clazz.getDeclaredFields();
				List<String> metaData = TableMetaData.getMetaData(entity);
				System.out.println(metaData);
				Boolean rsState = true;
				
				try {
					if(!rs.next())
					{
						rsState = false;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				int metaSize = metaData.size();
				try {
					for (int i = 0; i < metaSize; i++) {
						String columnName = metaData.get(i);
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
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	    
	    return fieldMap;
	}
}
