package com.netbanking.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.netbanking.exception.CustomException;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.object.QueryRequest;

public class DaoHandler<T extends Model> implements Dao<T>{
	public void insertHandler(T object) {
		String objectName = object.getClass().getSimpleName();
		List<String> tableNames = YamlMapper.getRelatedTableNames(objectName);
		Map<String, Object> pojoValuesMap = null;
		
		try {
			pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		Long refrenceKey = null;
		for(String subTable : tableNames) {
			Map<String, Object> tableData= YamlMapper.getTableField(subTable);
			@SuppressWarnings("unchecked")
			Map<String, Object> tableFieldData= (Map<String, Object>) tableData.get("fields");
			Map<String, Object> insertValues = new HashMap<String, Object>();
			String autoinc = null;
			System.out.println(tableFieldData);
			if(tableData.containsKey("autoincrement_field"))
			{
				autoinc = (String) tableData.get("autoincrement_field");
			}
			
			for(Map.Entry<String, Object> tempMap : tableFieldData.entrySet())
			{
				String key = tempMap.getKey();
				if(key.equals(autoinc))
				{
					continue;
				} 
				if(tableData.containsKey("refrenceingKey") && tableData.get("refrenceingKey").equals(key)) {
					System.out.println("no ref zoho");
					insertValues.put(key, refrenceKey);
					continue;
				} 
				@SuppressWarnings("unchecked")
				String tempStr = (String) ((Map<String, String>) tempMap.getValue()).get("pojoname");
				Object value = pojoValuesMap.get(tempStr);
				insertValues.put(key, value);
			}	
			try {
				refrenceKey =  insert(subTable, insertValues);
                System.out.println("returned key is "+ refrenceKey);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void deleteHandler(String tableName, Map<String, Object> conditions) {
		try {
			delete(tableName, conditions);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateHandler(T object, 
            Map<String, Object> updates, 
            Map<String, Object> whereConditions,
            List<String> whereOperators,
            List<String> whereLogicalOperators) throws SQLException {
		// Get the object's name to map it to a table in the YAML
		String objectName = object.getClass().getSimpleName();
		
		// Get the list of related tables for the given object from YAML
		List<String> tableNames = YamlMapper.getRelatedTableNames(objectName);
		
		// Map to hold the POJO field values
		Map<String, Object> pojoValuesMap = null;
		
		try {
		// Map the object fields to their respective values
		pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		// Iterate over each related table
		for (String subTable : tableNames) {
		// Get table-specific field mapping and details from YAML
		Map<String, Object> tableData = YamlMapper.getTableField(subTable);
		@SuppressWarnings("unchecked")
		Map<String, Object> tableFieldData = (Map<String, Object>) tableData.get("fields");
		Map<String, Object> updateValues = new HashMap<>();
		
		// Extract the autoincrement field, if any
//		String autoinc = null;
//		if (tableData.containsKey("autoincrement_field")) {
//		autoinc = (String) tableData.get("autoincrement_field");
//		}
		
		// Iterate through the update fields provided and collect the relevant ones for this table
		Iterator<Map.Entry<String, Object>> iterator = updates.entrySet().iterator();
		while (iterator.hasNext()) {
		Map.Entry<String, Object> entry = iterator.next();
		String updateField = entry.getKey();
		Object updateValue = entry.getValue();
		
		// If the field is in this table, add it to the updateValues map
		if (tableFieldData.containsKey(updateField)) {
		  updateValues.put(updateField, updateValue);
		  iterator.remove(); // Remove the field from the updates map since it's processed
		}
		}
		
		// If there are no update values for this table, skip to the next one
		if (updateValues.isEmpty()) {
		continue;
		}
		
		// Create a QueryRequest for updating
		QueryRequest request = new QueryRequest();
		request.setTableName(subTable); // Set the table to update
		request.setUpdates(updateValues); // Set the values to be updated
		
		// Determine the number of conditions to use (equals the number of fields being updated)
		int conditionCount = updateValues.size();
		
		// Extract the first n conditions for this update
		Map<String, Object> subWhereConditions = new HashMap<>();
		Iterator<String> conditionKeys = whereConditions.keySet().iterator();
		for (int i = 0; i < conditionCount && conditionKeys.hasNext(); i++) {
		String key = conditionKeys.next();
		subWhereConditions.put(key, whereConditions.get(key));
		conditionKeys.remove(); // Remove the used condition from the original map
		}
		
		// Extract the corresponding first n operators and logical operators
		List<String> subWhereOperators = new ArrayList<>();
		List<String> subWhereLogicalOperators = new ArrayList<>();
		for (int i = 0; i < conditionCount && !whereOperators.isEmpty(); i++) {
		subWhereOperators.add(whereOperators.remove(0));
		}
		for (int i = 0; i < conditionCount - 1 && !whereLogicalOperators.isEmpty(); i++) {
		subWhereLogicalOperators.add(whereLogicalOperators.remove(0));
		}
		
		// Set the conditions, operators, and logical operators to the request
		request.setWhereConditions(subWhereConditions);
		request.setWhereOperators(subWhereOperators);
		request.setWhereLogicalOperators(subWhereLogicalOperators);
		
		// Perform the update using the DAO's default update method
		update(request);
		}
	}


	
	public List<Map<String, Object>> selectHandler(QueryRequest request) throws CustomException {
		try {
			return select(request);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data. "+ e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data. "+ e.toString());
		}

	}
}