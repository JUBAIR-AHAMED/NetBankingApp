package com.netbanking.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum Executor {
	EXECUTOR(Executors.newFixedThreadPool(10));
	
	ExecutorService executor;
	
	private Executor(ExecutorService executor) {
		this.executor = executor;
	}
	
	public ExecutorService getInstance() {
		return executor;
	}
}
