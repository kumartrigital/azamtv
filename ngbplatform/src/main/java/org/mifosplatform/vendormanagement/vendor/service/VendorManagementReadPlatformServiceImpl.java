/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendormanagement.vendor.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.vendormanagement.vendor.data.VendorManagementData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class VendorManagementReadPlatformServiceImpl implements VendorManagementReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public VendorManagementReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public List<VendorManagementData> retrieveAllVendorManagements() {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema() + "where bvm.is_deleted = 'N'";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class RetrieveMapper implements RowMapper<VendorManagementData> {

		public String schema() {
			return " bvm.id as id, bvm.vendor_code as vendorCode, bvm.vendor_name as vendorName, bvm.vendor_contact_name as contactName, "+
					"bvm.vendor_emailid as vendorEmailId, bvm.vendor_mobile as vendorMobileNo, bvm.vendor_landline as vendorLandlineNo, "+
					"bvm.vendor_address as vendorAddress, bvm.vendor_country as vendorCountryId,bc.country_name as vendorCountryName, "+
					"bvm.vendor_currency as vendorCurrency from b_vendor_management bvm "+
					"left join b_country bc on bc.id  = bvm.vendor_country ";

		}

		@Override
		public VendorManagementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			String vendorCode = rs.getString("vendorCode");
			String vendorName = rs.getString("vendorName");
			String vendorEmailId = rs.getString("vendorEmailId");
			String contactName = rs.getString("contactName");
			String vendormobileNo = rs.getString("vendorMobileNo");
			String vendorLandlineNo = rs.getString("vendorLandlineNo");
			String vendorAddress = rs.getString("vendorAddress");
			String vendorCountryName = rs.getString("vendorCountryName");
			Long vendorCountryId = rs.getLong("vendorCountryId");
			String vendorCurrency = rs.getString("vendorCurrency");
			
			return new VendorManagementData(id, vendorCode, vendorName, vendorEmailId, contactName, vendormobileNo, vendorLandlineNo,
					vendorAddress, vendorCountryName, vendorCountryId, vendorCurrency);
		}
	}
	
	@Override
	public VendorManagementData retrieveSigleVendorManagement(Long vendorId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema() +" where bvm.id = "+vendorId +" and bvm.is_deleted = 'N'";

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

}
