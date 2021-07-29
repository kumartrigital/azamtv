package org.mifosplatform.collectionbatch.usageratequantitytier.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.collectionbatch.ratableusagemetric.data.RatableUsageMetricData;
import org.mifosplatform.collectionbatch.usageratequantitytier.data.UsageRateQuantityTierData;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class UsageRateQuantityTierReadPlatformServiceImpl implements UsageRateQuantityTierReadPlatformService{
	
	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<UsageRateQuantityTierData> paginationHelper = new PaginationHelper<UsageRateQuantityTierData>();
	
	@Autowired
	public UsageRateQuantityTierReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<UsageRateQuantityTierData> retrieveUsageRateQuantityTierForDropdown() {
		try{
			QuantityTierMapper quantityTierMapper = new QuantityTierMapper();
			String sql = "SELECT distinct "+quantityTierMapper.schema();
			return jdbcTemplate.query(sql, quantityTierMapper,new Object[]{});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}
		class QuantityTierMapper implements RowMapper<UsageRateQuantityTierData> {
		    @Override
			public UsageRateQuantityTierData mapRow(ResultSet rs, int rowNum) throws SQLException {
				
		    	final Long planPriceId = rs.getLong("planPriceId");
		    	final Long rumId = rs.getLong("rumId");
		    	final Long timeModelId = rs.getLong("timeModelId");
		    	final String rumName = rs.getString("rumName");
		    	final String timemodelName = rs.getString("timemodelName");
			
			   
				
				return new UsageRateQuantityTierData(planPriceId, rumId, timeModelId,rumName,timemodelName);
			}
		    
			public String schema() {
				
				return " rp.planprice_id AS planPriceId, rp.rum_id AS rumId, rp.timemodel_id AS timeModelId, rm.rum_name AS rumName, tm.timemodel_name AS timemodelName from r_usage_rateplan rp join r_rum rm join r_timemodel tm  where rm.id=rp.rum_id and tm.id=rp.timemodel_id ";
				
			}
		}
		
		@Override
		public List<UsageRateQuantityTierData> retrieveTierForDropdown() {
			try{
				QuantityTierMapper1 quantityTierMapper1 = new QuantityTierMapper1();
				String sql = "SELECT distinct "+quantityTierMapper1.schema();
				return jdbcTemplate.query(sql, quantityTierMapper1,new Object[]{});
				}catch(EmptyResultDataAccessException ex){
					return null;
				}
		}
			class QuantityTierMapper1 implements RowMapper<UsageRateQuantityTierData> {
			    @Override
				public UsageRateQuantityTierData mapRow(ResultSet rs, int rowNum) throws SQLException {
					
			    	final Long tierId = rs.getLong("tierId");
			    	final String tierName = rs.getString("tierName");
				
				   
					
					return new UsageRateQuantityTierData(tierId, tierName);
				}
			    
				public String schema() {
					
					return " rt.id AS tierId, rt.tier_name AS tierName from r_usagerate_qty_tier rt ";
					
				}
			}
		
		
		
		
   }
