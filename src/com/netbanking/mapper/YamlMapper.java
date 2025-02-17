package com.netbanking.mapper;

import java.io.InputStream;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.yaml.snakeyaml.Yaml;

import com.netbanking.activityLogger.AsyncLoggerUtil;
import com.netbanking.util.Validator;

public class YamlMapper {
	private static Map<String, Object> map;
	
	private YamlMapper()
	{
		Yaml yaml = new Yaml();
		try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mapping.yml"))
		{
			if(Validator.isNull(inputStream))
			{
				throw new Exception("Failed loading map file.");
			}
			map  = yaml.load(inputStream);
		}
		catch(Exception e)
		{
			AsyncLoggerUtil.log(YamlMapper.class, Level.ERROR, e.getMessage());
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getObjectData(String objectName) {
		if (Validator.isNull(map)) {
			new YamlMapper();
		}
		return (Map<String, Object>) ((Map<String, Object>) map.get("pojo")).get(objectName);
	}
	
	public static String getTableName(String objectName) {
        return (String) getObjectData(objectName).get("tablename");  // Return null if no mapping is found
    }
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getFieldToColumnMap(String objectName) {		
		return (Map<String, String>) getObjectData(objectName).get("tablePojoNameRelation");
	}
	
	// Method to get the 'tablePojoNameRelation' map for a given tablename
	@SuppressWarnings("unchecked")
	public static Map<String, String> getFieldToColumnMapByTableName(String tablename) {
		if (Validator.isNull(map)) {
            new YamlMapper();
        }
		Map<String, Object> pojo =(Map<String, Object>) map.get("pojo");
	    for (Map.Entry<String, Object> entry : pojo.entrySet()) {
			Map<String, Object> tableData = (Map<String, Object>) entry.getValue();  // This will be the map for the object
	        
	        if (tableData.containsKey("tablename") && tableData.get("tablename").equals(tablename)) {
	            return (Map<String, String>) tableData.get("tablePojoNameRelation");
	        }
	    }
	    return null;
	}
}