package com.netbanking.mapper;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlMapper {
	private Map<String, Map<String, String>> map;
	
	public YamlMapper()
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
	
	public String getFieldName(String tableName, String columnName)
	{
		return map.get(tableName).get(columnName);
	}
}