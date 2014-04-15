package org.easycluster.easyquartz.quartz;

import java.util.HashMap;
import java.util.Map;

import org.easycluster.easyquartz.Closure;
import org.easycluster.easyquartz.CoreScheduler;

@SuppressWarnings("rawtypes")
public class QuartzEventData {
	public static final String KEY_EVENT_DATA = "EVENT_DATA";

	private Closure dispatcher;

	private CoreScheduler scheduler;

	private Map attributes = new HashMap();

	public QuartzEventData(Closure dispatcher, CoreScheduler scheduler) {
		this.dispatcher = dispatcher;
		this.scheduler = scheduler;
	}

	public Closure getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Closure dispatcher) {
		this.dispatcher = dispatcher;
	}

	public CoreScheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(CoreScheduler scheduler) {
		this.scheduler = scheduler;
	}

	public String getKey() {
		return KEY_EVENT_DATA;
	}

	public Map getAttributes() {
		return attributes;
	}

	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}

}
