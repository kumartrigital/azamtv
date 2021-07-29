package org.mifosplatform.billing.taxmapping.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.data.ChargeCodeData;
import org.mifosplatform.billing.taxmapping.data.TaxMapData;
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
public class TaxMapReadPlatformServiceImp implements TaxMapReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public TaxMapReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);

	}

	/* (non-Javadoc)
	 * @see #retriveTaxMapData(java.lang.String)
	 * based on charge code
	 */
	@Override
	public List<TaxMapData> retriveTaxMapData(final String chargeCode) {

		final TaxMapDataMapper mapper = new TaxMapDataMapper();
		final String sql = "Select " + mapper.schema()+ " and tmr.charge_code = ? and tmr.is_deleted = 'N'";
		return jdbcTemplate.query(sql,mapper,new Object[]{chargeCode});
	}

	/* (non-Javadoc)
	 * @see #retrievedSingleTaxMapData(java.lang.Long)
	 */
	@Override
	public TaxMapData retrievedSingleTaxMapData(final Long id) {
		
		try{
			
        final TaxMapDataMapper mapper = new TaxMapDataMapper();
		final String sql = "Select " + mapper.schema()+ " and tmr.id = ?";
		return jdbcTemplate.queryForObject(sql, mapper, new Object[] { id });
		
		}catch (EmptyResultDataAccessException accessException) {
			return null;
		}
		
	}

	private class TaxMapDataMapper implements RowMapper<TaxMapData> {
		
		
		public String schema(){
			
			return "tmr.id AS id,tmr.charge_code AS chargeCode,tmr.tax_code AS taxCode,tmr.start_date AS startDate,tmr.type as taxType,"
				+ "tmr.rate AS rate,tmr.tax_region_id as taxRegionId, pr.priceregion_name as region,tmr.is_deleted  FROM b_tax_mapping_rate tmr,"
				+ "b_priceregion_master pr  where tmr.tax_region_id=pr.id";
		}

		@Override
		public TaxMapData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final String chargeCode = rs.getString("chargeCode");
			final String taxCode = rs.getString("taxCode");
			final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
			final String taxType = rs.getString("taxType");
			final BigDecimal rate = rs.getBigDecimal("rate");
			final String region = rs.getString("region");
			final Long taxRegionId = rs.getLong("taxRegionId");
			return new TaxMapData(id, chargeCode, taxCode, startDate, taxType,
					              rate, region, taxRegionId);
		}

	}
	
	/* (non-Javadoc)
	 * @see #retrivedChargeCodeTemplateData()
	 */
	@Override
	public List<ChargeCodeData> retrivedChargeCodeTemplateData() {

		final TaxMapper rowMapper = new TaxMapper();
		final String sql = "select cc.charge_code as chargeCode, cc.charge_description as chargeDescription from b_charge_codes cc";
		return jdbcTemplate.query(sql, rowMapper);
	}

	private class TaxMapper implements RowMapper<ChargeCodeData> {
		@Override
		public ChargeCodeData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final String chargeCode = rs.getString("chargeCode");
			final String chargeDescription = rs.getString("chargeDescription");
			return new ChargeCodeData(chargeCode, chargeDescription);
		}
	}
	
	@Override
	public List<TaxMapData> retriveTaxMapingData() {
	
		try{
			
	        final TaxDataMapper mapper = new TaxDataMapper();
			final String sql = "Select " + mapper.schema();
			return jdbcTemplate.query(sql, mapper);
			}catch (EmptyResultDataAccessException accessException) {
				return null;
			}	
	}

	private class TaxDataMapper implements RowMapper<TaxMapData> {
		
		public String schema(){
			
			return  "tmr.tax_code AS taxCode FROM b_tax_mapping_rate tmr"
					+ " where tmr.is_deleted='N'";	
		}

		@Override
		public TaxMapData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final String taxCode = rs.getString("taxCode");
			return new TaxMapData(taxCode);
		}
	}
}


