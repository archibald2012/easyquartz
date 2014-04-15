package org.easycluster.easyquartz.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.easycluster.easyquartz.util.SystemUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/dao.xml" })
public class TriggerScheduleDaoTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private TriggerScheduleDao triggerScheduleDao;

	@Test
	public void testLoad() {

		TriggerSchedule schedule = new TriggerSchedule();
		schedule.setScheduleName("自动发布");
		schedule.setProcessor("processor");
		schedule.setCronExpression("0 0/5 * * * ?");
		schedule.setNamespace("prod1");
		schedule.putAttribute("appId", "1");

		boolean result = triggerScheduleDao.insert(schedule);
		assertTrue(result);

		schedule = triggerScheduleDao.load(schedule.getId());
		assertNotNull(schedule);

		List<TriggerSchedule> schedules = triggerScheduleDao.loadAll();
		assertTrue(schedules.size() > 0);

		schedules = triggerScheduleDao.queryUnscheduled("prod1");
		assertTrue(schedules.size() > 0);

		schedules = triggerScheduleDao.queryUnscheduled("prod2");
		assertEquals(0, schedules.size());

		String lockInstance = SystemUtil.getHostName();
		result = triggerScheduleDao.acquireLock(schedule.getId(),
				schedule.getGmtModified(), lockInstance);
		assertTrue(result);

		schedule = triggerScheduleDao.load(schedule.getId());
		result = triggerScheduleDao.updateLockTime(schedule.getId(),
				schedule.getGmtModified(), lockInstance);
		assertTrue(result);

		result = triggerScheduleDao.delete(schedule.getId());
		assertTrue(result);
	}

}
