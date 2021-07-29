package org.mifosplatform.crm.clientprospect.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.axis2.databinding.types.soapencoding.Decimal;
import org.mifosplatform.crm.clientprospect.data.QuoteData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class QuoteReadPlatformServiceImp implements QuoteReadPlatformService {
	
	
	
	private final JdbcTemplate jdbcTemplate;
	
	@Autowired
	public QuoteReadPlatformServiceImp( final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	
	}
	
	
	
	@Override
	public List<QuoteData> retrievePlans(Long serviceId) {
		try{
	      	PlansMapper plansMapper = new PlansMapper();
			String sql = "SELECT distinct "+plansMapper.schema()+" WHERE  pd.service_id = ? ";
			return jdbcTemplate.query(sql, plansMapper,new Object[]{serviceId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
		
	}
	private class PlansMapper implements RowMapper<QuoteData> {
	    @Override
		public QuoteData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long planId = rs.getLong("planId");
			final Long serviceId = rs.getLong("serviceId");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			return new QuoteData(planId, serviceId,planCode,planDescription);
		}
	    
		public String schema() {
			
			return " pd.plan_id as planId,pm.plan_code as planCode,pm.plan_description as planDescription,pd.service_id as serviceId from b_plan_detail pd join b_plan_master pm on pm.id=pd.plan_id ";
			
		}
	}
	@Override
	public List<QuoteData> retrievePlansPricing(Long planId, String chargecode) {
		try{
			QuoteMapper quoteMapper = new QuoteMapper();
			String sql = "SELECT DISTINCT "+quoteMapper.schema()+"where pp.plan_id = '"+planId+"' and pp.charge_code='"+chargecode+"' and pp.currencyId < 1001 ";
			return jdbcTemplate.query(sql, quoteMapper,new Object[]{});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}

	private class QuoteMapper implements RowMapper<QuoteData> {
	    @Override
		public QuoteData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long planId = rs.getLong("planId");
			final String chargeCode = rs.getString("chargeCode");
			final BigDecimal recurringCharge = rs.getBigDecimal("recurringCharge");
			final BigDecimal oneTimeCharge = rs.getBigDecimal("oneTimeCharge");
			final Long currencyId = rs.getLong("currencyId");
			return new QuoteData(planId, chargeCode, recurringCharge,oneTimeCharge, currencyId);
		}
	    
		public String schema() {
			
			return "  pp.plan_id as planId, pp.charge_code as chargeCode,(select sum(bp.price) from b_plan_pricing bp join "+
	               " b_charge_codes cc ON cc.charge_code = bp.charge_code where cc.charge_type = 'RC'and bp.charge_code = pp.charge_code and bp.plan_id = pp.plan_id and bp.is_deleted = 'N') as recurringCharge," + 
                   " (select sum(bp.price) from b_plan_pricing bp join b_charge_codes cc ON cc.charge_code = bp.charge_code where " +
	               " cc.charge_type = 'NRC'and bp.charge_code = pp.charge_code and bp.plan_id = pp.plan_id and bp.is_deleted = 'N') as oneTimeCharge, pp.currencyId as currencyId from b_plan_pricing pp ";
			
		}
	}
	@Override
	public List<QuoteData> retrivequotes(Long leadId ,Long quoteId) {
	
	try {
		final QuotesMapper quotesMapper = new QuotesMapper(); 
	StringBuilder sql = new StringBuilder("select distinct ");
	sql.append(quotesMapper.schema());
	sql.append(" WHERE  q.lead_id = ? ");
	if(quoteId!=null){
		sql.append("and q.id= '"+quoteId+"'");
	}
	sql.append(" and qo.is_deleted = 'N'  ");	
	return jdbcTemplate.query(sql.toString(), quotesMapper, new Object[] { leadId });
	} catch (EmptyResultDataAccessException e) {
		return null;
	}

}
	private class QuotesMapper implements RowMapper<QuoteData> {
	    @Override
		public QuoteData mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	final String quoteNumber  = rs.getString("quoteNumber");
	    	final Long QuoteId = rs.getLong("QuoteId");
	    	final Long LeadId = rs.getLong("LeadId");
	    	final String Status  = rs.getString("Status");
	    	final String serviceCode  = rs.getString("serviceCode");
	    	final String planDescription  = rs.getString("planDescription");
	    	final BigDecimal recurringCharge = rs.getBigDecimal("recurringCharge");
			final BigDecimal  oneTimeCharge = rs.getBigDecimal("oneTimeCharge");
			final BigDecimal  TotalCharge = rs.getBigDecimal("TotalCharge");
			final Long serviceId = rs.getLong("serviceId");
			final Long planId = rs.getLong("planId");
			final String chargeCode  = rs.getString("chargeCode");
			final String frequency  = rs.getString("frequency");
			final String Notes  = rs.getString("Notes");
			return new QuoteData(quoteNumber,QuoteId, LeadId,Status,serviceCode,planDescription,recurringCharge,oneTimeCharge,TotalCharge,serviceId,planId,chargeCode,frequency,Notes);
		}
	    
		public String schema() {
			
			return "q.quote_no as quoteNumber, q.id as QuoteId,q.lead_id as LeadId,q.quote_status as Status,qo.service_code as serviceCode,qo.plan_name as planDescription,qo.plan_recurirng_charge as recurringCharge,qo.plan_onetime_charge as oneTimeCharge,(select sum(total_charge) from b_quote bq where bq.lead_id = q.lead_id and bq.id = qo.quote_id) as TotalCharge,"+
			"s.id as serviceId,pm.id as planId, qo.charge_code as chargeCode,cc.charge_description as frequency,q.notes as Notes from b_quote q join b_quote_order qo on qo.quote_id =q.id " +
			"join b_service s on s.service_code = qo.service_code join b_plan_master pm on pm.plan_description = qo.plan_name join b_charge_codes cc on cc.charge_code = qo.charge_code";
			
		}
	}
	@Override
	public List<QuoteData> statusquotes(Long leadId) {
		try{
	      	QuotesMappers quotesMappers = new QuotesMappers();
			String sql = "SELECT distinct "+quotesMappers.schema()+" WHERE  q.lead_id =? and q.quote_status = 'Review' and qo.is_deleted = 'N' ";
			
			return jdbcTemplate.query(sql, quotesMappers,new Object[]{leadId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}
	private class QuotesMappers implements RowMapper<QuoteData> {
	    @Override
		public QuoteData mapRow(ResultSet rs, int rowNum) throws SQLException {
	    	final String quoteNumber  = rs.getString("quoteNumber");
	    	final Long QuoteId = rs.getLong("QuoteId");
	    	final String Status  = rs.getString("Status");
			return new QuoteData(quoteNumber,QuoteId,Status);
		}
	    
		public String schema() {
			
			return "q.quote_no as quoteNumber,q.id as QuoteId,q.quote_status as Status from b_quote q join b_quote_order qo ON qo.quote_id = q.id";
			
		}
	}
}
