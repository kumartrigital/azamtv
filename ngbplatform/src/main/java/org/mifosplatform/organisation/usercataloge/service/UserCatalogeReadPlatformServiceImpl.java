package org.mifosplatform.organisation.usercataloge.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.organisation.usercataloge.data.UserCatalogeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class UserCatalogeReadPlatformServiceImpl implements UserCatalogeReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<UserCatalogeData> paginationHelper = new PaginationHelper<UserCatalogeData>();
	private final PlatformSecurityContext context;
	
	@Autowired
	public UserCatalogeReadPlatformServiceImpl( final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	}
	
	@Override
	public Page<UserCatalogeData> retrieveUserCataloge(SearchSqlQuery searchUserCataloge) {
		UserCatalogeMapper usercatalogeMapper = new UserCatalogeMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(usercatalogeMapper.schema());
        sqlBuilder.append(" where uc.id IS NOT NULL and uc.is_deleted = 'N'");
        
        String sqlSearch = searchUserCataloge.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (id like '%"+sqlSearch+"%' OR" 
	    			+ " user_id like '%"+sqlSearch+"%' OR"
	    			+ " cataloge_id like '%"+sqlSearch+"%' OR"
	    			+ " username like '%"+sqlSearch+"%' OR"
	    			+ " name like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
        sqlBuilder.append(" GROUP BY uc.user_id ");

        if (searchUserCataloge.isLimited()) {
            sqlBuilder.append(" limit ").append(searchUserCataloge.getLimit());
        }

        if (searchUserCataloge.isOffset()) {
            sqlBuilder.append(" offset ").append(searchUserCataloge.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, usercatalogeMapper);
	}
	
	@Override
	public UserCatalogeData retrieveUserCataloge(Long usercatalogeId) {

		try{
			UserCatalogeMapper usercatalogeMapper = new UserCatalogeMapper();
			String sql = "SELECT "+usercatalogeMapper.schema()+" WHERE uc.is_deleted = 'N' AND uc.id = ?";
			return jdbcTemplate.queryForObject(sql, usercatalogeMapper,new Object[]{usercatalogeId});
			}catch(EmptyResultDataAccessException ex){
				return null;
		}
	}
	

	@Override
	public List<UserCatalogeData> retrieveUserCatalogeOfUser(Long userId) {
		try{
			UserCatalogeMapper usercatalogeMapper = new UserCatalogeMapper();
			String sql = "SELECT "+usercatalogeMapper.schema()+" WHERE uc.is_deleted = 'N' AND uc.user_id = ?";
			return jdbcTemplate.query(sql, usercatalogeMapper,new Object[]{userId});
			}catch(EmptyResultDataAccessException ex){
				return null;
		}
	}
	
	private class UserCatalogeMapper implements RowMapper<UserCatalogeData> {
	   
		@Override
		public UserCatalogeData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
	    	final Long id = rs.getLong("id");
			final Long userId = rs.getLong("userId");
			final Long catalogeId = rs.getLong("catalogeId");
			final String username = rs.getString("username");
			final String name = rs.getString("name");
			
			return new UserCatalogeData(id, userId, catalogeId,username,name);
		}
	    
		public String schema() {
			
			//return " uc.id AS id, uc.user_id AS userId, uc.cataloge_id AS catalogeId From b_user_cataloge uc "; 	 
			
			return "uc.id AS id, uc.user_id AS userId, uc.cataloge_id AS catalogeId,scd.name AS name,"+
				   "ud.username as username From b_user_cataloge uc " +
			       "join b_sales_cataloge scd on scd.id = uc.cataloge_id " +
			       "join m_appuser ud on ud.id = uc.user_id ";
		}
	}
	
	@Override
	public List<SalesCatalogeData> retrieveSelectedSalesCataloges(Long userId) {
		try{
			SalesCatalogeMapper salesCatalogeMapper = new SalesCatalogeMapper();
			String sql = "SELECT "+salesCatalogeMapper.schema()+" WHERE ucd.is_deleted = 'N' AND ucd.user_id = ?";
			return jdbcTemplate.query(sql, salesCatalogeMapper,new Object[]{userId});
		}catch(EmptyResultDataAccessException ex){
			return null;
		}	
	}
    
	private static final class SalesCatalogeMapper implements RowMapper<SalesCatalogeData>{
		
		public String  schema(){
			return "sc.id AS id,sc.name AS name,sc.sales_plan_category_id AS salesPlanCategoryId FROM b_sales_cataloge sc"
					+ " join b_user_cataloge ucd on ucd.cataloge_id = sc.id ";
			
		}
			
		@Override
		public SalesCatalogeData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			final Long id= rs.getLong("id");
			final String name = rs.getString("name");
			final Long salesPlanCategoryId= rs.getLong("salesPlanCategoryId");
			return new SalesCatalogeData(id,name,salesPlanCategoryId,null);
		}	
		
	}
	
	//This method is used for get Plans
		@Override
		public List<SalesCatalogeData> retrievePlans(Long userId,String category) {
			try{
				SalesCatalogeMapping salesCatalogeMapping = new SalesCatalogeMapping();
				String sql = "SELECT DISTINCT "+salesCatalogeMapping.schema()+" WHERE sc.is_deleted = 'N' AND ucd.user_id = ? ";
				if(category != null && !"all".equalsIgnoreCase(category)){
					 sql = sql+ " AND sc.sales_plan_category_id in (select id from m_code_value where code_value = '"+category+"') ";
				}
				return jdbcTemplate.query(sql, salesCatalogeMapping,new Object[]{userId});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}	
		}
	    
		private static final class SalesCatalogeMapping implements RowMapper<SalesCatalogeData>{
			
			public String  schema(){
				return "sc.id AS id,sc.name AS name,sc.sales_plan_category_id AS salesPlanCategoryId,mcv.code_value AS salesPlanCategoryName FROM b_sales_cataloge sc"
						+ " join b_user_cataloge ucd on ucd.cataloge_id = sc.id  left join m_code_value mcv ON sc.sales_plan_category_id = mcv.id ";
				
			}
				
			@Override
			public SalesCatalogeData mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				final Long id= rs.getLong("id");
				final String name = rs.getString("name");
				final Long salesPlanCategoryId= rs.getLong("salesPlanCategoryId");
				final String salesPlanCategoryName = rs.getString("salesPlanCategoryName");
				return new SalesCatalogeData(id,name,salesPlanCategoryId,salesPlanCategoryName);
			}	
			
		}
		
		
	
}
