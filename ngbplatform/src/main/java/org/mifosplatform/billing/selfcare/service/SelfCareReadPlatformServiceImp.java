package org.mifosplatform.billing.selfcare.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.selfcare.data.SelfCareData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class SelfCareReadPlatformServiceImp implements SelfCareReadPlatformService {

	private JdbcTemplate jdbcTemplate;
	private PlatformSecurityContext context;
	
	
	@Autowired
	public SelfCareReadPlatformServiceImp(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public Long getClientId(String uniqueReference) {
		
		String sql = null;
		if(uniqueReference.contains("@")){
			sql = "select c.id as clientId, c.email as email from m_client c where c.email = ?";
		}else{
			sql = "select c.id as clientId, c.email as email from m_client c where c.account_no = ?";
		}
		ClientIdMapper rowMapper = new ClientIdMapper();
		return jdbcTemplate.queryForObject(sql,rowMapper,new Object[]{uniqueReference.contains("@")?uniqueReference:Long.getLong(uniqueReference)});
	}
	
	@Override
	public String getEmail(Long clientId) {
		String sql = "select c.email as email from m_client c where c.id = ?";
		ClientEmailMapper rowMapper = new ClientEmailMapper();
		return jdbcTemplate.queryForObject(sql, rowMapper, new Object[]{clientId});
	}
	
	
	@Override
	public SelfCareData login(String userName, String password) {
		try{
		String sql = "";
		/*if(userName.contains("@")){*/

			sql = "select client_id as clientId, auth_pin as authPin, password as password,firsttime_login_remaining as firsttimeLoginRemaining ,nonexpired as nonExpired,nonlocked as nonLocked,"
					+ "nonexpired_credentials as nonExpiredCredentials,enabled as enabled from b_clientuser where unique_reference=? or username =? and password=? and is_deleted=0 ";
		/*}else{
			sql = "select client_id as clientId, auth_pin as authPin, password as password from b_clientuser where username=? and password=? and is_deleted=0";
		}	*/
		PasswordMapper mapper1 = new PasswordMapper();
		return jdbcTemplate.queryForObject(sql,mapper1,new Object[]{userName,userName,password});

		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	private class ClientEmailMapper implements RowMapper<String>{
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			String email = rs.getString("email");
			return email;
		}
	}
	
	private class ClientIdMapper implements RowMapper<Long>{
		@Override
		public Long mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			Long clientId = rs.getLong("clientId");
			return clientId;
		}
	}
	private class PasswordMapper implements RowMapper<SelfCareData>{
		@Override
		public SelfCareData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long clientId = rs.getLong("clientId");
			String authPin = rs.getString("authPin");
			String password = rs.getString("password");
			Integer firsttimeLoginRemaining = rs.getInt("firsttimeLoginRemaining");
			Integer nonExpired = rs.getInt("nonExpired");
			Integer nonLocked = rs.getInt("nonLocked");
			Integer nonExpiredCredentials = rs.getInt("nonExpiredCredentials");
			Integer enabled = rs.getInt("enabled");
			return new SelfCareData(authPin, clientId, password,firsttimeLoginRemaining,nonExpired,nonLocked,nonExpiredCredentials,enabled);
		}
	}
	
	@Override
	public List<SelfCareData> authlogin(String authToken, String username) {
		try{
		boolean isFound = username.contains("@");
		AuthLoginMapper authLoginMapper = new AuthLoginMapper();
		StringBuilder sql = new StringBuilder("Select ");
		if(isFound){
			sql.append(authLoginMapper.userNameSchema());
			sql.append(" where m.email like ?");
			return jdbcTemplate.query(sql.toString(),authLoginMapper,new Object[]{username});
		}else{
			sql.append(authLoginMapper.userNameSchema());
			sql.append(" where m.phone like ?");
			return jdbcTemplate.query(sql.toString(),authLoginMapper,new Object[]{username});
		}
		

		}catch(EmptyResultDataAccessException ex){
			return null;
		}
	}
	
	private class AuthLoginMapper implements RowMapper<SelfCareData>{
		
		public String authSchema(){
			StringBuilder query = new StringBuilder("client_id, username, password from b_clientuser");
			return query.toString();
		}
		
		public String userNameSchema(){
			StringBuilder query = new StringBuilder("cu.client_id as clientId, cu.username as username, cu.password as password from m_client m join b_clientuser cu on cu.client_id = m.id");
			return query.toString();
		}
		@Override
		public SelfCareData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long clientId = rs.getLong("clientId");
			String password = rs.getString("password");
			String username = rs.getString("username");
				
			return new SelfCareData(username, clientId, password);
		}
	}
}
