package com.netbanking.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netbanking.exception.CustomException;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.object.QueryRequest;
import com.netbanking.object.Where;

public class DaoHandler<T extends Model>{
	public Long insertHandler(T object) throws Exception {
		if(object==null) {
			throw new Exception("Object is null.");
		}
		DaoImpl<T> dao = new DaoImpl<T>();		
		Class<?> clazz = object.getClass();
		Class<?> superClass = clazz.getClass().getSuperclass();
		
		//Getting Table Names
		List<String> tableNames = new ArrayList<String>();
		tableNames.add(YamlMapper.getTableName(clazz.getSimpleName()));
		while(superClass!=null && !superClass.getSimpleName().equals("Object")) {
			tableNames.add(0, YamlMapper.getTableName(superClass.getSimpleName()));
			superClass = superClass.getSuperclass();
		}

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
				Long tempRef = dao.insert(subTable, insertValues);
				if(refrenceKey==null)
				{
					refrenceKey =  tempRef;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("Failed Inserting");
			}
		}
		return refrenceKey;
	}

	public void updateHandler(QueryRequest request, Class<?> clazz) throws SQLException {
		DaoImpl<T> dao = new DaoImpl<T>();
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
	    Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(YamlMapper.getTableName(objectName));

	    List<String> updateFields = new ArrayList<String>();
	    List<Object> updateValues = new ArrayList<Object>();
	    for(int i=0;i<updatesLength;i++)
        {
	    	String fieldName = updateField.get(i);
	        Object fieldValue = updateValue.get(i);
	        if (fieldToColumnMap.containsKey(fieldName)) {
	        	if (fieldToColumnMap.containsKey(fieldName)) {
		            updateFields.add(fieldToColumnMap.get(fieldName));
		            updateValues.add(fieldValue);
		        }
	        }
        }
	    if (updateField.isEmpty()) return;

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
	    QueryRequest sendRequest = new QueryRequest();
	    sendRequest.setTableName(tableName);
	    sendRequest.setWhereConditions(currWhereConditions);
	    sendRequest.setUpdateField(updateFields);
	    sendRequest.setUpdateValue(updateValues);
	    sendRequest.setWhereConditionsValues(currWhereValues);
	    sendRequest.setWhereOperators(currWhereOperators);
	    sendRequest.setWhereLogicalOperators(currWhereLogicalOperators);

	    dao.update(sendRequest);
	}
	
	public List<Map<String, Object>> selectHandler(QueryRequest request) throws CustomException {
		DaoImpl<T> dao = new DaoImpl<T>();
		List<Object> currWhereConditionsValues = new ArrayList<>();
		List<String> whereConditions=request.getWhereConditions();
		List<Where> whereConditionsType = request.getWhereConditionsType();
		
		if(whereConditions!=null) {
			convertFields(request.getTableName(), whereConditions);
		} else if(whereConditionsType!=null) {
			whereConditions = new ArrayList<String>();
			Map<String, Map<String, String>> fieldToColumnMap = new HashMap<>();
			for(Where entity: whereConditionsType) {
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
				whereConditions.add(sb.toString());
				currWhereConditionsValues.add(value);
			}
			request.setWhereConditionsValues(currWhereConditionsValues);
		}
		request.setWhereConditions(whereConditions);
		
		try {
			return dao.select(request);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data.");
		}
	}
	
	public List<String> convertFields(String tableName, List<String> fields) {
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		for(int i=0;i<fields.size();i++) {
			String fieldName = fields.remove(i);
	        if (fieldToColumnMap.containsKey(fieldName)) {
	        	if (fieldToColumnMap.containsKey(fieldName)) {
		            fields.add(i, fieldToColumnMap.get(fieldName));
		        }
	        }
		}
		return fields;
	}
}