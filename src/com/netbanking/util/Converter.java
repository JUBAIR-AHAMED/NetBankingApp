package com.netbanking.util;

public class Converter {
	public static Long convertToLong(Object value) {
		if(value==null) {
			return null;
		}
		
		if (value instanceof Integer) {
			return ((Integer) value).longValue();
		} else if (value instanceof Long) {
			return (Long) value;
		}
		
		return null;
	}
}
