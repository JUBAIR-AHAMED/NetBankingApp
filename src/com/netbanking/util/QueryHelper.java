package com.netbanking.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class QueryHelper {
	  // Helper methods for building the SQL query
    public void appendJoinConditions(StringBuilder sql, 
    								  Map<String, String> joinConditions, 
    								  List<String> joinOperators, 
    								  List<String> logicalOperators) {
        int index = 0;
        for (Map.Entry<String, String> entry : joinConditions.entrySet()) {
            sql.append(entry.getKey())
               .append(" ")
               .append(joinOperators.get(index))
               .append(" ")
               .append(entry.getValue());

            // Add logical operators (e.g., AND, OR) if it's not the last condition
            if (index < joinConditions.size() - 1) {
                sql.append(" ").append(logicalOperators.get(index)).append(" ");
            }
            index++;
        }
    }

    public void appendUpdateValues(StringBuilder sql, Map<String, Object> updates) {
        int index = 0;
        for (String key : updates.keySet()) {
            sql.append(key).append(" = ?");
            if (index < updates.size() - 1) {
                sql.append(", ");
            }
            index++;
        }
    }

    public void appendSelectColumns(StringBuilder sql, List<String> selectColumns) {
        if (selectColumns == null || selectColumns.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(String.join(", ", selectColumns));
        }
    }

    public void appendConditions(StringBuilder sql, List<String> conditions, List<Object> values, List<String> operators, List<String> logicOperators) {
        int index = 0;
        for (String condition : conditions) {
            sql.append(condition)
               .append(" ")
               .append(operators.get(index))
               .append(" ?");
            
            if (index < conditions.size() - 1 && logicOperators != null && index < logicOperators.size()) {
                sql.append(" ").append(logicOperators.get(index)).append(" ");
            }
            index++;
        }
    }

    
    public void setParameters(PreparedStatement stmt, Map<String, Object> updates, List<Object> conditions) throws SQLException {
        int index = 1;
        if (updates != null) {
            for (Object value : updates.values()) {
                stmt.setObject(index++, value);
            }
        }
        if (conditions != null) {
            for (Object value : conditions) {
                stmt.setObject(index++, value);
            }
        }
    }
}
