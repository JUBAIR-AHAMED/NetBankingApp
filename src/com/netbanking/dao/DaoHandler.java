package com.netbanking.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.buf.UDecoder;

import com.netbanking.exception.CustomException;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.object.QueryRequest;
import com.netbanking.object.User;

public class DaoHandler<T> implements Dao<T>{
	public <T extends Model> Long insertHandler(T object) throws Exception {
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
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("Failed Inserting");
			}
		}
		return refrenceKey;
	}
	
	public void deleteHandler(String tableName, String status, String whereField, String whereFieldValue) {
		try {
			QueryRequest request = new QueryRequest();
			request.setTableName(tableName);
			Map<String, Object> updates = new HashMap<String, Object>();
			updates.put("status", status);
			
			List<String> whereCondition = new ArrayList<String>(), whereConditionOperator = new ArrayList<String>();
			List<Object> whereConditionValue = new ArrayList<Object>();
			whereCondition.add(whereField);
			whereConditionValue.add(whereFieldValue);
			whereConditionOperator.add("=");	
			
			request.setUpdates(updates);
			request.setWhereConditions(whereCondition);
			request.setWhereConditionsValues(whereConditionValue);
			request.setWhereOperators(whereConditionOperator);
			update(request);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateHandler(
	        Map<String, Object> updates, 
	        Class<?> clazz,
	        List<String> whereConditions,
	        List<Object> whereConditionsValues,
	        List<String> whereOperators,
	        List<String> whereLogicalOperators) throws SQLException {

	    Class<?> superClass = clazz.getSuperclass();
	    String objectName = clazz.getSimpleName();
	    String tableName = YamlMapper.getTableName(objectName);

	    if (superClass != null && !superClass.getSimpleName().equals("Object")) {
	        try {
	            updateHandler(updates, superClass, whereConditions, whereConditionsValues, whereOperators, whereLogicalOperators);
	        } catch (ClassCastException e) {
	            throw new SQLException("Failed to cast object to its superclass", e);
	        }
	    }

	    Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMap(objectName);

	    Map<String, Object> updatesMap = new HashMap<>();
	    for (Map.Entry<String, Object> entry : updates.entrySet()) {
	        String fieldName = entry.getKey();
	        Object fieldValue = entry.getValue();

	        if (fieldToColumnMap.containsKey(fieldName)) {
	            updatesMap.put(fieldToColumnMap.get(fieldName), fieldValue);
	        }
	    }

	    if (updatesMap.isEmpty()) return;

	    List<String> currWhereConditions = new ArrayList<>();
	    List<String> currWhereOperators = new ArrayList<>();
	    List<String> currWhereLogicalOperators = new ArrayList<>();
	    List<Object> currWhereValues = new ArrayList<>();
	    int index = 0;
	    
	    for(int i=0; i<whereConditions.size();i++)
	    {
	    	String tempWhereCondition = whereConditions.get(i);
	    	if (fieldToColumnMap.containsKey(tempWhereCondition)) {
	    		if (index > 0) {
	                currWhereLogicalOperators.add(whereLogicalOperators.remove(0));
	            }
	    		String tempField =fieldToColumnMap.get(tempWhereCondition);
	    		currWhereConditions.add(tempField);
	    		currWhereOperators.add(whereOperators.remove(0));
	            currWhereValues.add(whereConditionsValues.remove(0));
	    	}
	    	index++;
	    }

	    QueryRequest request = new QueryRequest();
	    request.setTableName(tableName);
	    request.setWhereConditions(currWhereConditions);
	    request.setUpdates(updatesMap);
	    request.setWhereConditionsValues(currWhereValues);
	    request.setWhereOperators(currWhereOperators);
	    request.setWhereLogicalOperators(currWhereLogicalOperators);

	    update(request);
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