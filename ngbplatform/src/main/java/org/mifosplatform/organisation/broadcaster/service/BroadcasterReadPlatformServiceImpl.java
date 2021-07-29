package org.mifosplatform.organisation.broadcaster.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.mifosplatform.celcom.domain.PlanTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
		

@Service
public class BroadcasterReadPlatformServiceImpl implements BroadcasterReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<BroadcasterData> paginationHelper = new PaginationHelper<BroadcasterData>();
	
	
	@Autowired
	public BroadcasterReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
	//this is to retrive a perticular record 
	@Override
	public BroadcasterData retrieveBroadcaster(Long broadcasterId) {
		try{
		BroadcasterMapper broadcasterMapper = new BroadcasterMapper();
		String sql = "SELECT "+broadcasterMapper.schema()+" WHERE bc.is_deleted = 'N' AND bc.id = ?";
		return jdbcTemplate.queryForObject(sql, broadcasterMapper,new Object[]{broadcasterId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	@Override
	public Page<BroadcasterData> retrieveBroadcaster(SearchSqlQuery searchBroadcaster) {
		BroadcasterMapper broadcasterMapper = new BroadcasterMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(broadcasterMapper.schema());
        sqlBuilder.append(" where id IS NOT NULL ");
        
        String sqlSearch = searchBroadcaster.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " brc_code like '%"+sqlSearch+"%' OR"
	    			+ " brc_name like '%"+sqlSearch+"%' OR"
	    			+ " brc_contact_mobile like '%"+sqlSearch+"%' OR"
	    			+ " brc_contact_no like '%"+sqlSearch+"%' OR"
	    			+ " brc_contact_name like '%"+sqlSearch+"%' OR"
	    			+ " brc_contact_email like '%"+sqlSearch+"%' OR"
	    			+ " brc_address like '%"+sqlSearch+"%' OR"
	    			+ " br_pin like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
        sqlBuilder.append(" and is_deleted = 'N' ");

        if (searchBroadcaster.isLimited()) {
            sqlBuilder.append(" limit ").append(searchBroadcaster.getLimit());
        }

        if (searchBroadcaster.isOffset()) {
            sqlBuilder.append(" offset ").append(searchBroadcaster.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, broadcasterMapper);
	}



	@Override
	public List<BroadcasterData> retrieveBroadcastersForDropdown() {

		try{
		BroadcasterDropdownMapper broadcasterMapper = new BroadcasterDropdownMapper();
		String sql = "SELECT "+broadcasterMapper.schema()+" WHERE bc.is_deleted = 'N'";
		return jdbcTemplate.query(sql, broadcasterMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	
		
	}
	
	
	private class BroadcasterMapper implements RowMapper<BroadcasterData> {
	    @Override
		public BroadcasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String broadcasterCode = rs.getString("broadcasterCode");
			final String broadcasterName = rs.getString("broadcasterName");
		    final Long contactMobile = rs.getLong("contactMobile");
			final Long contactNumber = rs.getLong("contactNumber");
		    final String contactName = rs.getString("contactName");
			final String email = rs.getString("email");
			final String address = rs.getString("address");
			final String pin = rs.getString("pin");
			
			return new BroadcasterData(id, broadcasterCode, broadcasterName, contactMobile, contactNumber, contactName, email, address, pin);
		}
	    
		public String schema() {
			
			return " bc.id AS id, bc.brc_code AS broadcasterCode, bc.brc_name AS broadcasterName, bc.brc_contact_mobile AS contactMobile, " +
				   " bc.brc_contact_no AS contactNumber, bc.brc_contact_name AS contactName, bc.brc_contact_email AS email, bc.brc_address AS address, " +
				   " bc.br_pin AS pin from b_broadcaster bc ";
			
		}
	}
	
	
	private class BroadcasterDropdownMapper implements RowMapper<BroadcasterData> {
	    @Override
		public BroadcasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String broadcasterCode = rs.getString("broadcasterCode");
			final String broadcasterName = rs.getString("broadcasterName");
		   
			
			return new BroadcasterData(id, broadcasterCode, broadcasterName);
		}
	    
		public String schema() {
			
			return " bc.id AS id, bc.brc_code AS broadcasterCode, bc.brc_name AS broadcasterName from b_broadcaster bc ";
			
		}
	}

}
