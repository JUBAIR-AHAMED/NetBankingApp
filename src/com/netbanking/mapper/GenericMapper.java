package com.netbanking.mapper;

import java.util.Map;

import com.netbanking.model.Model;

public interface GenericMapper<T> {
    Map<String, Object> getMap(T entity) throws Exception;
}