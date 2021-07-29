package org.mifosplatform.portfolio.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.portfolio.client.data.BillSupressionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class BillSupressionProfileInfoReadPlatformServiceImpl implements BillSupressionProfileInfoReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	
	
	@Autowired
	public BillSupressionProfileInfoReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<BillSupressionData> getBillSupression() {

		try{
			final BillSupressionMapper supressionMapper = new BillSupressionMapper();
		String sql = "SELECT "+supressionMapper.schema();
		return jdbcTemplate.query(sql, supressionMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	private static final class BillSupressionMapper implements RowMapper<BillSupressionData> {
		
		@Override
		public BillSupressionData mapRow(final ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long minBillValue = rs.getLong("minBillValue");
			final Long noOfCycleSuppress = rs.getLong("noOfCycleSuppress");

			return new BillSupressionData(id, minBillValue, noOfCycleSuppress);
		}
		
		public String schema() {
			return "bs.id AS id, bs.min_bill_value AS minBillValue, bs.no_of_cycle_suppress AS noOfCycleSuppress from c_supression_profile bs";
		}
	}
	

}
