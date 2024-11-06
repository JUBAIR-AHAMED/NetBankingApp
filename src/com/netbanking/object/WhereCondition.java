package com.netbanking.object;

public class WhereCondition {
	private String field;
	private String table;
	private Object value;
	public WhereCondition(String field, String table, Object value) {
		this.field=field;
		this.table=table;
		this.value=value;
	}
	
	public String getField() {
		return field;
	}
	
	public String getTable() {
		return table;
	}
	
	public Object getValue() {
		return value;
	}
}
