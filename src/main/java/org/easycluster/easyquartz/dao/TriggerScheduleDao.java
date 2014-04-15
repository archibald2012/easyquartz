package org.easycluster.easyquartz.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractInterruptibleBatchPreparedStatementSetter;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class TriggerScheduleDao {

	private final String QUERY_ALL_SCHEDULE = "select id,processor,group_name,schedule_name,cron_expression,start_time,end_time,start_delay,end_delay,delay_unit,repeat_count,repeat_time,repeat_unit,misfire_instruction,namespace,gmt_create,gmt_modified,lock_instance,master_time,updated_at from trigger_schedule where updated_at is null or updated_at < date_sub(now(), interval 1 minute)";

	private final String QUERY_SCHEDULE_BY_NAMESPACE = "select id,processor,group_name,schedule_name,cron_expression,start_time,end_time,start_delay,end_delay,delay_unit,repeat_count,repeat_time,repeat_unit,misfire_instruction,namespace,gmt_create,gmt_modified,lock_instance,master_time,updated_at from trigger_schedule where namespace = ? and (updated_at is null or updated_at < date_sub(now(), interval 1 minute))";

	private final String QUERY_SCHEDULE_BY_ID = "select id,processor,group_name,schedule_name,cron_expression,start_time,end_time,start_delay,end_delay,delay_unit,repeat_count,repeat_time,repeat_unit,misfire_instruction,namespace,gmt_create,gmt_modified,lock_instance,master_time,updated_at from trigger_schedule where id = ?";

	private final String QUERY_SCHEDULE_ATTRIBUTE = "select schedule_id, name, value from trigger_schedule_attribute where schedule_id = ?";

	private final String UPDATE_NEW_LOCK = "update trigger_schedule set lock_instance = ?, master_time = now(), updated_at = now() where id = ? and gmt_modified = ?";

	private final String UPDATE_CHECK_LOCK = "update trigger_schedule set lock_instance = ?, updated_at = now() where id = ? and gmt_modified = ?";

	private final String DELETE_TRIGGER_SCHEDULE = "delete from trigger_schedule where id = ?";

	private final String DELETE_TRIGGER_SCHEDULE_ATTR = "delete from trigger_schedule_attribute where schedule_id = ?";

	private static final String INSERT_TRIGGER_SCHEDULE = "insert into trigger_schedule (processor,group_name,schedule_name,cron_expression,start_time,end_time,start_delay,end_delay,delay_unit,repeat_count,repeat_time,repeat_unit,misfire_instruction,namespace,lock_instance,master_time,updated_at,gmt_create,gmt_modified) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

	private static final String INSERT_TRIGGER_SCHEDULE_ATTR = "insert into trigger_schedule_attribute (schedule_id, name, value) VALUES (?, ?, ?)";

	private JdbcTemplate jdbcTemplate;

	private RowMapper triggerScheduleRowMapper = new RowMapper() {

		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			final TriggerSchedule schedule = new TriggerSchedule();
			schedule.setId(rs.getInt("id"));
			schedule.setProcessor(rs.getString("processor"));
			schedule.setGroupName(rs.getString("group_name"));
			schedule.setScheduleName(rs.getString("schedule_name"));
			schedule.setCronExpression(rs.getString("cron_expression"));
			schedule.setStartTime(rs.getDate("start_time"));
			schedule.setEndTime(rs.getDate("end_time"));
			schedule.setStartDelay(rs.getInt("start_delay"));
			schedule.setEndDelay(rs.getInt("end_delay"));
			schedule.setDelayUnit(rs.getString("delay_unit"));
			schedule.setRepeatTime(rs.getInt("repeat_time"));
			schedule.setRepeatUnit(rs.getString("repeat_unit"));
			schedule.setRepeatCount(rs.getInt("repeat_count"));
			schedule.setMisfireInstruction(rs.getInt("misfire_instruction"));
			schedule.setNamespace(rs.getString("namespace"));
			schedule.setLockInstance(rs.getString("lock_instance"));
			schedule.setMasterTime(toDate(rs.getTimestamp("master_time")));
			schedule.setUpdatedAt(toDate(rs.getTimestamp("updated_at")));
			schedule.setGmtCreate(toDate(rs.getTimestamp("gmt_create")));
			schedule.setGmtModified(toDate(rs.getTimestamp("gmt_modified")));

			jdbcTemplate.query(QUERY_SCHEDULE_ATTRIBUTE,
					new Object[] { schedule.getId() }, new RowMapper() {

						@Override
						public Object mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							schedule.putAttribute(rs.getString("NAME"),
									rs.getString("VALUE"));
							return null;
						}
					});
			return schedule;
		}
	};

	@SuppressWarnings("unchecked")
	public List<TriggerSchedule> loadAll() {
		return jdbcTemplate.query(QUERY_ALL_SCHEDULE, triggerScheduleRowMapper);
	}

	@SuppressWarnings("unchecked")
	public List<TriggerSchedule> queryUnscheduled(String namespace) {
		if (namespace == null || namespace.length() == 0) {
			return loadAll();
		} else {
			return jdbcTemplate.query(QUERY_SCHEDULE_BY_NAMESPACE,
					new Object[] { namespace }, triggerScheduleRowMapper);
		}
	}

	public TriggerSchedule load(int id) {
		return (TriggerSchedule) jdbcTemplate.queryForObject(
				QUERY_SCHEDULE_BY_ID, new Object[] { id },
				triggerScheduleRowMapper);
	}

	public boolean insert(final TriggerSchedule schedule) {

		KeyHolder keyHolder = new GeneratedKeyHolder();

		int rowAffected = jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				PreparedStatement ps = conn.prepareStatement(
						INSERT_TRIGGER_SCHEDULE,
						Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, schedule.getProcessor());
				ps.setString(2, schedule.getGroupName());
				ps.setString(3, schedule.getScheduleName());
				ps.setString(4, schedule.getCronExpression());
				ps.setTimestamp(5, toTimestamp(schedule.getStartTime()));
				ps.setTimestamp(6, toTimestamp(schedule.getEndTime()));
				ps.setInt(7, schedule.getStartDelay());
				ps.setInt(8, schedule.getEndDelay());
				ps.setString(9, schedule.getDelayUnit());
				ps.setInt(10, schedule.getRepeatCount());
				ps.setInt(11, schedule.getRepeatTime());
				ps.setString(12, schedule.getRepeatUnit());
				ps.setInt(13, schedule.getMisfireInstruction());
				ps.setString(14, schedule.getNamespace());
				ps.setString(15, schedule.getLockInstance());
				ps.setTimestamp(16, toTimestamp(schedule.getMasterTime()));
				ps.setTimestamp(17, toTimestamp(schedule.getUpdatedAt()));
				return ps;
			}
		}, keyHolder);

		if (rowAffected != 0) {

			final int scheduleId = keyHolder.getKey().intValue();
			schedule.setId(scheduleId);

			final Map<String, String> attributes = schedule.getAttributes();
			final List<NameValue> pairs = new ArrayList<NameValue>();
			for (Map.Entry<String, String> e : attributes.entrySet()) {
				pairs.add(new NameValue(e.getKey(), e.getValue()));
			}

			if (!pairs.isEmpty()) {
				jdbcTemplate
						.batchUpdate(
								INSERT_TRIGGER_SCHEDULE_ATTR,
								new AbstractInterruptibleBatchPreparedStatementSetter() {

									@Override
									public boolean setValuesIfAvailable(
											PreparedStatement ps, int i)
											throws SQLException {
										if (i >= pairs.size()) {
											return false;
										}

										NameValue pair = pairs.get(i);
										ps.setInt(1, scheduleId);
										ps.setString(2, pair.getName());
										ps.setString(3, pair.getValue());
										return true;
									}

									@Override
									public int getBatchSize() {
										return pairs.size();
									}

								});
			}
		}

		return rowAffected == 1;
	}

	public boolean acquireLock(int id, Date gmtModified, String lockInstance) {
		int rowAffected = jdbcTemplate.update(UPDATE_NEW_LOCK, new Object[] {
				lockInstance, id, gmtModified });
		return rowAffected == 1;
	}

	public boolean updateLockTime(int id, Date gmtModified, String lockInstance) {
		int rowAffected = jdbcTemplate.update(UPDATE_CHECK_LOCK, new Object[] {
				lockInstance, id, gmtModified });
		return rowAffected == 1;
	}

	public boolean delete(int id) {

		jdbcTemplate.update(DELETE_TRIGGER_SCHEDULE_ATTR, new Object[] { id });

		int rowAffected = jdbcTemplate.update(DELETE_TRIGGER_SCHEDULE,
				new Object[] { id });

		return rowAffected == 1;
	}

	private Timestamp toTimestamp(Date date) {
		return (date == null) ? null : new Timestamp(date.getTime());
	}

	private Date toDate(Timestamp timestamp) {
		return (timestamp == null) ? null : new Date(timestamp.getTime());
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	class NameValue implements Serializable {

		private static final long serialVersionUID = 1L;

		private String name;

		private String value;

		public NameValue() {
		}

		public NameValue(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

	}
}
