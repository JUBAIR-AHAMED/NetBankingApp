package com.netbanking.exception;

public class CustomException extends Exception {
	private final int statusCode;
	private final String message;

//	public CustomException(String message)
//	{
//		super(message);
//	}
//	public CustomException(String message, Throwable cause)
//	{
//		super(message, cause);
//	}
//	

    public CustomException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
