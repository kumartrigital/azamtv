package org.mifosplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.billing.planprice.service.PriceReadPlatformService;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.order.data.OrderData;
import org.mifosplatform.portfolio.order.data.OrderDiscountData;
import org.mifosplatform.portfolio.order.data.OrderHistoryData;
import org.mifosplatform.portfolio.order.data.OrderLineData;
import org.mifosplatform.portfolio.order.data.OrderPriceData;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.portfolio.order.data.OrderUssdData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.github.mustachejava.util.State;

@Service
public class OrderReadPlatformServiceImpl implements OrderReadPlatformService

{

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private static PriceReadPlatformService priceReadPlatformService;

	@Autowired
	public OrderReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource, final PriceReadPlatformService priceReadPlatformService) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		OrderReadPlatformServiceImpl.priceReadPlatformService = priceReadPlatformService;

	}

	@Override
	public List<PlanCodeData> retrieveAllPlatformData(Long planId, Long clientId, String state, String country,
			Long salesCatalogeId, Long clientServiceId) {
		AppUser user = context.authenticatedUser();
		StringBuilder sql = new StringBuilder();
		if (clientId != null) {
			/*
			 * sql =" SELECT s.id AS id, s.plan_code AS planCode, s.is_prepaid AS isPrepaid"
			 * +
			 * "  FROM b_plan_master s, b_plan_pricing p,b_priceregion_master prd,  b_priceregion_detail pd, b_client_address cd,b_state bs"
			 * +
			 * "  WHERE s.plan_status = 1 AND s.is_deleted = 'n' AND s.id != ?  AND  prd.id = pd.priceregion_id and  p.price_region_id = pd.priceregion_id"
			 * +
			 * "  and (pd.state_id =bs.id or (pd.state_id =0 and (pd.country_id = bs.parent_code or (pd.country_id = 0 and prd.priceregion_code ='Default'))))"
			 * + "  and s.id=p.plan_id" + "  and cd.client_id ="+clientId+" group by s.id";
			 */

			/*
			 * sql="SELECT s.id AS id, s.plan_code AS planCode, s.plan_description as planDescription, s.is_prepaid AS isPrepaid"
			 * +
			 * " FROM b_plan_master s, b_plan_pricing p,b_priceregion_master prd,b_priceregion_detail pd,b_client_address cd,b_state bs"
			 * + " WHERE  s.plan_status = 1 AND s.is_deleted = 'n' AND s.id != "
			 * +planId+"  AND prd.id = pd.priceregion_id AND p.price_region_id = pd.priceregion_id AND "
			 * +
			 * " (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_state c,b_charge_codes cc,b_client_address d"
			 * +
			 * " WHERE  b.priceregion_id = a.price_region_id AND b.state_id = c.id AND a.price_region_id = b.priceregion_id AND d.state = c.state_name "
			 * +
			 * " AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code AND d.address_key = 'PRIMARY' AND d.client_id = "
			 * +clientId+" " + "  AND a.plan_id != "
			 * +planId+" AND a.is_deleted = 'n'),0) AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, b_country c,"
			 * +
			 * " b_charge_codes cc,b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code"
			 * +
			 * " AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
			 * + " AND d.client_id = "
			 * +clientId+"  AND a.plan_id != ? AND a.is_deleted = 'n'),0)) AND s.id = p.plan_id AND cd.client_id = "
			 * +clientId+"  GROUP BY s.id"; "+user.getId()+"
			 */
			sql.append(
					"SELECT DISTINCT s.id AS id, s.plan_code AS planCode, s.plan_description as planDescription, s.is_prepaid AS isPrepaid, s.plan_poid AS planpoid,");
			sql.append(
					"(select distinct(deal_poid) from b_plan_detail x where s.id=x.plan_id) AS dealpoid,co.code_value AS planTypeName,(select billfrequency_code from b_charge_codes bcc where bcc.charge_code=p.charge_code) AS chargeCycle, ");
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
			// sql.append(" AND p.plan_id not in (Select distinct plan_id from b_orders
			// where order_status=1 and client_id= "+clientId+") ");
			sql.append(
					" AND p.plan_id not in (Select distinct plan_id from b_orders where is_deleted = 'n' and order_status = 1 and client_service_id= "
							+ clientServiceId + ") ");
			sql.append(
					" AND p.plan_id in (Select plan_id from b_sales_cataloge_mapping scm join b_sales_cataloge sd on scm.cataloge_id = sd.id where sd.id = "
							+ salesCatalogeId + " and scm.is_deleted = 'N') ");

		} else if (salesCatalogeId != null) {
			sql.append(
					"  SELECT DISTINCT s.id AS id, s.plan_code AS planCode,s.plan_description as planDescription,s.plan_poid AS planpoid, ");
			sql.append("(select distinct(deal_poid) from b_plan_detail x where s.id=x.plan_id) AS dealpoid,");
			sql.append(
					" s.is_prepaid AS isPrepaid,co.code_value AS planTypeName,(select billfrequency_code from b_charge_codes bcc where bcc.charge_code=p.charge_code) AS chargeCycle, ");
			sql.append(
					" (select id from b_contract_period cp where cp.contract_period = p.duration) AS contractPeriodId FROM b_plan_master s, b_plan_pricing p, b_priceregion_master prd,");
			sql.append("  b_priceregion_detail pd, b_state bs,m_code_value co ");
			sql.append(
					" WHERE s.plan_status = 1 AND s.is_deleted = 'n'  AND s.id != 0 AND prd.id = pd.priceregion_id AND p.is_deleted = 'n' ");
			sql.append(
					" AND (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, ");
			sql.append("  b_state c, b_charge_codes cc WHERE b.priceregion_id = a.price_region_id ");
			sql.append("  AND b.state_id = c.id AND a.price_region_id = b.priceregion_id ");
			sql.append("  AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code ");
			sql.append("  AND c.state_name = '" + state + "' AND a.plan_id != 0 AND a.is_deleted = 'n'),0) ");
			sql.append("  AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, ");
			sql.append("  b_priceregion_detail b, b_country c, b_charge_codes cc WHERE ");
			sql.append(
					"  b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code ");
			sql.append("  AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id ");
			sql.append("  AND c.country_name = '" + country + "' AND a.plan_id != 0 AND a.is_deleted = 'n') , 0)) ");
			sql.append("  AND s.id = p.plan_id AND s.plan_type = co.id ");
			sql.append(
					" AND p.plan_id in (Select plan_id from b_sales_cataloge_mapping scm join b_sales_cataloge sd on scm.cataloge_id = sd.id where sd.id = "
							+ salesCatalogeId + " and scm.is_deleted = 'N') ");
		} else {
			sql.append(
					"  SELECT DISTINCT s.id AS id, s.plan_code AS planCode,s.plan_description as planDescription,s.plan_poid AS planpoid, ");
			sql.append("(select distinct(deal_poid) from b_plan_detail x where s.id=x.plan_id) AS dealpoid,");
			sql.append(
					" s.is_prepaid AS isPrepaid,co.code_value AS planTypeName,(select billfrequency_code from b_charge_codes bcc where bcc.charge_code=p.charge_code) AS chargeCycle,");
			sql.append(
					" (select id from b_contract_period cp where cp.contract_period = p.duration) AS contractPeriodId FROM b_plan_master s, b_plan_pricing p, b_priceregion_master prd,");
			sql.append("  b_priceregion_detail pd, b_state bs,m_code_value co ");
			sql.append(
					" WHERE s.plan_status = 1 AND s.is_deleted = 'n'  AND s.id != 0 AND prd.id = pd.priceregion_id AND p.is_deleted = 'n' ");
			sql.append(
					" AND (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, ");
			sql.append("  b_state c, b_charge_codes cc WHERE b.priceregion_id = a.price_region_id ");
			sql.append("  AND b.state_id = c.id AND a.price_region_id = b.priceregion_id ");
			sql.append("  AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code ");
			sql.append("  AND c.state_name = '" + state + "' AND a.plan_id != 0 AND a.is_deleted = 'n'),0) ");
			sql.append("  AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, ");
			sql.append("  b_priceregion_detail b, b_country c, b_charge_codes cc WHERE ");
			sql.append(
					"  b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code ");
			sql.append("  AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id ");
			sql.append("  AND c.country_name = '" + country + "' AND a.plan_id != 0 AND a.is_deleted = 'n') , 0)) ");
			sql.append("  AND s.id = p.plan_id AND s.plan_type = co.id ");
		}

		if (!user.isSuperUser()) {
			sql.append(" AND p.plan_id in (select plan_id from b_sales_cataloge_mapping scm ");
			sql.append(" left join b_sales_cataloge sc on sc.id=scm.cataloge_id ");
			sql.append(" left join b_user_cataloge uc on uc.cataloge_id=sc.id ");
			sql.append(" where uc.user_id = " + user.getId() + " and uc.is_deleted='N' and scm.is_deleted='N') ");
			sql.append(" GROUP BY s.id ");
		}

		RowMapper<PlanCodeData> rm = new PeriodMapper();
		return this.jdbcTemplate.query(sql.toString(), rm, new Object[] {});
	}

	private static final class PeriodMapper implements RowMapper<PlanCodeData> {

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
			List<ServiceData> services = priceReadPlatformService.retrieveServiceDetails(id);
			return new PlanCodeData(id, planCode, services, isPrepaid, planDescription, planPoId, dealPoId,
					planTypeName, null, null, chargeCycle, contractPeriodId);

		}

	}
	/*
	 * public List<PlanCodeData> retrieveAllPlatformData(Long planId, Long clientId,
	 * String state, String country) { AppUser user = context.authenticatedUser();
	 * StringBuilder sql = new StringBuilder(); if (clientId != null) {
	 * 
	 * sql =
	 * " SELECT s.id AS id, s.plan_code AS planCode, s.is_prepaid AS isPrepaid" +
	 * "  FROM b_plan_master s, b_plan_pricing p,b_priceregion_master prd,  b_priceregion_detail pd, b_client_address cd,b_state bs"
	 * +
	 * "  WHERE s.plan_status = 1 AND s.is_deleted = 'n' AND s.id != ?  AND  prd.id = pd.priceregion_id and  p.price_region_id = pd.priceregion_id"
	 * +
	 * "  and (pd.state_id =bs.id or (pd.state_id =0 and (pd.country_id = bs.parent_code or (pd.country_id = 0 and prd.priceregion_code ='Default'))))"
	 * + "  and s.id=p.plan_id" + "  and cd.client_id ="+clientId+ " group by s.id";
	 * 
	 * 
	 * 
	 * sql=
	 * "SELECT s.id AS id, s.plan_code AS planCode, s.plan_description as planDescription, s.is_prepaid AS isPrepaid"
	 * +
	 * " FROM b_plan_master s, b_plan_pricing p,b_priceregion_master prd,b_priceregion_detail pd,b_client_address cd,b_state bs"
	 * + " WHERE  s.plan_status = 1 AND s.is_deleted = 'n' AND s.id != " +planId+
	 * "  AND prd.id = pd.priceregion_id AND p.price_region_id = pd.priceregion_id AND "
	 * +
	 * " (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_state c,b_charge_codes cc,b_client_address d"
	 * +
	 * " WHERE  b.priceregion_id = a.price_region_id AND b.state_id = c.id AND a.price_region_id = b.priceregion_id AND d.state = c.state_name "
	 * +
	 * " AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code AND d.address_key = 'PRIMARY' AND d.client_id = "
	 * +clientId+" " + "  AND a.plan_id != "+planId+
	 * " AND a.is_deleted = 'n'),0) AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, b_country c,"
	 * +
	 * " b_charge_codes cc,b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code"
	 * +
	 * " AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
	 * + " AND d.client_id = "+clientId+
	 * "  AND a.plan_id != ? AND a.is_deleted = 'n'),0)) AND s.id = p.plan_id AND cd.client_id = "
	 * +clientId+"  GROUP BY s.id"; "+user.getId()+"
	 * 
	 * sql.
	 * append("SELECT DISTINCT s.id AS id, s.plan_code AS planCode, s.plan_description as planDescription, s.is_prepaid AS isPrepaid, s.plan_poid AS planpoid,"
	 * ); sql.
	 * append("(select distinct(deal_poid) from b_plan_detail x where s.id=x.plan_id) AS dealpoid,co.code_value AS planTypeName "
	 * ); sql.
	 * append(" FROM b_plan_master s, b_plan_pricing p,b_priceregion_master prd,b_priceregion_detail pd,b_client_address cd,b_state bs,m_code_value co"
	 * ); sql.append(" WHERE  s.plan_status = 1 AND s.is_deleted = 'n' AND s.id != "
	 * + planId+
	 * "  AND prd.id = pd.priceregion_id AND p.price_region_id = pd.priceregion_id AND "
	 * ); sql.
	 * append(" (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_state c,b_charge_codes cc,b_client_address d"
	 * ); sql.
	 * append(" WHERE  b.priceregion_id = a.price_region_id AND b.state_id = c.id AND a.price_region_id = b.priceregion_id AND d.state = c.state_name "
	 * ); sql.
	 * append(" AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code AND d.address_key = 'PRIMARY' AND d.client_id = "
	 * + clientId + " "); sql.append("  AND a.plan_id != " + planId+
	 * " AND a.is_deleted = 'n'),0) AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, b_country c,"
	 * ); sql.
	 * append("b_charge_codes cc,b_client_address d WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code"
	 * ); sql.
	 * append(" AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id AND c.country_name = d.country AND d.address_key = 'PRIMARY'"
	 * ); sql.append(" AND d.client_id = " + clientId + "  AND a.plan_id != " +
	 * planId+
	 * " AND a.is_deleted = 'n'),0)) AND s.id = p.plan_id AND s.plan_type = co.id AND cd.client_id = "
	 * + clientId + " "); sql.
	 * append(" AND p.plan_id not in (Select distinct plan_id from b_orders where order_status=1 and client_id= "
	 * + clientId + ") ");
	 * 
	 * } else {
	 * 
	 * sql.append(
	 * "  SELECT DISTINCT s.id AS id, s.plan_code AS planCode,s.plan_description as planDescription,s.plan_poid AS planpoid, "
	 * ); sql.
	 * append("(select distinct(deal_poid) from b_plan_detail x where s.id=x.plan_id) AS dealpoid,"
	 * ); sql.append(
	 * " s.is_prepaid AS isPrepaid,co.code_value AS planTypeName FROM b_plan_master s, b_plan_pricing p, b_priceregion_master prd, "
	 * ); sql.append("  b_priceregion_detail pd, b_state bs,m_code_value co ");
	 * sql.append(
	 * " WHERE s.plan_status = 1 AND s.is_deleted = 'n'  AND s.id != 0 AND prd.id = pd.priceregion_id "
	 * ); sql.append(
	 * " AND (pd.state_id = ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, "
	 * ); sql.
	 * append("  b_state c, b_charge_codes cc WHERE b.priceregion_id = a.price_region_id "
	 * ); sql.
	 * append("  AND b.state_id = c.id AND a.price_region_id = b.priceregion_id ");
	 * sql.
	 * append("  AND cc.charge_code = a.charge_code AND cc.charge_code = p.charge_code "
	 * ); sql.append("  AND c.state_name = '" + state +
	 * "' AND a.plan_id != 0 AND a.is_deleted = 'n'),0) "); sql.
	 * append("  AND pd.country_id in ((SELECT DISTINCT c.id FROM b_plan_pricing a, "
	 * );
	 * sql.append("  b_priceregion_detail b, b_country c, b_charge_codes cc WHERE "
	 * ); sql.append(
	 * "  b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code "
	 * ); sql.
	 * append("  AND cc.charge_code = p.charge_code AND a.price_region_id = b.priceregion_id "
	 * ); sql.append("  AND c.country_name = '" + country +
	 * "' AND a.plan_id != 0 AND a.is_deleted = 'n') , 0)) ");
	 * sql.append("  AND s.id = p.plan_id AND s.plan_type = co.id "); }
	 * 
	 * if (!user.isSuperUser()) { sql.
	 * append(" AND p.plan_id in (select plan_id from b_sales_cataloge_mapping scm "
	 * ); sql.append(" left join b_sales_cataloge sc on sc.id=scm.cataloge_id ");
	 * sql.append(" left join b_user_cataloge uc on uc.cataloge_id=sc.id ");
	 * sql.append(" where uc.user_id = " + user.getId() +
	 * " and uc.is_deleted='N' and scm.is_deleted='N') ");
	 * sql.append(" GROUP BY s.id "); }
	 * 
	 * RowMapper<PlanCodeData> rm = new PeriodMapper(); return
	 * this.jdbcTemplate.query(sql.toString(), rm, new Object[] {}); }
	 * 
	 * private static final class PeriodMapper implements RowMapper<PlanCodeData> {
	 * 
	 * @Override public PlanCodeData mapRow(final ResultSet rs, final int rowNum)
	 * throws SQLException {
	 * 
	 * Long id = rs.getLong("id"); String planCode = rs.getString("planCode");
	 * String planDescription = rs.getString("planDescription"); String isPrepaid =
	 * rs.getString("isPrepaid"); String planTypeName =
	 * rs.getString("planTypeName"); Long planPoId = rs.getLong("planPoid"); Long
	 * dealPoId = rs.getLong("dealPoid"); List<ServiceData> services =
	 * priceReadPlatformService.retrieveServiceDetails(id); return new
	 * PlanCodeData(id, planCode, services, isPrepaid, planDescription, planPoId,
	 * dealPoId, planTypeName, null, null);
	 * 
	 * }
	 * 
	 * }
	 */

	@Override
	public List<PaytermData> retrieveAllPaytermData() {

		context.authenticatedUser();

		String sql = "select s.id as id,s.paymode_code as payterm_type,s.paymode_description as units from b_paymodes s";
		RowMapper<PaytermData> rm = new PaytermMapper();
		return this.jdbcTemplate.query(sql, rm, new Object[] {});
	}

	private static final class PaytermMapper implements RowMapper<PaytermData> {

		@Override
		public PaytermData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String paytermtype = rs.getString("payterm_type");
			String units = rs.getString("units");
			String data = units.concat(paytermtype);
			return new PaytermData(id, data, null, null, null);
		}
	}

	@Override
	public List<OrderPriceData> retrieveOrderPriceData(Long orderId) {
		context.authenticatedUser();

		/*
		 * String sql =
		 * "select s.id as id,s.order_id as order_id,s.charge_code as charge_code,s.service_id as service_id,s.charge_type as charge_type,s.charge_duration as charge_duration,"
		 * +
		 * "s.duration_type as duration_type,s.price as price from order_price s where s.order_id = ?"
		 * ;
		 */
		RowMapper<OrderPriceData> rm = new OrderPriceMapper();
		final OrderPriceMapper orderPriceMapper = new OrderPriceMapper();
		String sql = "select " + orderPriceMapper.schema();
		return this.jdbcTemplate.query(sql, rm, new Object[] { orderId });
	}

	private static final class OrderPriceMapper implements RowMapper<OrderPriceData> {
		public String schema() {
			return "s.id as id,s.order_id as order_id,s.charge_code as charge_code,s.product_id as product_id,s.charge_type as charge_type,s.charge_duration as charge_duration,s.chargeOwner as chargeOwner,"
					+ "s.duration_type as duration_type,s.price as price from b_order_price s where s.order_id = ?";

		}

		@Override
		public OrderPriceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			Long orderId = rs.getLong("order_id");
			Long productId = rs.getLong("product_id");
			String chargeCode = rs.getString("charge_code");
			String chargeType = rs.getString("charge_type");
			String chargeDuration = rs.getString("charge_duration");
			String durationtype = rs.getString("duration_type");
			BigDecimal price = rs.getBigDecimal("price");
			String chargeOwner = rs.getString("chargeOwner");
			return new OrderPriceData(id, orderId, productId, chargeCode, chargeType, chargeDuration, durationtype,
					price, null, null, null, null, null, null, null, chargeOwner);
		}

	}

	@Override
	public List<PaytermData> getChargeCodes(Long planCode, Long clientId) {

		context.authenticatedUser();

		String sql = " SELECT DISTINCT b.billfrequency_code AS billfrequencyCode,a.id AS id,c.contract_period AS duration,pm.is_prepaid AS isPrepaid,a.price as price"
				+ " FROM b_charge_codes b, b_plan_master pm,b_plan_pricing a LEFT JOIN b_contract_period c ON c.contract_period = a.duration"
				+ "  WHERE  a.charge_code = b.charge_code AND a.is_deleted = 'n' AND a.plan_id = ? AND pm.id = a.plan_id";

		if (clientId != null) {

			sql = "SELECT DISTINCT b.billfrequency_code AS billfrequencyCode,a.id AS id,c.contract_period AS duration,pm.is_prepaid AS isPrepaid,a.price AS price"
					+ " FROM b_charge_codes b,b_plan_master pm,b_plan_pricing a LEFT JOIN b_contract_period c ON c.contract_period = a.duration LEFT JOIN b_priceregion_detail pd"
					+ " ON pd.priceregion_id = a.price_region_id JOIN b_client_address ca LEFT JOIN b_state s ON ca.state = s.state_name LEFT JOIN b_country con ON ca.country = con.country_name"
					+ " WHERE   a.charge_code = b.charge_code AND a.is_deleted = 'n' AND (pd.state_id =ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_state c, b_charge_codes cc,"
					+ " b_client_address d  WHERE b.priceregion_id = a.price_region_id AND b.state_id = c.id AND a.price_region_id = b.priceregion_id AND d.state = c.state_name "
					+ " AND cc.charge_code = a.charge_code AND cc.charge_code = b.charge_code AND d.address_key = 'PRIMARY' AND d.client_id = "
					+ clientId + " " + " AND a.plan_id = " + planCode
					+ " and a.is_deleted = 'n'),0) AND pd.country_id =ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a,b_priceregion_detail b,b_country c, b_charge_codes cc,b_client_address d"
					+ " WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code AND cc.charge_code = b.charge_code AND a.price_region_id = b.priceregion_id"
					+ " AND c.country_name = d.country AND d.address_key = 'PRIMARY' AND d.client_id =" + clientId
					+ " AND a.plan_id =" + planCode + " and a.is_deleted = 'n'),0)) "
					+ " AND a.plan_id =?  AND pm.id = a.plan_id group by b.billfrequency_code";
		}

		RowMapper<PaytermData> rm = new BillingFreaquencyMapper();
		return this.jdbcTemplate.query(sql, rm, new Object[] { planCode });
	}

	private static final class BillingFreaquencyMapper implements RowMapper<PaytermData> {

		@Override
		public PaytermData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			String billfrequencyCode = rs.getString("billfrequencyCode");
			String duration = rs.getString("duration");
			String isPrepaid = rs.getString("isPrepaid");
			BigDecimal price = rs.getBigDecimal("price");
			return new PaytermData(id, billfrequencyCode, duration, isPrepaid, price);
		}
	}

	@Override
	public List<OrderPriceData> retrieveOrderPriceDetails(Long orderId, Long clientId) {
		RowMapper<OrderPriceData> rm = new OrderPriceDataMapper();

		String sql = "SELECT p.id AS id,o.client_id AS clientId,p.order_id AS order_id,c.charge_description AS chargeDescription, p.chargeOwner as chargeOwner,"
				+ "  if( p.product_id =0,'None',s.product_description) AS productDescription,p.charge_type AS charge_type,p.charge_duration AS chargeDuration, p.duration_type AS durationType,"
				+ "p.price AS price,p.bill_start_date as billStartDate,p.bill_end_date as billEndDate,p.next_billable_day as nextBillableDay,p.invoice_tilldate as invoiceTillDate,"
				+ "  o.billing_align as billingAlign, o.billing_frequency as billingFrequency FROM b_order_price p,b_charge_codes c,b_product s, b_orders o "
				+ " where p.charge_code = c.charge_code AND (p.product_id = s.id or p.product_id=0) AND o.id = p.order_id AND p.order_id = ? group by p.id";

		return this.jdbcTemplate.query(sql, rm, new Object[] { orderId });
	}

	private static final class OrderPriceDataMapper implements RowMapper<OrderPriceData> {

		@Override
		public OrderPriceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			Long orderId = rs.getLong("order_id");
			Long clientId = rs.getLong("clientId");
			String productDescription = rs.getString("productDescription");
			String chargeDescription = rs.getString("chargeDescription");
			String chargeDuration = rs.getString("chargeDuration");
			String durationtype = rs.getString("durationType");
			String billingAlign = rs.getString("billingAlign");
			String billingFrequency = rs.getString("billingFrequency");
			BigDecimal price = rs.getBigDecimal("price");
			LocalDate billStartDate = JdbcSupport.getLocalDate(rs, "billStartDate");
			LocalDate billEndDate = JdbcSupport.getLocalDate(rs, "billEndDate");
			LocalDate nextBillDate = JdbcSupport.getLocalDate(rs, "nextBillableDay");
			LocalDate invoiceTillDate = JdbcSupport.getLocalDate(rs, "invoiceTillDate");
			String chargeOwner = rs.getString("chargeOwner");

			return new OrderPriceData(id, orderId, clientId, productDescription, chargeDescription, chargeDuration,
					durationtype, price, billStartDate, billEndDate, nextBillDate, invoiceTillDate, billingAlign,
					billingFrequency, null, chargeOwner);
		}
	}

	@Override
	public List<OrderData> retrieveClientOrderDetails(Long clientId) {
		try {
			final ClientOrderMapper mapper = new ClientOrderMapper(); // and
																		// o.order_status
																		// != 3

			final String sql = "select " + mapper.clientOrderLookupSchema()
					+ " where o.plan_id = p.id and o.client_id= ? and o.is_deleted='n' and " +

					"o.contract_period = co.id AND c.id=o.client_id AND c.id=o.client_id order by o.id desc";

			return jdbcTemplate.query(sql, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public List<OrderData> retrieveClientServiceOrderDetails(Long clientId, Long clientServiceId) {
		try {
			final ClientOrderMapper mapper = new ClientOrderMapper(); // and
																		// o.order_status
																		// != 3

			StringBuilder sql = new StringBuilder("select ");
			sql.append(mapper.clientOrderLookupSchema());
			sql.append(" where o.plan_id = p.id and o.client_id= ? ");
			if (clientServiceId != null) {
				sql.append("and o.client_service_id = '" + clientServiceId + "'");
			}
			sql.append(" and o.order_status = 1 and o.is_deleted='n' and ");
			sql.append("o.contract_period = co.id AND c.id=o.client_id AND c.id=o.client_id order by o.id desc");

			return jdbcTemplate.query(sql.toString(), mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class ClientOrderMapper implements RowMapper<OrderData> {

		public String clientOrderLookupSchema() {
			return " o.id AS id,o.plan_id AS plan_id,p.plan_type AS planType,p.plan_description AS planName,p.plan_poid as planPoid, o.start_date AS start_date,o.order_status AS order_status,o.auto_renew as autoRenew,p.plan_code AS plan_code,"
					+ " o.end_date AS end_date,co.id as contractPeriodId,co.contract_period as contractPeriod,o.order_no as orderNo,o.user_action AS userAction,o.active_date AS activeDate,"
					+ " p.is_prepaid as isprepaid,p.allow_topup as allowTopUp, ifnull(g.group_name, p.plan_code) as groupName,  "
					+ " date_sub(o.next_billable_day,INTERVAL 1 DAY) as invoiceTillDate,(SELECT sum(ol.price) AS price FROM b_order_price ol WHERE o.id = ol.order_id and ol.currency_id <= 1000) AS price,(select ol.price as nonCurrency from b_order_price ol where o.id = ol.order_id and ol.currency_id >= 1001) as nonCurrency,"
					+ " p.provision_sys as provSys,o.client_service_id AS clientServiceId, (select distinct deal_poid from b_plan_detail pd "
					+ "where p.id=pd.plan_id) as dealPoId"
					+ " FROM b_orders o, b_plan_master p,b_contract_period co, m_client c "
					+ "  left join b_group g on g.id=c.group_id ";
		}

		@Override
		public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long planId = rs.getLong("plan_id");
			final String planDescription = rs.getString("planName");
			final long planType = rs.getLong("planType");
			final String plancode = rs.getString("plan_code");
			final String contractPeriod = rs.getString("contractPeriod");
			final int statusId = rs.getInt("order_status");
			LocalDate startDate = JdbcSupport.getLocalDate(rs, "start_date");
			LocalDate activaDate = JdbcSupport.getLocalDate(rs, "activeDate");
			LocalDate endDate = JdbcSupport.getLocalDate(rs, "end_date");
			LocalDate invoiceTillDate = JdbcSupport.getLocalDate(rs, "invoiceTillDate");
			final double price = rs.getDouble("price");
			final String isprepaid = rs.getString("isprepaid");
			final String allowtopup = rs.getString("allowTopUp");
			final String userAction = rs.getString("userAction");
			final String provSys = rs.getString("provSys");
			final String orderNo = rs.getString("orderNo");
			final String groupName = rs.getString("groupName");
			final String autoRenew = rs.getString("autoRenew");
			final Long clientServiceId = rs.getLong("clientServiceId");
			final Long dealPoId = rs.getLong("dealPoId");
			final Long planPoid = rs.getLong("planPoid");
			EnumOptionData Enumstatus = OrderStatusEnumaration.OrderStatusType(statusId);
			String status = Enumstatus.getValue();
			final Long contractPeriodId = rs.getLong("contractPeriodId");
			final double nonCurrency = rs.getDouble("nonCurrency");

			/*
			 * return new OrderData(id, planId, plancode, status, startDate, endDate, price,
			 * contractPeriod, isprepaid, allowtopup, userAction, provSys, orderNo,
			 * invoiceTillDate, activaDate, groupName, autoRenew, clientServiceId,
			 * planDescription, planPoid, dealPoId);
			 */
			OrderData orderdata = new OrderData(id, planId, plancode, planType, status, startDate, endDate, price,
					contractPeriod, isprepaid, allowtopup, userAction, provSys, orderNo, invoiceTillDate, activaDate,
					groupName, autoRenew, clientServiceId, planDescription, planPoid, dealPoId);

			orderdata.setContractPeriodId(contractPeriodId);
			orderdata.setNonCurrency(nonCurrency);
			return orderdata;
		}
	}

	@Override
	public List<OrderHistoryData> retrieveOrderHistoryDetails(Long orderId) {

		try {
			final OrderHistoryMapper mapper = new OrderHistoryMapper();
			final String sql = "select " + mapper.clientOrderLookupSchema();
			return jdbcTemplate.query(sql, mapper, new Object[] { orderId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class OrderHistoryMapper implements RowMapper<OrderHistoryData> {

		public String clientOrderLookupSchema() {
			return "  h.id AS id,h.transaction_date AS transDate,h.actual_date AS actualDate,h.transaction_type AS transactionType,"
					+ " h.prepare_id AS PrepareRequsetId, ifnull(a.username,'by Scheduler job') as userName  FROM b_orders_history h"
					+ "  left join m_appuser a on a.id=h.createdby_id WHERE h.order_id = ? ";
		}

		@Override
		public OrderHistoryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final LocalDate transDate = JdbcSupport.getLocalDate(rs, "transDate");
			final LocalDate actualDate = JdbcSupport.getLocalDate(rs, "actualDate");
			final LocalDate provisionongDate = JdbcSupport.getLocalDate(rs, "actualDate");
			final String transactionType = rs.getString("transactionType");
			final Long PrepareRequsetId = rs.getLong("PrepareRequsetId");
			final String userName = rs.getString("userName");

			return new OrderHistoryData(id, transDate, actualDate, provisionongDate, transactionType, PrepareRequsetId,
					userName);
		}
	}

	@Override
	public List<OrderData> getActivePlans(Long clientId, String planType) {

		try {
			final ActivePlanMapper mapper = new ActivePlanMapper();

			String sql = null;
			if (planType != null) {
				if (planType.equalsIgnoreCase("prepaid")) {
					sql = "select " + mapper.activePlanLookupSchema() + " AND p.is_prepaid = 'Y'";
				} else {
					sql = "select " + mapper.activePlanLookupSchema() + " AND p.is_prepaid = 'N'";
				}
			} else {
				sql = "select " + mapper.activePlanLookupSchema();
			}

			return jdbcTemplate.query(sql, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class ActivePlanMapper implements RowMapper<OrderData> {

		public String activePlanLookupSchema() {
			return "o.id AS orderId,p.plan_code AS planCode,p.plan_description as planDescription,o.billing_frequency AS billingFreq,"
					+ "o.end_date as endDate,c.contract_period as contractPeriod,(SELECT sum(ol.price) AS price FROM b_order_price ol"
					+ " WHERE o.id = ol.order_id)  AS price  FROM b_orders o, b_plan_master p, b_contract_period c WHERE client_id =?"
					+ " AND p.id = o.plan_id  and o.contract_period=c.id and o.order_status=1 and o.is_deleted ='N' ";
		}

		@Override
		public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long orderId = rs.getLong("orderId");
			final String planCode = rs.getString("planCode");
			final String planDescription = rs.getString("planDescription");
			final String billingFreq = rs.getString("billingFreq");
			final String contractPeriod = rs.getString("contractPeriod");
			final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
			final Double price = rs.getDouble("price");

			return new OrderData(orderId, planCode, planDescription, billingFreq, contractPeriod, price, endDate);
		}
	}

	@Override
	public OrderData retrieveOrderDetails(Long orderId) {
		try {
			final ClientOrderMapper mapper = new ClientOrderMapper();
			final String sql = "select " + mapper.clientOrderLookupSchema()
					+ " where o.plan_id = p.id and o.id=? and o.is_deleted='n'"
					+ " and o.contract_period = co.id  and  c.id=o.client_id order by o.id desc";

			return jdbcTemplate.queryForObject(sql, mapper, new Object[] { orderId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public Long getRetrackId(Long id) {
		try {

			final String sql = "select MAX(h.id) as id from b_orders_history h where h.order_id=? and h.transaction_type LIKE '%tion%'";
			RowMapper<Long> rm = new OSDMapper();
			return jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class OSDMapper implements RowMapper<Long> {

		@Override
		public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			return id;
		}
	}

	@Override
	public String getOSDTransactionType(Long id) {
		try {

			final String sql = "select h.transaction_type as type from b_orders_history h where h.id=?";
			RowMapper<String> rm = new OSDMapper1();
			return jdbcTemplate.queryForObject(sql, rm, new Object[] { id });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class OSDMapper1 implements RowMapper<String> {

		@Override
		public String mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final String type = rs.getString("type");
			return type;
		}
	}

	@Override
	public String checkRetrackInterval(final Long entityId) {

		final OSDMapper1 rm = new OSDMapper1();
		final String sql = "select if (max(created_date) < date_sub(now(),INTERVAL 1 HOUR) , 'yes','no') as type"
				+ " from b_orders_history where transaction_type in ('ACTIVATION','DISCONNECTION','RECONNECTION')"
				+ " and order_id=?";
		return jdbcTemplate.queryForObject(sql, rm, new Object[] { entityId });
	}

	@Override
	public List<OrderLineData> retrieveOrderServiceDetails(Long orderId) {

		try {
			final ClientOrderServiceMapper mapper = new ClientOrderServiceMapper();
			final String sql = "select " + mapper.orderServiceLookupSchema();
			return jdbcTemplate.query(sql, mapper, new Object[] { orderId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientOrderServiceMapper implements RowMapper<OrderLineData> {

		public String orderServiceLookupSchema() {
			return " ol.id AS id,p.id AS productId,ol.order_id AS orderId,p.product_code AS productCode,s.is_auto AS isAuto,"
					+ " p.product_description AS productDescription,s.service_type AS serviceType, 'image_path' AS image "
					+ " FROM b_order_line ol, b_product p,b_service s WHERE p.service_id = s.id  "
					+ " AND order_id = ? AND ol.product_id = p.id AND ol.is_deleted = 'N'";
		}

		@Override
		public OrderLineData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long orderId = rs.getLong("orderId");
			final String productCode = rs.getString("productCode");
			final String productDescription = rs.getString("productDescription");
			final String serviceType = rs.getString("serviceType");
			final Long productId = rs.getLong("productId");
			final String isAutoProvision = rs.getString("isAuto");
			final String image = rs.getString("image");
			return new OrderLineData(id, orderId, productCode, productDescription, serviceType, productId,
					isAutoProvision, image);
		}
	}

	@Override
	public List<OrderDiscountData> retrieveOrderDiscountDetails(Long orderId) {

		try {
			final ClientOrderDiscountMapper mapper = new ClientOrderDiscountMapper();
			final String sql = "select " + mapper.orderDiscountLookupSchema();
			return jdbcTemplate.query(sql, mapper, new Object[] { orderId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientOrderDiscountMapper implements RowMapper<OrderDiscountData> {

		public String orderDiscountLookupSchema() {
			return "od.id as id,od.orderprice_id as priceId,od.discount_rate as discountAmount,d.discount_code as discountCode,"
					+ "d.discount_description as discountdescription,od.discount_type as discountType,od.discount_startdate as startDate,"
					+ " od.discount_enddate as endDate  FROM b_order_discount od, b_discount_master d"
					+ " where od.discount_id=d.id and od.is_deleted='N' and od.order_id=?";
		}

		@Override
		public OrderDiscountData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long priceId = rs.getLong("priceId");
			final String discountCode = rs.getString("discountCode");
			final String discountdescription = rs.getString("discountdescription");
			final String discountType = rs.getString("discountType");
			final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
			final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
			final BigDecimal discountAmount = rs.getBigDecimal("discountAmount");
			return new OrderDiscountData(id, priceId, discountCode, discountdescription, discountAmount, discountType,
					startDate, endDate);
		}
	}

	@Override
	public Long retrieveClientActiveOrderDetails(Long clientId, String serialNo, Long clientServiceId) {

		try {
			final ClientActiveOrderMapper mapper = new ClientActiveOrderMapper();
			String sql = null;
			if (serialNo != null) {
				sql = "select " + mapper.activeOrderLookupSchemaForAssociation()
						+ "  and a.is_deleted = 'N' and a.hw_serial_no='" + serialNo + "'";
			} else {
				sql = "select " + mapper.activeOrderLookupSchema();
				if (clientServiceId != null) {
					sql = sql + " AND o.client_service_id = " + clientServiceId;
				}
			}
			return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class ClientActiveOrderMapper implements RowMapper<Long> {

		public String activeOrderLookupSchemaForAssociation() {
			return " ifnull(max(o.id),0) as orders from b_orders o, b_association a where  o.id = a.order_id and o.client_id = ? "
					+ " and  o.order_status=1 ";
		}

		public String activeOrderLookupSchema() {
			return " count(*) AS orders FROM b_orders o WHERE o.client_id = ? AND o.order_status = 1 ";
		}

		@Override
		public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long activeOrdersCount = rs.getLong("orders");
			return activeOrdersCount;
		}
	}

	/*
	 * (non-Java doc)
	 * 
	 * @see retrieveCustomerActiveOrders(java.lang.Long)
	 */
	@Override
	public List<OrderData> retrieveCustomerActiveOrders(Long clientId) {

		final OrderMapper mapper = new OrderMapper();
		final String sql = "select id  from b_orders where order_status=1 and client_id=?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	private static final class OrderMapper implements RowMapper<OrderData> {

		/*
		 * (non-Java doc) mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public OrderData mapRow(final ResultSet rs, int rowNum) throws SQLException {

			final Long id = rs.getLong("id");

			return new OrderData(id);
		}
	}

	@Override
	public List<Long> retrieveOrderActiveAndDisconnectionIds(Long clientId, Long planId) {

		final OrderIdMapper mapper = new OrderIdMapper();
		final String sql = "select id from b_orders o where o.order_status in (1,3) and o.client_id=? and o.plan_id =? and o.is_deleted = 'N' order by o.order_status";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId, planId });
	}

	private static final class OrderIdMapper implements RowMapper<Long> {

		@Override
		public Long mapRow(final ResultSet rs, int rowNum) throws SQLException {

			return rs.getLong("id");
		}

	}

	@Override
	public List<PlanCodeData> retrieveAllPlatformDatas(String planTypeName) {

		this.context.authenticatedUser();
		PeriodMappers mapper = new PeriodMappers();
		String sql = "select " + mapper.schema();
		if (null != planTypeName) {
			sql = sql + " and co.code_value = '" + planTypeName + "' ";
		}
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class PeriodMappers implements RowMapper<PlanCodeData> {

		@Override
		public PlanCodeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String planDescription = rs.getString("planDescription");
			String isPrepaid = rs.getString("isPrepaid");
			String planTypeName = rs.getString("planTypeName");
			Long planPoId = rs.getLong("planPoid");
			Long dealPoId = rs.getLong("dealPoid");
			List<ServiceData> services = priceReadPlatformService.retrieveServiceDetails(id);
			return new PlanCodeData(id, planCode, services, isPrepaid, planDescription, planPoId, dealPoId,
					planTypeName, null, null, null, null);

		}

		public String schema() {
			return "s.id AS id,s.plan_code AS planCode, s.plan_description as planDescription,"
					+ "s.is_prepaid AS isPrepaid, s.plan_type AS planType,co.code_value AS planTypeName,s.plan_poid AS planpoid,pd.deal_poid AS dealpoid "
					+ "FROM b_plan_master s join m_code_value co on s.plan_type = co.id join b_plan_detail pd on s.id = pd.plan_id ";
		}

	}

	@Override
	public OrderData retrieveAllPoidsRelatedToOrder(Long orderId) {

		this.context.authenticatedUser();
		OrderPoIdMappers mapper = new OrderPoIdMappers();
		String sql = "select distinct " + mapper.schema() + " where o.id=" + orderId;
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});

	}

	private static final class OrderPoIdMappers implements RowMapper<OrderData> {

		@Override
		public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			String clientPoId = rs.getString("clientPoId");
			String clientServicePoId = rs.getString("servicePoId");
			String planPoId = rs.getString("planPoId");
			String dealPoId = rs.getString("dealPoId");
			String orderNo = rs.getString("orderNo");
			String planCode = rs.getString("planCode");
			OrderData orderData = new OrderData(clientPoId, clientServicePoId, planPoId, dealPoId, orderNo);
			orderData.setPlanCode(planCode);
			return orderData;

		}

		public String schema() {
			return "c.po_id as clientPoId,pm.plan_code as planCode, cs.client_service_poid as servicePoId, pm.plan_poid as planPoId, o.order_no as orderNo,"
					+ " pd.deal_poid as dealPoId from b_plan_master pm join b_plan_detail pd on pm.id=pd.plan_id"
					+ " join b_orders o on pm.id =o.plan_id join m_client c on c.id = o.client_id "
					+ "join b_client_service cs on c.id=cs.client_id";
		}

	}

	@Override
	public List<PlanCodeData> retrieveDefaultPlatformDatas(String catalogeName, Long planId) {

		this.context.authenticatedUser();
		PeriodDefaultMappers mapper = new PeriodDefaultMappers();
		String sql = "select distinct " + mapper.schema() + " where s.id != '" + planId + "'  and sc.name = '"
				+ catalogeName + "' ";
		/*
		 * if(null != catalogeName){ sql = sql + " and sc.name = '" +catalogeName+"' ";
		 * }
		 */
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class PeriodDefaultMappers implements RowMapper<PlanCodeData> {

		@Override
		public PlanCodeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String planDescription = rs.getString("planDescription");
			String isPrepaid = rs.getString("isPrepaid");
			String planTypeName = rs.getString("planTypeName");
			Long planPoId = rs.getLong("planPoid");
			Long dealPoId = rs.getLong("dealPoid");
			Long catalogeId = rs.getLong("catalogeId");
			String catalogeName = rs.getString("catalogeName");
			List<ServiceData> services = priceReadPlatformService.retrieveServiceDetails(id);
			return new PlanCodeData(id, planCode, services, isPrepaid, planDescription, planPoId, dealPoId,
					planTypeName, catalogeId, catalogeName, null, null);

		}

		public String schema() {
			return "s.id AS id,s.plan_code AS planCode, s.plan_description as planDescription,"
					+ "s.is_prepaid AS isPrepaid, s.plan_type AS planType,co.code_value AS planTypeName,s.plan_poid AS planpoid,pd.deal_poid AS dealpoid,scm.cataloge_id As catalogeId,sc.name AS catalogeName "
					+ "FROM b_plan_master s join m_code_value co on s.plan_type = co.id join b_plan_detail pd on s.id = pd.plan_id join b_sales_cataloge_mapping scm on s.id = scm.plan_id join b_sales_cataloge sc on scm.cataloge_id=sc.id ";
		}

	}

	@Override
	public Long retriveMaxOrderId() {

		// return this.jdbcTemplate.queryForLong("select ifnull((count(id)),0) + 1 from
		// b_orders o where o.client_id = '" +clientId+ "' ");

		return this.jdbcTemplate.queryForLong("select max(id) from b_orders ");
	}

	@Override
	public OrderData retrieveAllPoidsRelatedToOrderNumbers(String orderNo) {

		this.context.authenticatedUser();
		OrderPoIdMapper mapper = new OrderPoIdMapper();
		int limit =1;
		String sql = "select distinct " + mapper.schema() + "where o.order_no = ? limit ?";
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { orderNo,limit });

	}

	private static final class OrderPoIdMapper implements RowMapper<OrderData> {

		@Override
		public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long orderId = rs.getLong("orderId");
			String planPoId = rs.getString("planPoId");
			String dealPoId = rs.getString("dealPoId");
			Long priceId = rs.getLong("priceId");
			return new OrderData(orderId, planPoId, dealPoId, priceId);
		}

		public String schema() {
			return "o.id as orderId,pm.plan_poid as planPoId,pd.deal_poid as dealPoId,pp.id as priceId from b_orders "
					+ "o join b_plan_master pm on pm.id = o.plan_id join b_plan_detail pd on pm.id= pd.plan_id join "
					+ " b_plan_pricing pp on pp.plan_id = pm.id ";
		}

	}

	@Override
	public OrderData retrieveClientServicePoid(Long clientServicePoid) {

		this.context.authenticatedUser();
		OrderPoIdMap mapper = new OrderPoIdMap();
		String sql = "select distinct " + mapper.schema()
				+ "And sp.clientservice_id = (Select id from b_client_service where client_service_poid  = ?)";
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientServicePoid });

	}

	private static final class OrderPoIdMap implements RowMapper<OrderData> {

		@Override
		public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long id = rs.getLong("id");
			Long clientId = rs.getLong("clientId");
			Long clientServiceId = rs.getLong("clientServiceId");
			Long provisioningSystem = rs.getLong("provisioningSystem");
			String provisioningSystemName = rs.getString("provisioningSystemName");
			String CommandName = rs.getString("CommandName");
			String status = rs.getString("status");

			return new OrderData(id, clientId, clientServiceId, provisioningSystem, provisioningSystemName, CommandName,
					status);
		}

		public String schema() {
			return " p.id as id," + "sp.client_id as clientId, " + "sp.clientservice_id as clientServiceId, "
					+ "p.provisioning_system as provisioningSystem, " + "ne.system_code as provisioningSystemName,"
					+ "p.command_name as CommandName, " + "p.status as status from " + "b_command p " + "Join "
					+ "b_network_element ne ON ne.id = p.provisioning_system " + "JOIN "
					+ "b_service_parameters sp ON sp.parameter_value = ne.id ";

		}

	}

	@Override
	public Integer insertOrderDetails(Long clientId, Long planId, LocalDate startDate, LocalDate endDate,
			Long clientServiceId, String packageId) {
		return this.jdbcTemplate.update(
				"INSERT INTO `b_orders` (`client_id`, `plan_id`,`active_date`,`start_date`,`end_date`, `transaction_type`, `order_status`,`billing_frequency`,`contract_period`,`billing_align`,`user_action`, `client_service_id`, `is_deleted`, `createdby_id`, `order_no`) VALUES (?,?,?,?,? 'Partner Agreement', '1', '1 Month', '1','Y','AGREEMENT','152','n','10011')",
				clientId, planId, startDate, startDate, endDate, clientServiceId, packageId);

	}

	@Override
	public List<OrderData> orderDetailsForClientBalance(Long orderId) {

		final OrderNewMapper mapper = new OrderNewMapper();
		final String sql = "select distinct " + mapper.schema() + " where od.id = ? ";
		return jdbcTemplate.query(sql.toString(), mapper, new Object[] { orderId });

	}

	private static final class OrderNewMapper implements RowMapper<OrderData> {

		public String schema() {

			return " od.id as orderId,od.client_id as clientId,od.client_service_id as clientServiceId,od.plan_id as planId,pm.is_prepaid as isPrepaid , op.chargeOwner as chargeOwner from b_orders od join b_plan_master pm on pm.id=od.plan_id join b_order_price op on op.order_id=od.id ";

		}

		@Override
		public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			/*
			 * List<OrderData> orderDataList = new ArrayList<OrderData>(); OrderData
			 * orderData = null;
			 */
			/*
			 * while(rs.next()) {
			 */
			Long orderId = rs.getLong("orderId");
			Long clientId = rs.getLong("clientId");
			Long clientServiceId = rs.getLong("clientServiceId");
			Long planId = rs.getLong("planId");
			String isPrepaid = rs.getString("isPrepaid");
			OrderData orderData = new OrderData(orderId, clientId, clientServiceId, planId, isPrepaid);
			// orderDataList.add(orderData);
			// }

			return orderData;
		}

	}

	@Override
	public OrderData getRenewalOrdersByClient(Long clientId, Long planType) {
		final RenewalOrderMapper renewalOrderMapper = new RenewalOrderMapper();
		final String sql = "select  " + renewalOrderMapper.schema() + "where o.client_id =" + clientId
				+ " and o.plan_id = p.id and p.plan_type=" + planType + ") ";
		return this.jdbcTemplate.queryForObject(sql, renewalOrderMapper);

	}

	private static final class RenewalOrderMapper implements RowMapper<OrderData> {

		public String schema() {
			return " od.id as orderId,od.client_id as clientId,od.active_date as activeDate,od.start_date as orderStartDate, od.end_date as orderendDate,od.client_service_id as clientServiceId,od.plan_id as planId,od.order_no as orderNo,od.order_status as orderStatus , pd.plan_code as planCode ,pd.plan_description as planDescription ,pd.plan_type as planType,pd.start_date as startDate,pd.end_date as endDate"
					+ " from b_orders od inner join b_plan_master pd on od.plan_id = pd.id where od.id = (select max(o.id) from b_orders o"
					+ ", b_plan_master p ";
		}

		@Override
		public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Long orderId = rs.getLong("orderId");
			Long clientId = rs.getLong("clientId");
			Long clientServiceId = rs.getLong("clientServiceId");
			Long planId = rs.getLong("planId");
			String orderNo = rs.getString("orderNo");
			String orderStatus = rs.getString("orderStatus");
			String planCode = rs.getString("planCode");
			String planDescription = rs.getString("planDescription");
			Long planType = rs.getLong("planType");
			LocalDate planStartDate = JdbcSupport.getLocalDate(rs, "startDate");
			LocalDate planEndDate = JdbcSupport.getLocalDate(rs, "endDate");
			LocalDate orderActiveDate = JdbcSupport.getLocalDate(rs, "activeDate");
			LocalDate orderStartDate = JdbcSupport.getLocalDate(rs, "orderStartDate");
			LocalDate orderEndDate = JdbcSupport.getLocalDate(rs, "orderendDate");

			OrderData orderData = new OrderData(orderId, clientId, clientServiceId, planId, orderNo, orderStatus,
					planCode, planType, planDescription, planStartDate, planEndDate, orderActiveDate, orderStartDate,
					orderEndDate);

			return orderData;
		}

	}

	@Override
	public OrderUssdData getOrderDetailsBySerialNo(String orderId) {
		final RenewalOrderUssdMapper renewalOrderUssdMapper = new RenewalOrderUssdMapper();
		final String sql = "select  " + renewalOrderUssdMapper.schema() + "id.serial_no = ?";
		return this.jdbcTemplate.queryForObject(sql, renewalOrderUssdMapper, new Object[] { orderId });
	}

	private static final class RenewalOrderUssdMapper implements RowMapper<OrderUssdData> {

		public String schema() {
			return " id.client_id as clientId, price as amount ,plan_description as description,   plan_status as status  FROM b_item_detail id, b_orders bo ,b_plan_master pm,b_plan_pricing pp\n"
					+ "where id.client_id = bo.client_id and bo.plan_id = pm.id and bo.id = (select max(id) from b_orders\n"
					+ "where client_id = id.client_id) and  pp.plan_id = pm.id and ";
		}

		@Override
		public OrderUssdData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			Double amount = rs.getDouble("amount");
			String status = "PENDING";
			String message = "Payment for specified ID can be done";

			return new OrderUssdData(status, message, amount);
		}

	}

}
