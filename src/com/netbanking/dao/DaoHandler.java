package com.netbanking.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.daoObject.Condition;
import com.netbanking.daoObject.Where;
import com.netbanking.exception.CustomException;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;

public class DaoHandler<T extends Model>{
	public Long insertHandler(T object) throws Exception {
		if(object==null) {
			throw new Exception("Object is null.");
		}
		DaoImpl<T> dao = new DaoImpl<T>();		
		Class<?> clazz = object.getClass();
		Class<?> superClass = clazz.getSuperclass();
		
		//Getting Table Names
		List<String> objectNames = new ArrayList<String>();
		objectNames.add(clazz.getSimpleName());
		while(superClass!=null && !superClass.getSimpleName().equals("Object")) {
			objectNames.add(0, superClass.getSimpleName());
			superClass = superClass.getSuperclass();
		}
		Map<String, Object> pojoValuesMap = null;
		try {
			pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		Long refrenceKey = null;
		for(String objectName : objectNames) {
			String tableName = YamlMapper.getTableName(objectName);
			Map<String, Object> objectData = YamlMapper.getObjectData(objectName);
			@SuppressWarnings("unchecked")
			Map<String, Object> objectFieldData = (Map<String, Object>) objectData.get("table_field_name");
			Map<String, Object> insertValues = new HashMap<String, Object>();
			String autoinc = null;
			if(objectData.containsKey("autoincrement_field"))
			{
				autoinc = (String) objectData.get("autoincrement_field");
			}
			for(Map.Entry<String, Object> tempMap : objectFieldData.entrySet())
			{
				String key = tempMap.getKey();
				String keyNameInTable  = (String)objectFieldData.get(key);
				if(key.equals(autoinc))
				{
					continue;
				} 
				if(objectData.containsKey("refrenceingKey") && objectData.get("refrenceingKey").equals(key)) {
					insertValues.put(keyNameInTable, refrenceKey);
					continue;
				} 
				Object value = pojoValuesMap.get(key);
				insertValues.put(keyNameInTable, value);
			}
			try {
				Long tempRef = dao.insert(tableName, insertValues);
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

	public void updateHandler(QueryRequest request) throws Exception {
		DaoImpl<T> dao = new DaoImpl<T>();
        List<String> whereConditions = request.getWhereConditions();
        List<String> updateField = request.getUpdateField();
        List<Condition> updatesType = request.getUpdatesType();
		List<Join> joins = request.getJoinConditions();
		List<Where> whereConditionsType = request.getWhereConditionsType();
		String tableName = request.getTableName();	    		
        
        if(joins!=null && !joins.isEmpty()) {
			for(Join join:joins) {
				int joinConditionLength = join.getLeftTable().size();
				List<String> leftTable=join.getLeftTable(), leftColumn=join.getLeftColumn(), rightTable=join.getRightTable(), rightColumn=join.getRightColumn();
				for(int i=0;i<joinConditionLength;i++) {
					String leftTableName = leftTable.get(i), leftColumnName = leftColumn.remove(i), rightTableName = rightTable.get(i), rightColumnName = rightColumn.remove(i);
					leftColumn.add(i, convertField(leftTableName, leftColumnName));
					rightColumn.add(i, convertField(rightTableName, rightColumnName));
				}
			}
		}        
	    
	    if(updateField!=null && !updateField.isEmpty()) {
	    	convertFields(tableName, updateField);
	    } else if(updatesType!=null && !updatesType.isEmpty()) {
	    	for(Condition updates:updatesType) {
	    		String table = updates.getTable();
	    		String field = updates.getField();
	    		field = convertField(table, field);
	    		request.putUpdateField(field);
	    	}
	    } else {
	    	return;
	    }

	    List<Object> currWhereConditionsValues = new ArrayList<>();
	    if(whereConditions!=null && !whereConditions.isEmpty()) {
	    	convertFields(tableName, whereConditions);
	    } else if(whereConditionsType!=null) {
			whereConditions = new ArrayList<String>();
			for(Where entity: whereConditionsType) {
				String whereTableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				field = convertField(whereTableName, field);
				StringBuilder sb = new StringBuilder(whereTableName).append(".").append(field);
				whereConditions.add(sb.toString());
				currWhereConditionsValues.add(value);
			}
			request.setWhereConditions(whereConditions);
			request.setWhereConditionsValues(currWhereConditionsValues);
		}
		request.setWhereConditions(whereConditions);

	    dao.update(request);
	}

	public void updateHandler(T object, QueryRequest request) throws Exception {
		Map<String, Object> pojoValuesMap = null;
		try {
			pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DaoImpl<T> dao = new DaoImpl<T>();
        List<String> whereConditions = request.getWhereConditions();
		List<String> updateField = new ArrayList<String>(pojoValuesMap.keySet());
        List<Object> updateValue = new ArrayList<Object>(pojoValuesMap.values());
        request.setUpdateValue(updateValue);
		List<Where> whereConditionsType = request.getWhereConditionsType();
		String tableName = request.getTableName();	    		
	    
	    if(updateField==null || updateField.isEmpty()) {
	    	return;
	    }
	    
	    convertFields(tableName, updateField);

	    List<Object> currWhereConditionsValues = new ArrayList<>();
	    if(whereConditions!=null && !whereConditions.isEmpty()) {
	    	convertFields(tableName, whereConditions);
	    } else if(whereConditionsType!=null) {
			whereConditions = new ArrayList<String>();
			for(Where entity: whereConditionsType) {
				String whereTableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				field = convertField(whereTableName, field);
				StringBuilder sb = new StringBuilder(whereTableName).append(".").append(field);
				whereConditions.add(sb.toString());
				currWhereConditionsValues.add(value);
			}
			request.setWhereConditions(whereConditions);
			request.setWhereConditionsValues(currWhereConditionsValues);
		}
		request.setWhereConditions(whereConditions);
		request.setUpdateField(updateField);
		request.setUpdateValue(updateValue);
	    dao.update(request);
	}

	
	public void updateHandler(List<QueryRequest> requests) throws Exception {
		DaoImpl<T> dao = new DaoImpl<T>();
		for(QueryRequest request:requests) {
			List<String> whereConditions = request.getWhereConditions();
			List<String> updateField = request.getUpdateField();
			List<Condition> updatesType = request.getUpdatesType();
			List<Join> joins = request.getJoinConditions();
			List<Where> whereConditionsType = request.getWhereConditionsType();
			String tableName = request.getTableName();	    		
			
			if(joins!=null && !joins.isEmpty()) {
				for(Join join:joins) {
					int joinConditionLength = join.getLeftTable().size();
					List<String> leftTable=join.getLeftTable(), leftColumn=join.getLeftColumn(), rightTable=join.getRightTable(), rightColumn=join.getRightColumn();
					for(int i=0;i<joinConditionLength;i++) {
						String leftTableName = leftTable.get(i), leftColumnName = leftColumn.remove(i), rightTableName = rightTable.get(i), rightColumnName = rightColumn.remove(i);
						leftColumn.add(i, convertField(leftTableName, leftColumnName));
						rightColumn.add(i, convertField(rightTableName, rightColumnName));
					}
				}
			}        
			
			if(updateField!=null && !updateField.isEmpty()) {
				convertFields(tableName, updateField);
			} else if(updatesType!=null && !updatesType.isEmpty()) {
				for(Condition updates:updatesType) {
					String table = updates.getTable();
					String field = updates.getField();
					field = convertField(table, field);
					request.putUpdateField(field);
				}
			} else {
				return;
			}
			
			List<Object> currWhereConditionsValues = new ArrayList<>();
			if(whereConditions!=null && !whereConditions.isEmpty()) {
				convertFields(tableName, whereConditions);
			} else if(whereConditionsType!=null) {
				whereConditions = new ArrayList<String>();
				for(Where entity: whereConditionsType) {
					String whereTableName = entity.getTable();
					String field = entity.getField();
					Object value = entity.getValue();
					field = convertField(whereTableName, field);
					StringBuilder sb = new StringBuilder(whereTableName).append(".").append(field);
					whereConditions.add(sb.toString());
					currWhereConditionsValues.add(value);
				}
				request.setWhereConditions(whereConditions);
				request.setWhereConditionsValues(currWhereConditionsValues);
			}
			request.setWhereConditions(whereConditions);
		}
		dao.updateMany(requests);
	}
	
	public List<Map<String, Object>> selectHandler(QueryRequest request) throws Exception {
		DaoImpl<T> dao = new DaoImpl<T>();
		List<Object> currWhereConditionsValues = new ArrayList<>();
		List<String> whereConditions=request.getWhereConditions();
		List<Where> whereConditionsType = request.getWhereConditionsType();
		List<Join> joins = request.getJoinConditions();
		List<Condition> selects = request.getSelects();
		
		if(selects!=null && !selects.isEmpty()) {
			for(Condition select:selects) {
				String table = select.getTable();
				String field = select.getField();
				field = convertField(table, field);
				request.putSelectColumns(field);
			}
		}
		
		if(joins!=null&&!joins.isEmpty()) {
			for(Join join:joins) {
				int joinConditionLength = join.getLeftTable().size();
				List<String> leftTable=join.getLeftTable(), leftColumn=join.getLeftColumn(), rightTable=join.getRightTable(), rightColumn=join.getRightColumn();
				for(int i=0;i<joinConditionLength;i++) {
					String leftTableName = leftTable.get(i), leftColumnName = leftColumn.remove(i), rightTableName = rightTable.get(i), rightColumnName = rightColumn.remove(i);
					leftColumn.add(i, convertField(leftTableName, leftColumnName));
					rightColumn.add(i, convertField(rightTableName, rightColumnName));
				}
			}
		}
		
		if(whereConditions!=null) {
			convertFields(request.getTableName(), whereConditions);
		} else if(whereConditionsType!=null) {
			whereConditions = new ArrayList<String>();
			for(Where entity: whereConditionsType) {
				String tableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				field = convertField(tableName, field);
				StringBuilder sb = new StringBuilder(tableName).append(".").append(field);
				whereConditions.add(sb.toString());
				currWhereConditionsValues.add(value);
			}
			request.setWhereConditions(whereConditions);
			request.setWhereConditionsValues(currWhereConditionsValues);
		}
		
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
	
	public T selectHandle(QueryRequest request, Class<?> clazz) throws Exception {
		DaoImpl<T> dao = new DaoImpl<T>();
		List<Object> currWhereConditionsValues = new ArrayList<>();
		List<String> whereConditions=request.getWhereConditions();
		List<Where> whereConditionsType = request.getWhereConditionsType();
		List<Condition> selects = request.getSelects();
		
		if(selects!=null && !selects.isEmpty()) {
			for(Condition select:selects) {
				String table = select.getTable();
				String field = select.getField();
				field = convertField(table, field);
				request.putSelectColumns(field);
			}
		}
				
		if(whereConditions!=null) {
			convertFields(request.getTableName(), whereConditions);
		} else if(whereConditionsType!=null) {
			whereConditions = new ArrayList<String>();
			for(Where entity: whereConditionsType) {
				String tableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				field = convertField(tableName, field);
				StringBuilder sb = new StringBuilder(tableName).append(".").append(field);
				whereConditions.add(sb.toString());
				currWhereConditionsValues.add(value);
			}
			request.setWhereConditions(whereConditions);
			request.setWhereConditionsValues(currWhereConditionsValues);
		}
		
		try {
			return dao.selectOneGetObject(request, clazz);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Failed to fetch the data.");
		}
	}
	
	public void convertFields(String tableName, List<String> fields) {
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		for(int i=0;i<fields.size();i++) {
			String fieldName = fields.remove(i);
	        if (fieldToColumnMap.containsKey(fieldName)) {
	        	if (fieldToColumnMap.containsKey(fieldName)) {
		            fields.add(i, fieldToColumnMap.get(fieldName));
		        }
	        }
		}
	}
	
	public String convertField(String tableName, String field) throws Exception {
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		if(fieldToColumnMap==null) {
			throw new Exception("Table name is invalid");
		}
		String newField = fieldToColumnMap.get(field);
		if(newField==null) {
			throw new Exception("Field name is invalid.");
		}
		return newField;
	}
}