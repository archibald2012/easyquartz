package org.easycluster.easyquartz.quartz;

import java.util.HashMap;
import java.util.Map;

import org.easycluster.easyquartz.Closure;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultQuartzJob implements StatefulJob {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultQuartzJob.class);

	@SuppressWarnings("unchecked")
	public final void execute(JobExecutionContext context)
			throws JobExecutionException {

		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		QuartzEventData eventData = (QuartzEventData) jobDataMap
				.get(QuartzEventData.KEY_EVENT_DATA);

		if (eventData == null) {
			String error = "Missing quartz data " + eventData + " for job "
					+ context.getJobDetail();
			LOGGER.error(error);
			throw new JobExecutionException(error);
		}

		try {

			TimerEvent timerEvent = new TimerEvent(eventData.getScheduler());

			Map<String, Object> message = new HashMap<String, Object>();
			message.putAll(eventData.getAttributes());
			message.put("request", timerEvent);

			Closure dispatcher = (Closure) eventData.getDispatcher();
			if (dispatcher != null) {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Sending timerEvent " + message
							+ " to dispatcher " + dispatcher);
				}
				dispatcher.execute(message);
			} else {
				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("No dispatcher to relay timerEvent "
							+ timerEvent);
				}
			}
		} catch (Exception e) {
			String message = "Failed to handle Quartz job with error "
					+ e.getMessage();
			LOGGER.error(message, e);
			//throw new JobExecutionException(message, e);
		}
	}
}
