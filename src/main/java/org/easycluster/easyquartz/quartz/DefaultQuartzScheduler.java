package org.easycluster.easyquartz.quartz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.easycluster.easyquartz.Closure;
import org.easycluster.easyquartz.TimeSchedule;
import org.easycluster.easyquartz.dao.TriggerSchedule;
import org.easycluster.easyquartz.dao.TriggerScheduleDao;
import org.easycluster.easyquartz.exception.SchedulerException;
import org.easycluster.easyquartz.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class DefaultQuartzScheduler extends AbstractQuartzScheduler implements
		BeanFactoryAware {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultQuartzScheduler.class);

	private static final long NANOS_IN_MILLIS = 1000000L;

	private TriggerScheduleDao triggerScheduleDao;
	private BeanFactory beanFactory;
	private String namespace;

	private long refreshDelay = 5;
	private ScheduledExecutorService updateChecker = Executors
			.newSingleThreadScheduledExecutor();
	private Map<String, TriggerSchedule> scheduled = new HashMap<String, TriggerSchedule>();
	private String lockInstance = SystemUtil.getHostName() + ":"
			+ SystemUtil.getPid();

	public void start() {
		long startNano = System.nanoTime();

		scheduleJob();

		updateChecker.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					scheduleJob();
				} catch (Throwable t) {
					LOGGER.error("", t);
				}
			}
		}, refreshDelay, refreshDelay, TimeUnit.SECONDS);

		startScheduler();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(this + " is started in time "
					+ (System.nanoTime() - startNano) / NANOS_IN_MILLIS);
		}
	}

	public void stop() {
		shutdownScheduler();
	}

	private void scheduleJob() {

		List<TriggerSchedule> triggerSchedules = triggerScheduleDao
				.queryUnscheduled(namespace);
		Map<String, TriggerSchedule> newJobs = new HashMap<String, TriggerSchedule>();
		for (TriggerSchedule triggerSchedule : triggerSchedules) {
			String key = triggerSchedule.getGroupName() + "-"
					+ triggerSchedule.getScheduleName();
			newJobs.put(key, triggerSchedule);
		}

		// unschedule removed/updated ones.
		List<TriggerSchedule> needRemove = new ArrayList<TriggerSchedule>();
		for (TriggerSchedule snapshot : scheduled.values()) {
			String key = snapshot.getGroupName() + "-"
					+ snapshot.getScheduleName();
			if (!newJobs.containsKey(key)) {
				needRemove.add(snapshot);
			}
		}
		for (TriggerSchedule triggerSchedule : needRemove) {
			unscheduleJob(triggerSchedule.getScheduleName(),
					triggerSchedule.getGroupName());
			scheduled.remove(triggerSchedule.getGroupName() + "-"
					+ triggerSchedule.getScheduleName());
		}

		for (TriggerSchedule triggerSchedule : newJobs.values()) {

			String processorName = triggerSchedule.getProcessor();
			Closure processor = (Closure) beanFactory.getBean(processorName);
			if (processor == null) {
				throw new SchedulerException("processor [" + processorName
						+ "] not defined in the context.");
			}

			String key = triggerSchedule.getGroupName() + "-"
					+ triggerSchedule.getScheduleName();

			TriggerSchedule old = scheduled.get(key);
			if (old != null) {
				if (!old.equals(triggerSchedule)) {
					triggerScheduleDao.updateLockTime(triggerSchedule.getId(),
							triggerSchedule.getGmtModified(), lockInstance);
					TimeSchedule timeSchedule = transform(triggerSchedule);
					scheduleJob(timeSchedule, processor);
					scheduled.put(key, triggerSchedule);
				} else {
					// do nothing
				}
			} else {
				boolean acquired = triggerScheduleDao.acquireLock(
						triggerSchedule.getId(),
						triggerSchedule.getGmtModified(), lockInstance);
				if (acquired) {
					TimeSchedule timeSchedule = transform(triggerSchedule);
					scheduleJob(timeSchedule, processor);
					scheduled.put(key, triggerSchedule);
				} else {
					// do nothing
				}
			}
		}

	}

	private TimeSchedule transform(TriggerSchedule triggerSchedule) {
		TimeSchedule timeSchedule = new TimeSchedule();
		timeSchedule.setGroupName(triggerSchedule.getGroupName());
		timeSchedule.setScheduleName(triggerSchedule.getScheduleName());
		if (triggerSchedule.getCronExpression() != null) {
			timeSchedule.setCronExpression(triggerSchedule.getCronExpression());
		}
		if (triggerSchedule.getStartTime() != null) {
			timeSchedule.setStartTime(triggerSchedule.getStartTime());
		}
		if (triggerSchedule.getEndTime() != null) {
			timeSchedule.setEndTime(triggerSchedule.getEndTime());
		}
		timeSchedule.setStartDelay(triggerSchedule.getStartDelay());

		timeSchedule.setEndDelay(triggerSchedule.getEndDelay());

		if (triggerSchedule.getDelayUnit() != null) {
			timeSchedule.setDelayUnit(TimeUnit.valueOf(triggerSchedule
					.getDelayUnit()));
		}
		timeSchedule.setRepeatTime(triggerSchedule.getRepeatTime());

		if (triggerSchedule.getRepeatUnit() != null) {
			timeSchedule.setRepeatUnit(TimeUnit.valueOf(triggerSchedule
					.getRepeatUnit()));
		}
		timeSchedule.setRepeatCount(triggerSchedule.getRepeatCount());

		timeSchedule.setMisfireInstruction(triggerSchedule
				.getMisfireInstruction());

		timeSchedule.setAttributes(triggerSchedule.getAttributes());
		return timeSchedule;
	}

	public void setTriggerScheduleDao(TriggerScheduleDao triggerScheduleDao) {
		this.triggerScheduleDao = triggerScheduleDao;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setLockInstance(String lockInstance) {
		this.lockInstance = lockInstance;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
