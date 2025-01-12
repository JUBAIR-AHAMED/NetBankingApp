package com.netbanking.daoObject;

import java.util.ArrayList;
import java.util.List;

public class Join {
	private String joinType;
	private String tableName;
    private List<String> leftTable;
    private List<String> leftColumn;
    private List<String> rightTable;
    private List<String> rightColumn;
    private List<String> operator;
    private List<String> logicalOperator;

    public Join() {}

    public String getJoinType() {
        return joinType;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public List<String> getLeftTable() {
        return leftTable;
    }

    public List<String> getLeftColumn() {
        return leftColumn;
    }

    public List<String> getRightTable() {
        return rightTable;
    }

    public List<String> getRightColumn() {
        return rightColumn;
    }

    public List<String> getOperator() {
        return operator;
    }

    public List<String> getLogicalOperator() {
        return logicalOperator;
    }

    // Setters
    public Join setJoinType(String joinType) {
        this.joinType = joinType;
        return this;
    }
    
    public Join setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
    
    public Join setLeftTable(List<String> leftTable) {
        this.leftTable = leftTable;
        return this;
    }

    public Join setLeftColumn(List<String> leftColumn) {
        this.leftColumn = leftColumn;
        return this;
    }

    public Join setRightTable(List<String> rightTable) {
        this.rightTable = rightTable;
        return this;
    }

    public Join setRightColumn(List<String> rightColumn) {
        this.rightColumn = rightColumn;
        return this;
    }

    public Join setOperator(List<String> operator) {
        this.operator = operator;
        return this;
    }

    public Join setLogicalOperator(List<String> logicalOperator) {
        this.logicalOperator = logicalOperator;
        return this;
    }
    
    // Put
    public Join putLeftTable(String... leftTable) {
        if(this.leftTable==null) {
        	this.leftTable = new ArrayList<>();
        }
    	for(String value:leftTable) {
        	this.leftTable.add(value);
        }
    	return this;
    }
    
    public Join putLeftColumn(String... leftColumn) {
        if(this.leftColumn==null) {
        	this.leftColumn = new ArrayList<>();
        }
    	for(String value:leftColumn) {
        	this.leftColumn.add(value);
        }
    	return this;
    }
    
    public Join putRightTable(String... rightTable) {
        if(this.rightTable==null) {
        	this.rightTable = new ArrayList<>();
        }
    	for(String value:rightTable) {
        	this.rightTable.add(value);
        }
    	return this;
    }
    
    public Join putRightColumn(String... rightColumn) {
        if(this.rightColumn==null) {
        	this.rightColumn = new ArrayList<>();
        }
    	for(String value:rightColumn) {
        	this.rightColumn.add(value);
        }
    	return this;
    }
    
    public Join putOperator(String... operator) {
        if(this.operator==null) {
        	this.operator = new ArrayList<>();
        }
    	for(String value:operator) {
        	this.operator.add(value);
        }
    	return this;
    }
    
    public Join putLogicalOperator(String... logicalOperator) {
        if(this.logicalOperator==null) {
        	this.logicalOperator = new ArrayList<>();
        }
    	for(String value:logicalOperator) {
        	this.logicalOperator.add(value);
        }
    	return this;
    }
    
    public String toString() {
    	return null;
    }
}
