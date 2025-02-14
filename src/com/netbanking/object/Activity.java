package com.netbanking.object;

import com.netbanking.activityLogger.ActivityLogger;
import com.netbanking.model.Model;

public class Activity implements Model{
	private Long actorId;
	private Long subjectId;
	private Long keyValue;
	private String recordname;
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
	public String getRecordname() {
		return recordname;
	}
	public Activity setRecordname(String recordname) {
		this.recordname = recordname;
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
