/**
 * 
 */
package org.mifosplatform.cms.eventmaster.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.cms.eventmaster.data.EventDetailsData;
import org.mifosplatform.cms.eventmaster.data.EventMasterData;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.eventmaster.domain.OptType;
import org.mifosplatform.cms.eventmaster.domain.OptTypeEnum;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


/**{@link Service} Class for {@link EventMaster} Read Service
 *implements {@link EventMasterReadPlatformService}
 * @author pavani
 * @author Rakesh
 */
@Service
public class EventMasterReadPlatformServiceImpl implements
		EventMasterReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public EventMasterReadPlatformServiceImpl (final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<EnumOptionData> retrieveOptTypeData() {
		final EnumOptionData rent = OptTypeEnum.optType(OptType.RENT);
		final EnumOptionData own = OptTypeEnum.optType(OptType.OWN);
		final List<EnumOptionData> optType = Arrays.asList(rent,own);
		return optType;
	}

	@Override
	public List<EnumOptionData> retrieveNewStatus() {
		final EnumOptionData active = OrderStatusEnumaration
				.OrderStatusType(StatusTypeEnum.ACTIVE);
		final EnumOptionData inactive = OrderStatusEnumaration
				.OrderStatusType(StatusTypeEnum.INACTIVE);
		final List<EnumOptionData> categotyType = Arrays.asList(active, inactive);
		return categotyType;

	}
	
	@Override
	public List<EventMasterData> retrieveEventMasterDataForEventOrders() {
		try {
			final EventMasterMapper eventMasterMapper = new EventMasterMapper();
			final String sql = "SELECT " + eventMasterMapper.eventMasterSchema() + " where  evnt.event_end_date > now() or evnt.event_end_date is null";
			return jdbcTemplate.query(sql, eventMasterMapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public List<EventMasterData> retrieveEventMasterData() {
		try {
			final EventMasterMapper eventMasterMapper = new EventMasterMapper();
			final String sql = "SELECT " + eventMasterMapper.eventMasterSchema() + " where evnt.event_end_date > now() or evnt.event_end_date is null";
			return jdbcTemplate.query(sql, eventMasterMapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class EventMasterMapper implements RowMapper<EventMasterData> {
		public String eventMasterSchema() {
			return " evnt.id as id, evnt.event_name as eventName, evnt.event_description as eventDescription, evnt.status as status, evnt.created_date as createdDate, "
						+ "evnt.event_category as eventCategory from b_mod_master evnt ";
		}
		@Override
		public EventMasterData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			
			final Long id = resultSet.getLong("id");
			final String eventName = resultSet.getString("eventName");
			final String eventDescription = resultSet.getString("eventDescription");
			final Integer statusId = resultSet.getInt("status");
			final String status = OrderStatusEnumaration.OrderStatusType(statusId).getValue();
			final LocalDate createdDate = JdbcSupport.getLocalDate(resultSet, "createdDate");
			final String eventCategory = resultSet.getString("eventCategory");
			return new EventMasterData(id, eventName, eventDescription, status, null, createdDate, eventCategory);
		}
	}

	@Override
	public EventMasterData retrieveEventMasterDetails(final Integer eventId){
		final String sql = " select evnt.id as id, evnt.event_name as eventName, evnt.event_description as eventDescription, evnt.status as status, evnt.event_start_date as eventStartDate, " +
						" evnt.event_end_date as eventEndDate, evnt.event_validity as eventValidity, charge_code as chargeCode, evnt.event_category as eventCategory "
						+ " from b_mod_master evnt "
						+ "where evnt.id='"+eventId+"'";
		RowMapper<EventMasterData> rowMap = new EventMapper();
		return this.jdbcTemplate.queryForObject(sql, rowMap, new Object[]{});
	}
	
	private static final class EventMapper implements RowMapper<EventMasterData> {
		@Override
		public EventMasterData mapRow(final ResultSet resultSet,final int rowNum) throws SQLException {
			final Long id = resultSet.getLong("id");
			final String eventName = resultSet.getString("eventName");
			final String eventDescription = resultSet.getString("eventDescription");
			final Date eventStartDate = resultSet.getTimestamp("eventStartDate");
			final Date eventEndDate = resultSet.getTimestamp("eventEndDate");
			final LocalDate eventValidity = JdbcSupport.getLocalDate(resultSet, "eventValidity");
			final Long status = resultSet.getLong("status");
			final String chargeData = resultSet.getString("chargeCode"); 
			final String eventCategory = resultSet.getString("eventCategory");
			return new EventMasterData(id, eventName, eventDescription, status, null, eventStartDate, eventEndDate, eventValidity, chargeData, eventCategory);
		}
	}
	
	@Override
	public List<EventDetailsData> retrieveEventDetailsData(final Integer eventId) {
		final String sql = "Select ed.id as id, ed.event_id as eventId, ed.media_id as mediaId, m.title as title "
					  +" from b_mod_detail ed, b_media_asset m where ed.media_id=m.id and event_id = ?";
		
		final RowMapper<EventDetailsData> rowMap = new EventDetailsMapper();
		
		return this.jdbcTemplate.query(sql, rowMap, new Object[]{eventId});
	}
	
	@Override
	public EventDetailsData retrieveEventDetails(final Integer eventId) {
		final String sql = "Select id as id, event_id as eventId, media_id as mediaId," 
				 + " event_start_date as eventStartDate, event_end_date as eventEndDate " 
				 + " from b_mod_detail where event_id = ?";
		final RowMapper<EventDetailsData> rowMap = new EventDetailsMapper();
			
		return this.jdbcTemplate.queryForObject(sql, rowMap, new Object[] {eventId});
 	}
	private static final class EventDetailsMapper implements RowMapper<EventDetailsData> {
		@Override
		public EventDetailsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long id = resultSet.getLong("id");
			final Integer eventId = resultSet.getInt("eventId");
			final Long mediaId = resultSet.getLong("mediaId");
			final String mediaTitle = resultSet.getString("title");
			return new EventDetailsData(id, eventId, mediaId, mediaTitle);
		}
	}
}	
