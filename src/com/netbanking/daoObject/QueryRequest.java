package com.netbanking.daoObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryRequest {
    private String tableName;
    private List<Join> joinConditions;
    private List<String> whereConditions;
    private List<Where> whereConditionsType;
    private List<Object> whereConditionsValues;
    private Map<String, Object> updates; 
    private List<String> updateField;
    private List<Condition> updatesType;
    private List<Object> updateValue;
    private Boolean selectAllColumns;
    private List<String> selectColumns;
    private List<Condition> selects;
    private List<String> orderByColumns;
    private List<String> orderDirections;
    private List<String> whereOperators;
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

    public List<Join> getJoinConditions() {
        return this.joinConditions;
    }
    
    public void setJoinConditions(List<Join> joinConditions) {
        this.joinConditions = joinConditions;
    }

    public void putJoinConditions(Join... condition) {
    	if (this.joinConditions == null) {
    		this.joinConditions = new ArrayList<>();
    	}
    	for(Join joins:condition)
    	{
    		this.joinConditions.add(joins);
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

    public void setWhereConditionsType(List<Where> whereConditionsType) {
        this.whereConditionsType = whereConditionsType;
    }

    public List<Where> getWhereConditionsType() {
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
    
    public List<String> getUpdateField() {
        return updateField;
    }

    public void setUpdateField(List<String> updateField) {
        this.updateField = updateField;
    }
    
    public void putUpdateField(String... updateFields) {
        if(updateField==null)
        {
        	updateField = new ArrayList<String>();
        }
        for(String field : updateFields) {
        	updateField.add(field);
        }
    }
    
    public List<Object> getUpdateValue() {
        return updateValue;
    }

    public void setUpdateValue(List<Object> updateValue) {
        this.updateValue = updateValue;
    }
    
    public void putUpdateValue(Object... updateValues) {
        if(updateValue==null)
        {
        	updateValue = new ArrayList<Object>();
        }
        for(Object value : updateValues) {
        	updateValue.add(value);
        }
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
    
    public void putSelectColumns(String... selectColumns) {
        if(this.selectColumns==null) {
        	this.selectColumns = new ArrayList<String>();
        }
        for(String select:selectColumns) {
        	this.selectColumns.add(select);
        }
        selectAllColumns = false;
    }
    
    public List<Condition> getSelects() {
        return selects;
    }

    public void setSelects(List<Condition> selects) {
        this.selects = selects;
        selectAllColumns = false;
    }
    
    public void putSelects(Condition... selects) {
        if(this.selects==null) {
        	this.selects = new ArrayList<Condition>();
        }
        for(Condition select:selects) {
        	this.selects.add(select);
        }
        selectAllColumns = false;
    }
    
    public List<Condition> getUpdatesType() {
        return updatesType;
    }

    public void setUpdatesType(List<Condition> updatesType) {
        this.updatesType = updatesType;
    }
    
    public void putUpdatesType(Condition... updatesType) {
        if(this.updatesType==null) {
        	this.updatesType = new ArrayList<Condition>();
        }
        for(Condition updates:updatesType) {
        	this.updatesType.add(updates);
        }
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
