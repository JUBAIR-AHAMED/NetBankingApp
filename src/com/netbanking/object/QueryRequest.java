package com.netbanking.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netbanking.exception.CustomException;

public class QueryRequest {
    private String tableName;
    private String joinTableName;
    private Map<String, String> joinConditions; 
    private List<String> whereConditions;
    private List<WhereCondition> whereConditionsType;
    private List<Object> whereConditionsValues;
    private Map<String, Object> updates; 
    private Boolean selectAllColumns;
    private List<String> selectColumns; 
    private List<String> orderByColumns;
    private List<String> orderDirections;
    private List<String> joinOperators;
    private List<String> whereOperators;
    private List<String> joinLogicalOperators;
    private List<String> whereLogicalOperators;
    private Integer limit;

    // Constructors
    public QueryRequest() {}

    // Getters and Setters
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public void setJoinTableName(String joinTableName) {
        this.joinTableName = joinTableName;
    }

    public Map<String, String> getJoinConditions() {
        return joinConditions;
    }

    public void setJoinConditions(Map<String, String> joinConditions) {
        this.joinConditions = joinConditions;
    }
    
    public void putJoinConditions(String... conditions) throws CustomException {
    	if(joinConditions == null)
    	{
    		joinConditions = new HashMap<String, String>();
    	}
    	int length = conditions.length;
    	if(length%2==1) throw new CustomException("Pairs expected.");
    	for(int i=0;i<length;i++)
    	{
    		joinConditions.put(conditions[i], conditions[i+1]);
    		i++;
    	}
    }

    public List<String> getWhereConditions() {
        return whereConditions;
    }

    public void setWhereConditions(List<String> whereConditions) {
        this.whereConditions = whereConditions;
    }
    
    public void putWhereConditions(String... conditions) {
        if(whereConditions==null)
        {
        	whereConditions = new ArrayList<String>();
        }
        for(String condition : conditions) {
        	whereConditions.add(condition);
        }
    }

    public void setWhereConditionsType(List<WhereCondition> whereConditionsType) {
        this.whereConditionsType = whereConditionsType;
    }

    public List<WhereCondition> getWhereConditionsType() {
        return whereConditionsType;
    }

    public List<Object> getWhereConditionsValues() {
    	return whereConditionsValues;
    }
    
    public void setWhereConditionsValues(List<Object> whereConditionsValues) {
        this.whereConditionsValues = whereConditionsValues;
    }
    
    public void putWhereConditionsValues(Object... values) {
    	if(whereConditionsValues==null)
    	{
    		whereConditionsValues = new ArrayList<Object>();
    	}
        for(Object value : values)
        {
        	whereConditionsValues.add(value);
        }
    }
    
    public Map<String, Object> getUpdates() {
        return updates;
    }

    public void setUpdates(Map<String, Object> updates) {
        this.updates = updates;
    }

    public Boolean getSelectAllColumns() {
        return selectAllColumns;
    }

    public void setSelectAllColumns(Boolean selectAllColumns) {
        this.selectAllColumns = selectAllColumns;
    }
    
    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
        selectAllColumns = false;
    }

    public List<String> getOrderByColumns() {
        return orderByColumns;
    }

    public void setOrderByColumns(List<String> orderByColumns) {
        this.orderByColumns = orderByColumns;
    }
    
    public List<String> getOrderDirections() {
        return orderDirections;
    }

    public void setOrderDirections(List<String> orderDirections) {
        this.orderDirections = orderDirections;
    }
    
    public List<String> getJoinOperators() {
        return joinOperators;
    }

    public void setJoinOperators(List<String> joinOperators) {
        this.joinOperators = joinOperators;
    }
    
    public void putJoinOperators(String... operators) {
        if(joinOperators==null)
        {
        	joinOperators=new ArrayList<String>();
        }
        for(String operator : operators)
        {
        	joinOperators.add(operator);
        }
    }
    
    public List<String> getWhereOperators() {
        return whereOperators;
    }

    public void setWhereOperators(List<String> whereOperators) {
        this.whereOperators = whereOperators;
    }
    
    public void putWhereOperators(String... operators) {
        if(whereOperators==null)
        {
        	whereOperators=new ArrayList<String>();
        }
        for(String operator : operators)
        {
        	whereOperators.add(operator);
        }
    }
    
    public List<String> getJoinLogicalOperators() {
        return joinLogicalOperators;
    }

    public void setJoinLogicalOperators(List<String> joinLogicalOperators) {
        this.joinLogicalOperators = joinLogicalOperators;
    }
    
    public void putJoinLogicalOperators(String... logicalOperators) {
        if(joinLogicalOperators==null)
        {
        	joinLogicalOperators=new ArrayList<String>();
        }
        for(String operator : logicalOperators)
        {
        	joinLogicalOperators.add(operator);
        }
    }
    
    public List<String> getWhereLogicalOperators() {
        return whereLogicalOperators;
    }

    public void setWhereLogicalOperators(List<String> whereLogicalOperators) {
        this.whereLogicalOperators = whereLogicalOperators;
    }
    
    public void putWhereLogicalOperators(String... logicalOperators) {
        if(whereLogicalOperators==null)
        {
        	whereLogicalOperators=new ArrayList<String>();
        }
        for(String operator : logicalOperators)
        {
        	whereLogicalOperators.add(operator);
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
