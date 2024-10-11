//package com.netbanking.dao;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.List;
//import com.netbanking.model.Model;
//import com.netbanking.util.DBConnection;
//
//public class Dao {
//
//	public <T extends Model> void insert(T entity) throws SQLException {
//        Connection connection = DBConnection.getConnection();
//        
//        // Get table name
//        String tableName = entity.getTableName();
//        
//        // Get column metadata dynamically from ResultSetMetaData
//        String query = "SELECT * FROM " + tableName + " LIMIT 1";
//        Statement stmt = connection.createStatement();
//        ResultSet rs = stmt.executeQuery(query);
//        ResultSetMetaData metaData = rs.getMetaData();
//        
//        int columnCount = metaData.getColumnCount();
//        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
//        StringBuilder placeholders = new StringBuilder("VALUES (");
//
//        // Dynamically construct the SQL query with column names and placeholders
//        for (int i = 1; i <= columnCount; i++) {
//            String columnName = metaData.getColumnName(i);
//            sql.append(columnName);
//            placeholders.append("?");
//            if (i < columnCount) {
//                sql.append(", ");
//                placeholders.append(", ");
//            }
//        }
//        sql.append(") ").append(placeholders).append(")");
//
//        // Prepare the statement
//        try (PreparedStatement preparedStmt = connection.prepareStatement(sql.toString())) {
//            List<Object> fields = entity.getFields();
//            for (int i = 0; i < fields.size(); i++) {
//                preparedStmt.setObject(i + 1, fields.get(i));
//            }
//            // Execute the insert query
//            preparedStmt.executeUpdate();
//        }
//	}
//
//    
//	public <T extends Model> void update(T entity) throws SQLException {
//        Connection connection = DBConnection.getConnection();
//        
//        String tableName = entity.getTableName();
//        Long entityId = entity.getId(); // Assuming the primary key is 'id'
//        
//        // Get column metadata dynamically using ResultSetMetaData
//        String query = "SELECT * FROM " + tableName + " LIMIT 1";
//        Statement stmt = connection.createStatement();
//        ResultSet rs = stmt.executeQuery(query);
//        ResultSetMetaData metaData = rs.getMetaData();
//        
//        int columnCount = metaData.getColumnCount();
//        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
//        
//        // Dynamically construct the SQL query for updating all fields except the primary key
//        int fieldIndex = 1;
//        for (int i = 1; i <= columnCount; i++) {
//            String columnName = metaData.getColumnName(i);
//            if (!columnName.equalsIgnoreCase("customer_id") && !columnName.equalsIgnoreCase("user_id")) { // Skip primary key
//                sql.append(columnName).append(" = ?");
//                if (fieldIndex < columnCount - 1) {
//                    sql.append(", ");
//                }
//                fieldIndex++;
//            }
//        }
//        sql.append(" WHERE customer_id = ?"); // Assuming primary key is 'customer_id'
//
//        // Prepare the statement
//        try (PreparedStatement preparedStmt = connection.prepareStatement(sql.toString())) {
//            List<Object> fields = entity.getFields();
//
//            // Set the values for each field (skip ID in fields)
//            fieldIndex = 1;
//            for (int i = 1; i < fields.size(); i++) { // Start at 1 to skip ID
//                preparedStmt.setObject(fieldIndex, fields.get(i));
//                fieldIndex++;
//            }
//
//            // Set the primary key value at the end
//            preparedStmt.setObject(fieldIndex, entityId);
//            
//            // Execute the update
//            preparedStmt.executeUpdate();
//        }
//
//        connection.close();
//    }
//	
////	public <T extends Model> void delete(T entity) {
////		Long entityId = entity.getId();
////		String tableName = entity.getTableName();
////		
////		String query = "DELETE FROM "+tableName+" WHERE "+entityId+" = ";
////		Statement stmt = 
////	}
//}

package com.netbanking.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import com.netbanking.mapper.GenericMapper;
import com.netbanking.mapper.ReflectionMapper;
import com.netbanking.util.DBConnection;

public class Dao {

	public <T> void insert(T entity) throws SQLException {
	    Connection connection = DBConnection.getConnection();
	    GenericMapper<T> mapper = new ReflectionMapper<T>();
	    
	    // Get the table name from the entity
	    String tableName = entity.getClass().getSimpleName().toLowerCase();
	    
	    // Get column metadata dynamically from ResultSetMetaData
	    String query = "SELECT * FROM " + tableName + " LIMIT 1";
	    Statement metaStmt = connection.createStatement();
	    ResultSet rs = metaStmt.executeQuery(query);
	    ResultSetMetaData metaData = rs.getMetaData();
	    
	    
	    // Map fields from entity
	    Map<String, Object> fields = mapper.toMap(entity, metaData);
	    	    
	    // Check if fields map is empty
	    if (fields.isEmpty()) {
	        throw new SQLException("No fields found to insert for entity: " + entity);
	    }
	    
	    // Dynamically construct the SQL query
	    StringBuilder sql = new StringBuilder("INSERT INTO ");
	    sql.append(tableName).append(" (");
	    
	    // Add columns
	    int index = 0;
	    for (String column : fields.keySet()) {
	        sql.append(column);
	        if (index < fields.size() - 1) {
	            sql.append(", ");
	        }
	        index++;
	    }
	    sql.append(") VALUES (");
	    
	    // Add placeholders
	    index = 0;
	    for (int i = 0; i < fields.size(); i++) {
	        sql.append("?");
	        if (i < fields.size() - 1) {
	            sql.append(", ");
	        }
	    }
	    sql.append(")");

	    System.out.println(sql.toString()); // Check the constructed SQL query
	    
	    // Prepare the statement
	    try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
	        index = 0;
	        for (Object value : fields.values()) {
	            stmt.setObject(index + 1, value);
	            index++;
	        }

	        // Execute the insert
	        stmt.executeUpdate();
	        System.out.println("Insert successful for table: " + tableName);
	    }
	    
	    rs.close();
	    metaStmt.close();
	    connection.close();
	}

    
    public <T> void update(T entity) throws SQLException {
        Connection connection = DBConnection.getConnection();
        GenericMapper<T> mapper = new ReflectionMapper<T>();
        
        String tableName = entity.getClass().getSimpleName().toLowerCase();
        String query = "SELECT * FROM " + tableName + " LIMIT 1"; // Use a simple query to get the metadata
        Statement metaStmt = connection.createStatement();
        ResultSet rs = metaStmt.executeQuery(query);
        ResultSetMetaData metaData = rs.getMetaData();
        
        //To get the primary key
        String primaryKey = null;
//        DatabaseMetaData dbMetaData = connection.getMetaData();
//        ResultSet rsForPrimeKey = dbMetaData.getPrimaryKeys(null, "netbanking", "user");

        String squery= "SHOW KEYS FROM "+tableName+" WHERE Key_name = 'PRIMARY'";
        System.out.println("squery: "+squery);
        try (Statement stmt = connection.createStatement();
        	     ResultSet rsForPrimeKey = stmt.executeQuery("SHOW KEYS FROM "+tableName+" WHERE Key_name = 'PRIMARY'")) {
        	    
        	    if (rsForPrimeKey.next()) {
        	        primaryKey = rsForPrimeKey.getString("Column_name");
        	        System.out.println("Primary Key: " + primaryKey);
        	    } else {
        	        System.out.println("No primary key found for the table 'user'.");
        	    }
        	} catch (SQLException e) {
        	    e.printStackTrace();
        	}


        
        rs.close();
        metaStmt.close();
        
        Map<String, Object> fields = mapper.toMap(entity, metaData); // Pass ResultSetMetaData
        
        StringBuilder sql = new StringBuilder("UPDATE ");
        sql.append(tableName).append(" SET ");

        // Dynamically construct the SQL query for updating all fields except ID
        int index = 0;
        for (String column : fields.keySet()) {
            if (!column.equals(primaryKey)) { // Adjust based on your ID field name
                sql.append(column).append(" = ?");
                if (index < fields.size() - 2) {
                    sql.append(", ");
                }
            }
            index++;
        }
        sql.append(" WHERE ").append(primaryKey).append(" = ?"); // Adjust this line as needed for the ID field

        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            index = 0;
            
            for(Map.Entry<String, Object> entry : fields.entrySet())
            {
            	
            	System.out.println(entry.getKey()+" "+entry.getValue());
            	if(!primaryKey.contentEquals(entry.getKey()))
            	{
            		stmt.setObject(index+1, entry.getValue());
            		index++;
            	}
            }
        	
            stmt.setObject(index, fields.get(primaryKey)); // Use your ID getter method
            stmt.executeUpdate();
        }
        
        connection.close();
    }
}