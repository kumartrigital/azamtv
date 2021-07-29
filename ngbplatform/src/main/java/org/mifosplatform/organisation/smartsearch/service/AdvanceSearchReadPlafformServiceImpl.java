package org.mifosplatform.organisation.smartsearch.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.smartsearch.data.AdvanceSearchData;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AdvanceSearchReadPlafformServiceImpl implements AdvanceSearchReadPlafformService{
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<AdvanceSearchData> paginationHelper = new PaginationHelper<AdvanceSearchData>();
	
@Autowired
public AdvanceSearchReadPlafformServiceImpl(final PlatformSecurityContext securityContext,
		                   final TenantAwareRoutingDataSource  dataSource){
	
	this.context=securityContext;
	this.jdbcTemplate=new JdbcTemplate(dataSource);
	
}
	

  @Transactional
  @Override
  public Page<AdvanceSearchData> retrieveAllSearchData(SearchParameters searchParameters) {
	  try{
		  this.context.authenticatedUser();
		  final AdvanceSearchMapper advanceSearchMapper=new AdvanceSearchMapper();
		  final Object[] objectArray = new Object[3];
	      int arrayPos = 0;
	      StringBuilder stringBuilder = new StringBuilder();
		  if(searchParameters.getSearchType().equalsIgnoreCase("TICKETS")){
			  
			  stringBuilder.append(advanceSearchMapper.schema());
			  
			  if(searchParameters.getSqlSearch() != null){
				  stringBuilder.append(" AND (t.description LIKE '%"+searchParameters.getSqlSearch()+"%' OR c.display_name LIKE '%"+searchParameters.getSqlSearch()+"%')");
			  }
			  
			  if(searchParameters.getCategory() != null){
				  stringBuilder.append(" AND v.id="+searchParameters.getCategory());
			  }
			  if(searchParameters.getStatus() != null){
				  stringBuilder.append(" AND t.status='"+searchParameters.getStatus()+"'");
			  }
			  if(searchParameters.getAssignedTo() != null){
				  stringBuilder.append(" AND t.assigned_to ="+searchParameters.getAssignedTo());
			  }
			  if(searchParameters.getClosedBy() != null){
				  stringBuilder.append(" AND t.lastmodifiedby_id="+searchParameters.getClosedBy());
			  }
			  if(searchParameters.getClosedBy() != null){
				  stringBuilder.append(" AND t.lastmodifiedby_id="+searchParameters.getClosedBy());
			  }
			  if (searchParameters.getFromDataParam() != null || searchParameters.getToDateParam() != null) {
		        	
		            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		            String fromDateString = null;
		            String toDateString = null;
		            
		            if (searchParameters.getFromDataParam() != null && searchParameters.getToDateParam() != null) {
		                //sql += "  AND p.payment_date BETWEEN ? AND ?";
		            	stringBuilder.append("  AND Date_format(t.ticket_date,'%Y-%m-%d') between ? AND ?");
		                fromDateString = df.format(searchParameters.getFromDataParam());
		                toDateString = df.format(searchParameters.getToDateParam());
		                objectArray[arrayPos] = fromDateString;
		                arrayPos = arrayPos + 1;
		                objectArray[arrayPos] = toDateString;
		                arrayPos = arrayPos + 1;
		                
		            } else if (searchParameters.getFromDataParam() != null) {
		                //sql += " AND p.payment_date >= ? ";
		            	stringBuilder.append(" AND  Date_format(t.ticket_date,'%Y-%m-%d') >= ? ");
		                fromDateString = df.format(searchParameters.getFromDataParam());
		                objectArray[arrayPos] = fromDateString;
		                arrayPos = arrayPos + 1;
		                
		            } else if (searchParameters.getToDateParam() != null) {
		                //sql += "  AND p.payment_date <= ? ";
		            	stringBuilder.append("  AND  Date_format(t.ticket_date,'%Y-%m-%d') <= ? ");
		                toDateString = df.format(searchParameters.getToDateParam());
		                objectArray[arrayPos] = toDateString;
		                arrayPos = arrayPos + 1;
		            }
		        }

		        //sql += "  order by p.payment_date limit "+limit+" offset "+offset;
			  stringBuilder.append(" order by t.id limit "+searchParameters.getLimit()+" offset "+searchParameters.getOffset());
			  
		  }else if(searchParameters.getSearchType().equalsIgnoreCase("LEADS")){
			  
			  stringBuilder.append(advanceSearchMapper.schemaForLeads());
			  
			  if(searchParameters.getSqlSearch() != null){
				  stringBuilder.append(" AND (concat(p.first_name,' ',p.last_name) LIKE '%"+searchParameters.getSqlSearch()+"%' or concat(p.last_name,' ',p.first_name) LIKE '%"+searchParameters.getSqlSearch()+"%')");
			  }
			  if(searchParameters.getName() != null){
				  stringBuilder.append(" AND (concat(p.first_name,' ',p.last_name) LIKE '%"+searchParameters.getName()+"%' or concat(p.last_name,' ',p.first_name) LIKE '%"+searchParameters.getName()+"%')");
			  }
			  if(searchParameters.getCreatedBy() != null){
				  stringBuilder.append(" AND (p.createdby_id ="+searchParameters.getCreatedBy()+")");
			  }
			  if(searchParameters.getAssignedTo() != null){
				  stringBuilder.append(" AND (pd.assigned_to ="+searchParameters.getAssignedTo()+")");
			  }
			  if(searchParameters.getEmailId() != null){
				  stringBuilder.append(" AND (p.email ='"+searchParameters.getEmailId()+"')");
			  }
			  if(searchParameters.getSource() != null){
				  stringBuilder.append(" AND (p.source_of_publicity ='"+searchParameters.getSource()+"')");
			  }
			  if(searchParameters.getPhone() != null){
				  stringBuilder.append(" AND (p.mobile_number ="+searchParameters.getPhone()+")");
			  }
			  
			  stringBuilder.append(" order by p.id limit "+searchParameters.getLimit()+" offset "+searchParameters.getOffset());
		  }else if(searchParameters.getSearchType().equalsIgnoreCase("CLIENTS")){
			  
			  stringBuilder.append(advanceSearchMapper.schemaForClients());
			  
			  if(searchParameters.getSqlSearch() != null){
				  stringBuilder.append(" AND (concat(mc.firstname,' ',mc.lastname) LIKE '%"+searchParameters.getSqlSearch()+"%' or concat(mc.lastname,' ',mc.firstname) LIKE '%"+searchParameters.getSqlSearch()+"%')");
			  }
			  if(searchParameters.getName() != null){
				  stringBuilder.append(" AND (concat(mc.firstname,' ',mc.lastname) LIKE '%"+searchParameters.getName()+"%' or concat(mc.lastname,' ',mc.firstname) LIKE '%"+searchParameters.getName()+"%')");
			  }
			  if(searchParameters.getPhone() != null){
				  stringBuilder.append(" AND (mc.phone ="+searchParameters.getPhone()+")");
			  }
			  if(searchParameters.getEmailId() != null){
				  stringBuilder.append(" AND (mc.email ='"+searchParameters.getEmailId()+"')");
			  }
			  if(searchParameters.getCity() != null){
				  stringBuilder.append(" AND (a.city ='"+searchParameters.getCity()+"')");
			  }
			  if(searchParameters.getAddress() != null){
				  stringBuilder.append(" AND (a.address_no ='"+searchParameters.getAddress()+"')");
			  }
			  if(searchParameters.getExternalId() != null){
				  stringBuilder.append(" AND (mc.external_id ='"+searchParameters.getExternalId()+"')");
			  }
			  
			  stringBuilder.append(" order by mc.id limit "+searchParameters.getLimit()+" offset "+searchParameters.getOffset()); 
		  }else{
			  
		  }
		   final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
		   
		   return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",stringBuilder.toString(),
				   finalObjectArray, advanceSearchMapper);
		  
	  }catch(EmptyResultDataAccessException accessException){
		  return null;  
	  }
		
	}

  private static final class AdvanceSearchMapper implements RowMapper<AdvanceSearchData> {

      public String schema() {
          return " SELECT t.id AS id, c.display_name AS clientName,c.id as clientId,c.account_no AS accountNo,t.ticket_date as transactionDate," +
          		"  t.status as status,v.code_value as category,a.username as userName FROM b_ticket_master t, m_client c, m_code_value  v," +
          		" m_appuser a WHERE  c.id = t.client_id and t.problem_code=v.id AND a.id=t.createdby_id ";
      }
      
      public String schemaForLeads(){
    	  
    	  return "SELECT p.id as id, p.created_date as transactionDate,concat(p.first_name,' ',p.last_name) as clientName,"+
    			 "p.status as status,p.source_of_publicity as category, a.username as userName,p.mobile_number as mobileNumber," +
    			 "p.email as email,status_remark as clientId,mc.account_no as accountNo,pd.assigned_to as assignTo "+
    			 " from b_prospect p left join m_appuser a on (p.createdby_id = a.id) "+
    			 " left join m_client mc on (p.status_remark=mc.id) "+
    			 " left join b_prospect_detail pd on(p.id =pd.prospect_id "+ 
    			 " and pd.id= (select max(id) from b_prospect_detail pd2 "+
    			 " where  pd2.prospect_id=pd.prospect_id )  ) where  p.is_deleted='N' ";
    			
      }
      
      public String schemaForClients() {
          return "select mc.id as id,mc.activation_date as transactionDate,mc.id as clientId,mc.account_no as accountNo,"+
        		  "concat(mc.firstname,' ',mc.lastname) as clientName,mc.status_enum as status,mc.category_type as category,"+
        		  "0 as userName,a.city as city FROM m_client mc "+
        		  "inner join b_client_address a on  a.client_id = mc.id  and a.address_key = 'PRIMARY' ";
      }
      
      @Override
      public AdvanceSearchData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

          final Long id = rs.getLong("id");
          final LocalDate transactionDate = JdbcSupport.getLocalDate(rs, "transactionDate");
          final Long clientId = rs.getLong("clientId");
          final String accountNo = rs.getString("accountNo");
          final String clientName = rs.getString("clientName");
          final String status = rs.getString("status");
          final String category = rs.getString("category");
          final String userName = rs.getString("userName");
       
          return new AdvanceSearchData(id, clientId, accountNo, clientName, transactionDate, category, status, userName);
      }
  }
}
