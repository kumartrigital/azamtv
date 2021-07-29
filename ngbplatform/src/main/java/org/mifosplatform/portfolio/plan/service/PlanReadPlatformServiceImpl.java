package org.mifosplatform.portfolio.plan.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.planprice.service.PriceReadPlatformService;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.partner.data.PartnersData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.data.VolumeTypeEnumaration;
import org.mifosplatform.portfolio.order.domain.StatusTypeEnum;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.plan.domain.VolumeTypeEnum;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 *
 */
@Service
public class PlanReadPlatformServiceImpl implements PlanReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private static PriceReadPlatformService priceReadPlatformService;
	public final static String POST_PAID = "postpaid";
	public final static String PREPAID = "prepaid";
	private final PaginationHelper<PlanData> paginationHelper = new PaginationHelper<PlanData>();

	@Autowired
	public PlanReadPlatformServiceImpl(final PlatformSecurityContext context,
			final PriceReadPlatformService priceReadPlatformService, final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.priceReadPlatformService = priceReadPlatformService;
	}

	/*
	 * @Override public List<PlanData> retrievePlanData(final String planType) {
	 * 
	 * context.authenticatedUser(); String sql = null; PlanDataMapper mapper = new
	 * PlanDataMapper(this.priceReadPlatformService);
	 * 
	 * if (planType != null && PREPAID.equalsIgnoreCase(planType)) { sql = "select "
	 * + mapper.schema() + " AND pm.is_prepaid ='Y' " + "group by pm.id"; } else if
	 * (planType != null && planType.equalsIgnoreCase(POST_PAID)) { sql = "select "
	 * + mapper.schema() + " AND pm.is_prepaid ='N' " + "group by pm.id"; } else {
	 * sql = "select " + mapper.schema() + "group by pm.id"; }
	 * 
	 * return this.jdbcTemplate.query(sql, mapper, new Object[] {}); }
	 */
	
	@Override
	public Page<PlanData> retrievePlanData(final String planType, SearchSqlQuery searchSqlPlan) {

	context.authenticatedUser();
	String sql=null;
	PlanDataMapper mapper = new PlanDataMapper(this.priceReadPlatformService);
	
		final StringBuilder sqlBuilder = new StringBuilder(200);
		if(planType!=null && PREPAID.equalsIgnoreCase(planType)){
			sqlBuilder.append("select " + mapper.schema()+" AND pm.is_prepaid ='Y' ");
		}else if(planType!=null && planType.equalsIgnoreCase(POST_PAID)){
			sqlBuilder.append("select " + mapper.schema()+" AND pm.is_prepaid ='N' ");
		}else{
			sqlBuilder.append("select " + mapper.schema());
		}
		
		String sqlSearch = searchSqlPlan.getSqlSearch();
        String extraCriteria = null;
	    if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and ( pm.id like '%"+sqlSearch+"%' OR" 
	    			+ " pm.plan_code like '%"+sqlSearch+"%' OR"
	    			+ " plan_description like '%"+sqlSearch+"%' OR"
	    			+ " pm.start_date like '%"+sqlSearch+"%' OR"
	    			+ " pm.end_date like '%"+sqlSearch+"%' OR"
	    			+ " pm.plan_status like '%"+sqlSearch+"%' OR"
	    			+ " pm.is_prepaid like '%"+sqlSearch+"%' OR"
	    			+ " pm.provision_sys like '%"+sqlSearch+"%' OR"
	    			+ " pm.plan_type like '%"+sqlSearch+"%')";
	    }
	    if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
	    
	    sqlBuilder.append("group by pm.id");
	    
	    if (searchSqlPlan.isLimited()) {
            sqlBuilder.append(" limit ").append(searchSqlPlan.getLimit());
        }

        if (searchSqlPlan.isOffset()) {
            sqlBuilder.append(" offset ").append(searchSqlPlan.getOffset());
        }
	    
		//return this.jdbcTemplate.query(sqlBuilder.toString(), mapper, new Object[] {});
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, mapper);
	}

	private static final class PlanDataMapper implements RowMapper<PlanData> {
		private final PriceReadPlatformService priceReadPlatformService;

		public PlanDataMapper(final PriceReadPlatformService priceReadPlatformService) {
			this.priceReadPlatformService = priceReadPlatformService;
		}

		public String schemaForpartner(Long userId) {
			return " SELECT pm.id,pm.plan_code AS planCode,pm.plan_description AS planDescription,pm.start_date AS startDate,"
					+ " pm.end_date AS endDate,pm.plan_status AS planStatus, pm.is_prepaid AS isprepaid, pm.provision_sys AS provisionSystem, pm.plan_type AS planType"
					+ " FROM b_plan_master pm left outer join b_plan_qualifier pq on pm.id =pq.plan_id left outer join m_office mo on mo.id=pq.partner_id"
					+ " WHERE pm.is_deleted = 'n' and mo.hierarchy like '%' group by pm.id";
		}

		public String schema() {
			return " pm.id,pm.plan_code as planCode,pm.plan_description as planDescription,pm.start_date as startDate,"
					+ " pm.end_date as endDate,pm.plan_status as planStatus,pm.is_prepaid AS isprepaid,pm.duration as duration,"
					+ " pm.provision_sys as provisionSystem,pm.plan_type as planType,co.code_value AS planTypeName,count(o.id) as orders ,pm.currencyId as currencyId,mc.code as currencyCode FROM  b_plan_master pm"
					+ " join m_code_value co ON pm.plan_type = co.id" + " LEFT OUTER JOIN" +

					" b_orders o on (pm.id = o.plan_id and o.order_status in(1,4))"
					+ " LEFT OUTER join m_currency mc on mc.id = pm.currencyId" + " WHERE pm.is_deleted = 'n' ";

		}

		@Override
		public PlanData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
			final Long planStatus = rs.getLong("planStatus");
			final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
			final EnumOptionData enumstatus = OrderStatusEnumaration.OrderStatusType(planStatus.intValue());
			List<ServiceData> services = null;
			String duration = null;
			if (rs.getString("isprepaid").equalsIgnoreCase("Y")) {
				services = priceReadPlatformService.retrieveServiceDetails(id);
				duration = rs.getString("duration");
			}
			final Long count = rs.getLong("orders");
			final String provisionSystem = rs.getString("provisionSystem");
			final Long planType = rs.getLong("planType");
			final String planTypeName = rs.getString("planTypeName");
			final Long currencyId = rs.getLong("currencyId");
			final String currencyCode = rs.getString("currencyCode");

			return new PlanData(id, planCode, startDate, endDate, null, null, planStatus, planDescription,
					provisionSystem, enumstatus, null, null, null, null, null, services, null, null, count, planType,
					planTypeName, currencyId, currencyCode, null, duration);
		}
	}

	@Override
	public List<SubscriptionData> retrieveSubscriptionData(final Long orderId, final String planType) {

		context.authenticatedUser();
		SubscriptionDataMapper mapper = new SubscriptionDataMapper();
		String sql = null;
		if (planType != null && orderId != null && PREPAID.equalsIgnoreCase(planType)) {

			sql = "select " + mapper.schemaForPrepaidPlans() + " and o.id=" + orderId
					+ " GROUP BY sb.contract_period order by sb.contract_period";
		} else {
			sql = "select " + mapper.schema() + " order by contract_period";
		}

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	/**
	 * @author hugo
	 *
	 */
	private static final class SubscriptionDataMapper implements RowMapper<SubscriptionData> {

		public String schema() {
			return " sb.id as id,sb.contract_period as contractPeriod,sb.contract_duration as units,sb.contract_type as contractType,0 as priceId "
					+ " from b_contract_period sb where is_deleted='N'";

		}

		public String schemaForPrepaidPlans() {

			/*
			 * return
			 * "  sb.id AS id,sb.contract_period AS contractPeriod,sb.contract_duration AS units,sb.contract_type AS contractType"
			 * +
			 * " FROM b_contract_period sb, b_orders o, b_plan_pricing p WHERE sb.is_deleted = 'N' and sb.contract_period=p.duration "
			 * + " and o.plan_id = p.plan_id  ";
			 */
			/*
			 * return
			 * "  sb.id AS id,sb.contract_period AS contractPeriod,sb.contract_duration AS units,sb.contract_type AS contractType,"
			 * +
			 * " p.id as  priceId,prm.priceregion_code as priceRegionCode, p.plan_id as planId,p.service_code as serviceCode "
			 * +
			 * " FROM b_contract_period sb, b_orders o left join b_client_address ca on ca.client_id = o.client_id "
			 * +
			 * " left join b_state s on s.state_name = ca.state left join b_priceregion_detail pd "
			 * +
			 * " on (pd.state_id = s.id or (pd.state_id = 0 and pd.country_id = s.parent_code)) "
			 * + " left join b_priceregion_master prm ON prm.id = pd.priceregion_id "+
			 * " join b_plan_pricing p on p.plan_id = o.plan_id and p.price_region_id = prm.id "
			 * +
			 * "  WHERE  sb.is_deleted = 'N' AND sb.contract_period = p.duration AND o.plan_id = p.plan_id  "
			 * ;
			 */
			/*
			 * return
			 * " sb.id AS id,sb.contract_period AS contractPeriod,sb.contract_duration AS units,sb.contract_type AS contractType,"
			 * +
			 * " p.id as  priceId,prm.priceregion_code as priceRegionCode, p.plan_id as planId,p.service_code as serviceCode"
			 * +
			 * " FROM b_contract_period sb,b_orders o LEFT JOIN b_client_address ca ON ca.client_id = o.client_id LEFT JOIN b_state s"
			 * + " ON s.state_name = ca.state LEFT JOIN b_priceregion_detail pd " +
			 * " on (pd.state_id = ifnull((SELECT DISTINCT c.id FROM  b_plan_pricing a, b_priceregion_detail b, b_state c,"
			 * +
			 * " b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.state_id = c.id AND d.state = c.state_name AND "
			 * +
			 * " d.address_key = 'PRIMARY' AND d.client_id =o.client_id  and a.plan_id = o.plan_id),0) and pd.country_id = ifnull((SELECT DISTINCT c.id"
			 * +
			 * " FROM b_plan_pricing a,b_priceregion_detail b,b_country c, b_state s,b_client_address d"
			 * +
			 * " WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
			 * +
			 * " AND d.client_id =o.client_id and a.plan_id = o.plan_id and  d.state = s.state_name and "
			 * +
			 * " (s.id =b.state_id or(b.state_id = 0 and b.country_id = c.id ))), 0)) LEFT JOIN b_priceregion_master prm ON prm.id = pd.priceregion_id"
			 * +
			 * " JOIN b_plan_pricing p ON p.plan_id = o.plan_id AND p.is_deleted='N' AND p.price_region_id = prm.id WHERE     sb.is_deleted = 'N' AND sb.contract_period = p.duration"
			 * + " AND o.plan_id = p.plan_id ";
			 */

			/*
			 * return
			 * " sb.id AS id,sb.contract_period AS contractPeriod,sb.contract_duration AS units,sb.contract_type AS contractType,"
			 * +
			 * " p.id as  priceId,prm.priceregion_code as priceRegionCode, p.plan_id as planId,p.service_code as serviceCode "
			 * +
			 * " FROM b_contract_period sb,b_orders o LEFT JOIN b_client_address ca ON ca.client_id = o.client_id LEFT JOIN b_state s"
			 * +
			 * " ON s.state_name = ca.state LEFT JOIN b_priceregion_detail pd on (pd.state_id = ifnull((SELECT DISTINCT c.id FROM  "
			 * +
			 * " b_plan_pricing a, b_priceregion_detail b, b_state c, b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.state_id = c.id"
			 * +
			 * " AND d.state = c.state_name AND d.address_key = 'PRIMARY' AND d.client_id =o.client_id  and a.plan_id = o.plan_id),0) and pd.country_id = ifnull((SELECT DISTINCT c.id"
			 * +
			 * " FROM b_plan_pricing a,b_priceregion_detail b,b_country c, b_state s,b_client_address d "
			 * +
			 * " WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
			 * +
			 * " AND d.client_id =o.client_id and a.plan_id = o.plan_id and  d.state = s.state_name "
			 * +
			 * " and (s.id =b.state_id or(b.state_id = 0 and b.country_id = c.id ))), 0)) LEFT JOIN b_priceregion_master prm ON prm.id = pd.priceregion_id "
			 * +
			 * " JOIN b_plan_pricing p ON p.plan_id = o.plan_id AND p.price_region_id = prm.id WHERE  sb.is_deleted = 'N' AND sb.contract_period = p.duration"
			 * + " AND o.plan_id = p.plan_id AND o.id = ?" + " union all" +
			 * " select sb.id AS id,sb.contract_period AS contractPeriod,sb.contract_duration AS units,sb.contract_type AS contractType,"
			 * +
			 * " p.id as  priceId,prm.priceregion_code as priceRegionCode, p.plan_id as planId,p.service_code as serviceCode "
			 * +
			 * " FROM b_contract_period sb, b_orders o left join b_client_address ca on ca.client_id = o.client_id left join b_state s on s.state_name = ca.state left join b_priceregion_detail pd"
			 * +
			 * " on (pd.state_id = s.id or (pd.state_id = 0 and pd.country_id =0)) left join b_priceregion_master prm ON prm.id = pd.priceregion_id "
			 * +
			 * " join b_plan_pricing p on p.plan_id = o.plan_id and p.price_region_id = prm.id "
			 * +
			 * " WHERE  sb.is_deleted = 'N' AND sb.contract_period = p.duration AND o.plan_id = p.plan_id and sb.contract_period not in (SELECT sb.contract_period AS contractPeriod"
			 * +
			 * " FROM b_contract_period sb,b_orders o LEFT JOIN b_client_address ca ON ca.client_id = o.client_id LEFT JOIN b_state s ON s.state_name = ca.state"
			 * +
			 * " LEFT JOIN b_priceregion_detail pd on (pd.state_id = ifnull((SELECT DISTINCT c.id FROM  b_plan_pricing a, b_priceregion_detail b, b_state c,"
			 * +
			 * " b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.state_id = c.id "
			 * +
			 * " AND d.state = c.state_name AND d.address_key = 'PRIMARY' AND d.client_id =o.client_id  and a.plan_id = o.plan_id),0) and pd.country_id = ifnull((SELECT DISTINCT c.id"
			 * +
			 * " FROM b_plan_pricing a,b_priceregion_detail b,b_country c, b_state s,b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
			 * +
			 * " AND d.client_id =o.client_id and a.plan_id = o.plan_id and  d.state = s.state_name and (s.id =b.state_id or(b.state_id = 0 and b.country_id = c.id ))), 0))"
			 * +
			 * " LEFT JOIN b_priceregion_master prm ON prm.id = pd.priceregion_id JOIN b_plan_pricing p ON p.plan_id = o.plan_id AND p.price_region_id = prm.id "
			 * +
			 * " WHERE     sb.is_deleted = 'N' AND sb.contract_period = p.duration AND o.plan_id = p.plan_id AND o.id = ?)"
			 * ;
			 */

			return " sb.id AS id,sb.contract_period AS contractPeriod,sb.contract_duration AS units, "
					+ " sb.contract_type AS contractType,p.id as priceId, prm.priceregion_code as priceRegionCode, "
					+ " p.plan_id as planId, p.product_id as serviceCode FROM b_contract_period sb, b_orders o "
					+ " LEFT JOIN b_client_address ca ON ca.client_id = o.client_id "
					+ " LEFT JOIN b_state s ON s.state_name = ca.state " + " LEFT JOIN b_priceregion_detail pd ON "
					+ " (pd.state_id = ifnull((SELECT DISTINCT c.id  FROM b_plan_pricing a, "
					+ " b_priceregion_detail b,b_state c, b_client_address d "
					+ " WHERE b.priceregion_id = a.price_region_id AND b.state_id = c.id "
					+ " AND d.state = c.state_name AND d.address_key = 'PRIMARY'"
					+ " AND d.client_id = o.client_id and a.plan_id = o.plan_id), 0)"
					+ " and pd.country_id = ifnull((SELECT DISTINCT "
					+ " c.id FROM b_plan_pricing a, b_priceregion_detail b, b_country c, b_state s, b_client_address d"
					+ " WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id"
					+ " AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
					+ " AND d.client_id = o.client_id and a.plan_id = o.plan_id "
					+ " and d.state = s.state_name and (s.id = b.state_id"
					+ " or (b.state_id = 0 and b.country_id = c.id))),0))"
					+ " LEFT JOIN b_priceregion_master prm ON prm.id = pd.priceregion_id"
					+ " JOIN b_plan_pricing p ON p.plan_id = o.plan_id"
					+ " AND p.is_deleted = 'N' AND p.price_region_id = prm.id"
					+ " LEFT join b_charge_codes cc on cc.charge_code = p.charge_code "
					+ " WHERE  sb.is_deleted = 'N' ";
			/* + " WHERE  sb.is_deleted = 'N' AND cc.billfrequency_code = p.duration"; */

		}

		@Override
		public SubscriptionData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String contractPeriod = rs.getString("contractPeriod");
			final String subscriptionType = rs.getString("contractType");
			final Long priceId = rs.getLong("priceId");
			return new SubscriptionData(id, contractPeriod, subscriptionType, priceId);
		}

	}

	/*
	 * Method for Status Retrieval
	 */
	@Override
	public List<EnumOptionData> retrieveNewStatus() {

		final EnumOptionData active = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.ACTIVE);
		final EnumOptionData inactive = OrderStatusEnumaration.OrderStatusType(StatusTypeEnum.INACTIVE);
		return Arrays.asList(active, inactive);

	}

	@Override
	public PlanData retrievePlanData(final Long planId) {
		context.authenticatedUser();
		final String sql = "SELECT pm.id AS id,pm.plan_code AS planCode,pm.duration As duration,pm.plan_description AS planDescription,pm.start_date AS startDate,pm.end_date AS endDate,"
				+ "pm.plan_status AS planStatus,pm.provision_sys AS provisionSys,pm.plan_type AS planType,co.code_value AS planTypeName,pm.bill_rule AS billRule,pm.is_prepaid as isPrepaid,"
				+ " pm.allow_topup as allowTopup,v.volume_type as volumeType, v.units as units,pm.is_hw_req as isHwReq,v.units_type as unitType,count(o.id) as orders,pm.currencyId as currencyId,mc.code as currencyCode"
				+ " FROM (b_plan_master pm  left join b_volume_details v on pm.id = v.plan_id) join m_code_value co ON pm.plan_type = co.id"
				+ " LEFT OUTER JOIN b_orders o on (pm.id = o.plan_id and o.order_status in(1,4))"
				+ " left outer join m_currency mc on mc.id = pm.currencyId "
				+ "  WHERE pm.id = ? AND pm.is_deleted = 'n' group by pm.id";

		RowMapper<PlanData> rm = new ServiceMapper();

		return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { planId });

	}

	private static final class ServiceMapper implements RowMapper<PlanData> {

		@Override
		public PlanData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
			final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
			final Long billRule = rs.getLong("billRule");
			final Long planStatus = rs.getLong("planStatus");
			final String planDescription = rs.getString("planDescription");
			final String provisionSys = rs.getString("provisionSys");
			final String isPrepaid = rs.getString("isPrepaid");
			final String volume = rs.getString("volumeType");
			final String allowTopup = rs.getString("allowTopup");
			final String isHwReq = rs.getString("isHwReq");
			final String units = rs.getString("units");
			final String unitType = rs.getString("unitType");
			final Long count = rs.getLong("orders");
			final Long planType = rs.getLong("planType");
			final String planTypeName = rs.getString("planTypeName");
			final Long currencyId = rs.getLong("currencyId");
			final String currencyCode = rs.getString("currencyCode");
			final String duration = rs.getString("duration");

			return new PlanData(id, planCode, startDate, endDate, billRule, null, planStatus, planDescription,
					provisionSys, null, isPrepaid, allowTopup, volume, units, unitType, null, null, isHwReq, count,
					planType, planTypeName, currencyId, currencyCode, null, duration);
		}
	}

	/*
	 * @param planId
	 * 
	 * @return PlanDetails
	 */
	@Override
	public List<ServiceData> retrieveSelectedProducts(final Long planId) {
		context.authenticatedUser();

		String sql = "SELECT sm.id AS id,sm.product_description AS productDescription,sm.product_code as productCode,p.plan_code AS planCode,"
				+ "pm.product_id AS productId,psd.image AS image,sm.service_id as serviceId,s.service_code as serViceCode "
				+ "FROM b_plan_detail pm,b_plan_master p,b_product sm "
				+ "left join b_prov_service_details psd on psd.service_id = sm.id join b_service s on s.id = sm.service_id "
				+ "WHERE pm.product_id = sm.id AND p.id = pm.plan_id AND sm.is_deleted = 'N'"
				+ " AND pm.plan_id = ? GROUP BY sm.id;";

		RowMapper<ServiceData> rm = new PeriodMapper();

		return this.jdbcTemplate.query(sql, rm, new Object[] { planId });
	}

	private static final class PeriodMapper implements RowMapper<ServiceData> {

		@Override
		public ServiceData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String productCode = rs.getString("productCode");
			final String productDescription = rs.getString("productDescription");
			final String image = rs.getString("image");
			final Long serviceId = rs.getLong("serviceId");
			final String serviceCode = rs.getString("serviceCode");
			return new ServiceData(id, productCode, productDescription, image, serviceId, serviceCode);

		}
	}

	@Override
	public List<EnumOptionData> retrieveVolumeTypes() {

		final EnumOptionData iptv = VolumeTypeEnumaration.VolumeTypeEnum(VolumeTypeEnum.IPTV);
		final EnumOptionData vod = VolumeTypeEnumaration.VolumeTypeEnum(VolumeTypeEnum.VOD);
		return Arrays.asList(iptv, vod);

	}

	@Override
	public List<PartnersData> retrieveAvailablePartnersData(Long planId) {

		try {
			this.context.authenticatedUser();
			PlanQulifierMapper mapper = new PlanQulifierMapper();
			final String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] { planId });

		} catch (EmptyResultDataAccessException dve) {
			return null;
		}

	}

	private static final class PlanQulifierMapper implements RowMapper<PartnersData> {

		public String schema() {
			return " o.id as id, o.name as partnerName" + " FROM m_office o, m_code_value cv"
					+ " WHERE  o.id  NOT IN (select partner_id from b_plan_qualifier where plan_id = ? ) "
					+ " and cv.id = o.office_type AND cv.code_value ='Agent'";

		}

		public String schemaForPartners() {

			return "o.id AS id, o.name AS partnerName" + " FROM m_office o, m_code_value cv, b_plan_qualifier pq "
					+ "WHERE cv.id = o.office_type AND cv.code_value = 'Agent' AND o.id =pq.partner_id  and pq.plan_id=?";
		}

		@Override
		public PartnersData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String partnerName = rs.getString("partnerName");
			return new PartnersData(id, partnerName, null);
		}

	}

	@Override
	public List<PartnersData> retrievePartnersData(Long planId) {

		try {
			this.context.authenticatedUser();
			PlanQulifierMapper mapper = new PlanQulifierMapper();
			final String sql = "select " + mapper.schemaForPartners();
			return this.jdbcTemplate.query(sql, mapper, new Object[] { planId });

		} catch (EmptyResultDataAccessException dve) {
			return null;
		}

	}

	@Override
	public List<PlanData> retrievePlanDataForDropdown() {
		try {
			PlanMapperForDropDown planMapper = new PlanMapperForDropDown();
			String sql = "SELECT DISTINCT " + planMapper.schema() + " WHERE p.is_deleted = 'N'";
			return jdbcTemplate.query(sql, planMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	private static final class PlanMapperForDropDown implements RowMapper<PlanData> {

		public String schema() {
			return "p.id AS id,p.plan_code AS planCode,p.plan_description AS planDescription, p.plan_poid as planPoid, d.deal_poid as dealPoid"
					+ " FROM b_plan_master p join b_plan_detail d on p.id=d.plan_id";

		}

		@Override
		public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final String planPoid = rs.getString("planPoid");
			final String dealPoid = rs.getString("dealPoid");
			return new PlanData(id, planCode, planDescription, planPoid, dealPoid, null);
		}

	}


	@Override
	public List<PlanCodeData> retrievePlanData(Long salesCatalogeId, Long planId, Long clientId, Long clientServiceId) {
		AppUser user = context.authenticatedUser();
		StringBuilder sql = new StringBuilder();
		if (clientId != null) {
			/*
			 * sql.
			 * append("SELECT DISTINCT s.id AS id, s.plan_code AS planCode, s.plan_description as planDescription, s.is_prepaid AS isPrepaid, s.plan_poid AS planpoid,"
			 * ); sql.
			 * append("(select distinct(deal_poid) from b_plan_detail x where s.id=x.plan_id) AS dealpoid,co.code_value AS planTypeName "
			 * ); sql.
			 * append(" FROM b_plan_master s, b_plan_pricing p,b_priceregion_master prd,b_priceregion_detail pd,b_client_address cd,b_state bs,m_code_value co"
			 * ); sql.append(" WHERE  s.plan_status = 1 AND s.is_deleted = 'n' AND s.id != "
			 * +planId+"  AND prd.id = pd.priceregion_id AND p.price_region_id = pd.priceregion_id AND "
			 * ); sql.
			 * append(" (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_state c,b_charge_codes cc,b_client_address d"
			 * ); sql.
			 * append(" WHERE  b.priceregion_id = a.price_region_id AND b.state_id = c.id AND a.price_region_id = b.priceregion_id AND d.state = c.state_name "
			 * ); sql.
			 * append(" AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code AND d.address_key = 'PRIMARY' AND d.client_id = "
			 * +clientId+" "); sql.append("  AND a.plan_id != "
			 * +planId+" AND a.is_deleted = 'n'),0) AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, b_country c,"
			 * ); sql.
			 * append("b_charge_codes cc,b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code"
			 * ); sql.
			 * append(" AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
			 * ); sql.append(" AND d.client_id = "+clientId+"  AND a.plan_id != "
			 * +planId+" AND a.is_deleted = 'n'),0)) AND s.id = p.plan_id AND s.plan_type = co.id AND cd.client_id = "
			 * +clientId+" "); sql.
			 * append(" AND p.plan_id not in (Select distinct plan_id from b_orders where order_status=1 and client_id= "
			 * +clientId+") "); sql.
			 * append(" AND p.plan_id in (Select plan_id from b_sales_cataloge_mapping scm join b_sales_cataloge sd on scm.cataloge_id = sd.id where sd.sales_plan_category_id = "
			 * +salesPlanCategoryId+" and scm.is_deleted = 'N') ");
			 */
			sql.append(
					"SELECT DISTINCT s.id AS id, s.plan_code AS planCode, s.plan_description as planDescription, s.is_prepaid AS isPrepaid, s.plan_poid AS planpoid,p.price as planPrice,");
			sql.append(
					"(select distinct(deal_poid) from b_plan_detail x where s.id=x.plan_id) AS dealpoid,co.code_value AS planTypeName,(select billfrequency_code from b_charge_codes bcc where bcc.charge_code=p.charge_code) AS chargeCycle,");
			sql.append(
					"(select id from b_contract_period cp where cp.contract_period = p.duration) AS contractPeriodId ");
			sql.append(
					" FROM b_plan_master s, b_plan_pricing p,b_priceregion_master prd,b_priceregion_detail pd,b_client_address cd,b_state bs,m_code_value co");
			sql.append(" WHERE  s.plan_status = 1 AND s.is_deleted = 'n' AND s.id != " + planId
					+ "  AND prd.id = pd.priceregion_id AND p.price_region_id = pd.priceregion_id AND p.is_deleted = 'n' AND ");
			sql.append(
					" (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_state c,b_charge_codes cc,b_client_address d");
			sql.append(
					" WHERE  b.priceregion_id = a.price_region_id AND b.state_id = c.id AND a.price_region_id = b.priceregion_id AND d.state = c.state_name ");
			sql.append(
					" AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code AND d.address_key = 'PRIMARY' AND d.client_id = "
							+ clientId + " ");
			sql.append("  AND a.plan_id != " + planId
					+ " AND a.is_deleted = 'n'),0) AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, b_country c,");
			sql.append(
					"b_charge_codes cc,b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code");
			sql.append(
					" AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id AND c.country_name = d.country AND d.address_key = 'PRIMARY'");
			sql.append(" AND d.client_id = " + clientId + "  AND a.plan_id != " + planId
					+ " AND a.is_deleted = 'n'),0)) AND s.id = p.plan_id AND s.plan_type = co.id AND cd.client_id = "
					+ clientId + " ");
			sql.append(
					" AND p.plan_id not in (Select distinct plan_id from b_orders where is_deleted = 'n' and order_status = 1 and client_service_id= "
							+ clientServiceId + ") ");
			sql.append(
					" AND p.plan_id in (Select plan_id from b_sales_cataloge_mapping scm join b_sales_cataloge sd on scm.cataloge_id = sd.id where sd.id = "
							+ salesCatalogeId + " and scm.is_deleted = 'N') ");

		}

		RowMapper<PlanCodeData> rm = new PlanDataForAddPlans();
		return this.jdbcTemplate.query(sql.toString(), rm, new Object[] {});
	}

	private static final class PlanDataForAddPlans implements RowMapper<PlanCodeData> {

		@Override
		public PlanCodeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String planDescription = rs.getString("planDescription");
			String isPrepaid = rs.getString("isPrepaid");
			String planTypeName = rs.getString("planTypeName");
			Long planPoId = rs.getLong("planPoid");
			Long dealPoId = rs.getLong("dealPoid");
			String chargeCycle = rs.getString("chargeCycle");
			Long contractPeriodId = rs.getLong("contractPeriodId");
			BigDecimal price = rs.getBigDecimal("planPrice");
			List<ServiceData> services = priceReadPlatformService.retrieveServiceDetails(id);
			return new PlanCodeData(id, planCode, services, isPrepaid, planDescription, planPoId, dealPoId,
					planTypeName, null, null, chargeCycle, contractPeriodId,price);

		}

	}
	@Override
	public List<PlanData> retrievePlansForPlanDataPoIds(Long planId) {
		try {
			PlanMapperForPlanPoIds planPoIdMapper = new PlanMapperForPlanPoIds();
			String sql = "SELECT " + planPoIdMapper.schema() + " WHERE p.is_deleted = 'N' and p.id=" + planId;
			return jdbcTemplate.query(sql, planPoIdMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}

	}

	private static final class PlanMapperForPlanPoIds implements RowMapper<PlanData> {

		public String schema() {
			return "p.plan_description AS planDescription,p.id as id,p.is_prepaid as isPrepaid, p.plan_poid as planPoid, d.deal_poid as dealPoid"
					+ " FROM b_plan_master p join b_plan_detail d on p.id=d.plan_id";

		}

		@Override
		public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final String planDescription = rs.getString("planDescription");
			Long planId = rs.getLong("id");
			final String isPrepaid = rs.getString("isPrepaid");
			String planPoidString = null;
			if (rs.getString("planPoid") != null) {
				planPoidString = rs.getString("planPoid");
			}
			String dealPoidString = null;
			if (rs.getString("dealPoid") != null) {
				dealPoidString = rs.getString("dealPoid");
			}
			return new PlanData(planId, null, planDescription, planPoidString, dealPoidString, isPrepaid);
		}

	}

	@Override
	public PlanData retrievePlanDataPoIds(Long planId) {
		try {
			PlanMapperForPoIds planPoIdMapper = new PlanMapperForPoIds();
			String sql = "SELECT " + planPoIdMapper.schema() + " WHERE p.is_deleted = 'N' and p.id=" + planId;
			return jdbcTemplate.queryForObject(sql, planPoIdMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}

	}

	private static final class PlanMapperForPoIds implements RowMapper<PlanData> {

		public String schema() {
			return "p.plan_description AS planDescription,p.id as id, p.plan_poid as planPoid, d.deal_poid as dealPoid, p.is_prepaid AS isPrepaid "
					+ " FROM b_plan_master p join b_plan_detail d on p.id=d.plan_id";

		}

		@Override
		public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final String planDescription = rs.getString("planDescription");
			Long planId = rs.getLong("id");
			String planPoidString = null;
			if (rs.getString("planPoid") != null) {
				planPoidString = rs.getString("planPoid");
			}
			String dealPoidString = null;
			if (rs.getString("dealPoid") != null) {
				dealPoidString = rs.getString("dealPoid");
			}
			final String isPrepaid = rs.getString("isPrepaid");
			return new PlanData(planId, null, planDescription, planPoidString, dealPoidString, isPrepaid);
		}

	}

	public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
		final String planDescription = rs.getString("planDescription");
		Long planPoId = null;
		if (rs.getString("planPoid") != null) {
			final String planPoidString = rs.getString("planPoid");
			planPoId = Long.parseLong(planPoidString);
		}
		Long dealPoId = null;
		if (rs.getString("dealPoid") != null) {
			final String dealPoidString = rs.getString("dealPoid");
			dealPoId = Long.parseLong(dealPoidString);
		}
		final Long id = rs.getLong("id");
		return new PlanData(id, null, planDescription, planPoId.toString(), dealPoId.toString(), null);
	}

	@Override
	public PlanData retrievePlanDataPoIdsNew(String planCode) {
		try {
			PlanMapperForPoIds planPoIdMapper = new PlanMapperForPoIds();
			String sql = "SELECT distinct " + planPoIdMapper.schema() + " WHERE p.is_deleted = 'N' and p.plan_code='"
					+ planCode + "'";
			return jdbcTemplate.queryForObject(sql, planPoIdMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}

	}

	@Override
	public PlanData retrievePlanDataPoIdsUsingPlanCode(String planCode) {
		try {
			PlanMapperForPoIds planPoIdMapper = new PlanMapperForPoIds();
			String sql = "SELECT distinct " + planPoIdMapper.schema() + " WHERE p.is_deleted = 'N' and p.plan_code='"
					+ planCode + "'";
			return jdbcTemplate.queryForObject(sql, planPoIdMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	@Override
	public PlanData retrievePlanDataPoIds1(String planCode) {
		try {
			PlanMapperForPoIds planPoIdMapper = new PlanMapperForPoIds();
			String sql = "SELECT " + planPoIdMapper.schema() + " WHERE p.is_deleted = 'N' and p.plan_code=" + planCode;
			return jdbcTemplate.queryForObject(sql, planPoIdMapper, new Object[] { planCode });
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}

	}

	@Override
	public List<PlanData> retrivebouque() {
		try {
			PlanbouqueMapper pb = new PlanbouqueMapper();
			final String sql = "select distinct " + pb.schema();
			return jdbcTemplate.query(sql, pb, new Object[] {});

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class PlanbouqueMapper implements RowMapper<PlanData> {

		public String schema() {
			return " a.id as productId,a.product_code as productCode,a.product_description as productDescription,c.id as channelId,c.channel_name as channelName,c.is_hd_channel as isHdChannel, "
					+ " m.plan_code  as PlanCode, m.plan_description as planDescription,m.id as id,m.is_prepaid as isPrepaid,a.service_id as serviceId "
					+ " from b_product a,b_prd_ch_mapping b, b_channel c,b_plan_detail d, b_plan_master m,m_code_value cv where a.id=b.product_id and m.id = d.plan_id   and cv.id = m.plan_type and b.channel_id=c.id "
					+ " and d.product_id=a.id  and a.is_deleted='N' and b.is_deleted='N' and is_bouquet='Y'  and cv.code_value = 'BuildYourPlan' ";

		}

		@Override
		public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long productId = rs.getLong("productId");
			final String productCode = rs.getString("productCode");
			final String productDescription = rs.getString("productDescription");
			final Long channelid = rs.getLong("channelId");
			final String channelName = rs.getString("channelName");
			final boolean isHdChannel = rs.getBoolean("isHdChannel");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final Long id = rs.getLong("id");
			final String isPrepaid = rs.getString("isPrepaid");
			final Long serviceId = rs.getLong("serviceId");

			PlanData plandata = new PlanData();
			plandata.setProductId(productId);
			plandata.setProductcode(productCode);
			plandata.setProductDescription(productDescription);
			plandata.setChannelId(channelid);
			plandata.setChannelname(channelName);
			plandata.setIsHdChannel(isHdChannel);
			plandata.setPlanCode(planCode);
			plandata.setPlanDescription(planDescription);
			plandata.setId(id);
			plandata.setIsPrepaid(isPrepaid);
			plandata.setServiceId(serviceId);
			return plandata;
		}

	}

	@Override
	public List<PlanData> retriveNonbouque() {
		try {
			PlanNonbouqueMapper pn = new PlanNonbouqueMapper();
			final String sql = "select distinct " + pn.schema();
			return jdbcTemplate.query(sql, pn, new Object[] {});

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class PlanNonbouqueMapper implements RowMapper<PlanData> {

		public String schema() {
			return " a.id as productId,a.product_code as productCode,a.product_description as productDescription,c.id as channelId,c.channel_name as channelName,c.is_hd_channel as isHdChannel,c.channel_category as channelCategory, "
					+ " m.plan_code  as planCode, m.plan_description as planDescription,m.id as id, m.is_prepaid as isPrepaid,a.service_id as serviceId "
					+ " from b_product a,b_prd_ch_mapping b, b_channel c,b_plan_detail d, b_plan_master m,m_code_value cv where a.id=b.product_id and m.id = d.plan_id   and cv.id = m.plan_type and b.channel_id=c.id "
					+ " and d.product_id=a.id and a.is_deleted='N' and b.is_deleted='N' and is_bouquet='N' and cv.code_value = 'BuildYourPlan' ";

		}

		@Override
		public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long productId = rs.getLong("productId");
			final String productCode = rs.getString("productCode");
			final String productDescription = rs.getString("productDescription");
			final Long channelid = rs.getLong("channelId");
			final String channelName = rs.getString("channelName");
			final boolean isHdChannel = rs.getBoolean("isHdChannel");
			final String channelCategory = rs.getString("channelCategory");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final Long id = rs.getLong("id");
			final String isPrepaid = rs.getString("isPrepaid");
			final Long serviceId = rs.getLong("serviceId");

			PlanData plandatas = new PlanData();
			plandatas.setProductId(productId);
			plandatas.setProductcode(productCode);
			plandatas.setProductDescription(productDescription);
			plandatas.setChannelId(channelid);
			plandatas.setChannelname(channelName);
			plandatas.setIsHdChannel(isHdChannel);
			plandatas.setChannelCategory(channelCategory);
			plandatas.setPlanCode(planCode);
			plandatas.setPlanDescription(planDescription);
			plandatas.setId(id);
			plandatas.setIsPrepaid(isPrepaid);
			plandatas.setServiceId(serviceId);
			return plandatas;
		}

	}

	@Override
	public PlanData retrivePlan(Long planId) {

		try {
			PlanMapper planMapper = new PlanMapper();
			String sql = "SELECT DISTINCT " + planMapper.schema() + " AND s.is_deleted = 'N' and s.id=" + planId;
			return jdbcTemplate.queryForObject(sql, planMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	private static final class PlanMapper implements RowMapper<PlanData> {

		public String schema() {
			return "  s.id as planId,s.plan_code As planCode,s.plan_description as planDescription,s.is_prepaid AS isPrepaid,co.code_value AS planTypeName,"
					+ "  (select billfrequency_code from b_charge_codes bcc where bcc.charge_code=p.charge_code) AS chargeCycle,"
					+ "  (select id from b_contract_period cp where cp.contract_period = p.duration) AS contractPeriodId FROM b_plan_master s,"
					+ "  b_plan_pricing p, b_priceregion_master prd,  b_priceregion_detail pd, b_state bs,m_code_value co "
					+ "  WHERE s.plan_status = 1 AND s.id != 0 AND prd.id = pd.priceregion_id AND p.is_deleted = 'n' AND s.id = p.plan_id AND s.plan_type = co.id ";

		}

		@Override
		public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("planId");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final String isPrepaid = rs.getString("isPrepaid");
			final String planTypeName = rs.getString("planTypeName");
			final String chargeCycle = rs.getString("chargeCycle");
			final Long contractPeriodId = rs.getLong("contractPeriodId");

			PlanData plandata = new PlanData();
			plandata.setId(id);
			plandata.setPlanCode(planCode);
			plandata.setPlanDescription(planDescription);
			plandata.setIsPrepaid(isPrepaid);
			plandata.setPlanTypeName(planTypeName);
			plandata.setChargeCycle(chargeCycle);
			plandata.setContractPeriodId(contractPeriodId);
			;
			return plandata;
		}

	}

	@Override
	public PlanData retrivePlanByPlanCode(String planCode) {

		try {
			PlanCodeMapper planMapper = new PlanCodeMapper();
			String sql = "SELECT DISTINCT " + planMapper.schema() + " AND s.is_deleted = 'N' and s.plan_code='"
					+ planCode + "'";
			return jdbcTemplate.queryForObject(sql, planMapper, new Object[] {});
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}
	}

	private static final class PlanCodeMapper implements RowMapper<PlanData> {

		public String schema() {
			return "  s.id as planId,s.plan_code As planCode,s.plan_description as planDescription,s.is_prepaid AS isPrepaid,co.code_value AS planTypeName, pdl.service_id as serviceId,"
					+ "  (select billfrequency_code from b_charge_codes bcc where bcc.charge_code=p.charge_code) AS chargeCycle,"
					+ "  (select id from b_contract_period cp where cp.contract_period = p.duration) AS contractPeriodId FROM b_plan_master s,"
					+ "  b_plan_pricing p, b_priceregion_master prd,  b_priceregion_detail pd, b_state bs,m_code_value co, b_plan_detail pdl "
					+ "  WHERE s.plan_status = 1 AND s.id != 0 AND prd.id = pd.priceregion_id AND p.is_deleted = 'n' AND s.id = p.plan_id AND s.plan_type = co.id AND pdl.plan_id=p.plan_id";

		}

		@Override
		public PlanData mapRow(ResultSet rs, int rowNum) throws SQLException {
			final Long id = rs.getLong("planId");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final String isPrepaid = rs.getString("isPrepaid");
			final String planTypeName = rs.getString("planTypeName");
			final String chargeCycle = rs.getString("chargeCycle");
			final Long contractPeriodId = rs.getLong("contractPeriodId");
			final Long serviceId = rs.getLong("serviceId");
			PlanData plandata = new PlanData();
			plandata.setId(id);
			plandata.setPlanCode(planCode);
			plandata.setPlanDescription(planDescription);
			plandata.setIsPrepaid(isPrepaid);
			plandata.setPlanTypeName(planTypeName);
			plandata.setChargeCycle(chargeCycle);
			plandata.setContractPeriodId(contractPeriodId);
			plandata.setServiceId(serviceId);
			return plandata;
		}

	}

}
