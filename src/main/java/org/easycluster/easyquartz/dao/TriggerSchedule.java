package org.easycluster.easyquartz.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TriggerSchedule implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String processor;
	private String groupName;
	private String scheduleName;
	private String cronExpression;
	private Date startTime;
	private Date endTime;
	private int startDelay;
	private int endDelay;
	private String delayUnit;
	private int repeatTime;
	private String repeatUnit;
	private int repeatCount;
	private int misfireInstruction;
	private String namespace;
	private Date gmtCreate;
	private Date gmtModified;

	private String lockInstance;
	private Date masterTime;
	private Date updatedAt;

	private Map<String, String> attributes = new HashMap<String, String>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getScheduleName() {
		return scheduleName;
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

	public void setEndDelay(int endDelay) {
		this.endDelay = endDelay;
	}

	public String getDelayUnit() {
		return delayUnit;
	}

	public void setDelayUnit(String delayUnit) {
		this.delayUnit = delayUnit;
	}

	public int getRepeatTime() {
		return repeatTime;
	}

	public void setRepeatTime(int repeatTime) {
		this.repeatTime = repeatTime;
	}

	public String getRepeatUnit() {
		return repeatUnit;
	}

	public void setRepeatUnit(String repeatUnit) {
		this.repeatUnit = repeatUnit;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public int getMisfireInstruction() {
		return misfireInstruction;
	}

	public void setMisfireInstruction(int misfireInstruction) {
		this.misfireInstruction = misfireInstruction;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void putAttribute(String name, String value) {
		this.attributes.put(name, value);
	}

	public String getLockInstance() {
		return lockInstance;
	}

	public void setLockInstance(String lockInstance) {
		this.lockInstance = lockInstance;
	}

	public Date getMasterTime() {
		return masterTime;
	}

	public void setMasterTime(Date masterTime) {
		this.masterTime = masterTime;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 1).append(id).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, new String[] {
				"gmtModified", "lockInstance", "masterTime", "updatedAt" });
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
