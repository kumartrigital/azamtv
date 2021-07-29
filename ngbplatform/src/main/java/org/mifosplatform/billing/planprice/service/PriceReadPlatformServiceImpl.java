package org.mifosplatform.billing.planprice.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.mifosplatform.billing.planprice.data.PricingData;
import org.mifosplatform.billing.planprice.domain.SavingChargeVaraint;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.voucher.data.VoucherData;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.pricing.domain.ChargeVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PriceReadPlatformServiceImpl implements PriceReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public PriceReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<SubscriptionData> retrievePaytermData() {

		context.authenticatedUser();
		SubscriptionDataMapper mapper = new SubscriptionDataMapper();
		String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class SubscriptionDataMapper implements
			RowMapper<SubscriptionData> {

		public String schema() {
			return " sb.id as id,sb.payterm_type as paytermType,sb.units as units "
					+ " from b_payments sb ";
		}

		@Override
		public SubscriptionData mapRow(final ResultSet rs, int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String payterm_type = rs.getString("paytermType");
			final String units = rs.getString("units");
			final String contractPeriod = units.concat(payterm_type);
			return new SubscriptionData(id, contractPeriod, null,null);

		}
	}

	@Override
	public List<ServiceData> retrieveServiceDetails(final Long planId) {

		context.authenticatedUser();

		String sql = "SELECT sm.id AS id,sm.product_description AS product_description,sm.product_code AS product_code,p.plan_code AS planCode,pm.product_id AS product_id,p.is_prepaid as isprepaid,pr.chargeOwner as chargeOwner,"
				+ " c.billfrequency_code as billingfreq ,p.currencyId as currencyId,mc.code as currencyCode FROM b_plan_detail pm,  b_product sm, b_plan_master p left join b_plan_pricing pr on pr.plan_id = p.id"
				+ " left join b_charge_codes c ON c.charge_code = pr.charge_code left join m_currency mc ON mc.id = p.currencyId WHERE pm.product_id = sm.id AND p.id = pm.plan_id "
				+ " AND sm.is_deleted = 'n' AND pm.plan_id = ? group by pm.product_id";

		RowMapper<ServiceData> rm = new PeriodMapper();
		return this.jdbcTemplate.query(sql, rm, new Object[] { planId });
	}

	private static final class PeriodMapper implements RowMapper<ServiceData> {

		@Override
		public ServiceData mapRow(final ResultSet rs, final int rowNum)throws SQLException {
			
			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String productCode = rs.getString("product_code");
			String productDescription = rs.getString("product_description");
			String billingfreq = rs.getString("billingfreq");
			String isprepaid = rs.getString("isprepaid");
			final Long currencyId = rs.getLong("currencyId");
			final String currencyCode = rs.getString("currencyCode");
			final String chargeOwner = rs.getString("chargeOwner");
			
			
			return new ServiceData(id, null, planCode, billingfreq,
					productCode, productDescription, null, null, null,
					isprepaid,currencyId,currencyCode,null,null,chargeOwner);

		}
	}

	@Override
	public List<ServiceData> retrievePriceDetails(final Long planId,final String region) {

		context.authenticatedUser();
		String sql = "SELECT p.plan_code AS plan_code,p.is_prepaid as isPrepaid,cp.id as contractId,pm.id AS id,pm.product_id AS productId,pm.chargeOwner AS chargeOwner,"
                + "if(pm.product_id=0,null,se.product_description) AS productDescription,if(pm.product_id=0,null,se.product_code) AS productCode, pm.duration as contract,c.charge_description AS chargeDescription,pm.discount_id AS discountId,"
                + "pm.charge_code AS charge_code,pm.charging_variant AS chargingVariant,pm.price AS price,c.billfrequency_code as billingFrequency,"
                + "pr.priceregion_name AS priceregion ,pm.currencyId AS currencyId,mc.code AS currencyCode,pm.gl_id As glId,pm.rounding_type AS roundingType FROM b_plan_master p,b_product se,b_charge_codes c,b_plan_pricing pm  left join b_priceregion_master "
                + "pr on  pm.price_region_id=pr.id LEFT JOIN  m_currency mc ON mc.id = pm.currencyId LEFT JOIN b_contract_period cp ON cp.contract_period = pm.duration WHERE p.id = pm.plan_id  AND pm.charge_code=c.charge_code and "
                + " (pm.product_id = se.id or pm.product_id = 0) and pm.is_deleted='n' and se.is_deleted='n' and  pm.plan_id =? group by pm.id";


		if (region != null) {
			/*sql = " SELECT p.plan_code AS plan_code,p.is_prepaid as isPrepaid,cp.id AS contractId,pm.id AS id,pm.service_code AS serviceCode,se.service_description AS serviceDescription,"
					+ " pm.duration AS contract,c.charge_description AS chargeDescription,pm.charge_code AS charge_code,pm.charging_variant AS chargingVariant,pm.discount_id AS discountId,"
					+ " pm.price AS price,c.billfrequency_code AS billingFrequency,pr.priceregion_name AS priceregion FROM b_plan_master p,b_service se, b_charge_codes c,"
					+ " b_priceregion_detail pd, b_state s,b_plan_pricing pm LEFT JOIN b_priceregion_master pr ON pm.price_region_id = pr.id LEFT JOIN "
					+ " b_contract_period cp ON cp.contract_period = pm.duration WHERE p.id = pm.plan_id AND pm.charge_code = c.charge_code AND "
					+ " (pm.service_code = se.service_code OR pm.service_code = 'None') AND pm.is_deleted = 'n' AND se.is_deleted = 'n' and  "
					+ " pm.price_region_id = pd.priceregion_id AND ( s.id = pd.state_id OR (pd.state_id = 0 AND pd.country_id = s.parent_code)) AND "
					+ " s.state_name = '"
					+ region
					+ "' AND pm.plan_id =? GROUP BY pm.id";*/
			sql =" SELECT p.plan_code AS plan_code,p.is_prepaid AS isPrepaid,cp.id AS contractId,p.id as planId,pm.id AS id,pm.product_id AS productId,pm.chargeOwner AS chargeOwner," +
			     " se.product_description AS productDescription,se.product_code AS productCode,pm.duration AS contract,c.charge_description AS chargeDescription,pm.charge_code AS charge_code," +
			     " pm.charging_variant AS chargingVariant,pm.discount_id AS discountId,pm.price AS price,c.billfrequency_code AS billingFrequency," +
			     " pm.currencyId as currencyId,mc.code AS currencyCode,pr.priceregion_name AS priceregion" +
			     " FROM b_plan_master p, b_product se,b_charge_codes c,b_state s,b_plan_pricing pm " +
			     " LEFT JOIN b_priceregion_master pr ON pm.price_region_id = pr.id " +
			     " LEFT JOIN  m_currency mc ON mc.id = pm.currencyId " +
			     " LEFT JOIN b_contract_period cp ON cp.contract_period = pm.duration" +
			     " LEFT JOIN b_priceregion_detail pd ON pd.priceregion_id = pm.price_region_id" +
			     " WHERE p.id = pm.plan_id AND pm.charge_code = c.charge_code AND (pm.product_id = se.id OR pm.product_id = 'None')" +
			     " AND pm.is_deleted = 'n' AND se.is_deleted = 'n' " +
			     " AND (pd.state_id =ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b, b_state c WHERE  b.priceregion_id = a.price_region_id" +
			     " AND a.price_region_id = b.priceregion_id and pm.charge_code = a.charge_code AND c.id = b.state_id AND c.state_name = '"+region+"' AND a.plan_id = "+planId+"),0)" +
			     " AND pd.country_id =ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_country c,  b_state s WHERE b.priceregion_id = a.price_region_id" +
			     " AND b.country_id = c.id and pm.charge_code = a.charge_code AND   s.parent_code = c.id AND s.state_name = '"+region+"' AND a.price_region_id = b.priceregion_id" +
			     " AND a.plan_id = "+planId+"),0)) AND pm.plan_id = ? GROUP BY pm.id";	
		}

		RowMapper<ServiceData> rm = new PriceMapper();
		return this.jdbcTemplate.query(sql, rm, new Object[] { planId });
	}

	private static final class PriceMapper implements RowMapper<ServiceData> {

		@Override
		public ServiceData mapRow(final ResultSet rs, final int rowNum)	throws SQLException {

			final Long id = rs.getLong("id");
			final Long contractId = rs.getLong("contractId");
			final String planCode = rs.getString("plan_code");
			final String duration = rs.getString("contract");
			final String planDescription = null;
			final String productCode = rs.getString("productCode");
			final String chargeCode = rs.getString("chargeDescription");
			final String billingFrequency = rs.getString("billingFrequency");
			final String chargingVariant = rs.getString("chargingVariant");
			final String priceregion = rs.getString("priceregion");
			final BigDecimal price = rs.getBigDecimal("price");
			final Long discountId  = rs.getLong("discountId");
			final String isPrepaid = rs.getString("isPrepaid");
			final int chargingVariant1 = Integer.valueOf(chargingVariant);
			EnumOptionData chargingvariant = SavingChargeVaraint
					.interestCompoundingPeriodType(chargingVariant1);
			final String chargeValue = chargingvariant.getValue();
			final Long currencyId = rs.getLong("currencyId");
			final String currencyCode = rs.getString("currencyCode");
			final Long productId = rs.getLong("productId");
			final String chargeOwner = rs.getString("chargeOwner");
			/*final String roundingType = rs.getString("roundingType");*/
			/*final Long glId = rs.getLong("glId");*/
			return new ServiceData(id, planCode, productCode, planDescription,chargeCode, chargeValue, price, priceregion,
					contractId,duration, billingFrequency,discountId,isPrepaid,currencyId,currencyCode,null,productId,null,null,chargeOwner);
		}
	}

	@Override
	public List<EnumOptionData> retrieveChargeVariantData() {

		EnumOptionData base = SavingChargeVaraint.interestCompoundingPeriodType(ChargeVariant.BASE);
		return Arrays.asList(base);

	}

	@Override
	public List<ServiceData> retrieveServiceCodeDetails(Long planCode) {

		context.authenticatedUser();
		final String sql = "SELECT p.id AS planId, pm.id AS id,ch.charge_description AS chargeDescription, pm.plan_id AS plan_code,pm.chargeOwner as chargeOwner,"
				+ "pm.product_id AS product_id,pm.charge_code AS charge_code,pm.price_region_id as priceregion,pm.currencyId as currencyId "
				+ " FROM b_plan_master p, b_plan_pricing pm,b_charge_codes ch"
				+ " WHERE p.id = pm.plan_id AND  ch.charge_code = pm.charge_code and pm.is_deleted='n' and pm.plan_id="
				+ planCode;

		RowMapper<ServiceData> rm = new ServiceMapper();
		return this.jdbcTemplate.query(sql, rm, new Object[] {});
	}

	private static final class ServiceMapper implements RowMapper<ServiceData> {

		@Override
		public ServiceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long planId = rs.getLong("planId");
			final String planCode = rs.getString("plan_code");
			final String productId = rs.getString("product_id");
			final String chargeCode = rs.getString("charge_code");
			final String chargeDescription = rs.getString("chargeDescription");
			final String priceRegion = rs.getString("priceregion");
			final Long currencyId = rs.getLong("currencyId");
			final String chargeOwner = rs.getString("chargeOwner");
			ServiceData  servicedata=new ServiceData(id, planId, planCode, chargeCode,productId, null, chargeDescription, priceRegion, null,null,null,null,null,null,chargeOwner);
			servicedata.setCurrencyId(currencyId);
			return servicedata;
		}
	}

	@Override
	public PricingData retrieveSinglePriceDetails(String priceId) {

		try {
			context.authenticatedUser();
			PricingMapper rm = new PricingMapper();
			final String sql = "SELECT " + rm.schema()
					+ " AND p.id =? group by p.id";

			return this.jdbcTemplate.queryForObject(sql, rm,
					new Object[] { priceId });

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class PricingMapper implements RowMapper<PricingData> {

		public String schema() {

			return " p.plan_id AS planId,pm.plan_code AS planCode,p.id AS priceId,p.product_id AS productId,c.charge_code AS chargeCode,s.product_code AS productCode , p.chargeOwner AS chargeOwner, "
					+ " p.charging_variant AS chargeVariant,p.price AS price,p.discount_id AS discountId,p.duration as contractperiod,p.price_region_id AS priceregion, pm.currencyId as currencyId,mc.code as currencyCode "
					+ " FROM b_plan_pricing p,b_product s,b_charge_codes c,b_plan_master pm left join m_currency mc ON mc.id = pm.currencyId WHERE p.charge_code = c.charge_code  AND (p.product_id = s.id or "
					+ " p.product_id ='None') AND pm.id = p.plan_id ";


		}

		@Override
		public PricingData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long planId = rs.getLong("planId");
			final Long priceId = rs.getLong("priceId");
			final String productCode = rs.getString("productCode");
			final String chargeCode = rs.getString("chargeCode");
			final BigDecimal price = rs.getBigDecimal("price");
			final Long discountId = rs.getLong("discountId");
			final String chargeVariant = rs.getString("chargeVariant");
			final int chargeVariantId = Integer.valueOf(chargeVariant);
			final Long priceregion = rs.getLong("priceregion");
			final String planCode = rs.getString("planCode");
			final String contractperiod = rs.getString("contractperiod");
			final Long currencyId = rs.getLong("currencyId");
			final String currencyCode = rs.getString("currencyCode");
			final Long productId = rs.getLong("productId");
			final String chargeOwner = rs.getString("chargeOwner");
			return new PricingData(planId, productCode, chargeCode, price,
					discountId, chargeVariantId, priceregion, planCode,
					priceId, contractperiod,currencyId,currencyCode,productId,chargeOwner);
		}
	}

	@Override
	public List<PricingData> retrievePlanAndPriceDetails(final String region) {

		try {
			this.context.authenticatedUser();
			PlanAndPricingMapper mapper = new PlanAndPricingMapper(this, region);
			String sql = "SELECT pm.plan_code AS planCode, pm.id AS planId, pm.is_prepaid as isPrepaid FROM b_plan_master pm where pm.is_deleted='N'";

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class PlanAndPricingMapper implements
			RowMapper<PricingData> {

		private final PriceReadPlatformServiceImpl priceReadPlatformServiceImp;
		private final String region;

		public PlanAndPricingMapper(PriceReadPlatformServiceImpl priceReadPlatformServiceImpl,String region) {
			this.priceReadPlatformServiceImp = priceReadPlatformServiceImpl;
			this.region = region;

		}

		@Override
		public PricingData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long planId = rs.getLong("planId");
			String planCode = rs.getString("planCode");
			String isPrepaid = rs.getString("isPrepaid");
			List<ServiceData> pricingData = this.priceReadPlatformServiceImp.retrievePriceDetails(planId, region);

			return new PricingData(planId, planCode, isPrepaid, pricingData);
		}
	}

	@Override
	public List<VoucherData> retrieveVoucherDatas(Long priceId) {
		try {
			this.context.authenticatedUser();
			VoucherDataMapper voucherDataMapper = new VoucherDataMapper();
			String sql = "select price_id as priceId from b_pin_master where price_id = ?";
			return this.jdbcTemplate.query(sql, voucherDataMapper, new Object[] {priceId});
			
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class VoucherDataMapper implements RowMapper<VoucherData> {

		@Override
		public VoucherData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			
			final Long priceId=resultSet.getLong("priceId");
			
			return new VoucherData(priceId);
		}
	}

	@Override
	public List<PricingData> retrievePlanPricesForDropdown() {
		try{
			TemplateMapper templateMapper = new TemplateMapper();
			String sql = "SELECT distinct "+templateMapper.schema();
			return jdbcTemplate.query(sql, templateMapper,new Object[]{});
			}catch(EmptyResultDataAccessException ex){
				return null;
			}
	}
		class TemplateMapper implements RowMapper<PricingData> {
		    @Override
			public PricingData mapRow(ResultSet rs, int rowNum) throws SQLException {
				
		    	final Long planId = rs.getLong("planId");
				final String planDesc = rs.getString("planDesc");
				final Long productId = rs.getLong("productId");
				final String productDesc = rs.getString("productDesc");
			
			   
				
				return new PricingData(planId, planDesc,productId,productDesc);
			}
		    
			public String schema() {
				
				return " pm.id as planId,pm.plan_description as planDesc,pd.id as productId,pd.product_description as productDesc from b_product pd join b_plan_master pm join b_plan_pricing pc where pm.id=pc.plan_id and pd.id=pc.product_id";  
				
			}
}
}
