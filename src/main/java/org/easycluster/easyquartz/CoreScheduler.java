package org.easycluster.easyquartz;

public interface CoreScheduler {

	void scheduleJob(TimeSchedule timeSchedule, Closure processor);

	void unscheduleJob(String scheduleName, String groupName);

	void startScheduler();

	void suspendScheduler();

	void shutdownScheduler();
}
