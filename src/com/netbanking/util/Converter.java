package com.netbanking.util;

import java.util.function.Function;

public class Converter {
//	public static Long convertToLong(Object value) {
//		if(value==null) {
//			return null;
//		}
//		
//		if (value instanceof Integer) {
//			return ((Integer) value).longValue();
//		} else if (value instanceof Long) {
//			return (Long) value;
//		}
//		
//		return null;
//	}
	
	public static Long convertToLong(Object value) {
		if(value == null) {
			return null;
		}
		Function<String, Long> parser = Long::parseLong;
		return parser.apply(value.toString());
	}
}
