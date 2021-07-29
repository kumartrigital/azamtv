package org.mifosplatform.crm.ticketmaster.ticketmapping.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.data.TicketMasterData;
import org.mifosplatform.crm.ticketmaster.ticketmapping.data.TicketTeamMappingData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.useradministration.data.AppUserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;



@Service
public class TicketMappingReadPlatformServiceImpl implements TicketMappingReadPlatformService {

	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<TicketTeamMappingData> paginationHelper = new PaginationHelper<TicketTeamMappingData>();
	
	
	@Autowired
	public TicketMappingReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
	//this is to retrive a perticular record 
	@Override
	public List<TicketTeamMappingData> retrieveTicketMapping(Long ticketmappingId) {
		try{
		TicketMappingMapper ticketMappingMapper = new TicketMappingMapper();
		String sql = "SELECT distinct "+ticketMappingMapper.schema()+" WHERE tm.is_deleted = 'N' AND tm.team_id = ?";
		return jdbcTemplate.query(sql, ticketMappingMapper,new Object[]{ticketmappingId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
		@Override
	public Page<TicketTeamMappingData> retrieveTicketMapping(SearchSqlQuery searchTicketMapping) {
		TicketMappingMapper ticketMappingMapper = new TicketMappingMapper();

		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(ticketMappingMapper.schema());
        sqlBuilder.append(" where tm.id IS NOT NULL and tm.is_deleted = 'N' ");
        
        String sqlSearch = searchTicketMapping.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " team_id like '%"+sqlSearch+"%' OR"
	    			+ " user_id like '%"+sqlSearch+"%' OR"
	    			+ " user_role like '%"+sqlSearch+"%' OR"
	    			+ " is_team_lead like '%"+sqlSearch+"%' OR"
	    			+ " status like '%"+sqlSearch+"%')";
	    }
        
	    if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }

	    sqlBuilder.append(" GROUP BY tm.team_id ");
	    
        if (searchTicketMapping.isLimited()) {
            sqlBuilder.append(" limit ").append(searchTicketMapping.getLimit());
        }

	    
        if (searchTicketMapping.isOffset()) {
            sqlBuilder.append(" offset ").append(searchTicketMapping.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, ticketMappingMapper);
	}


	private class TicketMappingMapper implements RowMapper<TicketTeamMappingData> {
	    @Override
		public TicketTeamMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
	    	final Long teamId = rs.getLong("teamId");
	    	final Long userId = rs.getLong("userId");
			final String userRole = rs.getString("userRole");
			final Boolean isTeamLead = rs.getBoolean("isTeamLead");
			final String status = rs.getString("status");
			final String teamCode = rs.getString("teamCode");
			
			
			return new TicketTeamMappingData(id, teamId, userId, userRole, isTeamLead, status,teamCode);
		}
	    
		
		public String schema(){
			return "tm.id AS id, tm.team_id AS teamId, tm.user_id AS userId, tm.user_role AS userRole, tm.is_team_lead AS isTeamLead,"
					+ "  tm.status AS status,tt.team_code as teamCode from b_team_user tm JOIN b_team tt ON tm.team_id=tt.id";
		}
	}
	



	@Override
	public List<TicketTeamMappingData> retrieveTicketTeamForDropdown() {
	
		try{
			TicketTeamForDropdownMapper ticketteamMapper = new TicketTeamForDropdownMapper();
			String sql = "SELECT "+ticketteamMapper.schema()+" WHERE tt.is_deleted = 'N'";
			return jdbcTemplate.query(sql, ticketteamMapper,new Object[]{});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}
	
	
	
	private class TicketTeamForDropdownMapper implements RowMapper<TicketTeamMappingData> {
	    @Override
		public TicketTeamMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String teamCode = rs.getString("teamCode");
			final String teamDescription = rs.getString("teamDescription");
			final String teamEmail = rs.getString("teamEmail");
			return new TicketTeamMappingData(id, teamCode, teamDescription,teamEmail);
		}
	    
		public String schema() {
			
			return " tt.id AS id,tt.team_email as teamEmail, tt.team_code AS teamCode, tt.team_description AS teamDescription from b_team tt ";
			
		}
	}


	@Override
	public List<TicketTeamMappingData> retrieveSelectedUsers(Long teamId) {
		try{
			TicketTeamForDropdownMappe ticketteamMapper = new TicketTeamForDropdownMappe();
			String sql = "SELECT "+ticketteamMapper.schema()+" WHERE tm.is_deleted = 'N' and tm.team_id = ?";
			return jdbcTemplate.query(sql, ticketteamMapper,new Object[]{teamId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}	
	}


	private class TicketTeamForDropdownMappe implements RowMapper<TicketTeamMappingData> {
	    @Override
		public TicketTeamMappingData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id= rs.getLong("id");
			final String username = rs.getString("username");
			final Long userId =  rs.getLong("userId");
			
			return new TicketTeamMappingData(id, username, userId );
		}
	    
		public String schema() {
			
			return "tm.id as id,u.username as username ,tm.user_id as userId from b_team_user tm join b_team t on tm.team_id=t.id join m_appuser u on tm.user_id=u.id";
			
		}
	}



	@Override
	public List<AppUserData> retrieveAppUserDataForDropdown() {
		try{
			AppUserDataForDropdown appuserMapper = new AppUserDataForDropdown();
			String sql = "SELECT DISTINCT "+appuserMapper.schema()+" WHERE ud.is_deleted = 'N' ";
			return jdbcTemplate.query(sql, appuserMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}	
	
	}
	
	 private static final class AppUserDataForDropdown implements RowMapper<AppUserData>{
			
			public String  schema(){
				return "ud.id as id, ud.username as username from m_appuser ud";
				
			}
				
			@Override
			public AppUserData mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				final Long id= rs.getLong("id");
				final Long userId = id;
				final String username = rs.getString("username");
				AppUserData appUserData= new AppUserData(id,username, username, id, username, username, username, null, null, null);
				appUserData.setUserId(userId);
				return appUserData;
			}	
			
		}



	



	


	
	
	
}
