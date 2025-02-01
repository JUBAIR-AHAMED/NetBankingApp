package com.netbanking.dao;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryBuilder;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.util.DBConnectionPool;

public class DataAccessObject<T extends Model> implements Dao<T> {
	//Insert operation
	public Long insert(T object) throws Exception {
		if(object==null) {
			throw new Exception("Object is null.");
		}		
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
		pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		System.out.println(pojoValuesMap);
				
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
			Long tempRef = insertHandler(tableName, insertValues);
			if(refrenceKey==null)
			{
				refrenceKey =  tempRef;
			}
		}
		return refrenceKey;
	}
	
	private Long insertHandler(String tableName, Map<String, Object> insertValues) throws SQLException {
		QueryBuilder qb = new QueryBuilder();
	    qb.insert(tableName, insertValues.keySet());
	    String sqlQuery = qb.finish();
	    Long generatedKeysList = null;
	    try (Connection connection = DBConnectionPool.getConnection();
    	    PreparedStatement stmt = connection.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
	    	
    	    setValuesInPstm(stmt, insertValues.values(), 1);
    	    System.out.println(stmt);
    	    stmt.executeUpdate();

    	    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedKeysList = generatedKeys.getLong(1);
                }
            }
    	}
		return generatedKeysList;
	}
	
	public void update(T object) throws Exception {
		Map<String, Object> pojoValuesMap = new PojoValueMapper<T>().getMapExcludingParent(object);
		Class<?> clazz = object.getClass();
		GetMetadata metadata = GetMetadata.fromClass(clazz);
		String tableName = metadata.getTableName();
		String key = metadata.getPrimaryKeyColumn();
		String getterName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
		Method getKeyValue = null;
		for (Method method : clazz.getMethods()) {
            if (method.getName().equals(getterName)) {
                getKeyValue = method;
            }
        }
		QueryRequest request = new QueryRequest();
		request.setTableName(tableName)
				.putWhereConditions(key)
				.putWhereOperators("=")
				.putWhereConditionsValues(getKeyValue.invoke(object, (Object[]) null));
		List<String> updateFields = new ArrayList<String>(pojoValuesMap.keySet());
		List<Object> updateValues = new ArrayList<Object>(pojoValuesMap.values());
	    
	    if(updateFields==null || updateFields.isEmpty()) {
	    	return;
	    }
	    convertFields(tableName, updateFields);
	    
	    QueryBuilder qb = new QueryBuilder();
	    qb.update(request.getTableName())
		    .set(updateFields)
	    	.where(request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators());
        
    	try (Connection connection = DBConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	count = setValuesInPstm(stmt, updateValues, count);
    		setValuesInPstm(stmt, request.getWhereConditionsValues(), count);
        	System.out.println(stmt);
        	stmt.executeUpdate();
        }
    }

    //Select operation
    public List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception {
    	String tableName = request.getTableName();
		List<Join> joins = request.getJoinConditions();
		List<String> selectList = request.getSelects();
		
    	QueryBuilder qb = new QueryBuilder();
    	if(request.getCount()||request.getSelectAllColumns()) {
        	qb.select(request.getCount());
        } else {
        	qb.select(selectList);
        }
        
    	qb.from(tableName).join(joins)
    		.where(request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators())
        	.order(request.getOrderByColumns(), request.getOrderDirections())
            .limit(request.getLimit()).offset(request.getOffset());
        
        Map<String, String> tableField = new HashMap<String, String>(YamlMapper.getFieldToColumnMapByTableName(tableName));
        if(joins!=null) {
        	for(Join join:joins)
        	{
        		tableField.putAll(YamlMapper.getFieldToColumnMapByTableName(join.getTableName()));
        	}
        }
        
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection connection = DBConnectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	setValuesInPstm(stmt, request.getWhereConditionsValues(), count);
        	System.out.println(stmt);
        	ResultSet rs = stmt.executeQuery();
        	// Preparing map for the purpose of returning the selected values from the database
        	if(request.getCount()) {
        		if(rs.next()) {
        			Map<String, Object> map = new HashMap<>();
        			map.put("count", rs.getObject("count"));
        			list.add(map);
        			return list;
        		}
        	}
            while(rs.next())
            {
            	Map<String, Object> map = new HashMap<>();
            	if(request.getSelectColumns()!=null) {
            		for(String columnName : request.getSelectColumns())
            		{
						String fieldName = tableField.get(columnName);
            			map.put(columnName, rs.getObject(fieldName));
            		}
            	}
            	else {
            	    for(Map.Entry<String, String> entry:tableField.entrySet()) {
            	    	String columnName = entry.getValue();
            	    	map.put(entry.getKey(), rs.getObject(columnName));
            	    }
            	}
            	list.add(map);
            }
            return list;
        }
    }

    private void convertFields(String tableName, List<String> fields) throws Exception {
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		if(fieldToColumnMap==null) {
			throw new Exception("Table name is invalid");
		}
		for(int i=0;i<fields.size();i++) {
			String fieldName = fields.remove(i);
	        if (fieldToColumnMap.containsKey(fieldName)) {
	            fields.add(i, fieldToColumnMap.get(fieldName));
	        }
		}
	}
    
    public static int setValuesInPstm(PreparedStatement pstm, Collection<Object> values, int count) throws SQLException {
		if(values==null || values.isEmpty()) {
			return count;
		}
		for(Object value:values) {
			pstm.setObject(count, value);
			count++;
		}
		return count;
	}
}