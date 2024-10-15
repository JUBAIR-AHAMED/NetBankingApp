package com.netbanking.util;

import com.netbanking.model.Model;

public class TableHelper {
	public static <T extends Model> String getTableName(T entity)
	{
		return entity.getClass().getSimpleName().toLowerCase();
	}
}
