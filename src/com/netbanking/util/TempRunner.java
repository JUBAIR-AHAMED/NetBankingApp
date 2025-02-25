package com.netbanking.util;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class TempRunner {
	public static void main(String[] args) {
//		new FutureTask<String>(new TempRunner.x());
//		Callable<String> task = () -> {
//            System.out.println("Running in a separate thread!");
//            return "Task Completed"; 
//        };
//		new String("PUT").concat("asd");
		System.out.println(
		PasswordUtility.hashPassword("234567"));
	}
	static class x implements Callable<String>{
		@Override
		public String call() throws Exception {
			return null;
		}
	}
}
