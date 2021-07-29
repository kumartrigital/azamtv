package org.mifosplatform.provisioning.networkelement.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.celcom.domain.PaymentTypeEnum;
import org.mifosplatform.celcom.domain.SearchTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;
import org.mifosplatform.provisioning.networkelement.domain.NetworkElement;
import org.mifosplatform.provisioning.networkelement.domain.StatusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.sun.xml.bind.annotation.OverrideAnnotationOf;

@Service
public class NetworkElementReadPlatformServiceImpl implements NetworkElementReadPlatformService  {
	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<NetworkElementData> paginationHelper = new PaginationHelper<NetworkElementData>();
	private final PlatformSecurityContext context;
	
	@Autowired
	public NetworkElementReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	
	//this is to retrive a particular record [get by id]
		@Override
		public NetworkElementData retrieveNetworkElement(Long networkelementId) {

			try{
			NetworkElementMapper networkelementMapper = new NetworkElementMapper();
			String sql = "SELECT "+networkelementMapper.schema()+" WHERE is_deleted = 'N' AND id = ?";
			return jdbcTemplate.queryForObject(sql, networkelementMapper,new Object[]{networkelementId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
		
		}

	
	
		//this is to retrieve all records[get by all] 
	
	@Override
	public Page<NetworkElementData> retrieveNetworkElement(SearchSqlQuery searchNetworkElement) {
		NetworkElementMapper networkelementMapper = new NetworkElementMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(networkelementMapper.schema());
        sqlBuilder.append(" where is_deleted = 'N' ");
        
        String sqlSearch = searchNetworkElement.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " system_code like '%"+sqlSearch+"%' OR"
	    			+ " system_name like '%"+sqlSearch+"%' OR"
	    			+ " status like '%"+sqlSearch+"%' )";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchNetworkElement.isLimited()) {
            sqlBuilder.append(" limit ").append(searchNetworkElement.getLimit());
        }

        if (searchNetworkElement.isOffset()) {
            sqlBuilder.append(" offset ").append(searchNetworkElement.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, networkelementMapper);
	}
	
	

	private class NetworkElementMapper implements RowMapper<NetworkElementData> {
	    @Override
		public NetworkElementData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String systemcode  = rs.getString("systemcode");
			final String systemname = rs.getString("systemname");
			final String  status = rs.getString("status");
		    
			return new NetworkElementData(id, systemcode, systemname, status, null);
		}
	    
		public String schema() {
			
			return " id,  system_code as systemcode,system_name as systemname,status  from b_network_element ";
			
		}
	}
	
	
	@Override
    public List<StatusTypeEnum> retrieveStatusTypeEnum() {
        List<StatusTypeEnum> StatusType = new ArrayList<StatusTypeEnum>();
        for(int i=1;i<=2;i++){
        	StatusType.add(StatusTypeEnum.fromInt(i));
        }
        return StatusType;
	}

	@Override
	public List<NetworkElementData> retrieveNetworkElements() {

		try{
			NetworkDropDownMapper mapper = new NetworkDropDownMapper();
		String sql = "SELECT "+mapper.schema()+" WHERE ne.is_deleted = 'N'";
		return jdbcTemplate.query(sql, mapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
		
	}
	private class NetworkDropDownMapper implements RowMapper<NetworkElementData> {
	    @Override
		public NetworkElementData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String systemcode  = rs.getString("systemcode");
			final String systemname = rs.getString("systemname");
			final String isGroupSupported = rs.getString("isGroupSupported");
			
			return new NetworkElementData(id, systemcode, systemname, null, isGroupSupported);
		}
	    
		public String schema() {
			
			return "ne.id as id,ne.system_code as systemcode,ne.system_name as systemname,ne.status as status," +
					"ne.is_group_supported as isGroupSupported  from b_network_element ne";

		}
	}
	
	@Override
	public List<NetworkElementData> retriveNetworkElementsForService(String query) {
		NetworkDropDownMapper rowMapper = new NetworkDropDownMapper();
		return jdbcTemplate.query(query, rowMapper,new Object[]{});
	}

}
