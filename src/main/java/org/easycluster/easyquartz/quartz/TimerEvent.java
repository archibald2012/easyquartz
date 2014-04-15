package org.easycluster.easyquartz.quartz;

import java.util.Date;

public class TimerEvent {

	private Object source;

	private Date timestamp;

	public TimerEvent(Object source) {
		this.source = source;
		this.timestamp = new Date();
	}

	public Date getTime() {
		return timestamp;
	}

	public void setTime(Date time) {
		this.timestamp = time;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass()
				.getSimpleName());

		builder.append("[source=").append(source);
		builder.append(",time=").append(timestamp).append(']');

		return builder.toString();
	}
}
