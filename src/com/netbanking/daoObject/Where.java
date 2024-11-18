package com.netbanking.daoObject;

public class Where {
	private String field;
	private String table;
	private Object value;
	
	public Where(String field, String table, Object value) {
		this.field=field;
		this.table=table;
		this.value=value;
	}
	
	public void setField(String field) {
		this.field=field;
	}
	
	public void setTable(String table) {
		this.table=table;
	}
	
	public void setValue(Object value) {
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
