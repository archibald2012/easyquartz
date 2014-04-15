package org.easycluster.easyquartz;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeSchedule {

	private String groupName;

	private String scheduleName;

	private Date startTime;

	private Date endTime;

	private int startDelay = -1;

	private int endDelay = -1;

	private TimeUnit DelayUnit = TimeUnit.SECONDS;

	private int repeatCount = -1;

	private int repeatTime = 10;

	private TimeUnit repeatUnit = TimeUnit.SECONDS;

	private String cronExpression;

	private int misfireInstruction = -1;

	private Map<String, String> attributes = new HashMap<String, String>();

	public TimeSchedule() {
	}

	public TimeSchedule(String scheduleName, String groupName) {
		this.scheduleName = scheduleName;
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	public TimeUnit getRepeatUnit() {
		return repeatUnit;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public int getMisfireInstruction() {
		return misfireInstruction;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getStartDelay() {
		return startDelay;
	}

	public void setStartDelay(int startDelay) {
		this.startDelay = startDelay;
	}

	public int getEndDelay() {
		return endDelay;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setEndDelay(int endDelay) {
		this.endDelay = endDelay;
	}

	public TimeUnit getDelayUnit() {
		return DelayUnit;
	}

	public void setDelayUnit(TimeUnit delayUnit) {
		DelayUnit = delayUnit;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public void setRepeatUnit(TimeUnit repeatUnit) {
		this.repeatUnit = repeatUnit;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public void setMisfireInstruction(int misfireInstruction) {
		this.misfireInstruction = misfireInstruction;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

}
