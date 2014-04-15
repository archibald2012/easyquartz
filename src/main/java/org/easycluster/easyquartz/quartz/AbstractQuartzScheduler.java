package org.easycluster.easyquartz.quartz;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.easycluster.easyquartz.Closure;
import org.easycluster.easyquartz.CoreScheduler;
import org.easycluster.easyquartz.TimeSchedule;
import org.easycluster.easyquartz.exception.SchedulerException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractQuartzScheduler implements CoreScheduler,
		InitializingBean {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractQuartzScheduler.class);

	private Class<?> quartzJobClass = DefaultQuartzJob.class;
	private StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
	private Scheduler scheduler;
	private JobListener jobListener;
	private TriggerListener triggerListener;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Creating scheduler " + scheduler);
		}
		try {
			scheduler = schedulerFactory.getScheduler();
			if (jobListener != null) {
				scheduler.addGlobalJobListener(jobListener);
			}
			if (triggerListener != null) {
				scheduler.addGlobalTriggerListener(triggerListener);
			}
		} catch (org.quartz.SchedulerException e) {
			handleException("get scheduler", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startScheduler() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Starting scheduler " + scheduler);
		}
		try {
			scheduler.start();
		} catch (org.quartz.SchedulerException e) {
			handleException("start scheduler", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void suspendScheduler() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Suspending scheduler " + scheduler);
		}
		try {
			scheduler.standby();
		} catch (org.quartz.SchedulerException e) {
			handleException("suspend scheduler", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdownScheduler() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Shutting down scheduler " + scheduler);
		}
		try {
			scheduler.shutdown();
		} catch (org.quartz.SchedulerException e) {
			handleException("shutdown scheduler", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void scheduleJob(TimeSchedule timeSchedule, Closure processor) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Schedule job. timeSchedule {}, processor {}",
					timeSchedule, processor);
		}
		try {
			JobDetail jobDetail = createJobDetail(timeSchedule);
			Trigger trigger = createTrigger(timeSchedule);

			// add its own event data
			QuartzEventData eventData = new QuartzEventData(processor, this);
			eventData.setAttributes(timeSchedule.getAttributes());
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put(QuartzEventData.KEY_EVENT_DATA, eventData);

			// Tell quartz to schedule the job using the trigger
			scheduler.scheduleJob(jobDetail, trigger);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Add scheduled job={}, trigger={}, processor={}",
						new Object[] { jobDetail, trigger, processor });
			}
		} catch (org.quartz.SchedulerException e) {
			handleException("schedule job", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unscheduleJob(String scheduleName, String groupName) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("unschedule job, scheduleName {}, groupName {}",
					scheduleName, groupName);
		}
		try {
			scheduler.unscheduleJob(scheduleName, groupName);
		} catch (org.quartz.SchedulerException e) {
			handleException("unschedule job", e);
		}
	}

	/**
	 * @param message
	 * @param e
	 */
	protected void handleException(String message,
			org.quartz.SchedulerException e) {
		String error = "Failed to " + message + " with errorCode "
				+ e.getErrorCode() + " error " + e.getMessage();
		LOGGER.error(error, e);
		throw new SchedulerException(error, e);
	}

	/**
	 * @param timeSchedule
	 * @return
	 */
	private JobDetail createJobDetail(TimeSchedule timeSchedule) {
		JobDataMap jobDataMap = new JobDataMap();
		JobDetail jobDetail = new JobDetail(timeSchedule.getScheduleName(),
				timeSchedule.getGroupName(), quartzJobClass);
		jobDetail.setJobDataMap(jobDataMap);
		return jobDetail;
	}

	/**
	 * @param timeSchedule
	 * @return
	 */
	private Trigger createTrigger(TimeSchedule timeSchedule) {
		Trigger trigger = null;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Scheduling a trigger for schedule " + timeSchedule);
		}

		String scheduleName = timeSchedule.getScheduleName();
		String groupName = timeSchedule.getGroupName();

		if (timeSchedule.getCronExpression() != null) {
			try {
				CronTrigger cronTrigger = (scheduleName != null && scheduleName
						.length() > 0) ? new CronTrigger(scheduleName,
						groupName) : new CronTrigger();
				cronTrigger.setCronExpression(timeSchedule.getCronExpression());
				trigger = cronTrigger;
			} catch (ParseException e) {
				String error = "Failed to compile cronExpression "
						+ timeSchedule.getCronExpression() + " with error "
						+ e.getMessage();
				LOGGER.error(error, e);
				throw new SchedulerException(error, e);
			}
		} else {
			SimpleTrigger simpleTrigger = (scheduleName != null && scheduleName
					.length() > 0) ? new SimpleTrigger(scheduleName, groupName)
					: new SimpleTrigger();
			simpleTrigger
					.setRepeatInterval(TimeUnit.MILLISECONDS.convert(
							timeSchedule.getRepeatTime(),
							timeSchedule.getRepeatUnit()));

			if (timeSchedule.getRepeatCount() <= 0) {
				simpleTrigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
			} else {
				simpleTrigger.setRepeatCount(timeSchedule.getRepeatCount());
			}

			trigger = simpleTrigger;
		}

		if (timeSchedule.getStartTime() != null) {
			trigger.setStartTime(timeSchedule.getStartTime());
		}
		if (timeSchedule.getStartDelay() > 0) {
			long startMillis = trigger.getStartTime().getTime()
					+ TimeUnit.MILLISECONDS.convert(
							timeSchedule.getStartDelay(),
							timeSchedule.getDelayUnit());
			trigger.setStartTime(new Date(startMillis));
		}
		if (timeSchedule.getEndTime() != null) {
			trigger.setEndTime(timeSchedule.getEndTime());
		}
		if (timeSchedule.getEndDelay() > 0) {
			long endMillis = System.currentTimeMillis()
					+ TimeUnit.MILLISECONDS.convert(timeSchedule.getEndDelay(),
							timeSchedule.getDelayUnit());
			trigger.setEndTime(new Date(endMillis));
		}
		if (timeSchedule.getMisfireInstruction() >= 0) {
			trigger.setMisfireInstruction(timeSchedule.getMisfireInstruction());
		}

		return trigger;
	}

	public void setQuartzJobClass(Class<?> quartzJobClass) {
		this.quartzJobClass = quartzJobClass;
	}

	public void setQuartzConfig(String quartzConfig) {
		InputStream inputStream = null;
		try {
			inputStream = getClass().getResourceAsStream(quartzConfig);
			if (inputStream != null) {
				this.schedulerFactory.initialize(inputStream);
			} else {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("No quartz properties file [" + quartzConfig
							+ "] found, using system default properties");
				}
				this.schedulerFactory.initialize();
			}
		} catch (org.quartz.SchedulerException e) {
			handleException("Initializing quartz config [" + quartzConfig
					+ "] error with message " + e.getMessage(), e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public void setJobListener(JobListener jobListener) {
		this.jobListener = jobListener;
	}

	public void setTriggerListener(TriggerListener triggerListener) {
		this.triggerListener = triggerListener;
	}

}
