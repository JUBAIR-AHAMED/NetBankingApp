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

import org.apache.logging.log4j.Level;

import com.netbanking.activityLogger.AsyncLoggerUtil;
import com.netbanking.daoObject.Join;
import com.netbanking.daoObject.QueryBuilder;
import com.netbanking.daoObject.QueryRequest;
import com.netbanking.enumHelper.GetMetadata;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;
import com.netbanking.util.DBConnectionPool;
import com.netbanking.util.Validator;

public class DataAccessObject<T extends Model> implements Dao<T> {
	//Insert operation
	public Long insert(T object) throws Exception {
		if(Validator.isNull(object)) {
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
		Map<String, Object> pojoValuesMap = new PojoValueMapper<T>().getMap(object);
		Long refrenceKey = null;
		try (Connection connection = DBConnectionPool.getConnection();){
			connection.setAutoCommit(false);
			try {
				
				for(String objectName : objectNames) {
					String tableName = YamlMapper.getTableName(objectName);
					Map<String, Object> objectData = YamlMapper.getObjectData(objectName);
					Map<String, String> objectFieldData = YamlMapper.getFieldToColumnMap(objectName);
					Map<String, Object> insertValues = new HashMap<String, Object>();
					String autoIncrementKey = null;
					if(objectData.containsKey("autoIncrementKey"))
					{
						autoIncrementKey = (String) objectData.get("autoIncrementKey");
					}
					for(Map.Entry<String, String> tempMap : objectFieldData.entrySet())
					{
						String key = tempMap.getKey();
						String keyNameInTable  = objectFieldData.get(key);
						if(key.equals(autoIncrementKey))
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
					Long tempRef = insertHandler(tableName, insertValues, connection);
					if(Validator.isNull(refrenceKey))
					{
						refrenceKey =  tempRef;
					}
				}
			} catch (Exception e) {
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
		}
		return refrenceKey;
	}

	private Long insertHandler(String tableName, Map<String, Object> insertValues, Connection connection) throws SQLException {
		QueryBuilder qb = new QueryBuilder();
	    qb.insert(tableName, insertValues.keySet());
	    String sqlQuery = qb.finish();
	    Long generatedKey = null;
	    try (PreparedStatement stmt = connection.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
    	    setValuesInPstm(stmt, insertValues.values(), 1);
    	    AsyncLoggerUtil.log(DataAccessObject.class, Level.INFO, stmt);
    	    stmt.executeUpdate();

    	    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedKey = generatedKeys.getLong(1);
                }
            }
    	}
		return generatedKey;
	}
	
	//Update operation
	public void update(T object) throws Exception {
		if(Validator.isNull(object)) {
			throw new Exception("Object is null.");
		}
		Class<?> clazz = object.getClass();
		GetMetadata metadata = GetMetadata.fromClass(clazz);
		// Getting the primary key value for update
		String tableName = metadata.getTableName();
		String key = metadata.getPrimaryKeyColumn();
		String getterName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
		Method getKeyValue = null;
		for (Method method : clazz.getMethods()) {
            if (method.getName().equals(getterName)) {
                getKeyValue = method;
            }
        }

		Map<String, Object> pojoValuesMap = new PojoValueMapper<T>().getMapExcludingParent(object);
		List<String> updateFields = new ArrayList<String>(pojoValuesMap.keySet());
		List<Object> updateValues = new ArrayList<Object>(pojoValuesMap.values());
	    
		QueryRequest request = new QueryRequest()
				.setTableName(tableName)
				.putWhereConditions(key)
				.putWhereOperators("=")
				.putWhereConditionsValues(getKeyValue.invoke(object, (Object[]) null))
				.setUpdateField(updateFields);

		if(Validator.isNull(updateFields) || updateFields.isEmpty()) {
	    	return;
	    }
	    
	    QueryBuilder qb = new QueryBuilder()
						    .update(request.getTableName())
							.set(request.getUpdateField())
							.where(request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators());
        
    	try (Connection connection = DBConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	count = setValuesInPstm(stmt, updateValues, count);
    		setValuesInPstm(stmt, request.getWhereConditionsValues(), count);
    		AsyncLoggerUtil.log(DataAccessObject.class, Level.INFO, stmt);
        	stmt.executeUpdate();
        }
    }

    //Select operation
    public List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception {
    	String tableName = request.getTableName();
		List<Join> joins = request.getJoinConditions();
		List<String> listOfSelectsType = request.getSelects();
		List<String> selectColNames = request.getSelectColumns();
    	QueryBuilder qb = new QueryBuilder();
    	if(request.getCount()||request.getSelectAllColumns()) {
    		qb.select(request.getCount());
        } else if(listOfSelectsType!=null&&!listOfSelectsType.isEmpty()) {
        	qb.select(listOfSelectsType);
        } else if(selectColNames!=null&&!selectColNames.isEmpty()) {
        	qb.select(selectColNames);
        }
        
    	qb.from(tableName).join(joins)
    		.where(request.getWhereConditions(), request.getWhereOperators(), request.getWhereLogicalOperators())
        	.order(request.getOrderByColumns(), request.getOrderDirections())
            .limit(request.getLimit()).offset(request.getOffset());
        
        Map<String, String> tableFields = new HashMap<String, String>(YamlMapper.getFieldToColumnMapByTableName(tableName));
        if(joins!=null) {
        	for(Join join:joins)
        	{
        		tableFields.putAll(YamlMapper.getFieldToColumnMapByTableName(join.getTableName()));
        	}
        }
        
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection connection = DBConnectionPool.getConnection();
            PreparedStatement stmt = connection.prepareStatement(qb.finish())) {
        	int count = 1;
        	setValuesInPstm(stmt, request.getWhereConditionsValues(), count);
        	AsyncLoggerUtil.log(DataAccessObject.class, Level.INFO, stmt);
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
        	// Traversing through rows
            while(rs.next())
            {
            	Map<String, Object> map = new HashMap<>();
            	if(selectColNames!=null&&!selectColNames.isEmpty()) {
            		for(String columnName : request.getSelectColumnsPojoVer())
            		{
            			String fieldName = tableFields.get(columnName);
            			map.put(columnName, rs.getObject(fieldName));
            		}
            	} 
            	else if(listOfSelectsType!=null&&!listOfSelectsType.isEmpty()) {
            		// Traversing through columns
            		for(String columnName : request.getSelectsPojoVer())
            		{
						String fieldName = tableFields.get(columnName);
            			map.put(columnName, rs.getObject(fieldName));
            		}
            	} 
            	else {
            		// Traversing through columns
            	    for(Map.Entry<String, String> entry:tableFields.entrySet()) {
            	    	String columnName = entry.getValue();
            	    	map.put(entry.getKey(), rs.getObject(columnName));
            	    }
            	}
            	list.add(map);
            }
            return list;
        }
    }

    public static int setValuesInPstm(PreparedStatement pstm, Collection<Object> values, int count) throws SQLException {
		if(Validator.isNull(values) || values.isEmpty()) {
			return count;
		}
		for(Object value:values) {
			pstm.setObject(count, value);
			count++;
		}
		return count;
	}
}