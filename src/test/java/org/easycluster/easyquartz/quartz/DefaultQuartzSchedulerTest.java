package org.easycluster.easyquartz.quartz;

import static org.junit.Assert.assertTrue;

import org.easycluster.easyquartz.dao.TriggerSchedule;
import org.easycluster.easyquartz.dao.TriggerScheduleDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/scheduler.xml" })
public class DefaultQuartzSchedulerTest extends
		AbstractJUnit4SpringContextTests {

	@Autowired
	private TriggerScheduleDao triggerScheduleDao;

	@Test
	public void testLoad() throws Exception {

		TriggerSchedule schedule = new TriggerSchedule();
		schedule.setScheduleName("自动发布");
		schedule.setProcessor("echoClosure");
		schedule.setCronExpression("0/1 * * * * ?");
		schedule.setNamespace("DefaultQuartzSchedulerTest");
		schedule.putAttribute("id", "1");

		boolean result = triggerScheduleDao.insert(schedule);
		assertTrue(result);

		TriggerSchedule schedule2 = new TriggerSchedule();
		schedule2.setScheduleName("自动发布2");
		schedule2.setProcessor("echoClosure2");
		schedule2.setCronExpression("0/1 * * * * ?");
		schedule2.setNamespace("DefaultQuartzSchedulerTest");
		schedule2.putAttribute("id", "2");

		result = triggerScheduleDao.insert(schedule2);
		assertTrue(result);

		Thread.sleep(10000L);

		TriggerSchedule schedule3 = new TriggerSchedule();
		schedule3.setScheduleName("自动发布3");
		schedule3.setProcessor("echoClosure3");
		schedule3.setCronExpression("0/1 * * * * ?");
		schedule3.setNamespace("DefaultQuartzSchedulerTest");
		schedule3.putAttribute("id", "3");

		result = triggerScheduleDao.insert(schedule3);
		assertTrue(result);

		result = triggerScheduleDao.delete(schedule.getId());
		assertTrue(result);

		Thread.sleep(10000L);

		result = triggerScheduleDao.delete(schedule2.getId());
		assertTrue(result);
		result = triggerScheduleDao.delete(schedule3.getId());
		assertTrue(result);
		
		Thread.sleep(10000L);
	}

}
