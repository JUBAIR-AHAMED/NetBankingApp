package com.netbanking.util;

import com.netbanking.activityLogger.Logger;
import com.netbanking.dao.Dao;
import com.netbanking.dao.DataAccessObject;
import com.netbanking.object.Activity;

public class ActivityLogger implements Logger {
    @Override
    public void log(Activity activity) {
    	Executor executor = Executor.EXECUTOR;
        executor.getInstance().submit(() -> {
        		Dao<Activity> daoHandler = new DataAccessObject<>();
        		try {
					daoHandler.insert(activity);
				} catch (Exception e) {
					e.printStackTrace();
				}
            });
    }
}
