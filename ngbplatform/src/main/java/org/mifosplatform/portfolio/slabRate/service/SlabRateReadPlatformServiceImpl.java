package org.mifosplatform.portfolio.slabRate.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.slabRate.data.SlabRateData;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SlabRateReadPlatformServiceImpl implements SlabRateReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<SlabRateData> paginationHelper = new PaginationHelper<SlabRateData>();
	private final PlatformSecurityContext context;
	
	@Autowired
	public SlabRateReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	//this is to retrieve all SlabRate
	@Override
	public List<SlabRateData> retrieveSlabRates() {
		try{
		SlabRateMapper slabRateMapper = new SlabRateMapper();
		String sql = "SELECT "+slabRateMapper.schema();
		return jdbcTemplate.query(sql, slabRateMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
		
	}
	
	private class SlabRateMapper implements RowMapper<SlabRateData> {
	    @Override
		public SlabRateData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long SlabId = rs.getLong("SlabId");
			final String SlabFrom  = rs.getString("SlabFrom");
			final String slabTo = rs.getString("slabTo");
			final Float  Rate = rs.getFloat("Rate");
			final Boolean Isdeleted = rs.getBoolean("Isdeleted");
			return new SlabRateData(SlabId, SlabFrom, slabTo,Rate, Isdeleted);
		}
	    
		public String schema() {
			
			return " id as SlabId,slab_from as SlabFrom,slab_to as slabTo,slab_rate as Rate,is_deleted as Isdeleted from b_NFC_slab_rate ";
			
		}
	}

	@Override
	public List<SlabRateData> retrieveSlabRatesbyId(String slabFrom, String slabTo) {
		try{
		SlabRateMapper slabRateMapper = new SlabRateMapper();
		String sql = "SELECT "+slabRateMapper.schema()+"where slab_from= '"+slabFrom+"' and slab_to='"+slabTo+"'";
		return jdbcTemplate.query(sql, slabRateMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
		
	}

	
}
