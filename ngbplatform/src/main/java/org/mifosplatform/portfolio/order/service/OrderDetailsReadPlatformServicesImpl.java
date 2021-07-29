package org.mifosplatform.portfolio.order.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.billing.planprice.data.PriceData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailsReadPlatformServicesImpl implements OrderDetailsReadPlatformServices{

	  private final JdbcTemplate jdbcTemplate;
	
 @Autowired

 public OrderDetailsReadPlatformServicesImpl(final TenantAwareRoutingDataSource dataSource) {
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	    }

	@Override
	public List<ServiceData> retrieveAllServices(Long plan_code) {


		PlanMapper mapper = new PlanMapper();

		String sql = "select " + mapper.schema()+" and da.plan_id = '"+plan_code+"'" ;
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class PlanMapper implements RowMapper<ServiceData> {

		public String schema() {
			/*return "da.id as id,se.id as serviceId, da.service_code as service_code, da.plan_id as plan_code,se.service_type as serviceType "
					+" from b_plan_detail da,b_service se where da.service_code = se.service_code";*/
			
			return "da.id as id,se.id as productId,se.product_poid as productPoId, da.product_id as product_id,se.product_code as product_code,se.service_id as serviceType,da.plan_id as plan_code "
			+" from b_plan_detail da,b_product se where da.product_id = se.id";

		}

		@Override
		public ServiceData mapRow(final ResultSet rs,final int rowNum) throws SQLException {
			
			Long id = rs.getLong("id");
			String productCode = rs.getString("product_code");
			String serviceType = rs.getString("serviceType");
			Long productId = rs.getLong("productId");
			Long productPoId = JdbcSupport.getLong(rs, "productPoId");

			return new ServiceData(id,productId,productCode,serviceType,productPoId);
	}
	}
	@Override
		public List<PriceData> retrieveAllPrices(Long planId,String billingFreq, Long clientId ) {


			PriceMapper mapper = new PriceMapper();

			/*String sql = "select " + mapper.schema()+" and da.plan_id = '"+plan_code+"' and (c.billfrequency_code='"+billingFreq+"'  or c.billfrequency_code='Once')" +
					" AND ca.client_id = ?  AND da.price_region_id =pd.priceregion_id AND s.state_name = ca.state And s.parent_code=pd.country_id" +
					" AND pd.state_id = s.id group by da.id";*/
			
			String sql ="SELECT da.id AS id,if(da.product_id = 'None', 0, se.id) AS productId,da.product_id AS product_id,da.charge_code AS charge_code, da.chargeOwner as chargeOwner," +

				     " da.charging_variant AS charging_variant,c.charge_type AS charge_type,c.charge_duration AS charge_duration,c.duration_type AS duration_type," +
				     " da.discount_id AS discountId,c.tax_inclusive AS taxInclusive,da.price AS price,da.price_region_id,s.id AS stateId,s.parent_code AS countryId," +
				     " pd.state_id AS regionState,con.country_name,pd.country_id AS regionCountryId,da.currencyId as currencyId " +
				     " FROM b_plan_pricing da LEFT JOIN b_charge_codes c ON c.charge_code = da.charge_code  " +
				     " LEFT JOIN b_product se ON (da.product_id = se.id OR da.product_id = 'None')" +
				     " LEFT JOIN b_priceregion_detail pd ON pd.priceregion_id = da.price_region_id" +
				     " JOIN b_client_address ca LEFT JOIN b_state s ON ca.state = s.state_name LEFT JOIN b_country con ON ca.country = con.country_name" +
				     " WHERE da.is_deleted = 'n' AND ca.address_key = 'PRIMARY' AND da.plan_id = "+planId+" AND c.billfrequency_code ='"+billingFreq+"' " +
				     " AND ca.client_id = "+clientId+" AND (pd.state_id =ifnull((SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, b_state c," +
				     " b_charge_codes cc,b_client_address d" +
				     " WHERE b.priceregion_id = a.price_region_id  AND cc.charge_code = a.charge_code AND  b.state_id = c.id   AND a.price_region_id = b.priceregion_id " +
				     " AND d.state = c.state_name AND d.address_key = 'PRIMARY' AND d.client_id = "+clientId+" and a.plan_id="+planId+" AND cc.billfrequency_code = '"+billingFreq+"'),0)" +
				     " AND pd.country_id = ifnull( (SELECT DISTINCT c.id FROM b_plan_pricing a, b_priceregion_detail b, b_country c, b_charge_codes cc,b_client_address d " +
				     " WHERE b.priceregion_id = a.price_region_id AND b.country_id = c.id AND cc.charge_code = a.charge_code AND a.price_region_id = b.priceregion_id " +
				     " AND c.country_name = d.country AND d.address_key = 'PRIMARY' AND d.client_id = "+clientId+" AND a.plan_id = "+planId+" AND cc.billfrequency_code =  '"+billingFreq+"'),0))" +
				     " GROUP BY da.id";
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		} 

		private static final class PriceMapper implements RowMapper<PriceData> {

			public String schema() {
				return " da.id AS id,if(da.product_id ='None',0, se.id) AS productId, da.product_id AS product_id,da.charge_code AS charge_code,da.charging_variant AS charging_variant,da.chargeOwner as chargeOwner," +
						"c.charge_type AS charge_type,c.charge_duration AS charge_duration,c.duration_type AS duration_type,da.discount_id AS discountId," +
						"c.tax_inclusive AS taxInclusive,da.price AS price,da.price_region_id,s.id AS stateId,s.parent_code AS countryId,pd.state_id AS regionState," +
						"pd.country_id AS regionCountryId,da.currencyId as currencyId FROM b_plan_pricing da,b_charge_codes c,b_product se,b_client_address ca,b_state s,b_country con,b_priceregion_detail pd,b_priceregion_master prd" +
						" WHERE  da.charge_code = c.charge_code  AND ( da.product_id = se.id or da.product_id ='None') AND da.is_deleted = 'n' AND ca.address_key='PRIMARY'" ;
					   

			}

			@Override
			public PriceData mapRow(final ResultSet rs,final int rowNum) throws SQLException {

				Long id = rs.getLong("id");
				Long productId = rs.getLong("product_id");
				String chargeCode = rs.getString("charge_code");
				String chargingVariant = rs.getString("charging_variant");
				BigDecimal price=rs.getBigDecimal("price");
				String chargeType = rs.getString("charge_type");
				String chargeDuration = rs.getString("charge_duration");
				String durationType = rs.getString("duration_type");
				/*Long serviceid = rs.getLong("serviceId");*/
				Long discountId=rs.getLong("discountId");
				Long stateId = rs.getLong("stateId");
				Long countryId=rs.getLong("countryId");
				Long regionState = rs.getLong("regionState");
				Long regionCountryId=rs.getLong("regionCountryId");
				boolean taxinclusive=rs.getBoolean("taxInclusive");
				String currencyId=rs.getString("currencyId");
				String chargeOwner=rs.getString("chargeOwner");
				
				return new PriceData(id, productId, chargeCode,chargingVariant,price,chargeType,chargeDuration,durationType,
			              null,discountId,taxinclusive,stateId,countryId,regionState,regionCountryId,currencyId,chargeOwner);

			}
	}
		
		@Override
		public List<PriceData> retrieveDefaultPrices(Long planId,String billingFrequency, Long clientId) {
			
			PriceMapper mapper1 = new PriceMapper();
			/*String sql = "select " + mapper1.schema()+" and da.plan_id = '"+planId+"' and (c.billfrequency_code='"+billingFrequency+"'  or c.billfrequency_code='Once')" +
					" AND ca.client_id = ?  AND da.price_region_id =pd.priceregion_id AND s.state_name = ca.state" +
					" And (pd.country_id =s.parent_code or (pd.country_id =0 and prd.priceregion_code ='Default'))" +
					" AND pd.state_id =0 group by da.id";*/
			String sql="select " + mapper1.schema()+" and da.plan_id ='"+planId+"' AND c.billfrequency_code = '"+billingFrequency+"' AND ca.client_id =?" +
					" AND da.price_region_id = pd.priceregion_id AND s.state_name = ca.state AND con.country_name=ca.country" +
					" AND pd.country_id=con.id and prd.id =pd.priceregion_id AND (pd.country_id = s.parent_code or (pd.country_id =0 and prd.priceregion_code ='default'))" +
					" AND pd.state_id = 0 GROUP BY da.id; ";
			return this.jdbcTemplate.query(sql, mapper1, new Object[] { clientId });
		}

	}


