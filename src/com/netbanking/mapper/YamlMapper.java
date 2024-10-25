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
				System.out.println("File Not Found");
			}
			map  = yaml.load(inputStream);
			System.out.println(map);
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
	
	public static List<String> getRelatedTableNames(String objectName) {
        if (map == null) {
            new YamlMapper();
        }

        @SuppressWarnings("unchecked")
        List<String> tables = (List<String>) ((Map<String, Object>) ((Map<String, Object>) map.get("relatedtable")).get(objectName)).get("tables");

        return tables;  // Return null if no mapping is found
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