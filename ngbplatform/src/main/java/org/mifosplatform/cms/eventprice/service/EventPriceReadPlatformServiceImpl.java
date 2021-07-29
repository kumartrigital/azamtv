/**
 * 
 */
package org.mifosplatform.cms.eventprice.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.cms.eventprice.data.ClientTypeData;
import org.mifosplatform.cms.eventprice.data.EventPriceData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * {@link Service} Class for {@link EventPricing} Read Service implements
 * {@link EventPriceWritePlatformService}
 * 
 * @author pavani
 *
 */
@Service
public class EventPriceReadPlatformServiceImpl implements EventPriceReadPlatformService {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public EventPriceReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<EventPriceData> retrieventPriceData(final Long eventId) {
		try {
			final EventPricingMapper eventPricingMapper = new EventPricingMapper();
			final String sql = "SELECT " + eventPricingMapper.eventPricingSchema() + " where event_id = ? ";
			return jdbcTemplate.query(sql, eventPricingMapper, new Object[] { eventId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public EventPriceData retrieventPriceDetails(final Long eventPriceId) {
		try {
			final EventPricingMapper eventPricingMapper = new EventPricingMapper();
			final String sql = "SELECT " + eventPricingMapper.eventPricingSchema()
					+ " where ep.id = ? and is_deleted = 'n'";
			return jdbcTemplate.queryForObject(sql, eventPricingMapper, new Object[] { eventPriceId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class EventPricingMapper implements RowMapper<EventPriceData> {

		public String eventPricingSchema() {
			return " ep.id AS id,em.id AS eventId,em.event_name AS eventName,ep.format_type AS formatType,ep.opt_type AS optType, ep.client_typeid as clientTypeId,"
					+ " d.discount_description AS discount,ep.price AS price,mc.code_value as clientType,ep.currencyId as currencyId,ep.discount_id as discountId,c.code as currencyCode "
					+ " from b_mod_pricing ep " + " inner join m_code_value mc on  ep.client_typeid=mc.id"
					+ "  inner join b_discount_master d ON d.id = ep.discount_id "
					+ " inner join b_mod_master em ON em.id = ep.event_id join m_currency c on ep.currencyId=c.id ";
		}

		@Override
		public EventPriceData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final Long eventId = resultSet.getLong("eventId");
			final String eventName = resultSet.getString("eventName");
			final String formatType = resultSet.getString("formatType");
			final String optType = resultSet.getString("optType");
			final Long clientTypeId = resultSet.getLong("clientTypeId");
			final Long discountId = resultSet.getLong("discountId");
			final String discount = resultSet.getString("discount");
			final BigDecimal price = resultSet.getBigDecimal("price");
			final String clientType = resultSet.getString("clientType");
			final Long currencyId = resultSet.getLong("currencyId");
			final String currencyCode = resultSet.getString("currencyCode");

			return new EventPriceData(id, eventName, formatType, optType, clientTypeId, discount, price, eventId,
					clientType, discountId, currencyId, currencyCode);
		}

	}

	@Override
	public EventPriceData retriveMoviePriceDetails(final Long eventId) {
		try {
			final MoviePricingMapper eventPricingMapper = new MoviePricingMapper();
			final String sql = "SELECT " + eventPricingMapper.eventPricingSchema() + "  AND ma.is_deleted = 'n'";
			return jdbcTemplate.queryForObject(sql, eventPricingMapper, new Object[] { eventId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class MoviePricingMapper implements RowMapper<EventPriceData> {

		public String eventPricingSchema() {
			return " ep.id AS id,ep.event_id AS eventId,ep.format_type AS formatType,ep.opt_type AS optType, ep.client_typeid as clientTypeId,"
					+ " ep.price AS price,ep.currencyId as currencyId,ep.discount_id as discountId  from b_mod_pricing ep,b_mod_master mm,b_media_asset ma "
					+ " where ma.title=mm.event_description AND mm.id=ep.event_id AND  ma.id = ? ";
		}

		@Override
		public EventPriceData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final Long eventId = resultSet.getLong("eventId");
			// final String eventName = resultSet.getString("eventName");
			final String formatType = resultSet.getString("formatType");
			final String optType = resultSet.getString("optType");
			final Long clientTypeId = resultSet.getLong("clientTypeId");
			final Long discountId = resultSet.getLong("discountId");
			final BigDecimal price = resultSet.getBigDecimal("price");
			// final String clientType = resultSet.getString("clientType");
			final Long currencyId = resultSet.getLong("currencyId");
			// final String currencyCode = resultSet.getString("currencyCode");

			return new EventPriceData(id, null, formatType, optType, clientTypeId, null, price, eventId, null,
					discountId, currencyId, null);
		}

	}

	@Override
	public List<ClientTypeData> clientType() {
		try {
			final ClientTypeMapper clientTypeMapper = new ClientTypeMapper();
			final String sql = "SELECT " + clientTypeMapper.clientTypeSchema()
					+ " where mc.code_name='Client Category'";
			return jdbcTemplate.query(sql, clientTypeMapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientTypeMapper implements RowMapper<ClientTypeData> {

		public String clientTypeSchema() {
			return " mcv.id as id , mcv.code_value as type from m_code_value mcv "
					+ " inner join m_code mc on mc.id = mcv.code_id ";
		}

		@Override
		public ClientTypeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String type = resultSet.getString("type");
			return new ClientTypeData(id, type);
		}

	}

	public Double findMoviePricingByMovieCode(String overview) {
		try {
			final GetMoviePricingMapper getMoviePricingMapper = new GetMoviePricingMapper();
			final String sql = "SELECT " + getMoviePricingMapper.eventPricingSchema() + "  AND ma.is_deleted = 'n'";
			return jdbcTemplate.queryForObject(sql, getMoviePricingMapper, new Object[] { overview });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class GetMoviePricingMapper implements RowMapper<Double> {

		public String eventPricingSchema() {
			return "mp.price as price  from  b_media_asset ma , b_mod_master mm, b_mod_pricing mp \n"
					+ "where  ma.title = mm.event_description AND mm.id = mp.event_id AND overview = ?";
		}

		@Override
		public Double mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Double moviePrice = resultSet.getDouble("price");
			return moviePrice;
		}

	}

}
