package org.mifosplatform.organisation.salescataloge.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SalesCatalogeReadPlatformServiceImpl implements SalesCatalogeReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<SalesCatalogeData> paginationHelper = new PaginationHelper<SalesCatalogeData>();
	private final PlatformSecurityContext context;
	
	@Autowired
	public SalesCatalogeReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource, final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}


	@Override
	public SalesCatalogeData retrieveSalesCataloge(Long salescatalogeId) {
		try{
			SalesCatalogeMapper salesCatalogeMapper = new SalesCatalogeMapper();
			String sql = "SELECT DISTINCT "+salesCatalogeMapper.schema()+" WHERE sc.is_deleted = 'N' AND sc.id = ?";
			return jdbcTemplate.queryForObject(sql, salesCatalogeMapper,new Object[]{salescatalogeId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}

	@Override
	public Page<SalesCatalogeData> retrieveSalesCataloge(SearchSqlQuery searchSalesCataloge) {
		SalesCatalogeMapper salesCatalogeMapper = new SalesCatalogeMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(salesCatalogeMapper.schema());
        sqlBuilder.append(" where sc.id IS NOT NULL and sc.is_deleted = 'N'");
        
        String sqlSearch = searchSalesCataloge.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ "name like '%"+sqlSearch+"%' OR"
	    			+ "sales_plan_category_Id like '%"+sqlSearch+"%' OR"
	    			+" code_value like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchSalesCataloge.isLimited()) {
            sqlBuilder.append(" limit ").append(searchSalesCataloge.getLimit());
        }

        if (searchSalesCataloge.isOffset()) {
            sqlBuilder.append(" offset ").append(searchSalesCataloge.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, salesCatalogeMapper);
	}

	private class SalesCatalogeMapper implements RowMapper<SalesCatalogeData> {
	    @Override
		public SalesCatalogeData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final String name = rs.getString("name");
			final Long salesPlanCategoryId = rs.getLong("salesPlanCategoryId");
			final String salesPlanCategoryName = rs.getString("salesPlanCategoryName");
			
			return new SalesCatalogeData(id, name,salesPlanCategoryId,salesPlanCategoryName);
		}
	    
		public String schema() {
			
			return "sc.id AS id,sc.name AS name,sc.sales_plan_category_id AS salesPlanCategoryId,mcv.code_value AS salesPlanCategoryName "+
			       "FROM b_sales_cataloge sc left join m_code_value mcv ON sc.sales_plan_category_id = mcv.id ";
			
		}
	}

	@Override
	public List<SalesCatalogeData> retrieveSalesCatalogesForDropdown() {
		try{
			SalesCatalogeForDropDown salescatalogeMapper = new SalesCatalogeForDropDown();
			String sql = "SELECT DISTINCT "+salescatalogeMapper.schema()+" WHERE scd.is_deleted = 'N'";
			return jdbcTemplate.query(sql, salescatalogeMapper,new Object[]{});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}	
	}
	
	private static final class SalesCatalogeForDropDown implements RowMapper<SalesCatalogeData>{
		
		public String  schema(){
			return "scd.id AS id,scd.name AS name,scd.sales_plan_category_id AS salesPlanCategoryId,mcv.code_value AS salesPlanCategoryName "+
		           "FROM b_sales_cataloge scd left join m_code_value mcv ON scd.sales_plan_category_id = mcv.id ";
			
		}
			
		@Override
		public SalesCatalogeData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long id= rs.getLong("id");
			final String name = rs.getString("name");
			final Long salesPlanCategoryId = rs.getLong("salesPlanCategoryId");
			final String salesPlanCategoryName = rs.getString("salesPlanCategoryName");
			
			return new SalesCatalogeData(id,name,salesPlanCategoryId,salesPlanCategoryName);
		}	
		
	}
	
	@Override
	public List<PlanData> retrieveSelectedPlans(Long salescatalogeId) {
		try{
			PlanMapper planMapper = new PlanMapper();
			String sql = "SELECT DISTINCT "+planMapper.schema()+" WHERE sd.is_deleted = 'N' AND sd.cataloge_id = ?";
			return jdbcTemplate.query(sql, planMapper,new Object[]{salescatalogeId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}	
	}
    
	private static final class PlanMapper implements RowMapper<PlanData>{
		
		public String  schema(){
			return "p.id AS id,p.plan_code AS planCode,p.plan_description AS planDescription, p.plan_poid as planPoid,"
					+ "pd.deal_poid as dealPoid FROM b_plan_master p join b_sales_cataloge_mapping sd on " +
					"sd.plan_id = p.id join b_plan_detail pd on p.id=pd.plan_id  ";
			
		}
			
		@Override
		public PlanData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long id= rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final String planPoid=rs.getString("planPoid");
			final String dealPoid=rs.getString("dealPoid");
			return new PlanData(id,planCode,planDescription,planPoid,dealPoid,null);
		}	
		
	}
	
}
