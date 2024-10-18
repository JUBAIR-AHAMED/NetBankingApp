package com.netbanking.object;

import java.util.List;
import java.util.Map;

public class QueryRequest {
    private String tableName;
    private String joinTableName;
    private Map<String, String> joinConditions; 
    private Map<String, Object> whereConditions;
    private Map<String, Object> updates; 
    private Boolean selectAllColumns;
    private List<String> selectColumns; 
    private List<String> orderByColumns;
    private List<String> joinOperators;
    private List<String> whereOperators;
    private List<String> joinLogicalOperators;
    private List<String> whereLogicalOperators;
    private Integer limit;

    // Constructors
    public QueryRequest() {}

    public QueryRequest(String tableName, 
                        String joinTableName, 
                        Map<String, String> joinConditions, 
                        Map<String, Object> whereConditions, 
                        Map<String, Object> updates, 
                        List<String> selectColumns, 
                        List<String> orderByColumns, 
                        List<String> joinOperators,
                        Integer limit) {
        this.tableName = tableName;
        this.joinTableName = joinTableName;
        this.joinConditions = joinConditions;
        this.whereConditions = whereConditions;
        this.updates = updates;
        this.selectColumns = selectColumns;
        this.orderByColumns = orderByColumns;
        this.limit = limit;
    }

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

    public Map<String, Object> getWhereConditions() {
        return whereConditions;
    }

    public void setWhereConditions(Map<String, Object> whereConditions) {
        this.whereConditions = whereConditions;
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
    }

    public List<String> getOrderByColumns() {
        return orderByColumns;
    }

    public void setOrderByColumns(List<String> orderByColumns) {
        this.orderByColumns = orderByColumns;
    }
    
    public List<String> getJoinOperators() {
        return joinOperators;
    }

    public void setJoinOperators(List<String> joinOperators) {
        this.joinOperators = joinOperators;
    }
    
    public List<String> getWhereOperators() {
        return whereOperators;
    }

    public void setWhereOperators(List<String> whereOperators) {
        this.whereOperators = whereOperators;
    }
    
    public List<String> getJoinLogicalOperators() {
        return joinLogicalOperators;
    }

    public void setJoinLogicalOperators(List<String> joinLogicalOperators) {
        this.joinLogicalOperators = joinLogicalOperators;
    }
    
    public List<String> getWhereLogicalOperators() {
        return whereLogicalOperators;
    }

    public void setWhereLogicalOperators(List<String> whereLogicalOperators) {
        this.whereLogicalOperators = whereLogicalOperators;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
