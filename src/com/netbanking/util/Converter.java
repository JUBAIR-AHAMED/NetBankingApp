package com.netbanking.util;

import java.util.function.Function;

public class Converter {
	public static Long convertToLong(Object value) {
		if(Validator.isNull(value)) {
			return null;
		}
		Function<String, Long> parser = Long::parseLong;
		return parser.apply(value.toString());
	}
}
