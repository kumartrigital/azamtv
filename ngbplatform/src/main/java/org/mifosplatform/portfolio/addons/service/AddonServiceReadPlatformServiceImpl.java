package org.mifosplatform.portfolio.addons.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.addons.data.AddonsData;
import org.mifosplatform.portfolio.addons.data.AddonsPriceData;
import org.mifosplatform.portfolio.order.data.OrderAddonsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class AddonServiceReadPlatformServiceImpl  implements AddonServiceReadPlatformService {

	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;

 @Autowired
 public AddonServiceReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource ){
		
	 this.context=context;
	 this.jdbcTemplate=new JdbcTemplate(dataSource);
}
	
	
	
	
@Transactional
@Override
public List<AddonsData> retrieveAllPlatformData() {
	
 try{
	 this.context.authenticatedUser();
	 AddonsMapper mapper = new AddonsMapper();
	 final String sql="Select "+mapper.schema();
	 return this.jdbcTemplate.query(sql, mapper,new Object[]{});
		
 }catch(EmptyResultDataAccessException accessException){
		return null;
	}

}


private class AddonsMapper implements RowMapper<AddonsData>{
	
	public String schema(){
		return " ads.id as id,p.plan_code as planCode,pr.priceregion_code as priceRegion, c.charge_code as chargeCode" +
			   " FROM b_addons_service ads, b_plan_master p, b_priceregion_master pr, b_charge_codes c " +
			   " WHERE ads.is_deleted = 'N' and ads.plan_id=p.id and pr.id=ads.price_region_id and ads.charge_code = c.charge_code";
	}

	@Override
	public AddonsData mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		final Long id = rs.getLong("id");
		final String planCode = rs.getString("planCode");
		final String chargeCode =rs.getString("chargeCode");
		final String prieRegion = rs.getString("priceRegion");
		return new AddonsData(id,planCode,chargeCode,prieRegion);
		
		
	}
	
}

private class AddonPriceMapper implements RowMapper<AddonsPriceData>{
	
	public String schema(){
		return "  adp.id as id,adp.service_id as serviceid,s.service_code as serviceCode,adp.price as price" +
				" FROM b_addons_service_price adp, b_service s WHERE adp.is_deleted = 'N' and adp.service_id = s.id ";
	}

	@Override
	public AddonsPriceData mapRow(ResultSet rs, int rowNum) throws SQLException {
		 final Long id= rs.getLong("id");
		 final Long serviceId=rs.getLong("serviceId");
		 final String serviceCode=rs.getString("serviceCode");
		 final BigDecimal price =rs.getBigDecimal("price");
		 return new AddonsPriceData(id,serviceId,serviceCode,price,null,null);
	}
	
}


@Override
public AddonsData retrieveSingleAddonData(Long addonId) {
	
	
 try{
	 this.context.authenticatedUser();
	 AddonsMapper mapper = new AddonsMapper();
	 final String sql="Select "+mapper.schema()+ " and ads.id = ?";
	 return this.jdbcTemplate.queryForObject(sql, mapper,new Object[]{addonId});
		
 }catch(EmptyResultDataAccessException accessException){
		return null;
	}

}




@Override
public List<AddonsPriceData> retrieveAddonPriceDetails(Long addonId) {
	try{
		this.context.authenticatedUser();
		AddonPriceMapper mapper = new AddonPriceMapper();
		final String sql="Select "+mapper.schema()+" and adp.adservice_id =?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {addonId});
		
	}catch(EmptyResultDataAccessException accessException){
		return null;
		
	}
	
}




@Transactional
@Override
public List<AddonsPriceData> retrievePlanAddonDetails(Long planId,String chargeCode) {
 
	try{

		this.context.authenticatedUser();
		PlanAddonsMapper mapper=new PlanAddonsMapper();
		final String sql="select "+mapper.schema()+" AND ads.plan_id = ?";
		return this.jdbcTemplate.query(sql,mapper,new Object[]{planId});
	
	}catch(EmptyResultDataAccessException dve){
	 return null;
 }
	
	
}

private class PlanAddonsMapper implements RowMapper<AddonsPriceData>{
	
	public String schema(){
		return "  ads.id AS id,adp.service_id AS serviceId,c.id as chargeCodeId,s.service_code AS serviceCode,adp.price AS price, " +
			   " c.charge_description as chargecodeDescription FROM b_addons_service ads,b_addons_service_price adp,b_service s,b_charge_codes c " +
			   "  WHERE ads.id = adp.adservice_id AND ads.is_deleted = 'N' AND ads.charge_code = c.charge_code AND s.id = adp.service_id ";
	}

	@Override
	public AddonsPriceData mapRow(ResultSet rs, int rowNum) throws SQLException {
		 final Long id= rs.getLong("id");
		 final Long serviceId=rs.getLong("serviceId");
		 final Long chargeCodeId=rs.getLong("chargeCodeId");
		 final String serviceCode=rs.getString("serviceCode");
		 final String chargecodeDescription=rs.getString("chargecodeDescription");
		 final BigDecimal price =rs.getBigDecimal("price");
		 return new AddonsPriceData(id,serviceId,serviceCode,price,chargeCodeId,chargecodeDescription);
	}
	
}


}
