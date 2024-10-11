package com.netbanking.mapper;

import java.sql.ResultSetMetaData;
import java.util.Map;

public interface GenericMapper<T> {
    Map<String, Object> toMap(T entity, ResultSetMetaData metaData);
    Long getId(T entity);
}