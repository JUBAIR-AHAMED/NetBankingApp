package com.netbanking.mapper;

import java.util.Map;

import com.netbanking.model.Model;

public interface GenericMapper<T extends Model> {
    Map<String, Object> toMap(T entity);
}