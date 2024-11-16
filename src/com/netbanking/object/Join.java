package com.netbanking.object;

import java.util.ArrayList;
import java.util.List;

public class Join {
	private String tableName;
    private List<String> leftTable;
    private List<String> leftColumn;
    private List<String> rightTable;
    private List<String> rightColumn;
    private List<String> operator;
    private List<String> logicalOperator;

    public Join() {}

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
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public void setLeftTable(List<String> leftTable) {
        this.leftTable = leftTable;
    }

    public void setLeftColumn(List<String> leftColumn) {
        this.leftColumn = leftColumn;
    }

    public void setRightTable(List<String> rightTable) {
        this.rightTable = rightTable;
    }

    public void setRightColumn(List<String> rightColumn) {
        this.rightColumn = rightColumn;
    }

    public void setOperator(List<String> operator) {
        this.operator = operator;
    }

    public void setLogicalOperator(List<String> logicalOperator) {
        this.logicalOperator = logicalOperator;
    }
    
    // Put
    public void putLeftTable(String... leftTable) {
        if(this.leftTable==null) {
        	this.leftTable = new ArrayList<>();
        }
    	for(String value:leftTable) {
        	this.leftTable.add(value);
        }
    }
    
    public void putLeftColumn(String... leftColumn) {
        if(this.leftColumn==null) {
        	this.leftColumn = new ArrayList<>();
        }
    	for(String value:leftColumn) {
        	this.leftColumn.add(value);
        }
    }
    
    public void putRightTable(String... rightTable) {
        if(this.rightTable==null) {
        	this.rightTable = new ArrayList<>();
        }
    	for(String value:rightTable) {
        	this.rightTable.add(value);
        }
    }
    
    public void putRightColumn(String... rightColumn) {
        if(this.rightColumn==null) {
        	this.rightColumn = new ArrayList<>();
        }
    	for(String value:rightColumn) {
        	this.rightColumn.add(value);
        }
    }
    
    public void putOperator(String... operator) {
        if(this.operator==null) {
        	this.operator = new ArrayList<>();
        }
    	for(String value:operator) {
        	this.operator.add(value);
        }
    }
    
    public void putLogicalOperator(String... logicalOperator) {
        if(this.logicalOperator==null) {
        	this.logicalOperator = new ArrayList<>();
        }
    	for(String value:logicalOperator) {
        	this.logicalOperator.add(value);
        }
    }
    
    public String toString() {
    	return null;
    }
}
