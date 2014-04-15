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
		schedule.setCronExpression("0/5 * * * * ?");
		schedule.setNamespace("DefaultQuartzSchedulerTest");
		schedule.putAttribute("id", "1");

		boolean result = triggerScheduleDao.insert(schedule);
		assertTrue(result);
		
		Thread.sleep(60000L);
		
		result =triggerScheduleDao.delete(schedule.getId());
		assertTrue(result);
	}

}
