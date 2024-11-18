package com.netbanking.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.netbanking.daoObject.QueryRequest;

public interface Dao<T> {
    Long insert(String tableName, Map<String, Object> insertValues) throws SQLException;
    void delete(String tableName, List<String> whereFields, List<Object> whereValues, List<String> whereOperators, List<String> logicalOperators) throws SQLException;
    void update(QueryRequest request) throws SQLException;
    List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception;
}