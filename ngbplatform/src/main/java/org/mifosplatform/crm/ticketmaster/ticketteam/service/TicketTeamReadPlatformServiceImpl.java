package org.mifosplatform.crm.ticketmaster.ticketteam.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.crm.ticketmaster.ticketteam.data.TicketTeamData;
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
public class TicketTeamReadPlatformServiceImpl implements TicketTeamReadPlatformService {

	
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<TicketTeamData> paginationHelper = new PaginationHelper<TicketTeamData>();
	
	
	@Autowired
	public TicketTeamReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
	//this is to retrive a perticular record 
	@Override
	public TicketTeamData retrieveTicketTeam(Long ticketteamId) {
		try{
		TicketTeamMapper ticketTeamMapper = new TicketTeamMapper();
		String sql = "SELECT "+ticketTeamMapper.schema()+" WHERE tt.is_deleted = 'N' AND tt.id = ?";
		return jdbcTemplate.queryForObject(sql, ticketTeamMapper,new Object[]{ticketteamId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	@Override
	public Page<TicketTeamData> retrieveTicketTeam(SearchSqlQuery searchTicketTeam) {
		TicketTeamMapper ticketTeamMapper = new TicketTeamMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(ticketTeamMapper.schema());
        sqlBuilder.append(" where tt.id IS NOT NULL and tt.is_deleted = 'N'");
        String sqlSearch = searchTicketTeam.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " user_id like '%"+sqlSearch+"%' OR"
	    			+ " team_code like '%"+sqlSearch+"%' OR"
	    			+ " team_description like '%"+sqlSearch+"%' OR"
	    			+ " team_category like '%"+sqlSearch+"%' OR"
	    			+ " team_email like '%"+sqlSearch+"%' OR"
	    			+ " status like '%"+sqlSearch+"%')";
	    }
        
	    if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }

	    
        if (searchTicketTeam.isLimited()) {
            sqlBuilder.append(" limit ").append(searchTicketTeam.getLimit());
        }

        if (searchTicketTeam.isOffset()) {
            sqlBuilder.append(" offset ").append(searchTicketTeam.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, ticketTeamMapper);
	}


	private class TicketTeamMapper implements RowMapper<TicketTeamData> {
	    @Override
		public TicketTeamData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
	    	final Long userId = rs.getLong("userId");
			final String teamCode = rs.getString("teamCode");
			final String teamDescription = rs.getString("teamDescription");
			final String teamCategory = rs.getString("teamCategory");
			final String status = rs.getString("status");
			final String userName= rs.getString("username");
			final String teamEmail= rs.getString("teamEmail");
			
			return new TicketTeamData(id, userId, teamCode, teamDescription, teamCategory, status,userName,teamEmail);
		}
	    
		public String schema() {
			
			return " tt.id AS id, tt.user_id AS userId, tt.team_code AS teamCode, tt.team_description AS teamDescription, " +
				   " tt.team_category AS teamCategory, tt.status AS status, tt.team_email AS teamEmail, u.username AS username FROM  b_team tt  LEFT JOIN" + 
				   " m_appuser u ON u.id = tt.user_id " ;
				  
			
		}
	}
	
	
	
	

	
	
	
	
}
