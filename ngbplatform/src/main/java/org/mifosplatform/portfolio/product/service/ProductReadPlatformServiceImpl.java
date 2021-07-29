package org.mifosplatform.portfolio.product.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.product.domain.ProductDetailData;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
public class ProductReadPlatformServiceImpl implements ProductReadPlatformService {

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ProductData> paginationHelper = new PaginationHelper<ProductData>();
	
	
	@Autowired
	public ProductReadPlatformServiceImpl(final TenantAwareRoutingDataSource dataSource,final PlatformSecurityContext context) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
	
	}

	@Override
	public Page<ProductData> retrieveProduct(SearchSqlQuery searchProduct) {
		
		ProductMapper productMapper = new ProductMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(productMapper.schema());
        //sqlBuilder.append(" where p.id IS NOT NULL and p.is_deleted = 'N'");
        
        String sqlSearch = searchProduct.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and (p.id like '%"+sqlSearch+"%' OR" 
	    			+ " product_code like '%"+sqlSearch+"%' OR"
	    			+ " product_description like '%"+sqlSearch+"%' OR"
	    			+ " product_category like '%"+sqlSearch+"%' OR"
	    			+ " service_id like '%"+sqlSearch+"%' OR"
	    	/*		+ " provision_id like '%"+sqlSearch+"%' OR"
	    			+ " validity_start like '%"+sqlSearch+"%' OR"*/
	    			+ " service_code like '%"+sqlSearch+"%' OR"
	    			+ " s.status like '%"+sqlSearch+"%' OR"
	    			+ " is_Bouquet like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }


        if (searchProduct.isLimited()) {
            sqlBuilder.append(" limit ").append(searchProduct.getLimit());
        }

        if (searchProduct.isOffset()) {
            sqlBuilder.append(" offset ").append(searchProduct.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, productMapper);
	
	}

	//this is to retrive a particular record 
	@Override
	public ProductData retrieveProduct(Long productId) {
		try{
	      	ProductMapper productMapper = new ProductMapper();
			String sql = "SELECT "+productMapper.schema()+" WHERE p.is_deleted = 'N' AND p.id = ?";
			return jdbcTemplate.queryForObject(sql, productMapper,new Object[]{productId});
			}catch(EmptyResultDataAccessException ex){
				return null;
		}
	
	}
	
	protected static final class ProductMapper implements RowMapper<ProductData> {

		@Override
		public ProductData mapRow(final ResultSet rs, final int rowNum)throws SQLException {
			
			final Long id=rs.getLong("id");
			final String productCode = rs.getString("productCode");
			final String productDescription=rs.getString("productDescription");
			final String productCategory=rs.getString("productCategory");
			final Long serviceId=rs.getLong("serviceId");
			final String serviceCode=rs.getString("serviceCode");
			/*final Long provisioningCode=rs.getLong("provisionId");
			final Date validityStart=rs.getDate("validityStart");
			final Date validityEnd=rs.getDate("validityEnd");*/
			final String status = rs.getString("status");
			final Boolean isBouquet = rs.getBoolean("isBouquet");
			

			return new ProductData(id,productCode,productDescription,productCategory,serviceId,serviceCode,/*provisioningCode,validityStart,validityEnd,*/status,isBouquet);

		}


		public String schema() {
			return "p.id AS id,p.product_code AS productCode,p.product_description AS productDescription,p.product_category AS productCategory," +
					"p.service_id as serviceId,s.service_code AS serviceCode,p.status as status,p.is_Bouquet AS isBouquet FROM b_product p "+
				    "left join b_service s on s.id = p.service_id";
			
			 /*p.provision_id as provisionId,pcm.provision_code as provisionCode,p.validity_start as validityStart,p.validity_end as validityEnd,*/
		}

   }

	@Override
	public Collection<ProductDetailData> retrieveProductDetailsAgainestMasterIdandParamCategory(Long productId,
			String paramCategory) {
		try{
			   this.context.authenticatedUser();
			   final ProductDetailMapper mapper = new ProductDetailMapper();
			   String sql="select "+mapper.schema();
			   if(paramCategory != null){
				   sql = sql +" AND sd.param_category = '"+paramCategory+"' ";
			   }
			   return this.jdbcTemplate.query(sql, mapper,new Object[] {productId});
			}catch(EmptyResultDataAccessException dve){
				return null;	
			}
		}
	
	@Override
	public Collection<ProductDetailData> retrieveProductDetails(final Long productId,String paramCategory) {
		try{
		   this.context.authenticatedUser();
		   final ProductDetailMapper mapper = new ProductDetailMapper();
		   String sql="select "+mapper.schema();
		   if(paramCategory != null){
			   sql = sql +" AND sd.param_category = '"+paramCategory+"' ";
		   }
		   return this.jdbcTemplate.query(sql, mapper,new Object[] {productId});
		}catch(EmptyResultDataAccessException dve){
			return null;	
		}
	}
	
	private static final class ProductDetailMapper implements RowMapper<ProductDetailData>{

		@Override
		public ProductDetailData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long paramName = rs.getLong("paramName");
			final String paramType = rs.getString("paramType");
			final String paramValue = rs.getString("paramValue");
			final String codeParamName = rs.getString("codeParamName");
			final String productCode = rs.getString("productCode");
			final String paramCategory = rs.getString("paramCategory");
			return new ProductDetailData(id,paramName,paramType,paramValue,codeParamName,productCode,paramCategory);
		
		}
		
		public String schema() {
			return "  sd.id AS id, sd.param_name AS paramName, sd.param_type AS paramType, sd.param_value AS paramValue, mcv.code_value AS codeParamName, " +
				   " (select product_code from b_product where id= sd.product_id) as productCode,sd.param_category AS paramCategory FROM b_product_detail sd left join m_code_value mcv ON mcv.id = sd.param_name " +
				   " WHERE sd.product_id = ? AND  sd.is_deleted = 'N' ";
			}
		
		public String schemaForPlan() {
			return "  sd.id AS id, sd.param_name AS paramName, sd.param_type AS paramType, sd.param_value AS paramValue, mcv.code_value AS codeParamName, " +
				   " s.product_code as productCode,sd.param_category AS paramCategory FROM b_product_detail sd left join m_code_value mcv ON mcv.id = sd.param_name "+
				   " JOIN b_product s ON s.id = sd.product_id "+
				   " JOIN b_plan_detail pl ON pl.product_code = s.product_code " +
				   " JOIN b_plan_master p ON p.id = pl.plan_id "+
				   " WHERE sd.is_deleted = 'N'  ";
			}
		
	}

	@Override
	public List<ServiceData> retriveProducts(String serviceCategory, String productCode) {
		
		this.context.authenticatedUser();
		ProductDetailsMapper mapper = new ProductDetailsMapper();
		String sql = "select " + mapper.schema();
		if(null != serviceCategory){
			sql = sql + " and da.product_category = '"+serviceCategory+"'";
		}
		if(productCode!=null){
			productCode= productCode+"%";
			sql = sql + " and da.product_code like '"+productCode+"'";
		}
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	
	}
	private static final class ProductDetailsMapper implements RowMapper<ServiceData> {

		@Override
		public ServiceData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String productCode = rs.getString("product_code");
			String productDescription = rs.getString("product_description");
			Long serviceId = rs.getLong("service_id");
			String serviceCode = rs.getString("service_code");
			return new ServiceData(id,productCode,productDescription,null,serviceId,serviceCode);

		}
		

		public String schema() {
			return "da.id as id, da.product_code as product_code, da.product_description as product_description,da.service_id as service_id,s.service_code as service_code "
					+ " from b_product da left join b_service s ON da.service_id = s.id where da.is_deleted='N' ";

			
			
/*return "da.id as id,da.product_code as product_code,da.product_description as product_description,da.service_id as service_id,s.service_code as service_code,pd.param_value as paramvalue "
+"(select code_value from m_code_value mcv where mcv.id = pd.param_name) as paramName from b_product da left join b_service s ON da.service_id = s.id left join b_product_detail pd on pd.product_id = da.id "
+" where da.is_deleted = 'N' ";*/
		}

	}
	
		
	@Override
	public Long retrieveSession(String username) {

		try {
			
			final String sql = "select pd.param_value from m_code_value cv join  b_product_detail pd on cv.id = pd.param_name " +
					"inner join b_product as p on pd.product_id = p.id join b_order_price op on op.product_id = p.id join b_orders o " +
					"on o.id = op.order_id join m_client c on c.id = o.client_id JOIN b_clientuser cu on cu.client_id= c.id where " +
					"cu.username = ? and cv.code_value = 'SESSION' ";
			
			return jdbcTemplate.queryForLong(sql, new Object[] {username});
			} catch (EmptyResultDataAccessException e) {
			return null;
			}

			
	}

}
