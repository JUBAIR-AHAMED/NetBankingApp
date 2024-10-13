package com.netbanking.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

public interface GenericMapper<T> {
    Map<String, Object> toMap(T entity, ResultSet rs);
    Long getId(T entity);
}