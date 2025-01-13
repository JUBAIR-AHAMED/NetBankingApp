package com.netbanking.object;

import com.netbanking.model.Model;

public class Activity implements Model{
	private Long userId;
	private String tablename;
	private String action;
	private String details;
	private Long actionTime;
	
	public Long getUserId() {
		return userId;
	}
	public Activity setUserId(Long userId) {
		this.userId = userId;
		return this;
	}
	public String getTablename() {
		return tablename;
	}
	public Activity setTablename(String tablename) {
		this.tablename = tablename;
		return this;
	}
	public String getAction() {
		return action;
	}
	public Activity setAction(String action) {
		this.action = action;
		return this;
	}
	public String getDetails() {
		return details;
	}
	public Activity setDetails(String details) {
		this.details = details;
		return this;
	}
	public Long getActionTime() {
		return actionTime;
	}
	public Activity setActionTime(Long actionTime) {
		this.actionTime = actionTime;
		return this;
	}
}
