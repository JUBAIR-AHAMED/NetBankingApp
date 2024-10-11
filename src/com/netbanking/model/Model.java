package com.netbanking.model;

import java.util.List;

public interface Model {
	Long getId();
	String getTableName();
	List<Object> getFields();
	String getIdField();
}
