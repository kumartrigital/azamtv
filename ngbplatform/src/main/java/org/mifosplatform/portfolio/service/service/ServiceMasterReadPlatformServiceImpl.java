package org.mifosplatform.portfolio.service.service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;
import org.mifosplatform.portfolio.service.data.ServiceMasterData;
import org.mifosplatform.portfolio.service.data.ServiceMasterOptionsData;
import org.mifosplatform.portfolio.service.data.ServiceStatusEnumaration;
import org.mifosplatform.portfolio.service.data.ServiceTypeEnum;
import org.mifosplatform.portfolio.service.data.ServiceUnitTypeEnum;
import org.mifosplatform.portfolio.service.data.ServiceUnitTypeEnumaration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
@Service
public class ServiceMasterReadPlatformServiceImpl implements  ServiceMasterReadPlatformService{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<ServiceMasterOptionsData> paginationHelper = new PaginationHelper<ServiceMasterOptionsData>();

	@Autowired
	public  ServiceMasterReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Collection<ServiceMasterData> retrieveAllServiceMasterData() {
		
		this.context.authenticatedUser();

		final ServiceMasterMapper mapper = new ServiceMasterMapper();
		final String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	protected static final class ServiceMasterMapper implements RowMapper<ServiceMasterData> {

		@Override
		public ServiceMasterData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			final String discounType = rs.getString("serviceType");
			final String discountValue=rs.getString("categoryType");


			return new ServiceMasterData(discounType,discountValue);

		}


		public String schema() {
			return "d.servicetype as servicetype , d.categorytype as categorytype from m_servicemaster_type d";
		}
	}

	@Override
	public Page<ServiceMasterOptionsData> retrieveServices(SearchSqlQuery searchCodes,String serviceCategory) {
		this.context.authenticatedUser();

		final ServiceMapper mapper = new ServiceMapper();
		String sql = "select " + mapper.schema()+" where d.is_deleted='n' ";
		StringBuilder sqlBuilder = new StringBuilder(200);
		
		
		if(null !=serviceCategory){
			sql = sql + " AND d.service_category = '"+serviceCategory+"' ";
		}
		sqlBuilder.append(sql);
		if (searchCodes.isLimited()) {
            sqlBuilder.append(" limit ").append(searchCodes.getLimit());
        }
        if (searchCodes.isOffset()) {
            sqlBuilder.append(" offset ").append(searchCodes.getOffset());
        }

		//return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {}, mapper);

	}

	protected static final class ServiceMapper implements RowMapper<ServiceMasterOptionsData> {

		@Override
		public ServiceMasterOptionsData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			final Long id=rs.getLong("id");
			final String serviceCode = rs.getString("serviceCode");
			final String serviceDescription=rs.getString("serviceDescription");
			final String serviceType=rs.getString("serviceType");
			final String serviceUnitType=rs.getString("serviceUnitType");
			final String status=rs.getString("status");
			final String isOptional=rs.getString("isOptional");
			final String isAutoProvision=rs.getString("isAuto");
			final String serviceCategory = rs.getString("serviceCategory");
			

			return new ServiceMasterOptionsData(id,serviceCode,serviceDescription,serviceType,serviceUnitType,status,isOptional,isAutoProvision,serviceCategory);

		}


		public String schema() {
				return "d.id AS id,d.service_code AS serviceCode,d.service_description AS serviceDescription,if(d.service_category = 'S',d.service_type,(select service_code from b_service where id=d.service_type)) AS serviceType," +
						"d.service_unittype as serviceUnitType,d.status as status,d.is_optional as isOptional,d.is_auto as isAuto,d.service_category as serviceCategory "+
						" FROM b_service d";
		}

}

	@Override
	public ServiceMasterOptionsData retrieveIndividualService(final Long serviceId) {
		final ServiceMapper mapper = new ServiceMapper();
		final String sql = "select " + mapper.schema()+" where d.id="+serviceId;

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});

	}

	private static final class ServiceDetailsMapper implements RowMapper<ServiceData> {

		public String schema() {
			return "da.id as id, da.service_code as service_code, da.service_description as service_description "
					+ " from b_service da where da.is_deleted='N' ";

		}

		@Override
		public ServiceData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String serviceCode = rs.getString("service_code");
			String serviceDescription = rs.getString("service_description");
			return new ServiceData(id,null,null,null,null, null,null,null,null,null,null,null,serviceCode,serviceDescription,null);

		}
	}

	@Override
	public List<EnumOptionData> retrieveServicesTypes() {
		
		final EnumOptionData tv = ServiceStatusEnumaration.serviceType(ServiceTypeEnum.TV);
		final EnumOptionData bb= ServiceStatusEnumaration.serviceType(ServiceTypeEnum.BB);
		final EnumOptionData voId= ServiceStatusEnumaration.serviceType(ServiceTypeEnum.VOIP);
		final EnumOptionData iptv= ServiceStatusEnumaration.serviceType(ServiceTypeEnum.IPTV);
		final EnumOptionData vod= ServiceStatusEnumaration.serviceType(ServiceTypeEnum.VOD);
		final EnumOptionData none= ServiceStatusEnumaration.serviceType(ServiceTypeEnum.NONE);
		
		final List<EnumOptionData> categotyType = Arrays.asList(tv,bb,voId,iptv,vod,none);
			return categotyType;
	}

	@Override
	public List<EnumOptionData> retrieveServiceUnitType() {
		final EnumOptionData onOff = ServiceUnitTypeEnumaration.serviceUnitType(ServiceUnitTypeEnum.ON_OFF);
		final EnumOptionData scheme= ServiceUnitTypeEnumaration.serviceUnitType(ServiceUnitTypeEnum.SCHEME);
		final EnumOptionData quantity = ServiceUnitTypeEnumaration.serviceUnitType(ServiceUnitTypeEnum.QUANTITY);
		
		final List<EnumOptionData> categotyType = Arrays.asList(onOff,scheme,quantity);
			return categotyType;
	}

	@Override
	public List<ServiceData> retrieveAllServices(String serviceType) {


		context.authenticatedUser();
		ServiceDetailsMapper mapper = new ServiceDetailsMapper();

		String sql = "select " + mapper.schema()+" and da.is_optional = ?";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {serviceType});

	
}

	@Override
	public List<ServiceData> retriveServices(String serviceCategory) {

		this.context.authenticatedUser();
		ServiceDetailsMapper mapper = new ServiceDetailsMapper();
		String sql = "select " + mapper.schema();
		if(null != serviceCategory){
			sql = sql + " and da.service_category = '"+serviceCategory+"' ";
		}
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	
	}
	
	@Override
	public Collection<ServiceDetailData> retrieveServiceDetails(final Long serviceId,String paramCategory) {
		try{
		   this.context.authenticatedUser();
		   final ServiceDetailMapper mapper = new ServiceDetailMapper();
		   String sql="select "+mapper.schema();
		   if(paramCategory != null){
			   sql = sql +" AND sd.param_category = '"+paramCategory+"' ";
		   }
		   return this.jdbcTemplate.query(sql, mapper,new Object[] {serviceId});
		}catch(EmptyResultDataAccessException dve){
			return null;	
		}
	}
	
	@Override
	public List<ServiceDetailData> retriveServiceDetailsOfPlan(Long planId) {
		try{
			   //this.context.authenticatedUser();
			   final ServiceDetailMapper mapper = new ServiceDetailMapper();
			   final String sql="select distinct "+mapper.schemaForPlan()+" AND pln.id = ?";
			   return this.jdbcTemplate.query(sql, mapper,new Object[] {planId});
			}catch(EmptyResultDataAccessException dve){
				return null;	
			}
	}
	
	private static final class ServiceDetailMapper implements RowMapper<ServiceDetailData>{

		public String schema() {
			return "  sd.id AS id, sd.param_name AS paramName, sd.param_type AS paramType, sd.param_value AS paramValue, mcv.code_value AS codeParamName, " +
				   " (select service_code from b_service where id= sd.service_id) as serviceCode,sd.param_category AS paramCategory FROM b_service_detail sd left join m_code_value mcv ON mcv.id = sd.param_name " +
				   " WHERE sd.service_id = ? AND  sd.is_deleted = 'N' ";
			}
		public String schemaForPlan() {
				/*return "  sd.id AS id, sd.param_name AS paramName, sd.param_type AS paramType, sd.param_value AS paramValue, mcv.code_value AS codeParamName, " +
					   " s.service_code as serviceCode,sd.param_category AS paramCategory FROM b_service_detail sd left join m_code_value mcv ON mcv.id = sd.param_name "+
					   " JOIN b_service s ON s.id = sd.service_id "+
					   " JOIN b_plan_detail pl ON pl.service_code = s.service_code " +
					   " JOIN b_plan_master p ON p.id = pl.plan_id "+
					   " WHERE sd.is_deleted = 'N'  ";*/
			return " pd.id AS id, pd.param_name AS paramName, "
			     + " pd.param_type AS paramType, pd.param_value AS paramValue,"
			     + " mcv.code_value AS codeParamName, p.product_code as serviceCode,"
			     + " pd.param_category AS paramCategory FROM b_product_detail pd "
			     + " left join m_code_value mcv ON mcv.id = pd.param_name"
			     + " JOIN b_product p ON p.id = pd.product_id"
			     + " JOIN b_plan_detail pl ON pl.product_id = p.id "
			     + " JOIN b_plan_master pln ON pln.id = pl.plan_id "
			     + " WHERE pd.is_deleted = 'N' ";
					
			}
		
		@Override
		public ServiceDetailData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final Long paramName = rs.getLong("paramName");
			final String paramType = rs.getString("paramType");
			final String paramValue = rs.getString("paramValue");
			final String codeParamName = rs.getString("codeParamName");
			final String serviceCode = rs.getString("serviceCode");
			final String paramCategory = rs.getString("paramCategory");
			return new ServiceDetailData(id,paramName,paramType,paramValue,codeParamName,serviceCode,paramCategory);
		}
		
	}

	@Override
	public List<ServiceData> retriveServicesForDropdown(String serviceCode) {

		this.context.authenticatedUser();
		ServiceDetailsMapper mapper = new ServiceDetailsMapper();
		String sql = "select " + mapper.schema()+" and d.service_code = ?";

		return this.jdbcTemplate.query(sql, mapper, new Object[] {serviceCode});

	
	}

	@Override
	public Collection<ServiceDetailData> retrieveServiceDetailsAgainestMasterIdandParamCategory(Long serviceId,
			String paramCategory) {
		try{
			   this.context.authenticatedUser();
			   final ServiceDetailMapper mapper = new ServiceDetailMapper();
			   final String sql="select "+mapper.schema()+" AND sd.param_category = '"+paramCategory+"' ";
			   return this.jdbcTemplate.query(sql, mapper,new Object[] {serviceId});
			}catch(EmptyResultDataAccessException dve){
				return null;	
			}
		}


	@Override
	public ServiceData retriveServiceParam(String serviceCode) {
		try{
			   this.context.authenticatedUser();
			   final ServiceParamMapper mapper = new ServiceParamMapper();
			   final String sql="select "+mapper.schema()+" b.param_category='S' and a.service_code='"+serviceCode+"'";
			   return this.jdbcTemplate.queryForObject(sql, mapper,new Object[] {});
			}catch(EmptyResultDataAccessException dve){
				return null;	
			}
	}
	
	private static final class ServiceParamMapper implements RowMapper<ServiceData>{

		public String schema() {
			return "a.id,param_name from b_service a, b_service_detail b where a.id=b.service_id and ";
			}
		
		@Override
		public ServiceData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final Long id = rs.getLong("id");
			final String paramName = rs.getString("param_name");
			return new ServiceData(id,paramName);
		}
		
	}

	@Override
	public ServiceMasterOptionsData retriveServicesByUsingSerailNumber(String serialNumber, String serviceType,
			String serviceParamName) {
		try{
			   this.context.authenticatedUser();
			   final ServiceParamMappers sp = new ServiceParamMappers();
			   final String sql="select distinct "+sp.schema()+" where s.service_type='"+serviceType+"' and cv.code_value = '"+serviceParamName+"' and i.serial_no = ? ";
			   return this.jdbcTemplate.queryForObject(sql, sp,new Object[] {serialNumber});
			}catch(EmptyResultDataAccessException dve){
				return null;	
			}

	}
	private static final class ServiceParamMappers implements RowMapper<ServiceMasterOptionsData>{

		public String schema() {
			return " ne.id as provisioningId,ne.system_code as systemCode,s.service_code as serviceCode,s.id as serviceId,sd.param_name as paramName "+
				   " from b_item_attribute ia join b_item_detail i ON i.item_model = ia.id join b_network_element ne ON ne.id = ia.provisioning_id "+
				   "  join b_service s join b_service_detail sd ON s.id = sd.service_id join m_code_value cv on cv.id = sd.param_name ";
			}
		
		@Override
		public ServiceMasterOptionsData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final Long provisioningId = rs.getLong("provisioningId");
			final String systemCode = rs.getString("systemCode");
			final String serviceCode = rs.getString("serviceCode");
			final Long serviceId = rs.getLong("serviceId");
			final String paramName = rs.getString("paramName");
			ServiceMasterOptionsData serviceMasterOptionsData= new  ServiceMasterOptionsData();
			serviceMasterOptionsData.setProvisioningId(provisioningId);
			serviceMasterOptionsData.setSystemCode(systemCode);
			serviceMasterOptionsData.setServiceCode(serviceCode);
			serviceMasterOptionsData.setServiceId(serviceId);
			serviceMasterOptionsData.setParamName(paramName);
			return serviceMasterOptionsData;
			
		}
		
	}
	
	@Override
	public List<ServiceMasterData> retrieveAllServiceAvailabilty(String addressType, Long addressId) {
		
		this.context.authenticatedUser();

		final ServiceAvailablityMappers mapper = new ServiceAvailablityMappers();
		final String sql = "select " + mapper.schema()+"  where  level_id="+addressId+" and level ='"+addressType+"'";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}
	
	private static final class ServiceAvailablityMappers implements RowMapper<ServiceMasterData>{

		public String schema() {
			return "id,level,level_id,service_code from b_service_availability";
		}
		
		@Override
		public ServiceMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
			long id = rs.getLong("id");
			String level = rs.getString("level");
			long levelId = rs.getLong("level_id");
			long serviceId = rs.getLong("service_code");
			return new ServiceMasterData(id,level,levelId,serviceId);
		}
		
	}
	
	@Override
	public List<ServiceMasterData> retrieveAllServiceAvailabiltyByCity(String addressType, String address) {
		
		this.context.authenticatedUser();

		final ServiceAddressMappers mapper = new ServiceAddressMappers();
		final String sql = "select " + mapper.schema()+"  where  c.city_name ='"+address+"' and level ='"+addressType+"'";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}
	
	private static final class ServiceAddressMappers implements RowMapper<ServiceMasterData>{

		public String schema() {
			return "sa.id as id,sa.level as level,sa.level_id as levelId,s.service_code as serviceCode from b_service_availability sa join b_city c on sa.level_id = c.id join b_service s on sa.service_code = s.id";
		}
		
		
		@Override
		public ServiceMasterData mapRow(ResultSet rs, int rowNum) throws SQLException {
			long id = rs.getLong("id");
			String level = rs.getString("level");
			long levelId = rs.getLong("levelId");
			String serviceCode = rs.getString("serviceCode");
			return new ServiceMasterData(id,level,levelId,serviceCode);
		}
		
	}
	
	
	
}
