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
	
	public void updateHandler(T object, 
			Class<?> clazz,
            List<String> updates, 
            List<String> whereConditions,
            List<Object> whereConditionsValues,
            List<String> whereOperators,
            List<String> whereLogicalOperators) throws SQLException {

		Class<?> superClass = clazz.getSuperclass();
		String objectName = clazz.getSimpleName();
		String tableName = YamlMapper.getTableName(objectName);

		if (superClass != null && !superClass.getSimpleName().equals("Object")) {
	        try {
	            updateHandler(object, superClass, updates, whereConditions, whereConditionsValues, whereOperators, whereLogicalOperators);
	        } catch (ClassCastException e) {
	            throw new SQLException("Failed to cast object to its superclass", e);
	        }
		}
	    Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMap(objectName);
		
		Map<String, Object> pojoValuesMap = null;
		
		try {
		pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		Map<String, Object> updatesMap = new HashMap<>();
		for(String updateField : updates)
		{
			if(fieldToColumnMap.containsKey(updateField))
			{
				updatesMap.put(fieldToColumnMap.get(updateField), pojoValuesMap.get(updateField));
			}
		}
		if(updatesMap.isEmpty()) return;
		List<String> currWhereConditions = new ArrayList<String>(), currWhereOperators = new ArrayList<String>(), currWhereLogicalOperators = new ArrayList<String>();
		List<Object> currWhereValues = new ArrayList<Object>();
		int index=0;
		for(String whereCondition : whereConditions)
		{
			if(fieldToColumnMap.containsValue(whereCondition))
			{
				if(index>0)
				{
					currWhereLogicalOperators.add(whereLogicalOperators.remove(0));
				}
				currWhereConditions.add(whereConditions.remove(0));
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