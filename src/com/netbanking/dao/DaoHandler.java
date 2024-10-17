package com.netbanking.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.netbanking.mapper.PojoValueMapper;
import com.netbanking.mapper.YamlMapper;
import com.netbanking.model.Model;

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
	
}