/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.clientservice.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.Balance;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceReadPlatformServiceImpl implements ClientServiceReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;

	@Autowired
	public ClientServiceReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {

		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<ClientServiceData> retriveClientServices(Long clientId) {

		final ClientServiceMapper mapper = new ClientServiceMapper();
		final String sql = "select " + mapper.schema() + " where cs.client_id = ?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
	}

	@Override
	public ClientServiceData retriveClientService(Long id) {

		final ClientServiceMapper mapper = new ClientServiceMapper();
		final String sql = "select " + mapper.schema() + " where cs.id = ?";
		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { id });

	}

	@Override
	public List<ServiceParameterData> retriveClientServiceDetails(final Long serviceId) {
		final ClientServiceDetailMapper mapper = new ClientServiceDetailMapper();
		final String sql = "select " + mapper.schema() + " where csd.clientservice_id = ?";
		return this.jdbcTemplate.query(sql, mapper, new Object[] { serviceId });

	}

	protected static final class ClientServiceMapper implements RowMapper<ClientServiceData> {

		public String schema() {
			return " cs.id as id, cs.service_id as serviceId, s.service_code as serviceCode, "
					+ " s.service_description as serviceDescription,cs.lastmodified_date as lastmodifieddate,cs.created_date as startdate, cs.status as status,cs.client_service_poid as clientServicePoId from b_client_service cs "
					+ " Join b_service s On s.id = cs.service_id and s.is_deleted = 'N' and cs.is_deleted = 'N'";
		}

		@Override
		public ClientServiceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long serviceId = rs.getLong("serviceId");
			final String serviceCode = rs.getString("serviceCode");
			final String serviceDescription = rs.getString("serviceDescription");
			final String status = rs.getString("status");
			final String clientServicePoId = rs.getString("clientServicePoId");
			LocalDate lastmodifieddate = JdbcSupport.getLocalDate(rs, "lastmodifieddate");
			LocalDate startdate = JdbcSupport.getLocalDate(rs, "startdate");
			LocalDate lastModifiedLocalDate = new LocalDate(lastmodifieddate);
			ClientServiceData clientServiceData = new ClientServiceData(id, serviceId, serviceCode, serviceDescription,
					status, clientServicePoId);
			clientServiceData.setLastmodifieddate(lastmodifieddate);
			clientServiceData.setstartdate(startdate);
			return clientServiceData;
		}
	}

	protected static final class ClientServiceDetailMapper implements RowMapper<ServiceParameterData> {

		public String schema() {
			return " csd.id as id, csd.client_id as clientId,csd.clientservice_id as clientServiceId,cs.service_id as serviceId, s.service_code as serviceCode, "
					+ " s.service_description as serviceDescription,csd.order_id as orderId,csd.plan_name as planName, "
					+ " csd.parameter_name as parameterId,mc.code_value as parameterName,ne.system_code as type,csd.parameter_value as parameterValue, csd.status as status "
					+ " from b_service_parameters csd join b_client_service cs on cs.id = csd.clientservice_id "
					+ " Join b_service s On s.id = cs.service_id and s.is_deleted = 0 "
					+ " join m_code_value mc on mc.id = csd.parameter_name "
					+ " left join b_network_element ne on ne.id = csd.parameter_value";
		}

		@Override
		public ServiceParameterData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			final Long clientServiceId = rs.getLong("clientServiceId");
			final Long serviceId = rs.getLong("serviceId");
			final String serviceCode = rs.getString("serviceCode");
			final String serviceDescription = rs.getString("serviceDescription");
			final Long orderId = rs.getLong("orderId");
			final Long parameterId = rs.getLong("parameterId");
			final String parameterName = rs.getString("parameterName");
			final String parameterValue = rs.getString("parameterValue");
			final String type = rs.getString("type");
			final String status = rs.getString("status");

			return new ServiceParameterData(id, clientId, clientServiceId, serviceId, serviceCode, serviceDescription,
					orderId, parameterId, parameterName, parameterValue, type, status);

		}

	}

	@Override
	public Integer insertclientServiceDetail(Long resourceId) {
		// return this.jdbcTemplate.update("insert into b_client_service set office_id=?
		// where serial_no=? and client_id is null", officeCode, serialNo);
		return this.jdbcTemplate.update(
				"insert into  b_client_service (`client_id`, `Service_id`, `status`) VALUES ( ?, '0', 'ACTIVE')",
				resourceId);
	}

	@Override
	public Integer insertServiceParametersDetail(Long clientId, Long id) {
		return this.jdbcTemplate.update(
				"insert into b_service_parameters (`client_id`, `parameter_name`, `parameter_value`,`status`, `clientservice_id`) VALUES(?,'0','0','new',?)",
				clientId, id);
	}

	@Override
	public List<ClientServiceData> retriveActiveClientsInOrg(Long clientServiceID) {

		final ClientServiceOrgMapper mapper = new ClientServiceOrgMapper();
		StringBuilder sql = new StringBuilder("Select ");
		sql.append(mapper.schema());
		if (clientServiceID != 0) {
			sql.append(" and a.id =" + clientServiceID);
		}
		return this.jdbcTemplate.query(sql.toString(), mapper, new Object[] {});

	}

	protected static final class ClientServiceOrgMapper implements RowMapper<ClientServiceData> {

		public String schema() {
			StringBuilder query = new StringBuilder(
					"a.id as id, b.id as clientId from b_client_service a,m_client b  where ");
			query.append("a.client_id=b.id and a.status='ACTIVE' ");
			return query.toString();
		}

		@Override
		public ClientServiceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long clientServiceId = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			ClientServiceData clientServiceData = new ClientServiceData(clientId, clientServiceId);
			return clientServiceData;
		}
	}

	@Override
	public List<Balance> retriveBalance(Long clientId) {

		final BalanceMapper mapper = new BalanceMapper();

		String sql = "SELECT bc.client_id as clientId , bc.resource_id as currencyId, SUM(bc.balance_amount) as amount,c.code as countryCode"
				+ " FROM b_client_balance bc ,m_currency c WHERE client_id = " + clientId + " and c.id = bc.resource_id"
				+ " GROUP BY bc.resource_id";

		return this.jdbcTemplate.query(sql.toString(), mapper);

	}

	protected static final class BalanceMapper implements RowMapper<Balance> {

		@Override
		public Balance mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long clientId = rs.getLong("clientId");
			final String currency_id = rs.getString("currencyId");
			final String amount = rs.getString("amount");
			final String countryCode = rs.getString("countryCode");
			Balance balanceDate = new Balance(clientId, currency_id, amount, countryCode);
			return balanceDate;
		}
	}

}