package com.netbanking.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.netbanking.exception.CustomException;

public class Validator {
	public static void checkInvalidInput(Object... objects) throws CustomException
	{
		for(Object obj:objects)
		{		  
			if (obj == null) {
				throw new CustomException("Inputs cannot be null.");
			}
		}
	}
	
	// Invalid input exception/Out of bound handling for int
	public static void checkInvalidInput(int length, int nValue) throws CustomException
	{
		if (nValue<0||nValue>=length) {
			throw new CustomException("Invalid value for N or index.");
		}
	}
	
	public static void checkInsertIndex(int length, int nValue) throws CustomException
	{
		if (nValue<0||nValue>length) {
			throw new CustomException("Invalid value for insert index.");
		}
	}
	
	// getLength
	public static int getLength(String stringArgs) throws CustomException {
		checkInvalidInput(stringArgs);
		return stringArgs.length();
	}
	
	public static void checkFilePresence(File file) throws CustomException {
		if(file.exists())
		{
			throw new CustomException("File already present");
		}
	}
	
	public static boolean checkPathPresence(Path path)  {
		return Files.exists(path);
	}
	
	public static void isTxtFile(String fileName) throws CustomException {
		System.out.println(fileName);
		if(!fileName.endsWith(".txt"))
		{
			throw new CustomException("File name must be end with .txt");
		}
	}
}
