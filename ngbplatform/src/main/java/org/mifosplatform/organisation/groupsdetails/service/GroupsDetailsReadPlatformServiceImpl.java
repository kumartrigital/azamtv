package org.mifosplatform.organisation.groupsdetails.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.groupsdetails.data.GroupsDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class GroupsDetailsReadPlatformServiceImpl implements GroupsDetailsReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<GroupsDetailsData> paginationHelper = new PaginationHelper<GroupsDetailsData>();
	
	@Autowired
	public GroupsDetailsReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context){
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	@Override
	public Page<GroupsDetailsData> retrieveAllGroupsData(final SearchSqlQuery searchGroupsDetails) {
		
		context.authenticatedUser();
		final GroupsDetailsMapper groupsDetailsMapper = new GroupsDetailsMapper();
		final StringBuilder sqlBuilder = new StringBuilder(200);
		
		sqlBuilder.append("select ");
		sqlBuilder.append(groupsDetailsMapper.schema());
		
		String sqlSearch = searchGroupsDetails.getSqlSearch();
		String extraCriteria = "";
		if(sqlSearch != null){
			
			sqlSearch = sqlSearch.trim();
			extraCriteria = "where bg.group_name like '%"+sqlSearch+"%' ";
		}
		extraCriteria += " order by id desc ";
		sqlBuilder.append(extraCriteria);
		
		if(searchGroupsDetails.isLimited()){
			sqlBuilder.append(" limit ").append(searchGroupsDetails.getLimit());
		}
		if(searchGroupsDetails.isOffset()){
			sqlBuilder.append(" offset ").append(searchGroupsDetails.getOffset());
		}
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
                                                      new Object[] {}, groupsDetailsMapper);
	}
	
	private class GroupsDetailsMapper implements RowMapper<GroupsDetailsData>{

		@Override
		public GroupsDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final String groupName = rs.getString("groupName");
			final String groupAddress = rs.getString("groupAddress");
			final Long countNo = rs.getLong("countNo");
			final String attribute1 = rs.getString("attribute1");
			final String attribute2 = rs.getString("attribute2");
			final String attribute3 = rs.getString("attribute3");
			final String attribute4 = rs.getString("attribute4");
			final String isProvision = rs.getString("isProvision");
			
			return new GroupsDetailsData(id, groupName, groupAddress, countNo,attribute1,attribute2,attribute3,attribute4,isProvision);
		}
		
		public String schema(){
		
			final String sql = "bg.id as id,bg.group_name as groupName,bg.group_address as groupAddress,bg.attribute1 as attribute1, "+
						    "bg.attribute2 as attribute2,bg.attribute3 as attribute3,bg.attribute4 as attribute4,bg.is_provision as isProvision, "+
					        "ifnull(mc.cnt,0) as countNo "+
							"from b_group bg "+ 
							"left join (select  group_name ,count(*) cnt from m_client group by group_name) mc "+
							"on (bg.group_name=mc.group_name) ";
			return sql;
			
		}
	}

	@Override
	public List<Long> retrieveclientIdsByGroupId(final Long groupId) {
	try{

		this.context.authenticatedUser();
		final ClientIdMapper mapper=new ClientIdMapper();
		final String  sql="select "+mapper.schema();
		return this.jdbcTemplate.query(sql,mapper, new Object[]{groupId});
		
	}catch(EmptyResultDataAccessException dve){
		return null;
		
	}
	}
	
	private class ClientIdMapper implements RowMapper<Long>{

		@Override
		public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			
			final Long clientId = rs.getLong("clientId");
			return clientId;
		}
		public String schema(){
			final String sql = "id as clientId from m_client c where group_id=? ";
			return sql;
			
		}
	}


}
