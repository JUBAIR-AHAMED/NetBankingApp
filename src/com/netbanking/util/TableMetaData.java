package com.netbanking.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.netbanking.model.Model;

public class TableMetaData {
    private static final int CACHE_CAPACITY = 50; 
    private static Map<String, List<String>> cache = new HashMap<>();
    private static LinkedList<String> order = new LinkedList<>();

    public static <T extends Model> List<String> getMetaData(T entity) throws SQLException {
        String tableName = TableHelper.getTableName(entity);
        
        List<String> list  = cache.get(tableName);
        
        if (list != null) {
            order.remove(tableName);
            order.addFirst(tableName);
            return list;
        }

        List<String> metadata = fetchMetaDataFromDB(entity);

        if (cache.size() >= CACHE_CAPACITY) {
            String leastUsedTable = order.removeLast();
            cache.remove(leastUsedTable);
        }

        cache.put(tableName, metadata);
        order.addFirst(tableName);

        return metadata;
    }

    private static <T extends Model> List<String> fetchMetaDataFromDB(T entity) throws SQLException {
        List<String> columns = new ArrayList<>();
        String tableName = TableHelper.getTableName(entity);

        try (Connection connection = DBConnection.getConnection())
        { 	
	        try(Statement metaStmt = connection.createStatement();
	            ResultSet rs = metaStmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 1")) {
	
	            ResultSetMetaData metaData = rs.getMetaData();
	            int metaSize = metaData.getColumnCount();
	
	            for (int i = 1; i <= metaSize; i++) {
	                columns.add(metaData.getColumnName(i));
	            }
	            return columns;
	        } catch(SQLException ex) {
	        	throw ex;
	        }
        }
    }
}
