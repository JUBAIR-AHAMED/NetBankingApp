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
		sqlQuery.append(" = ? ");
		return this;
	}
	
	public QueryBuilder where(Collection<String> fields, List<String> operators, List<String> logicOperators) {
		sqlQuery.append(" WHERE ");
		int index = 0;
        for (String field : fields) {
            sqlQuery.append(field)
               .append(" ")
               .append(operators.get(index))
               .append(" ?");
            
            if (index < fields.size() - 1 && logicOperators != null && index < logicOperators.size()) {
                sqlQuery.append(" ").append(logicOperators.get(index)).append(" ");
            }
            index++;
        }
		return this;
	}
	
	public QueryBuilder join(List<Join> joins) {
		
		for(Join join:joins) {
			sqlQuery.append(" JOIN ").append(join.getTableName()).append(" ON ");
			int joinLength = join.getLeftColumn().size();
			for (int i = 0; i < joinLength; i++) {
				
				sqlQuery.append(join.getLeftTable().get(i))
				.append(".")
				.append(join.getLeftColumn().get(i))
				.append(" ")
				.append(join.getOperator().get(i))
				.append(" ")
				.append(join.getRightTable().get(i))
				.append(".")
				.append(join.getRightColumn().get(i));
				
				if (i < joinLength - 1 && join.getLogicalOperator().get(i) != null) {
					sqlQuery.append(" ").append(join.getLogicalOperator().get(i)).append(" ");
				}
			}
		}
		return this;
	}
	
	public QueryBuilder order(List<String> orderColumns, List<String> sortingOrders) {
		sqlQuery.append(" ORDER BY");
        for (int i = 0; i < orderColumns.size(); i++) {
            String column = orderColumns.get(i);
            String direction = sortingOrders != null && i < sortingOrders.size()
                ? sortingOrders.get(i)
                : "ASC";
            sqlQuery.append(" ").append(column).append(" ").append(direction);
        }
		return this;
	}
	
	public QueryBuilder limit(Integer limit) {
		sqlQuery.append(" LIMIT ").append(limit);
		return this;
	}
	
	public String finish() {
		sqlQuery.append(";");
		String query = sqlQuery.toString();
		sqlQuery.delete(0, sqlQuery.length());
		return query;
	}
}