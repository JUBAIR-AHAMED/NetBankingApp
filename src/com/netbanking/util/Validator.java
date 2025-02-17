package com.netbanking.util;

import com.netbanking.exception.CustomException;

public class Validator {
	public static boolean isNull(Object obj) {
		return obj == null ? true : false;
	}
	
	public static void checkInvalidInput(Object... objects) throws CustomException
	{
		for(Object obj:objects)
		{		  
			if (Validator.isNull(obj)) {
				throw new CustomException(400, "Inputs cannot be null.");
			}
		}
	}
	
    public static boolean decimalChecker(Float number) {        
        String numberStr = number.toString();
        
        if (numberStr.contains(".")) {
            String[] parts = numberStr.split("\\.");
            String decimalPart = parts[1];
            
            if (decimalPart.length() > 2) {
                return false;
            }
        }
        return true;
    }
}
