package com.netbanking.activityLogger;

import org.apache.logging.log4j.Level;

import com.netbanking.dao.Dao;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.object.Activity;
import com.netbanking.util.Executor;

public class ActivityLogger {
    public void log(Activity activity) {
    	Executor executor = Executor.EXECUTOR;
        executor.getInstance().submit(() -> {
        		Dao<Activity> daoHandler = new DataAccessObject<>();
        		try {
					daoHandler.insert(activity);
				} catch (Exception e) {
					AsyncLoggerUtil.log(ActivityLogger.class, Level.ERROR, e.getMessage());
					e.printStackTrace();
				}
            });
    }
}