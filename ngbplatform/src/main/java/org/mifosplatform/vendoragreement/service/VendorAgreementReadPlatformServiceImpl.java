/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendoragreement.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.vendoragreement.data.VendorAgreementData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class VendorAgreementReadPlatformServiceImpl implements VendorAgreementReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public VendorAgreementReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public List<VendorAgreementData> retrieveAllVendorAgreements() {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class RetrieveMapper implements RowMapper<VendorAgreementData> {

		public String schema() {
			return " bva.id as id, bva.vendor_id as vendorId, bva.vendor_agmt_status as agreementStatus, "+
					"bva.vendor_agmt_startdate as startDate, bva.vendor_agmt_enddate as endDate, bva.content_type as contentType,"+
					"bva.vendor_agmt_document as documentLocation from b_vendor_agreement bva  ";

		}

		@Override
		public VendorAgreementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			Long vendorId = rs.getLong("vendorId");
			String agreementStatus = rs.getString("agreementStatus");
			Date agreementStartDate = rs.getDate("startDate");
			Date agreementEndDate = rs.getDate("endDate");
			String contentType = rs.getString("contentType");
			String documentLocation = rs.getString("documentLocation");
			
			return new VendorAgreementData(id, vendorId, agreementStatus, agreementStartDate, agreementEndDate, contentType, documentLocation);
		}
	}
	
	@Override
	public VendorAgreementData retrieveVendorAgreement(Long vendorAgreementId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema() +" where bva.id = "+vendorAgreementId;

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<VendorAgreementData> retrieveVendorAgreementDetails(Long vendorAgreementId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveVendorDetailMapper mapper = new RetrieveVendorDetailMapper();
			sql = "SELECT  " + mapper.schema() +" and bvad.vendor_agmt_id = "+vendorAgreementId+" and bvad.is_deleted = 'N'";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class RetrieveVendorDetailMapper implements RowMapper<VendorAgreementData> {

		public String schema() {
			return " bvad.id AS id,bvad.vendor_agmt_id AS vendorAgreementId,if(va.content_type = 'Package',pm.plan_code,s.service_code) as contentCode," +
				   " bvad.content_code AS contentCodeId,bvad.loyalty_type AS loyaltyType,bvad.loyalty_share AS loyaltyShare, bvad.price_region AS priceRegionId," +
				   " rm.priceregion_name as regionName,bvad.content_cost AS contentCost, bvad.content_sellprice AS contentSellPrice, bvad.duration_id as durationId " +
				   " FROM  b_vendor_agreement va, b_vendor_agmt_detail bvad" +
				   " left join b_plan_master pm on pm.id = bvad.content_code " +
				   " left join b_service s on s.id = bvad.content_code" +
				   " left join  b_priceregion_master rm on rm.id = bvad.price_region " +
				   " WHERE bvad.vendor_agmt_id = va.id";

		}

		@Override
		public VendorAgreementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			Long vendorAgreementId = rs.getLong("vendorAgreementId");
			Long contentCodeId = rs.getLong("contentCodeId");
			String contentCode = rs.getString("contentCode");
			String loyaltyType = rs.getString("loyaltyType");
			BigDecimal loyaltyShare = rs.getBigDecimal("loyaltyShare");
			Long priceRegionId = rs.getLong("priceRegionId");
			String regionName = rs.getString("regionName");
			BigDecimal contentCost = rs.getBigDecimal("contentCost");
			BigDecimal contentSellPrice = rs.getBigDecimal("contentSellPrice");
			Long durationId = rs.getLong("durationId");
			return new VendorAgreementData(id, vendorAgreementId, contentCodeId, loyaltyType, loyaltyShare, priceRegionId, contentCost,contentCode,regionName, contentSellPrice,durationId);
			
		}
	}

	@Override
	public List<VendorAgreementData> retrieveRespectiveAgreementData(Long vendorId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema()+" where bva.vendor_id = ?";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { vendorId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ServiceDetailsMapper implements RowMapper<ServiceData> {

		public String schema() {
			return " da.id AS id,da.service_code AS service_code,da.service_description AS service_description" +
				   " FROM b_service da where  da.id not in (SELECT vgd.content_code FROM b_vendor_agmt_detail vgd, b_vendor_agreement vg " +
				   " where vgd.content_code = da.id  and vg.id = vgd.vendor_agmt_id and vg.content_type ='Service'  and vg.vendor_id !=? )and da.is_deleted = 'N'";

		}

		@Override
		public ServiceData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String serviceCode = rs.getString("service_code");
			String serviceDescription = rs.getString("service_description");
			return new ServiceData(id,null,null,null,serviceCode, serviceDescription,null,null,null,null,null,null,null,null,null);

		}
	}
	
	private static final class PlanDetailsMapper implements RowMapper<PlanData> {

		public String schema() {
			return "   da.id AS id,da.plan_code AS planCode,da.plan_description  AS planDescription, da.is_prepaid as isPrepaid" +
					" FROM  b_plan_master da where  da.id not in (SELECT vgd.content_code FROM b_vendor_agmt_detail vgd, b_vendor_agreement vg " +
					" where vgd.content_code = da.id  and vg.id = vgd.vendor_agmt_id and vg.content_type ='Package' and vg.vendor_id !=?) and da.is_deleted = 'N'";

		}

		@Override
		public PlanData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String planCode = rs.getString("planCode");
			String planDescription = rs.getString("planDescription");
			String isPrepaid = rs.getString("isPrepaid");
			return new PlanData(id,planCode,planDescription, isPrepaid);

		}
	}

	@Override
	public List<ServiceData> retrieveServices(Long agId) {
		try{
		
		final ServiceDetailsMapper mapper = new ServiceDetailsMapper();
		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {agId});
		}catch(EmptyResultDataAccessException exception){
			return null;
		}

	}

	@Override
	public List<PlanData> retrievePlans(Long agId) {
		try{
		final PlanDetailsMapper mapper = new PlanDetailsMapper();
		final String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {agId});
		}catch(EmptyResultDataAccessException exception){
			return null; 
		}

		

	}

	@Override
	public List<VendorAgreementData> retrievePlanDurationData(Long planId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrievePlanDuration mapper = new RetrievePlanDuration();
			sql = "select bp.id as id,bp.price_region_id as priceRegionId,bp.duration as duration" +
					" from b_plan_pricing bp where plan_id = ? group by bp.duration";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { planId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class RetrievePlanDuration implements RowMapper<VendorAgreementData> {

		@Override
		public VendorAgreementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			Long priceRegionId = rs.getLong("priceRegionId");
			String duration = rs.getString("duration");
			
			return new VendorAgreementData(id ,priceRegionId ,duration);
		}
	}

}
