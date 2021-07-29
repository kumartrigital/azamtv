package org.mifosplatform.portfolio.transactionhistory.service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;


import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.domain.ClientEnumerations;
import org.mifosplatform.portfolio.transactionhistory.data.TransactionHistoryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class TransactionHistoryReadPlatformServiceImp implements TransactionHistoryReadPlatformService{

	
	
	private JdbcTemplate jdbcTemplate;
	private PlatformSecurityContext context;
	private final PaginationHelper<TransactionHistoryData> paginationHelper = new PaginationHelper<TransactionHistoryData>();
	
	@Autowired
	public TransactionHistoryReadPlatformServiceImp(final TenantAwareRoutingDataSource dataSource,
			final PlatformSecurityContext context) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}

	public Page<TransactionHistoryData> retriveTransactionHistoryClientId(SearchSqlQuery searchTransactionHistory,final Long clientId,String fromDate,String toDate) {
		
		return  retriveByClientId(searchTransactionHistory,clientId,fromDate,toDate);
	}
	
	private Page<TransactionHistoryData> retriveByClientId(SearchSqlQuery searchTransactionHistory,Long id,String fromDate,String toDate){
		try{
			
			context.authenticatedUser();
			ClientTransactionHistoryMapper rowMapper = new ClientTransactionHistoryMapper();
			StringBuilder sql = new StringBuilder();
			sql.append("select "+rowMapper.schema()+" and  pcs.client_id = ?" );
		    if(fromDate != null && toDate != null) {
		    	sql.append(" and date_format(pcs.made_on_date,'%Y-%m-%d') between '"+fromDate+"' and '"+toDate+"' ");
		    }
		    String sqlSearch = searchTransactionHistory.getSqlSearch();
		    String extraCriteria = "";
		    if (sqlSearch != null) {
		    	sqlSearch=sqlSearch.trim();
		    	extraCriteria = "and (pcs.action_name like '%"+sqlSearch+"%' OR pcs.entity_name like '%"+sqlSearch+"%' OR" +
		    			" pcs.made_on_date like '%"+sqlSearch+"%' OR a.username like '%"+sqlSearch+"%' OR" +
		    		    " pcs.command_as_json like '%"+sqlSearch+"%')";
			    }
			   
		    sql.append(extraCriteria);
		    if (searchTransactionHistory.isLimited()) {
		    	sql.append(" order by transactionDate desc  limit ").append(searchTransactionHistory.getLimit());
		    }
		    if (searchTransactionHistory.isOffset()) {
		    	sql.append(" offset ").append(searchTransactionHistory.getOffset());
		     }
		    return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sql.toString(),
			            new Object[] {id}, rowMapper);
				
		}catch(DataIntegrityViolationException dve){
			throw new PlatformDataIntegrityException("", "", "");
		}
		
	}
	
	
	private class ClientTransactionHistoryMapper implements RowMapper<TransactionHistoryData>{
		
		public String schema(){
			
			return "SQL_CALC_FOUND_ROWS pcs.id AS id,pcs.client_id AS clientId,"+
				   "pcs.action_name AS actionName,pcs.entity_name AS entityName,pcs.made_on_date AS transactionDate,"+
				   "pcs.resource_id as resourceId,pcs.command_as_json AS history,a.username as userName "+
				   "FROM m_portfolio_command_source pcs,m_appuser a WHERE a.id = pcs.maker_id";
		}
		
		
		@Override
		public TransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String transactionType = rs.getString("actionName")+" "+ rs.getString("entityName");
			DateTime transactionDate=JdbcSupport.getDateTime(rs,"transactionDate");
			String resourceId=rs.getString("resourceId");
			String history = rs.getString("history");
			String user=rs.getString("userName");
			/*final LocalDate validFrom=JdbcSupport.getLocalDate(rs,"fromDate");
			final LocalDate validTo=JdbcSupport.getLocalDate(rs,"toDate");*/
		
			return new TransactionHistoryData(id,clientId, transactionType, transactionDate,null, history,user);
		}

	}
	
private class ClientOldTransactionHistoryMapper implements RowMapper<TransactionHistoryData>{
		
	
		
		@Override
		public TransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String transactionType = rs.getString("transactionType");
			DateTime transactionDate=JdbcSupport.getDateTime(rs,"transactionDate");
			String history = rs.getString("history");
			String user=rs.getString("userName");
			return new TransactionHistoryData(id,clientId, transactionType, transactionDate, null, history,user);
		}
		
		
	}
	
	private String query(){
		return " SQL_CALC_FOUND_ROWS th.id AS id,th.client_id AS clientId,th.transaction_type AS transactionType,th.transaction_date AS transactionDate,th.history AS history," +
		" a.username as userName FROM b_transaction_history th,m_appuser a WHERE a.id = th.createdby_id ";
		}
	
	
	
	
	//retrive users transaction data//
	
  public Page<TransactionHistoryData> retriveTransactionHistoryUserId(SearchSqlQuery searchTransactionHistory,final Long resourceId,String fromDate,String toDate) {
		
		return  retriveByUserId(searchTransactionHistory,resourceId,fromDate,toDate);
	}
	
	private Page<TransactionHistoryData> retriveByUserId(SearchSqlQuery searchTransactionHistory,Long id,String fromDate,String toDate){
		try{
			
			context.authenticatedUser();
			UserTransactionHistoryMapper rowMapper = new UserTransactionHistoryMapper();
			StringBuilder sql = new StringBuilder();
			sql.append("select "+rowMapper.schema()+" and  pcs.resource_id = ?" );
		    if(fromDate != null && toDate != null) {
		    	sql.append(" and date_format(pcs.made_on_date,'%Y-%m-%d') between '"+fromDate+"' and '"+toDate+"' ");
		    }
		    String sqlSearch = searchTransactionHistory.getSqlSearch();
		    String extraCriteria = "";
		    if (sqlSearch != null) {
		    	sqlSearch=sqlSearch.trim();
		    	extraCriteria = "and (pcs.action_name like '%"+sqlSearch+"%' OR pcs.entity_name like '%"+sqlSearch+"%' OR" +
		    			" pcs.made_on_date like '%"+sqlSearch+"%' OR a.username like '%"+sqlSearch+"%' OR" +
		    		    " pcs.command_as_json like '%"+sqlSearch+"%')";
			    }
			   
		    sql.append(extraCriteria);
		    if (searchTransactionHistory.isLimited()) {
		    	sql.append(" order by transactionDate desc  limit ").append(searchTransactionHistory.getLimit());
		    }
		    if (searchTransactionHistory.isOffset()) {
		    	sql.append(" offset ").append(searchTransactionHistory.getOffset());
		     }
		    return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sql.toString(),
			            new Object[] {id}, rowMapper);
				
		}catch(DataIntegrityViolationException dve){
			throw new PlatformDataIntegrityException("", "", "");
		}
		
	}
	
	
	private class UserTransactionHistoryMapper implements RowMapper<TransactionHistoryData>{
		
		public String schema(){
			
			return "SQL_CALC_FOUND_ROWS pcs.id AS id,pcs.client_id AS clientId,"+
				   "pcs.action_name AS actionName,pcs.entity_name AS entityName,pcs.made_on_date AS transactionDate,"+
				   "pcs.resource_id as resourceId,pcs.command_as_json AS history,a.username as userName "+
				   "FROM m_portfolio_command_source pcs,m_appuser a WHERE a.id = pcs.resource_id";
		}
		
		
		@Override
		public TransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String transactionType = rs.getString("actionName")+" "+ rs.getString("entityName");
			DateTime transactionDate=JdbcSupport.getDateTime(rs,"transactionDate");
			String resourceId=rs.getString("resourceId");
			String history = rs.getString("history");
			String user=rs.getString("userName");
			/*final LocalDate validFrom=JdbcSupport.getLocalDate(rs,"fromDate");
			final LocalDate validTo=JdbcSupport.getLocalDate(rs,"toDate");*/
		
			return new TransactionHistoryData(id,clientId, transactionType, transactionDate,resourceId, history,user);
		}

	}
	
public Page<TransactionHistoryData> retriveTransactionHistoryOfficeId(SearchSqlQuery searchTransactionHistory,String fromDate,String toDate) {
		
		return  retriveByOfficeId(searchTransactionHistory,fromDate,toDate);
	}
	
	private Page<TransactionHistoryData> retriveByOfficeId(SearchSqlQuery searchTransactionHistory,String fromDate,String toDate){
		try{
			
			context.authenticatedUser();
			OfficeTransactionHistoryMapper rowMapper = new OfficeTransactionHistoryMapper();
			StringBuilder sql = new StringBuilder();
			sql.append("select "+rowMapper.schema());
		    if(fromDate != null && toDate != null) {
		    	sql.append(" and date_format(pcs.made_on_date,'%Y-%m-%d') between '"+fromDate+"' and '"+toDate+"' ");
		    }
		    String sqlSearch = searchTransactionHistory.getSqlSearch();
		    String extraCriteria = "";
		    if (sqlSearch != null) {
		    	sqlSearch=sqlSearch.trim();
		    	extraCriteria = "and (pcs.action_name like '%"+sqlSearch+"%' OR pcs.entity_name like '%"+sqlSearch+"%' OR" +
		    			" pcs.made_on_date like '%"+sqlSearch+"%' OR a.username like '%"+sqlSearch+"%' OR" +
		    		    " pcs.command_as_json like '%"+sqlSearch+"%')";
			    }
			   
		    sql.append(extraCriteria);
		    if (searchTransactionHistory.isLimited()) {
		    	sql.append(" order by transactionDate desc  limit ").append(searchTransactionHistory.getLimit());
		    }
		    if (searchTransactionHistory.isOffset()) {
		    	sql.append(" offset ").append(searchTransactionHistory.getOffset());
		     }
		    return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sql.toString(),
			            new Object[] {}, rowMapper);
				
		}catch(DataIntegrityViolationException dve){
			throw new PlatformDataIntegrityException("", "", "");
		}
		
	}
	
	
	private class OfficeTransactionHistoryMapper implements RowMapper<TransactionHistoryData>{
		
		public String schema(){
			
			return "SQL_CALC_FOUND_ROWS pcs.id AS id,pcs.client_id AS clientId,"+
				   "pcs.action_name AS actionName,pcs.entity_name AS entityName,pcs.made_on_date AS transactionDate,"+
				   "pcs.office_id as officeId,pcs.command_as_json AS history,a.name as Name "+
				   "FROM m_portfolio_command_source pcs,m_office a WHERE a.id = pcs.office_id";
		}
		
		
		@Override
		public TransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String transactionType = rs.getString("actionName")+" "+ rs.getString("entityName");
			DateTime transactionDate=JdbcSupport.getDateTime(rs,"transactionDate");
			String history = rs.getString("history");
			String name=rs.getString("name");
			Long officeId=rs.getLong("officeId");
			/*final LocalDate validFrom=JdbcSupport.getLocalDate(rs,"fromDate");
			final LocalDate validTo=JdbcSupport.getLocalDate(rs,"toDate");*/
		
			return new TransactionHistoryData(id,clientId, transactionType, transactionDate,history,name,officeId);
		
		}

	}
private class UserOldTransactionHistoryMapper implements RowMapper<TransactionHistoryData>{
		
	
		
		@Override
		public TransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			String transactionType = rs.getString("transactionType");
			DateTime transactionDate=JdbcSupport.getDateTime(rs,"transactionDate");
			String history = rs.getString("history");
			String user=rs.getString("userName");
			return new TransactionHistoryData(id,clientId, transactionType, transactionDate, null, history,user);
		}

	}
	
	private String query1(){
		return " SQL_CALC_FOUND_ROWS th.id AS id,th.client_id AS clientId,th.transaction_type AS transactionType,th.transaction_date AS transactionDate,th.history AS history," +
		" a.username as userName FROM b_transaction_history th,m_appuser a WHERE a.id = th.createdby_id ";
		}

	@Override
	public Page<TransactionHistoryData> retriveTransactionHistoryById(SearchSqlQuery searchTransactionHistory, Long clientId) {context.authenticatedUser();
	
		try{
			String sql = "select "+query1()+" and th.client_id = ? order by transactionDate desc ";
			ClientOldTransactionHistoryMapper rowMapper = new ClientOldTransactionHistoryMapper();
			StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append(sql);
			String sqlSearch = searchTransactionHistory.getSqlSearch();
			String extraCriteria = "";
			
			if (sqlSearch != null) {
				sqlSearch=sqlSearch.trim();
				extraCriteria = " and (th.transaction_type like '%"+sqlSearch+"%' OR "
						+ " th.transaction_date like '%"+sqlSearch+"%' OR "
						+ " a.username like '%"+sqlSearch+"%' OR "
						+ " th.history like '%"+sqlSearch+"%') " ;
			}
			sqlBuilder.append(extraCriteria);
			if (searchTransactionHistory.isLimited()) {
				sqlBuilder.append(" limit ").append(searchTransactionHistory.getLimit());
			}
			if (searchTransactionHistory.isOffset()) {
				sqlBuilder.append(" offset ").append(searchTransactionHistory.getOffset());
			}
	
	return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),new Object[] {clientId}, rowMapper);
	
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
	}
	
	@Override
	public Page<TransactionHistoryData> retriveTransactionHistoryOnClients(SearchSqlQuery searchTransactionHistory) {
		context.authenticatedUser();
	
		try{
			OneMonthClientsTransactionMapper rowMapper = new OneMonthClientsTransactionMapper();
			StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append("Select");
			sqlBuilder.append(rowMapper.clientsExtractionQuery());
			sqlBuilder.append(" order by pcs.made_on_date desc");
			
			if (searchTransactionHistory.isLimited()) {
				sqlBuilder.append(" limit ").append(searchTransactionHistory.getLimit());
			}
			if (searchTransactionHistory.isOffset()) {
				sqlBuilder.append(" offset ").append(searchTransactionHistory.getOffset());
			}
	
	return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),new Object[] {}, rowMapper);
	
		}catch(EmptyResultDataAccessException accessException){
			return null;
		}
	}
	
	private class OneMonthClientsTransactionMapper implements RowMapper<TransactionHistoryData>{
		
	
		
		@Override
		public TransactionHistoryData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			final String customerName = rs.getString("displayName");
			final String accountNo = rs.getString("accountNo");
			final String officeName = rs.getString("officeName");
			final Integer statusEnum = JdbcSupport.getInteger(rs, "status");
	          final EnumOptionData status = ClientEnumerations.status(statusEnum);
			final String actionPerformed = rs.getString("action");
			String serialNo=rs.getString("serialNo");
			if(serialNo==null){
				serialNo="None";
			}
			return new TransactionHistoryData(id, clientId, customerName, accountNo, officeName, status, actionPerformed,serialNo);
		}
		
		private String clientsExtractionQuery(){
			/*Date today = new Date(new java.util.Date().getTime());
			Date oneMonthBack = Date.valueOf(today.toLocalDate().minusMonths(1));
			String userName=context.authenticatedUser().getUsername();
			StringBuilder stringBuilder = new StringBuilder(" pcs.id as id, pcs.client_id as clientId, c.display_name as displayName, c.account_no as accountNo,");
			stringBuilder.append(" replace(concat(pcs.action_name,' ', pcs.entity_name),'CLIENT','') as action,o.name as officeName, c.status_enum as status,");
			stringBuilder.append(" (SELECT coalesce(ba.serial_no, 'No Device') fROM b_allocation ba where c.id = ba.client_id");
		    stringBuilder.append(" AND ba.status = 'allocated' order by item_master_id limit 1) serialNo from ");
			stringBuilder.append(" m_portfolio_command_source pcs join m_client c on c.id = pcs. client_id");
			stringBuilder.append(" join m_office o on o.id=c.office_id join m_appuser u on u.id = pcs.maker_id where ");
			stringBuilder.append("u.username ='"+userName+"' and date_format(pcs.made_on_date, '%Y-%m-%d')");
			stringBuilder.append(" between '"+oneMonthBack+"' and '"+today+"' and pcs.entity_name like '%CLIENT%'");
			
			return stringBuilder.toString();*/
			return null;
		}

	}

}
