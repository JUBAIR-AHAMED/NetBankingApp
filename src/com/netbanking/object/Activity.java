package com.netbanking.object;

import com.netbanking.model.Model;
import com.netbanking.util.ActivityLogger;

public class Activity implements Model{
	private Long actorId;
	private Long subjectId;
	private Long keyValue;
	private String tablename;
	private String action;
	private String details;
	private Long actionTime;
	
	public Long getActorId() {
		return actorId;
	}
	public Activity setActorId(Long actorId) {
		this.actorId = actorId;
		return this;
	}
	public Long getSubjectId() {
		return subjectId;
	}
	public Activity setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
		return this;
	}
	public Long getKeyValue() {
		return keyValue;
	}
	public Activity setKeyValue(Long keyValue) {
		this.keyValue = keyValue;
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
	public void execute() {
		ActivityLogger activityLogger = new ActivityLogger();
		activityLogger.log(this);
	}
}
