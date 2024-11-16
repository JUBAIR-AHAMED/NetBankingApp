package com.netbanking.mapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlMapper {
	private static Map<String, Object> map;
	
	private YamlMapper()
	{
		Yaml yaml = new Yaml();
		try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("mapping.yml"))
		{
			if(inputStream == null)
			{
				throw new Exception("Failed loading map file.");
			}
			map  = yaml.load(inputStream);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getTableName(String objectName) {
        if (map == null) {
            new YamlMapper();
        }

        @SuppressWarnings("unchecked")
        String tableName = (String) ((Map<String, Object>) ((Map<String, Object>) map.get("pojo")).get(objectName)).get("tablename");

        return tableName;  // Return null if no mapping is found
    }
	
	public static Map<String, String> getFieldToColumnMap(String objectName) {
		if (map == null) {
            new YamlMapper();
        }
		
		@SuppressWarnings("unchecked")
		Map<String, String> tableFieldName =(Map<String, String>) ((Map<String, Object>) ((Map<String, Object>) map.get("pojo")).get(objectName)).get("table_field_name");

        return tableFieldName;
	}
	
	// Method to get the 'table_field_name' map for a given tablename
	@SuppressWarnings("unchecked")
	public static Map<String, String> getFieldToColumnMapByTableName(String tablename) {
		if (map == null) {
            new YamlMapper();
        }

		Map<String, Object> pojo =(Map<String, Object>) map.get("pojo");
	    for (Map.Entry<String, Object> entry : pojo.entrySet()) {
	        String objectName = entry.getKey();
			Map<String, Object> tableData = (Map<String, Object>) entry.getValue();  // This will be the map for the object
	        
	        if (tableData.containsKey("tablename") && tableData.get("tablename").equals(tablename)) {
	            return (Map<String, String>) tableData.get("table_field_name");
	        }
	    }
	    
	    return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTableMap(String tableName)
	{
		if (map == null) {
            new YamlMapper();
        }
		
        return (Map<String, Object>) ((Map<String, Object>) map.get("table")).get(tableName);  
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getTableField(String tableName)
	{
		if (map == null) {
            new YamlMapper();
        }
        return (Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) map.get("table")).get(tableName)).get("fields");
	}
}