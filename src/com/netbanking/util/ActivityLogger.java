package com.netbanking.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.netbanking.activityLogger.Logger;
import com.netbanking.dao.Dao;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.object.Activity;

public class ActivityLogger implements Logger {
    private final ExecutorService executorService;

    public ActivityLogger() {
        this.executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void log(Activity activity) {
        executorService.submit(() -> {
        		Dao<Activity> daoHandler = new DataAccessObject<>();
        		try {
					daoHandler.insert(activity);
				} catch (Exception e) {
					e.printStackTrace();
				}
            });
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}
