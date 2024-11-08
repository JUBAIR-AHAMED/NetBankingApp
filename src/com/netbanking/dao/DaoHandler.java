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
import com.netbanking.object.WhereCondition;

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
			Map<String, Object> tableData = YamlMapper.getTableMap(subTable);
			@SuppressWarnings("unchecked")
			Map<String, Object> tableFieldData= (Map<String, Object>) tableData.get("fields");
			Map<String, Object> insertValues = new HashMap<String, Object>();
			String autoinc = null;
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
				if(refrenceKey==null)
				{
					refrenceKey =  insert(subTable, insertValues);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("Failed Inserting");
			}
		}
		return refrenceKey;
	}
	
	public void deleteHandler(String tableName, String status, String whereField, String whereFieldValue) throws Exception {
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
			throw new Exception("Failed deleting.");
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
	            whereConditions.remove(i);
	            i--;
	            index++;
	    	}
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

	public void updateHandler(QueryRequest request, Class<?> clazz) throws SQLException {
        List<String> whereConditions = request.getWhereConditions();
        List<Object> whereConditionsValues = request.getWhereConditionsValues();
        List<String> whereOperators = request.getWhereOperators();
        List<String> whereLogicalOperators = request.getWhereLogicalOperators();
        List<String> updateField = request.getUpdateField();
		List<Object> updateValue = request.getUpdateValue();
        int updatesLength = updateField.size();
        //handling update values
	    Class<?> superClass = clazz.getSuperclass();
	    String objectName = clazz.getSimpleName();
	    String tableName = YamlMapper.getTableName(objectName);

	    if (superClass != null && !superClass.getSimpleName().equals("Object")) {
	        try {
	            updateHandler(request, superClass);
	        } catch (ClassCastException e) {
	            throw new SQLException("Failed to cast object to its superclass", e);
	        }
	    }

	    Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMap(objectName);

	    Map<String, Object> updatesMap = new HashMap<>();
	    for(int i=0;i<updatesLength;i++)
        {
	    	String fieldName = updateField.get(i);
	        Object fieldValue = updateValue.get(i);
	        if (fieldToColumnMap.containsKey(fieldName)) {
	        	updatesMap.put(fieldToColumnMap.get(fieldName), fieldValue);
	        }
        }
//	    for (Map.Entry<String, Object> entry : updates.entrySet()) {
//	        String fieldName = entry.getKey();
//	        Object fieldValue = entry.getValue();
//
//	        if (fieldToColumnMap.containsKey(fieldName)) {
//	            updatesMap.put(fieldToColumnMap.get(fieldName), fieldValue);
//	        }
//	    }

	    if (updatesMap.isEmpty()) return;

	    List<String> currWhereConditions = new ArrayList<>();
	    List<String> currWhereOperators = new ArrayList<>();
	    List<String> currWhereLogicalOperators = new ArrayList<>();
	    List<Object> currWhereValues = new ArrayList<>();
	    int index = 0;
	    for(int i=0; i<whereConditions.size();i++)
	    {
	    	System.out.println("+++"+whereOperators);
	    	String tempWhereCondition = whereConditions.get(i);
	    	if (fieldToColumnMap.containsKey(tempWhereCondition)) {
	    		if (index > 0) {
	                currWhereLogicalOperators.add(whereLogicalOperators.remove(0));
	            }
	    		String tempField =fieldToColumnMap.get(tempWhereCondition);
	    		currWhereConditions.add(tempField);
	    		currWhereOperators.add(whereOperators.remove(0));
	            currWhereValues.add(whereConditionsValues.remove(0));
	            whereConditions.remove(i);
	            i--;
	            index++;
	    	}
	    }
	    QueryRequest sendRequest = new QueryRequest();
	    sendRequest.setTableName(tableName);
	    sendRequest.setWhereConditions(currWhereConditions);
	    sendRequest.setUpdates(updatesMap);
	    sendRequest.setWhereConditionsValues(currWhereValues);
	    sendRequest.setWhereOperators(currWhereOperators);
	    sendRequest.setWhereLogicalOperators(currWhereLogicalOperators);

	    update(sendRequest);
	}
	
	public List<Map<String, Object>> selectHandler(QueryRequest request) throws CustomException {
		List<String> currWhereConditions = new ArrayList<>();
		List<Object> currWhereConditionsValues = new ArrayList<>();
		List<String> whereConditions=request.getWhereConditions();
		List<WhereCondition> wheConditionsType = request.getWhereConditionsType();
		
		if(whereConditions!=null) {
			Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(request.getTableName());
			for(String fieldName : whereConditions) {
				currWhereConditions.add(fieldToColumnMap.get(fieldName));
			}
		} else if(wheConditionsType!=null) {
			Map<String, Map<String, String>> fieldToColumnMap = new HashMap<>();
			for(WhereCondition entity: wheConditionsType) {
				String tableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				
				if(!fieldToColumnMap.containsKey(tableName))
				{
					fieldToColumnMap.put(tableName, YamlMapper.getFieldToColumnMapByTableName(tableName));
				}
				String newFieldValue = fieldToColumnMap.get(tableName).get(field);
				StringBuilder sb = new StringBuilder(tableName);
				sb.append(".").append(newFieldValue);
				currWhereConditions.add(sb.toString());
				currWhereConditionsValues.add(value);
			}
			request.setWhereConditionsValues(currWhereConditionsValues);
		}
		request.setWhereConditions(currWhereConditions);
		
		try {
			return select(request);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data.");
		}
	}
}