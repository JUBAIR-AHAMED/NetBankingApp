package com.netbanking.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.netbanking.daoObject.QueryRequest;
import com.netbanking.model.Model;

public interface Dao<T extends Model> {
    Long insert(T Object) throws Exception;
    void update(T object) throws Exception;
    List<Map<String, Object>> select(QueryRequest request) throws SQLException, Exception;
}