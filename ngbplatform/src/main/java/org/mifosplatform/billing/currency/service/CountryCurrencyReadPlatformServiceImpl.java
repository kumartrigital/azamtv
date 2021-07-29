package org.mifosplatform.billing.currency.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.currency.data.CountryCurrencyData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 * 
 */
@Service
public class CountryCurrencyReadPlatformServiceImpl implements CountryCurrencyReadPlatformService {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public CountryCurrencyReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {

		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #getCountryCurrencyDetailsByName(java.lang.String)
	 */
	@Override
	public List<CountryCurrencyData> getCountryCurrencyDetailsByName(final String country) {

		try {
			final CurrencyMapper mapper = new CurrencyMapper();
			final String sql = "select " + mapper.schema() + " WHERE country = ? and  c.is_deleted='N' ";
			return this.jdbcTemplate.query(sql, mapper,new Object[] { country });
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}

	private static final class CurrencyMapper implements
			RowMapper<CountryCurrencyData> {

		public String schema() {
			return " c.id as id,c.currency as currency,c.status as status,c.base_currency as baseCurrency, "
				+  " c.conversion_rate as conversionRate,c.valid_from as validFrom,c.valid_to as validTo,mc.code AS currencyCode,(select code from m_currency cm where cm.id = c.base_currency) as baseCurrencyCode FROM b_currency_exchange c join m_currency mc on  mc.id = c.currency";

		}

		@Override
		public CountryCurrencyData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			//final String country = rs.getString("country");
			final String currency = rs.getString("currency");
			final String status = rs.getString("status");
			final String baseCurrency = rs.getString("baseCurrency");
			final BigDecimal conversionRate = rs.getBigDecimal("conversionRate");
			//final String countryISD = rs.getString("countryISD");
			final LocalDate validFrom=JdbcSupport.getLocalDate(rs,"validFrom");
			final LocalDate validTo=JdbcSupport.getLocalDate(rs,"validTo");
			final String currencyCode=rs.getString("currencyCode");
			final String baseCurrencyCode=rs.getString("baseCurrencyCode");

			return new CountryCurrencyData(id,currency, baseCurrency,conversionRate, status, validFrom,validTo,currencyCode,baseCurrencyCode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #retrieveAllCurrencyConfigurationDetails()
	 */
	@Override
	public Collection<CountryCurrencyData> retrieveAllCurrencyConfigurationDetails() {

		try {

			final CurrencyMapper mapper = new CurrencyMapper();
			final String sql = "select " + mapper.schema() + " WHERE  c.is_deleted='N' ";
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #retrieveSingleCurrencyConfigurationDetails(java.lang.Long)
	 */
	@Override
	public CountryCurrencyData retrieveSingleCurrencyConfigurationDetails(final Long currencyId) {
		try {
			final CurrencyMapper mapper = new CurrencyMapper();
			final String sql = "select " + mapper.schema() + " WHERE  c.is_deleted='N' and c.id=?";
			return this.jdbcTemplate.queryForObject(sql, mapper,new Object[] { currencyId });
		} catch (EmptyResultDataAccessException exception) {
			return null;
		}
	}

}
