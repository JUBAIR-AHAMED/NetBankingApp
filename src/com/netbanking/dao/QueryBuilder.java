package com.netbanking.dao;

import java.util.Collection;
import java.util.List;

import com.netbanking.object.Join;

public class QueryBuilder {
	public StringBuilder sqlQuery = new StringBuilder();
	
	public QueryBuilder select() {
		sqlQuery.append("SELECT * ");
		return this;
	}
	
	public QueryBuilder select(List<String> fields) {
		sqlQuery.append("SELECT ");
		sqlQuery.append(String.join(", ", fields));
		return this;
	}
	
	public QueryBuilder from(String tableName) {
		sqlQuery.append(" FROM ").append(tableName);
		return this;
	}
	
	public QueryBuilder insert(String tableName, Collection<String> fields) {
		sqlQuery.append("INSERT INTO ").append(tableName);
		sqlQuery.append("(").append(String.join(", ", fields)).append(")");
		sqlQuery.append(" VALUES (");
		int length = fields.size();
		for(int i=0;i<length;i++)
		{
			sqlQuery.append("?");
			if(i<length-1) {
				sqlQuery.append(", ");
			}
		}
		sqlQuery.append(")");
		return this;
	}
	
	public QueryBuilder update(String tableName) {
		sqlQuery.append("UPDATE ").append(tableName);
		return this;
	}
	
	public QueryBuilder delete(String tableName) {
		sqlQuery.append("DELETE FROM ").append(tableName).append(" ");
		return this;
	}
	
	public QueryBuilder set(List<String> fields) {
		sqlQuery.append(" SET ");
		sqlQuery.append(String.join(" = ?,", fields));
		return this;
	}
	
	public QueryBuilder where(List<String> fields) {
		sqlQuery.append(" WHERE ");
		sqlQuery.append(String.join(" = ?,", fields));
		return this;
	}
	
	public QueryBuilder join(List<Join> joins) {
		for (int i = 0; i < joins.size(); i++) {
	        Join condition = joins.get(i);
	        
	        sqlQuery.append(condition.getLeftTable())
	           .append(".")
	           .append(condition.getLeftColumn())
	           .append(" ")
	           .append(condition.getOperator())
	           .append(" ")
	           .append(condition.getRightTable())
	           .append(".")
	           .append(condition.getRightColumn());

	        if (i < joins.size() - 1 && condition.getLogicalOperator() != null) {
	            sqlQuery.append(" ").append(condition.getLogicalOperator()).append(" ");
	        }
	    }
		return this;
	}
	
	public QueryBuilder order(List<String> orderCols, List<String> orders) {
		sqlQuery.append(" ORDER BY");
        for (int i = 0; i < orderCols.size(); i++) {
            String column = orderCols.get(i);
            String direction = orders != null && i < orders.size()
                ? orders.get(i)
                : "ASC";
            sqlQuery.append(" ").append(column).append(" ").append(direction);
        }
		return this;
	}
	
	public QueryBuilder limit(int limit) {
		sqlQuery.append(" LIMIT = ? ");
		return this;
	}
	
	public String finish() {
		sqlQuery.append(";");
		String query = sqlQuery.toString();
		sqlQuery.delete(0, sqlQuery.length());
		return query;
	}
}