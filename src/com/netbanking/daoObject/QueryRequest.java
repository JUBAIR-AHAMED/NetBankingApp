package com.netbanking.daoObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.netbanking.mapper.YamlMapper;

public class QueryRequest {
    private String tableName;
    private List<Join> joinConditions;
    private List<Where> whereConditionsType;
    private List<String> whereConditions;
    private List<Object> whereConditionsValues;
    private List<String> updateField;
    private List<Object> updateValue;
    private Boolean selectAllColumns;
    private List<String> selectColumns;
    private List<Condition> selects;
    private List<String> orderByColumns;
    private List<String> orderDirections;
    private List<String> whereOperators;
    private List<String> whereLogicalOperators;
    private Integer limit;
    private Integer offset;
    private Boolean count=false;

    // Getters and Setters
    public String getTableName() {
        return tableName;
    }

    public QueryRequest setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public List<Join> getJoinConditions() throws Exception {
    	if(joinConditions!=null&&!joinConditions.isEmpty()) {
			for(Join join:joinConditions) {
				int joinConditionLength = join.getLeftTable().size();
				List<String> leftTable=join.getLeftTable(), leftColumn=join.getLeftColumn(), rightTable=join.getRightTable(), rightColumn=join.getRightColumn();
				for(int i=0;i<joinConditionLength;i++) {
					String leftTableName = leftTable.get(i), leftColumnName = leftColumn.remove(i), rightTableName = rightTable.get(i), rightColumnName = rightColumn.remove(i);
					leftColumn.add(i, convertField(leftTableName, leftColumnName));
					rightColumn.add(i, convertField(rightTableName, rightColumnName));
				}
			}
		}
        return this.joinConditions;
    }
    
    public QueryRequest setJoinConditions(List<Join> joinConditions) {
        this.joinConditions = joinConditions;
        return this;
    }

    public QueryRequest putJoinConditions(Join... condition) {
    	if (this.joinConditions == null) {
    		this.joinConditions = new ArrayList<>();
    	}
    	for(Join joins:condition)
    	{
    		this.joinConditions.add(joins);
    	}
    	return this;
    }

    public List<String> getWhereConditions() throws Exception {
    	if(whereConditions!=null && !whereConditions.isEmpty()) {
        	convertFields(getTableName(), whereConditions);
	    }
    	if(whereConditionsType!=null) {
    		if(whereConditions==null) {
    			whereConditions = new ArrayList<String>();
    			whereConditionsValues = new ArrayList<Object>();
    		}
			for(Where entity: whereConditionsType) {
				String whereTableName = entity.getTable();
				String field = entity.getField();
				Object value = entity.getValue();
				field = convertField(whereTableName, field);
				StringBuilder sb = new StringBuilder(whereTableName).append(".").append(field);
				whereConditions.add(sb.toString());
				whereConditionsValues.add(value);
			}
		}
        return whereConditions;
    }

    public QueryRequest setWhereConditions(List<String> whereConditions) {
        this.whereConditions = whereConditions;
        return this;
    }
    
    public QueryRequest putWhereConditions(String... conditions) {
        if(whereConditions==null)
        {
        	whereConditions = new ArrayList<String>();
        }
        for(String condition : conditions) {
        	whereConditions.add(condition);
        }
        return this;
    }

    public QueryRequest setWhereConditionsType(List<Where> whereConditionsType) {
        this.whereConditionsType = whereConditionsType;
        return this;
    }

    public List<Where> getWhereConditionsType() {
        return whereConditionsType;
    }

    public List<Object> getWhereConditionsValues() {
    	return whereConditionsValues;
    }
    
    public QueryRequest setWhereConditionsValues(List<Object> whereConditionsValues) {
        this.whereConditionsValues = whereConditionsValues;
        return this;
    }
    
    public QueryRequest putWhereConditionsValues(Object... values) {
    	if(whereConditionsValues==null)
    	{
    		whereConditionsValues = new ArrayList<Object>();
    	}
        for(Object value : values)
        {
        	whereConditionsValues.add(value);
        }
        return this;
    }
    
    public List<String> getUpdateField() throws Exception {
    	convertFields(tableName, updateField);
        return updateField;
    }

    public QueryRequest setUpdateField(List<String> updateField) {
        this.updateField = updateField;
        return this;
    }
    
    public QueryRequest putUpdateField(String... updateFields) {
        if(updateField==null)
        {
        	updateField = new ArrayList<String>();
        }
        for(String field : updateFields) {
        	updateField.add(field);
        }
        return this;
    }
    
    public List<Object> getUpdateValue() {
        return updateValue;
    }

    public QueryRequest setUpdateValue(List<Object> updateValue) {
        this.updateValue = updateValue;
        return this;
    }
    
    public QueryRequest putUpdateValue(Object... updateValues) {
        if(updateValue==null)
        {
        	updateValue = new ArrayList<Object>();
        }
        for(Object value : updateValues) {
        	updateValue.add(value);
        }
        return this;
    }

    public Boolean getSelectAllColumns() {
        return selectAllColumns;
    }

    public QueryRequest setSelectAllColumns(Boolean selectAllColumns) {
        this.selectAllColumns = selectAllColumns;
        return this;
    }
    
    public List<String> getSelectColumns() throws Exception {
    	if(selectColumns==null) {
    		return null;
    	}
    	List<String> selectColsToReturn = new ArrayList<String>(selectColumns);
    	convertFields(getTableName(), selectColsToReturn);
        return selectColsToReturn;
    }
    
    public List<String> getSelectColumnsPojoVer() throws Exception {
        return selectColumns;
    }

    public QueryRequest setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
        selectAllColumns = false;
        return this;
    }
    
    public QueryRequest putSelectColumns(String... selectColumns) {
        if(this.selectColumns==null) {
        	this.selectColumns = new ArrayList<String>();
        }
        for(String select:selectColumns) {
        	this.selectColumns.add(select);
        }
        selectAllColumns = false;
        return this;
    }
    
    public List<String> getSelects() throws Exception {
    	List<String> selectColumns = new ArrayList<String>();
    	if(selects!=null && !selects.isEmpty()) {
			for(Condition select:selects) {
				String table = select.getTable();
				String field = select.getField();
				field = convertField(table, field);
				selectColumns.add(field);
			}
		}
        return selectColumns;
    }
    
    public List<String> getSelectsPojoVer() throws Exception {
    	List<String> selectColumns = new ArrayList<String>();
    	if(selects!=null && !selects.isEmpty()) {
			for(Condition select:selects) {
				String field = select.getField();
				selectColumns.add(field);
			}
		}
        return selectColumns;
    }

    public QueryRequest setSelects(List<Condition> selects) {
        this.selects = selects;
        selectAllColumns = false;
        return this;
    }
    
    public QueryRequest putSelects(Condition... selects) {
        if(this.selects==null) {
        	this.selects = new ArrayList<Condition>();
        }
        for(Condition select:selects) {
        	this.selects.add(select);
        }
        selectAllColumns = false;
        return this;
    }

    public List<String> getOrderByColumns() {
        return orderByColumns;
    }

    public QueryRequest setOrderByColumns(List<String> orderByColumns) {
        this.orderByColumns = orderByColumns;
        return this;
    }
    
    public List<String> getOrderDirections() {
        return orderDirections;
    }

    public QueryRequest setOrderDirections(List<String> orderDirections) {
        this.orderDirections = orderDirections;
        return this;
    }
    
    public List<String> getWhereOperators() {
        return whereOperators;
    }

    public QueryRequest setWhereOperators(List<String> whereOperators) {
        this.whereOperators = whereOperators;
        return this;
    }
    
    public QueryRequest putWhereOperators(String... operators) {
        if(whereOperators==null)
        {
        	whereOperators=new ArrayList<String>();
        }
        for(String operator : operators)
        {
        	whereOperators.add(operator);
        }
        return this;
    }
    
    public List<String> getWhereLogicalOperators() {
        return whereLogicalOperators;
    }

    public QueryRequest setWhereLogicalOperators(List<String> whereLogicalOperators) {
        this.whereLogicalOperators = whereLogicalOperators;
        return this;
    }
    
    public QueryRequest putWhereLogicalOperators(String... logicalOperators) {
        if(whereLogicalOperators==null)
        {
        	whereLogicalOperators=new ArrayList<String>();
        }
        for(String operator : logicalOperators)
        {
        	whereLogicalOperators.add(operator);
        }
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public QueryRequest setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }
    
    public Integer getOffset() {
        return offset;
    }

    public QueryRequest setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }
    
    public Boolean getCount() {
        return count;
    }

    public QueryRequest setCount(Boolean count) {
        this.count = count;
        return this;
    }
    
    public String convertField(String tableName, String field) throws Exception {
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		if(fieldToColumnMap==null) {
			throw new Exception("Table name is invalid");
		}
		String newField = fieldToColumnMap.get(field);
		if(newField==null) {
			throw new Exception("Field name is invalid: "+field);
		}
		return newField;
	}
    
    public void convertFields(String tableName, List<String> fields) throws Exception {
    	if(fields==null) {
    		return;
    	}
		Map<String, String> fieldToColumnMap = YamlMapper.getFieldToColumnMapByTableName(tableName);
		if(fieldToColumnMap==null) {
			throw new Exception("Table name is invalid: "+tableName);
		}
		for(int i=0;i<fields.size();i++) {
			String fieldName = fields.remove(i);
	        if (fieldToColumnMap.containsKey(fieldName)) {
	            fields.add(i, fieldToColumnMap.get(fieldName));
	        }
		}
	}
}
