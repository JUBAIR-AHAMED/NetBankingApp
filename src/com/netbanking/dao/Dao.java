package com.netbanking.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.netbanking.object.QueryRequest;

public interface Dao<T> {
    Long insert(String tableName, Map<String, Object> insertValues) throws SQLException;
    void delete(String tableName, Map<String, Object> conditions) throws SQLException;
    void update(QueryRequest request) throws SQLException;
    List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception;
}