/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.accounting.closure.data.LoanStatusEnumData;
import org.mifosplatform.finance.clientbalance.data.ClientBalanceData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.office.service.OfficeReadPlatformService;
import org.mifosplatform.portfolio.client.data.ClientAccountSummaryCollectionData;
import org.mifosplatform.portfolio.client.data.ClientAccountSummaryData;
import org.mifosplatform.portfolio.client.data.ClientAdditionalData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.ClientEnumerations;
import org.mifosplatform.portfolio.client.domain.ClientStatus;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientReadPlatformServiceImpl implements ClientReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final OfficeReadPlatformService officeReadPlatformService;
	// data mappers
	private final PaginationHelper<ClientData> paginationHelper = new PaginationHelper<ClientData>();
	// private final ClientMapper clientMapper = new ClientMapper();
	private final ClientLookupMapper lookupMapper = new ClientLookupMapper();
	private final ClientMembersOfGroupMapper membersOfGroupMapper = new ClientMembersOfGroupMapper();
	private final ParentGroupsMapper clientGroupsMapper = new ParentGroupsMapper();
	private final CodeValueReadPlatformService codeValueReadPlatformService;
	private final ConfigurationRepository configurationRepository;
	



	@Autowired
	public ClientReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource, final OfficeReadPlatformService officeReadPlatformService,
			final CodeValueReadPlatformService codeValueReadPlatformService,final ConfigurationRepository configurationRepository) {

		this.context = context;
		this.officeReadPlatformService = officeReadPlatformService;
		this.codeValueReadPlatformService = codeValueReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.configurationRepository =configurationRepository;
	}

	public PlatformSecurityContext getContext() {
		return this.context;
	}


	@Override
	public ClientData retrieveTemplate() {

		final AppUser currentUser = context.authenticatedUser();

		// final Collection<OfficeData> offices =
		// officeReadPlatformService.retrieveAllOfficesForDropdown();
		final Collection<ClientCategoryData> categoryDatas = this.retrieveClientCategories();
		final Collection<GroupData> groupDatas = this.retrieveGroupData();

		final Long officeId = currentUser.getOffice().getId();

		return ClientData.template(officeId, DateUtils.getLocalDateOfTenant(), null, categoryDatas, groupDatas, null);
	}

	@Override
	// @Cacheable(value = "clients", key =
	// "T(org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat(#root.target.context.authenticatedUser().getOffice().getHierarchy())")
	public Page<ClientData> retrieveAll(final SearchParameters searchParameters) {

		final AppUser currentUser = context.authenticatedUser();
		final ClientMapper clientMapper = new ClientMapper();
		final String hierarchy = currentUser.getOffice().getHierarchy();

		final String hierarchySearchString = hierarchy + "%";

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");

		sqlBuilder.append(clientMapper.schema());
		sqlBuilder.append(
				" where a.is_deleted='N' and a.address_key in ('PRIMARY','BILLING','BILLING1') and o.hierarchy like ?");

		final String extraCriteria = buildSqlStringFromClientCriteria(searchParameters);

		if (StringUtils.isNotBlank(extraCriteria)) {
			sqlBuilder.append(" and (").append(extraCriteria).append(")");
		}

		// DONT order by default - just use database natural ordering so doesnt
		// have to scan entire database table.
		// sql += " order by c.display_name ASC, c.account_no ASC";
		sqlBuilder.append(" group by c.id ");
		if (searchParameters.isOrderByRequested()) {
			sqlBuilder.append(" order by ").append(searchParameters.getOrderBy()).append(' ')
					.append(searchParameters.getSortOrder());
		}

		if (searchParameters.isLimited()) {
			sqlBuilder.append(" limit ").append(searchParameters.getLimit());
		}

		if (searchParameters.isOffset()) {
			sqlBuilder.append(" offset ").append(searchParameters.getOffset());
		}

		final String sqlCountRows = "SELECT FOUND_ROWS()";
		return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
				new Object[] { hierarchySearchString }, clientMapper);
	}

	private String buildSqlStringFromClientCriteria(final SearchParameters searchParameters) {
		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);


		final String sqlSearch = searchParameters.getSqlSearch();
		final Long officeId = searchParameters.getOfficeId();
		final String externalId = searchParameters.getExternalId();
		final String displayName = searchParameters.getName();
		final String firstname = searchParameters.getFirstname();
		final String lastname = searchParameters.getLastname();
		final String hierarchy = searchParameters.getHierarchy();
		final String groupName = searchParameters.getGroupName();
		final String status = searchParameters.getStatus();
		final String phone = searchParameters.getPhone();
		final String email = searchParameters.getEmailId();
		final String categoryType = searchParameters.getCategory();
		final String city = searchParameters.getCity();

		String extraCriteria = "";
	
		if (sqlSearch != null) {
			extraCriteria = " and (" + sqlSearch + ")";

			extraCriteria = " and ( display_name like '%" + sqlSearch + "%' OR c.phone like '%" + sqlSearch
					+ "%' OR c.account_no like '%" + sqlSearch + "%'" + " OR c.email like '%" + sqlSearch
					+ "%' OR c.firstname like '%" + sqlSearch + "%' OR c.lastname like '%" + sqlSearch
					+ "%' OR c.category_type like '%" + sqlSearch + "%'" + " OR ca.city like '%" + sqlSearch
					+ "%' OR IFNULL(( Select min(serial_no) from b_allocation ba where c.id=ba.client_id and ba. is_deleted = 'N'),'No Hardware') LIKE '%"
					+ sqlSearch + "%' )";

			// extraCriteria = " and " + sqlCondition + " = " + sqlSearch ;

		}

		if (officeId != null) {
			extraCriteria += " and office_id = " + officeId;
		}

		if (externalId != null) {
			extraCriteria += " and c.external_id like " + ApiParameterHelper.sqlEncodeString(externalId);
		}

		if (displayName != null) {
			extraCriteria += " and concat(ifnull(firstname, ''), if(firstname > '',' ', '') , ifnull(lastname, '')) like "
					+ ApiParameterHelper.sqlEncodeString(displayName);
		}

		if (firstname != null) {
			extraCriteria += " and c.firstname like " + ApiParameterHelper.sqlEncodeString(firstname);
		}

		if (lastname != null) {
			extraCriteria += " and c.lastname like " + ApiParameterHelper.sqlEncodeString(lastname);
		}

		if (hierarchy != null) {
			// Change here..based on global configuration of restrictToHierarchy
		
			
			if (restrictToHierarchy.isEnabled()) {
				extraCriteria += " and o.hierarchy like " + ApiParameterHelper.sqlEncodeString(hierarchy + "%");
			} else {
				extraCriteria += " and o.hierarchy like " + ApiParameterHelper.sqlEncodeString("%");
			}
			// end of change
		}

		if (groupName != null) {
			extraCriteria += " and g.group_name = " + ApiParameterHelper.sqlEncodeString(groupName);
		}

		if (phone != null) {
			extraCriteria += " and c.phone like " + ApiParameterHelper.sqlEncodeString(phone);
		}

		if (email != null) {
			extraCriteria += " and c.email like " + ApiParameterHelper.sqlEncodeString(email);
		}

		if (categoryType != null) {
			extraCriteria += " and c.category_type like " + ApiParameterHelper.sqlEncodeString(categoryType);
		}

		if (city != null) {
			extraCriteria += " and ca.city like " + ApiParameterHelper.sqlEncodeString(city);
		}

		if (StringUtils.isNotBlank(extraCriteria)) {
			extraCriteria = extraCriteria.substring(4);
		}

		if (status != null) {
			final Integer statusValue = ClientStatus.fromStatus(status);
			extraCriteria += " c.status_enum like " + statusValue;
		}

		return extraCriteria;
	}

	@Override
	public ClientData retrieveOne(final Long clientId) {

		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);
		
		 try {
			final AppUser currentUser = context.authenticatedUser();
			final String hierarchy = currentUser.getOffice().getHierarchy();

			// Change here..based on global configuration of restrictToHierarchy
			String hierarchySearchString = null;
			if (restrictToHierarchy.isEnabled()) {
				hierarchySearchString = hierarchy + "%";
			} else {
				hierarchySearchString = "%";
			}
			// end of change

			final ClientMapper clientMapper = new ClientMapper();
			final String sql = "select " + clientMapper.schema()
					+ " where o.hierarchy like ? and c.id = ? and a.address_key='PRIMARY' group by c.id";
			final ClientData clientData = this.jdbcTemplate.queryForObject(sql, clientMapper,
					new Object[] { hierarchySearchString, clientId });
			final String clientGroupsSql = "select " + this.clientGroupsMapper.parentGroupsSchema();

			final Collection<GroupGeneralData> parentGroups = this.jdbcTemplate.query(clientGroupsSql,
					this.clientGroupsMapper, new Object[] { clientId });
			return ClientData.setParentGroups(clientData, parentGroups);
		} catch (EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId);
		}
	}

	@Override
	public Collection<ClientData> retrieveAllForLookup(final String extraCriteria) {

		String sql = "select " + this.lookupMapper.schema();

		if (StringUtils.isNotBlank(extraCriteria)) {
			sql += " and (" + extraCriteria + ")";
		}

		return this.jdbcTemplate.query(sql, this.lookupMapper, new Object[] {});
	}

	@Override
	public Collection<ClientData> retrieveAllForLookupByOfficeId(final Long officeId) {

		final String sql = "select " + this.lookupMapper.schema() + " and c.office_id = ?";

		return this.jdbcTemplate.query(sql, this.lookupMapper, new Object[] { officeId });
	}

	@Override
	public Collection<ClientData> retrieveClientMembersOfGroup(final Long groupId) {
		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);

		final AppUser currentUser = context.authenticatedUser();
		final String hierarchy = currentUser.getOffice().getHierarchy();

		final String hierarchySearchString;
		// Change here..based on global configuration of restrictToHierarchy
		if (restrictToHierarchy.isEnabled()) {
			hierarchySearchString = hierarchy + "%";
		} else {
			hierarchySearchString = "%";
		}
		// end of change

		// final String hierarchySearchString = hierarchy + "%";

		final String sql = "select " + this.membersOfGroupMapper.schema()
				+ " where o.hierarchy like ? and pgc.group_id = ?";

		return this.jdbcTemplate.query(sql, this.membersOfGroupMapper, new Object[] { hierarchySearchString, groupId });
	}

	private static final class ClientMembersOfGroupMapper implements RowMapper<ClientData> {

		private final String schema;

		public ClientMembersOfGroupMapper() {
			final StringBuilder sqlBuilder = new StringBuilder(200);

			sqlBuilder.append(
					"c.id as id,c.title as title, c.account_no as accountNo,g.group_name as groupName, c.external_id as externalId, ");
			sqlBuilder.append("c.office_id as officeId, o.name as officeName, ");
			sqlBuilder.append(
					"c.firstname as firstname, c.middlename as middlename, c.lastname as lastname,c.is_indororp as entryType, ");
			sqlBuilder
					.append("c.fullname as fullname, c.display_name as displayName,c.category_type as categoryType, ");
			sqlBuilder.append(
					"c.email as email,c.phone as phone,c.home_phone_number as homePhoneNumber,c.activation_date as activationDate, c.image_key as imagekey,c.exempt_tax as taxExemption ");
			sqlBuilder.append("from m_client c ");
			sqlBuilder.append("join m_office o on o.id = c.office_id ");
			sqlBuilder.append("join m_group_client pgc on pgc.client_id = c.id");
			sqlBuilder.append(" left outer join b_group g on  g.id = c.group_id ");

			this.schema = sqlBuilder.toString();
		}

		public String schema() {
			return this.schema;
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final String accountNo = rs.getString("accountNo");
			final String groupName = rs.getString("groupName");
			final EnumOptionData status = null;
			final String title = rs.getString("title");
			final Long officeId = JdbcSupport.getLong(rs, "officeId");
			final Long id = JdbcSupport.getLong(rs, "id");
			final String firstname = rs.getString("firstname");
			final String middlename = rs.getString("middlename");
			final String lastname = rs.getString("lastname");
			final String fullname = rs.getString("fullname");
			final String displayName = rs.getString("displayName");
			final String externalId = rs.getString("externalId");
			final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
			final String imageKey = rs.getString("imageKey");
			final String officeName = rs.getString("officeName");
			final String categoryType = rs.getString("categoryType");
			final String email = rs.getString("email");
			final String phone = rs.getString("phone");
			final String homePhoneNumber = rs.getString("homePhoneNumber");
			final String currency = rs.getString("currency");
			final String taxExemption = rs.getString("taxExemption");
			final String entryType = rs.getString("entryType");
			final BigDecimal walletAmount = rs.getBigDecimal("walletAmount");
			final BigDecimal paidAmount = rs.getBigDecimal("paidAmount");
			final BigDecimal lastBillAmount = rs.getBigDecimal("lastBillAmount");
			final Date lastPaymentDate = rs.getDate("lastPaymentDate");
			final String poId = rs.getString("poId");

			return ClientData.instance(accountNo, groupName, status, officeId, officeName, id, firstname, middlename,
					lastname, fullname, displayName, externalId, activationDate, imageKey, categoryType, email, phone,
					homePhoneNumber, null, null, null, null, null, null, null, null, currency, taxExemption, entryType,
					walletAmount, null, null, null, title, paidAmount, lastBillAmount, lastPaymentDate, poId);
		}
	}

	private static final class ClientMapper implements RowMapper<ClientData> {

		private final String schema;

		public ClientMapper() {

			final StringBuilder builder = new StringBuilder(400);

			builder.append(
					" c.id as id,c.title as title, c.account_no as accountNo,g.group_name as groupName, c.external_id as externalId, c.status_enum as statusEnum, ");
			builder.append(
					" c.office_id as officeId, c.po_id as poId,o.name as officeName,o.po_id as officePoId,o.office_type as officeType,o.commision_model as AccountType,");
			builder.append(
					" c.firstname as firstname, c.middlename as middlename, c.lastname as lastname,c.is_indororp as entryType, ");
			builder.append(
					" c.fullname as fullname, c.display_name as displayName,mc.code_value as categoryType,m.code AS currencyCode, ");
			builder.append(
					" c.email as email,c.phone as phone,c.home_phone_number as homePhoneNumber,c.activation_date as activationDate, c.image_key as imagekey,c.exempt_tax as taxExemption, ");
			builder.append(
					" c.parent_id as parentId,Case When c.parent_id = 0 then 'Parent' When c.parent_id > 0 then concat('Child of ' , c.parent_id)");
			builder.append(" Else 'N/A' End as parent_info,cu.username as userName,cu.password as clientpassword, ");
			builder.append(
					" a.address_no as addrNo,a.street as street,a.city as city,a.state as state,a.country as country,a.district as district, ");
			// builder.append(" a.zip as zipcode,b.balance_amount as
			// balanceAmount,b.wallet_amount as walletAmount,bc.currency as currency,");
			builder.append(
					" a.zip as zipcode,(select sum(balance_amount) from b_client_balance bc where client_id = c.id  and bc.resource_id <= 1000) as balanceAmount,(select sum(balance_amount) from b_client_balance bc where client_id = c.id and bc.resource_id > 1000) as nonCurrencyAmount, b.wallet_amount as walletAmount, ");
			builder.append(
					" coalesce(min(ba.serial_no),min(oh.serial_number),'No Device') HW_Serial, cv.code_value as idKey,c.id_value as idValue, ");
			builder.append(
					" b.wallet_amount As walletAmount ,Null as nextBillDate,bp.amount_paid AS paidAmount, bp.payment_date as lastPaymentDate,bbm.bill_date as lastBillDate,bbm.Due_amount AS lastBillAmount, c.bill_mode as billMode,mcbp.bill_frequency AS chargeCycleId,bcc.billfrequency_code AS chargeCycle, ");
			builder.append(" getparent(o.id) as officeHierarchy from m_client c ");
			builder.append(" join m_office o on o.id = c.office_id ");
			builder.append(" left outer join b_client_balance b on  b.client_id = c.id ");
			builder.append(" left outer join b_group g on  g.id = c.group_id ");
			builder.append(" left outer join  m_code_value mc on  mc.id =c.category_type  ");
			builder.append(" left outer join b_client_address a on  a.client_id = c.id ");
			// builder.append(" left outer join b_country_currency bc on bc.country =
			// a.country ");
			builder.append(" left outer join b_allocation ba on (c.id = ba.client_id AND ba.is_deleted = 'N')");
			builder.append(" left outer join b_owned_hardware oh on (c.id=oh.client_id  AND oh.is_deleted = 'N')");
			builder.append(" left outer join b_clientuser cu on cu.client_id = c.id ");
			builder.append(" left outer join m_code_value cv on cv.id = c.id_key");
			builder.append(
					" left outer join b_payments bp ON bp.client_id = c.id and bp.id=(select max(bsp.id) from b_payments bsp where bp.client_id=bsp.client_id) ");
			builder.append(
					" left outer join b_bill_master bbm ON bbm.client_id = c.id and bbm.id=(select max(bbsm.id) from b_bill_master bbsm where bbm.client_id=bbsm.client_id) ");
			builder.append(" left outer join m_client_billprofile mcbp ON mcbp.client_id = c.id ");
			builder.append(" left outer join b_charge_codes bcc ON bcc.id = mcbp.bill_frequency ");
			builder.append("left outer join m_currency m ON m.id = mcbp.bill_currency");
			this.schema = builder.toString();
		}

		public String schema() {
			return this.schema;
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final String accountNo = rs.getString("accountNo");
			final String title = rs.getString("title");
			final String groupName = rs.getString("groupName");
			final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);

			final Long officeId = JdbcSupport.getLong(rs, "officeId");
			final Long id = JdbcSupport.getLong(rs, "id");
			final String firstname = rs.getString("firstname");
			final String middlename = rs.getString("middlename");
			final String lastname = rs.getString("lastname");
			final String fullname = rs.getString("fullname");
			final String displayName = rs.getString("displayName");
			final String externalId = rs.getString("externalId");
			final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
			final String imageKey = rs.getString("imageKey");
			final String officeName = rs.getString("officeName");
			String AccountType = rs.getString("AccountType");
			if (AccountType.equals("0")) {
				AccountType = "Postpaid";
			} else {
				AccountType = "Prepaid";
			}
			final String categoryType = rs.getString("categoryType");
			final String email = rs.getString("email");
			final String phone = rs.getString("phone");
			final String homePhoneNumber = rs.getString("homePhoneNumber");
			System.out.println(homePhoneNumber + " home phone number");
			final String addressNo = rs.getString("addrNo");
			final String street = rs.getString("street");
			final String city = rs.getString("city");
			final String state = rs.getString("state");
			final String country = rs.getString("country");
			final String zipcode = rs.getString("zipcode");
			final String hwSerial = rs.getString("HW_Serial");
			final BigDecimal clientBalance = rs.getBigDecimal("balanceAmount");
			final BigDecimal walletAmount = rs.getBigDecimal("walletAmount");
			/* final String currency=rs.getString("currency"); */
			final String taxExemption = rs.getString("taxExemption");
			final String entryType = rs.getString("entryType");
			final String userName = rs.getString("userName");
			final String clientpassword = rs.getString("clientpassword");
			final String parentId = rs.getString("parentId");
			final BigDecimal paidAmount = rs.getBigDecimal("paidAmount");
			final BigDecimal lastBillAmount = rs.getBigDecimal("lastBillAmount");
			final Date lastPaymentDate = rs.getDate("lastPaymentDate");
			final String billMode = rs.getString("billMode");
			final String district = rs.getString("district");
			final String poId = rs.getString("poId");
			final String parentInfo = rs.getString("parent_info");
			final String officeType = rs.getString("officeType");
			final String officePoId = rs.getString("officePoId");
			final String idKey = rs.getString("idKey");
			final String idValue = rs.getString("idValue");
			final Date lastBillDate = rs.getDate("lastBillDate");
			final Date nextBillDate = rs.getDate("nextBillDate");
			final Long chargeCycleId = rs.getLong("chargeCycleId");
			final String chargeCycle = rs.getString("chargeCycle");
			final String officeHierarchy = rs.getString("officeHierarchy");
			final BigDecimal nonCurrencyAmount = rs.getBigDecimal("nonCurrencyAmount");
			final String currencyCode = rs.getString("currencyCode");
			ClientData clientData = ClientData.instance(accountNo, groupName, status, officeId, officeName, id,
					firstname, middlename, lastname, fullname, displayName, externalId, activationDate, imageKey,
					categoryType, email, phone, homePhoneNumber, addressNo, street, city, state, country, zipcode,
					clientBalance, hwSerial, null, taxExemption, entryType, walletAmount, userName, clientpassword,
					parentId, title, paidAmount, lastBillAmount, lastPaymentDate, poId);

			clientData.setBillMode(billMode);
			clientData.setDistrict(district);
			clientData.setParentInfo(parentInfo);
			clientData.setAccountType(AccountType);
			clientData.setOfficeType(officeType);
			clientData.setOfficePoId(officePoId);
			clientData.setIdKey(idKey);
			clientData.setIdValue(idValue);
			clientData.setLastBillDate(lastBillDate);
			clientData.setNextBillDate(nextBillDate);
			clientData.setChargeCycleId(chargeCycleId);
			clientData.setChargeCycle(chargeCycle);
			clientData.setOfficeHierarchy(officeHierarchy);
			clientData.setNonCurrencyAmount(nonCurrencyAmount);
			clientData.setCurrencyCode(currencyCode);

			return clientData;

		}
	}

	private static final class ParentGroupsMapper implements RowMapper<GroupGeneralData> {

		public String parentGroupsSchema() {
			return "gp.id As groupId , gp.display_name As groupName from m_client cl JOIN m_group_client gc ON cl.id = gc.client_id "
					+ "JOIN m_group gp ON gp.id = gc.group_id WHERE cl.id  = ?";
		}

		@Override
		public GroupGeneralData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long groupId = JdbcSupport.getLong(rs, "groupId");
			final String groupName = rs.getString("groupName");

			return GroupGeneralData.lookup(groupId, groupName);
		}
	}

	private static final class ClientLookupMapper implements RowMapper<ClientData> {

		private final String schema;

		public ClientLookupMapper() {
			final StringBuilder builder = new StringBuilder(200);

			builder.append("c.id as id, c.display_name as displayName, ");
			builder.append("c.office_id as officeId, o.name as officeName ");
			builder.append("from m_client c ");
			builder.append("join m_office o on o.id = c.office_id ");

			this.schema = builder.toString();
		}

		public String schema() {
			return this.schema;
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String displayName = rs.getString("displayName");
			final Long officeId = rs.getLong("officeId");
			final String officeName = rs.getString("officeName");

			return ClientData.lookup(id, displayName, officeId, officeName);
		}
	}

	@Override
	public ClientAccountSummaryCollectionData retrieveClientAccountDetails(final Long clientId) {

		try {
			this.context.authenticatedUser();

			// Check if client exists
			retrieveOne(clientId);

			final List<ClientAccountSummaryData> pendingApprovalLoans = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> awaitingDisbursalLoans = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> openLoans = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> closedLoans = new ArrayList<ClientAccountSummaryData>();

			final ClientLoanAccountSummaryDataMapper rm = new ClientLoanAccountSummaryDataMapper();

			final String sql = "select " + rm.loanAccountSummarySchema() + " where l.client_id = ?";

			final List<ClientAccountSummaryData> results = this.jdbcTemplate.query(sql, rm, new Object[] { clientId });
			if (results != null) {
				for (ClientAccountSummaryData row : results) {

					final LoanStatusMapper statusMapper = new LoanStatusMapper(row.accountStatusId());

					if (statusMapper.isOpen()) {
						openLoans.add(row);
					} else if (statusMapper.isAwaitingDisbursal()) {
						awaitingDisbursalLoans.add(row);
					} else if (statusMapper.isPendingApproval()) {
						pendingApprovalLoans.add(row);
					} else {
						closedLoans.add(row);
					}
				}
			}

			final List<ClientAccountSummaryData> pendingApprovalDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> approvedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> withdrawnByClientDespositAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> closedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> rejectedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> preclosedDepositAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> maturedDepositAccounts = new ArrayList<ClientAccountSummaryData>();

			final List<ClientAccountSummaryData> pendingApprovalSavingAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> approvedSavingAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> withdrawnByClientSavingAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> rejectedSavingAccounts = new ArrayList<ClientAccountSummaryData>();
			final List<ClientAccountSummaryData> closedSavingAccounts = new ArrayList<ClientAccountSummaryData>();

			final ClientSavingsAccountSummaryDataMapper savingsAccountSummaryDataMapper = new ClientSavingsAccountSummaryDataMapper();
			final String savingsSql = "select " + savingsAccountSummaryDataMapper.schema() + " where sa.client_id = ?";
			final List<ClientAccountSummaryData> savingsAccounts = this.jdbcTemplate.query(savingsSql,
					savingsAccountSummaryDataMapper, new Object[] { clientId });

			approvedSavingAccounts.addAll(savingsAccounts);

			return new ClientAccountSummaryCollectionData(pendingApprovalLoans, awaitingDisbursalLoans, openLoans,
					closedLoans, pendingApprovalDepositAccounts, approvedDepositAccounts,
					withdrawnByClientDespositAccounts, rejectedDepositAccounts, closedDepositAccounts,
					preclosedDepositAccounts, maturedDepositAccounts, pendingApprovalSavingAccounts,
					approvedSavingAccounts, withdrawnByClientSavingAccounts, rejectedSavingAccounts,
					closedSavingAccounts);

		} catch (EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(clientId);
		}
	}

	@Override
	public Collection<ClientAccountSummaryData> retrieveClientLoanAccountsByLoanOfficerId(final Long clientId,
			final Long loanOfficerId) {

		this.context.authenticatedUser();

		// Check if client exists
		retrieveOne(clientId);

		final ClientLoanAccountSummaryDataMapper rm = new ClientLoanAccountSummaryDataMapper();

		final String sql = "select " + rm.loanAccountSummarySchema()
				+ " where l.client_id = ? and l.loan_officer_id = ?";

		final List<ClientAccountSummaryData> loanAccounts = this.jdbcTemplate.query(sql, rm,
				new Object[] { clientId, loanOfficerId });

		return loanAccounts;
	}

	private static final class ClientSavingsAccountSummaryDataMapper implements RowMapper<ClientAccountSummaryData> {

		final String schemaSql;

		public ClientSavingsAccountSummaryDataMapper() {
			final StringBuilder accountsSummary = new StringBuilder();
			accountsSummary.append("sa.id as id, sa.account_no as accountNo, sa.external_id as externalId,");
			accountsSummary.append("sa.product_id as productId, p.name as productName ");
			accountsSummary.append("from m_savings_account sa ");
			accountsSummary.append("join m_savings_product as p on p.id = sa.product_id ");

			this.schemaSql = accountsSummary.toString();
		}

		public String schema() {
			return this.schemaSql;
		}

		@Override
		public ClientAccountSummaryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = JdbcSupport.getLong(rs, "id");
			final String accountNo = rs.getString("accountNo");
			final String externalId = rs.getString("externalId");
			final Long productId = JdbcSupport.getLong(rs, "productId");
			final String loanProductName = rs.getString("productName");
			final LoanStatusEnumData loanStatus = null;

			return new ClientAccountSummaryData(id, accountNo, externalId, productId, loanProductName, loanStatus);
		}
	}

	private static final class ClientLoanAccountSummaryDataMapper implements RowMapper<ClientAccountSummaryData> {

		public String loanAccountSummarySchema() {

			final StringBuilder accountsSummary = new StringBuilder(
					"l.id as id, l.account_no as accountNo, l.external_id as externalId,");
			accountsSummary.append("l.product_id as productId, lp.name as productName,")
					.append("l.loan_status_id as statusId ").append("from m_loan l ")
					.append("LEFT JOIN m_product_loan AS lp ON lp.id = l.product_id ");

			return accountsSummary.toString();
		}

		@Override
		public ClientAccountSummaryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = JdbcSupport.getLong(rs, "id");
			final String accountNo = rs.getString("accountNo");
			final String externalId = rs.getString("externalId");
			final Long productId = JdbcSupport.getLong(rs, "productId");
			final String loanProductName = rs.getString("productName");

			return new ClientAccountSummaryData(id, accountNo, externalId, productId, loanProductName, null);
		}
	}

	@Override
	public ClientData retrieveClientByIdentifier(final Long identifierTypeId, final String identifierKey) {
		try {
			final ClientIdentifierMapper mapper = new ClientIdentifierMapper();

			final String sql = "select " + mapper.clientLookupByIdentifierSchema();

			return jdbcTemplate.queryForObject(sql, mapper, new Object[] { identifierTypeId, identifierKey });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientIdentifierMapper implements RowMapper<ClientData> {

		public String clientLookupByIdentifierSchema() {
			return "c.id as id, c.account_no as accountNo, c.status_enum as statusEnum, c.firstname as firstname, c.middlename as middlename, c.lastname as lastname, "
					+ "c.fullname as fullname, c.display_name as displayName,"
					+ "c.office_id as officeId, o.name as officeName "
					+ " from m_client c, m_office o, m_client_identifier ci "
					+ "where o.id = c.office_id and c.id=ci.client_id "
					+ "and ci.document_type_id= ? and ci.document_key like ?";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");

			final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);

			final String firstname = rs.getString("firstname");
			final String middlename = rs.getString("middlename");
			final String lastname = rs.getString("lastname");
			final String fullname = rs.getString("fullname");
			final String displayName = rs.getString("displayName");

			final Long officeId = rs.getLong("officeId");
			final String officeName = rs.getString("officeName");

			return ClientData.clientIdentifier(id, accountNo, status, firstname, middlename, lastname, fullname,
					displayName, officeId, officeName);
		}
	}

	@Override
	public Collection<ClientCategoryData> retrieveClientCategories() {
		try {
			final ClientCategoryMapper mapper = new ClientCategoryMapper();

			final String sql = "select " + mapper.clientLookupByCategorySchema();

			return jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientCategoryMapper implements RowMapper<ClientCategoryData> {

		public String clientLookupByCategorySchema() {
			return " mcv.id AS id, mcv.code_value AS codeValue FROM m_code_value mcv,m_code mc  where mcv.code_id=mc.id"
					+ " and mc.code_name='Client Category'";
		}

		@Override
		public ClientCategoryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String codeValue = rs.getString("codeValue");
			return new ClientCategoryData(id, codeValue, null, null, null, null, null);

		}
	}

	@Override
	public Collection<GroupData> retrieveGroupData() {
		try {

			final GroupDataMapper mapper = new GroupDataMapper();

			final String sql = "select " + mapper.groupDataSchema();

			return jdbcTemplate.query(sql, mapper, new Object[] {});

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class GroupDataMapper implements RowMapper<GroupData> {

		public String groupDataSchema() {

			return "bg.id as id,bg.group_name as groupName from b_group bg";
		}

		@Override
		public GroupData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String groupName = rs.getString("groupName");

			return new GroupData(id, groupName);
		}
	}

	private static final class AdditionalClientDataMapper implements RowMapper<ClientAdditionalData> {
		public String schema() {
			return "  a.id as id,a.client_id as clientId,a.job_title as jobTitle,a.gender_id as genderId,a.finance_id as financeId,"
					+ "  a.uts_customer_id as utsCustomerId,a.date_of_birth as dob,a.nationality_id as nationalityId,a.age_group_id as ageGroupId,"
					+ "  a.id_type as customerIdType,a.id_number as customerIdentification,a.prefere_lan_id as preferLanId,"
					+ "  a.prefere_communication_id as preferCommId,a.remarks as remarks"
					+ "  FROM additional_client_fields a where client_id=?";
		}

		@Override
		public ClientAdditionalData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			final String financeId = rs.getString("financeId");
			final String utsCustomerId = rs.getString("utsCustomerId");
			final String customerIdentification = rs.getString("customerIdentification");
			final String jobTitle = rs.getString("jobTitle");
			final LocalDate dob = JdbcSupport.getLocalDate(rs, "dob");
			final Long nationalityId = rs.getLong("nationalityId");
			final Long ageGroupId = rs.getLong("ageGroupId");
			final Long customerIdType = rs.getLong("customerIdType");
			final Long preferCommId = rs.getLong("preferCommId");
			final Long preferLanId = rs.getLong("preferLanId");
			final Long genderId = rs.getLong("genderId");
			final String remarks = rs.getString("remarks");
			return new ClientAdditionalData(id, clientId, financeId, utsCustomerId, customerIdentification, jobTitle,
					dob, nationalityId, ageGroupId, customerIdType, preferCommId, preferLanId, genderId, remarks);
		}
	}

	@Override
	public ClientData retrieveAllClosureReasons(final String clientClosureReason) {
		final List<CodeValueData> closureReasons = new ArrayList<CodeValueData>(
				this.codeValueReadPlatformService.retrieveCodeValuesByCode(clientClosureReason));
		return ClientData.template(null, null, null, null, closureReasons);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #retrieveClientBillModes(java.lang.Long)
	 */
	@Override
	public ClientCategoryData retrieveClientBillModes(final Long clientId) {

		try {

			this.context.authenticatedUser();
			final BillModeMapper mapper = new BillModeMapper();
			final String sql = "select id as id , bill_mode as billMode from m_client where id=?";
			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class BillModeMapper implements RowMapper<ClientCategoryData> {

		@Override
		public ClientCategoryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String billMode = rs.getString("billMode");
			return new ClientCategoryData(id, null, billMode, null, null, null, null);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #retrievingParentClients(java.lang.String)
	 */
	@Override
	public List<ClientCategoryData> retrievingParentClients(final String query) {

		try {
			this.context.authenticatedUser();
			final parentClientMapper mapper = new parentClientMapper();
			final String sql = "select c.id as id , c.po_id as poId, c.account_no as accountNo,c.display_name as displayName from m_client c where parent_id = 0"
					+ " and display_name like '%" + query + "%' ORDER BY id LIMIT 20 ";
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see #retrievedParentAndChildData(java.lang.Long, java.lang.Long)
	 */
	@Override

	public List<ClientCategoryData> retrievedParentAndChildData(final Long parentClientId, final Long clientId) {

		try {
			this.context.authenticatedUser();
			final parentClientMapper mapper = new parentClientMapper();
			// check parentClient information
			if (parentClientId != null) {
				final String sql = mapper.parentChildSchema() + " where m.id= ? ";
				return this.jdbcTemplate.query(sql, mapper, new Object[] { parentClientId });
			} else {
				final String sql = mapper.parentChildSchema() + " where m.parent_id= ?  ";
				return this.jdbcTemplate.query(sql, mapper, new Object[] { clientId });
			}
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class parentClientMapper implements RowMapper<ClientCategoryData> {

		public String parentChildSchema() {
			return "select id,account_no as accountNo,po_id as poId,display_name as displayName from m_client m ";
		}

		@Override
		public ClientCategoryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");
			final String displayName = rs.getString("displayName");
			final Long poId = rs.getLong("poId");

			ClientCategoryData clientCategoryData = new ClientCategoryData(id, null, null, accountNo, displayName, null,
					null);
			clientCategoryData.setPoId(poId);
			return clientCategoryData;

		}
	}

	@Override
	public Boolean countChildClients(final Long entityId) {
		context.authenticatedUser();
		boolean result = false;
		final String sql = "select count(id) from m_client m where m.parent_id= ? ";
		final int count = this.jdbcTemplate.queryForObject(sql, Integer.class, new Object[] { entityId });
		if (count > 0) {
			result = true;
		}
		return result;

	}

	@Override
	public ClientAdditionalData retrieveClientAdditionalData(Long clientId) {
		try {

			final AdditionalClientDataMapper mapper = new AdditionalClientDataMapper();
			final String sql = "select " + mapper.schema();
			return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientWalletMapper implements RowMapper<ClientData> {

		public String schema() {

			return " c.id as id, c.account_no as accountNo, b.wallet_amount as walletAmount, "
					+ " coalesce(min(ba.serial_no),min(oh.serial_number),'No Device') HW_Serial from m_client c "
					+ " left outer join b_client_balance b ON b.client_id = c.id "
					+ " left outer join b_allocation ba on (c.id = ba.client_id AND ba.is_deleted = 'N') "
					+ " left outer join b_owned_hardware oh on (c.id=oh.client_id  AND oh.is_deleted = 'N') "
					+ " left outer join b_clientuser cu ON cu.client_id = c.id ";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");
			final BigDecimal walletAmount = rs.getBigDecimal("walletAmount");
			final String hwSerialNumber = rs.getString("HW_Serial");

			return ClientData.walletAmount(id, accountNo, walletAmount, hwSerialNumber);
		}
	}

	@Override
	public ClientData retrieveClientWalletAmount(Long clientId, String type) {
		try {

			final ClientWalletMapper mapper = new ClientWalletMapper();
			String sql = "select " + mapper.schema();
			if (type != null && type.equalsIgnoreCase("userId")) {
				sql = sql + " where cu.zebra_subscriber_id = ? ";
			} else {
				sql = sql + " where  c.id = ? ";
			}
			sql += " and c.status_enum <> 400 group by c.id ";

			return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Page<ClientData> retrieveAllClients(SearchParameters searchParameters) {
		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);

		final AppUser currentUser = context.authenticatedUser();
		final ClientResourceMapper clientMapper = new ClientResourceMapper();
		final String hierarchy = currentUser.getOffice().getHierarchy();

		final String hierarchySearchString;

		// Change here..based on global configuration of restrictToHierarchy
		if (restrictToHierarchy.isEnabled()) {
			hierarchySearchString = hierarchy + "%";
		} else {
			hierarchySearchString = "%";
		}
		// end of change

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select distinct SQL_CALC_FOUND_ROWS ");

		sqlBuilder.append(clientMapper.schema());
		sqlBuilder.append(" where o.hierarchy like ?");
		// sqlBuilder.append(clientMapper.portfolioClient());

		final String extraCriteria = buildSqlStringFromClientCriteria(searchParameters);

		if (StringUtils.isNotBlank(extraCriteria)) {
			sqlBuilder.append(" and (").append(extraCriteria).append(")");
		}

		if (searchParameters.isLimited()) {
			sqlBuilder.append(" limit ").append(searchParameters.getLimit());
		}

		if (searchParameters.isOffset()) {
			sqlBuilder.append(" offset ").append(searchParameters.getOffset());
		}

		final String sqlCountRows = "SELECT FOUND_ROWS()";
		return this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows, sqlBuilder.toString(),
				new Object[] { hierarchySearchString }, clientMapper);

	}

	private class ClientResourceMapper implements RowMapper<ClientData> {

		public String schema() {

			return " c.id AS id, c.account_no AS accountNo, c.status_enum AS statusEnum, o.NAME AS officeName ,c.external_id AS externalId, "
					+ " c.firstname as firstName, c.lastname as lastName, c.phone as phone, c.email as  emailId, c.category_type as categoryType, ca.city as cityName,"
					+ " c.display_name AS displayName, (select sum(balance_amount) from b_client_balance where client_id = c.id) AS balanceAmount, cu.username AS userName, "
					+ " cu.password AS clientpassword, (SELECT coalesce(ba.serial_no, 'No Device') fROM b_allocation ba where c.id = ba.client_id AND ba.status='allocated' "
					+ " order by item_master_id limit 1) HW_Serial, "
					+ " b.wallet_amount As walletAmount ,bp.amount_paid AS paidAmount, bp.payment_date as lastPaymentDate,bbm.Due_amount AS lastBillAmount "
					+ " FROM m_client c JOIN m_office o ON o.id = c.office_id "
					+ " JOIN b_client_address ca on ca.client_id=c.id"
					+ " LEFT OUTER JOIN b_client_balance b ON b.client_id = c.id "
					+ " LEFT OUTER JOIN b_clientuser cu ON cu.client_id = c.id "
					+ " LEFT OUTER JOIN b_payments bp ON bp.client_id = c.id and bp.id=(select max(bsp.id) from b_payments bsp where bp.client_id=bsp.client_id) "
					+ " LEFT OUTER JOIN b_bill_master bbm ON bbm.client_id = c.id and bbm.id=(select max(bbsm.id) from b_bill_master bbsm where bbm.client_id=bbsm.client_id) ";

		}

		public String portfolioClient() {
			AppUser logedInUser = context.authenticatedUser();
			return "and c.id in (Select distinct pcs.client_id from m_portfolio_command_source pcs where "
					+ "date_format(pcs.made_on_date, '%Y-%m-%d') between curdate() - INTERVAL DAYOFWEEK(curdate()) + 10 DAY "
					+ "and curdate() and maker_id = " + logedInUser.getId() + ")";
		}

		@Override
		public ClientData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final String accountNo = rs.getString("accountNo");
			final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);
			final String officeName = rs.getString("officeName");
			final String externalId = rs.getString("externalId");
			final String displayName = rs.getString("displayName");
			final BigDecimal clientBalance = rs.getBigDecimal("balanceAmount");
			final String userName = rs.getString("userName");
			final String clientpassword = rs.getString("clientpassword");
			final String hwSerial = rs.getString("HW_Serial");
			final Long id = JdbcSupport.getLong(rs, "id");
			final BigDecimal walletAmount = rs.getBigDecimal("walletAmount");
			final BigDecimal paidAmount = rs.getBigDecimal("paidAmount");
			final BigDecimal lastBillAmount = rs.getBigDecimal("lastBillAmount");
			final Date lastPaymentDate = rs.getDate("lastPaymentDate");

			return ClientData.instance(accountNo, null, status, null, officeName, id, null, null, null, null,
					displayName, externalId, null, null, null, null, null, null, null, null, null, null, null, null,
					clientBalance, hwSerial, null, null, null, walletAmount, userName, clientpassword, null, null,
					paidAmount, lastBillAmount, lastPaymentDate, null);
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public ClientData retrieveSearchClientId(final String columnName, final String columnValue) {
		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);

		try {
			StringBuilder sqlQuery = new StringBuilder(200);
			SearchMapper mapper = new SearchMapper();
			final AppUser currentUser = context.authenticatedUser();
			final String hierarchy = currentUser.getOffice().getHierarchy();
			String hierarchySearchString;

			// Change here..based on global configuration of restrictToHierarchy
			if (restrictToHierarchy.isEnabled()) {
				hierarchySearchString = hierarchy + "%";
			} else

			{
				hierarchySearchString = "%";
			}
			// end of change

			sqlQuery.append("Select ");
			sqlQuery.append(mapper.schema());
			if ((columnName.equals("serial_no")) || (columnName.equals("provisioning_serialno"))) {
				sqlQuery.append(" join b_item_detail bid on bid.client_id=c.id where bid." + columnName + " = '"
						+ columnValue + "' and ");
			} else {
				sqlQuery.append(" where c." + columnName + " = '" + columnValue + "' and ");
			}
			sqlQuery.append("o.hierarchy like '" + hierarchySearchString + "'");
			
			System.out.println("ClientReadPlatformServiceImpl.retrieveSearchClientId(): "+sqlQuery);
			return this.jdbcTemplate.queryForObject(sqlQuery.toString(), mapper, new Object[] {});

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}


	
	@SuppressWarnings("deprecation")
	@Override
	public List<ClientData> retrieveTypeSearchClientId(final String columnName, final String columnValue) {
		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);

		try {
			StringBuilder sqlQuery = new StringBuilder(200);
			typeSearchMapper mapper = new typeSearchMapper();
			final AppUser currentUser = context.authenticatedUser();
			final String hierarchy = currentUser.getOffice().getHierarchy();
			String hierarchySearchString;

			// Change here..based on global configuration of restrictToHierarchy
			if (restrictToHierarchy.isEnabled()) {
				hierarchySearchString = hierarchy + "%";
			} else

			{
				hierarchySearchString = "%";
			}
			// end of change

			sqlQuery.append("Select ");
			sqlQuery.append(mapper.typeschema());
		
				sqlQuery.append(" where c." + columnName + " like '" +"%"+ columnValue + "%' limit 14");
			
				System.out.println("ClientReadPlatformServiceImpl.retrieveTypeSearchClientId() :" +sqlQuery.toString() );
			return this.jdbcTemplate.query(sqlQuery.toString(), mapper, new Object[] {});

		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	
	private static final class SearchMapper implements RowMapper<ClientData> {

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");
			final String phone = rs.getString("phone");
			final String name = rs.getString("customerName");
			final Integer statusEnum = JdbcSupport.getInteger(rs, "status");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);
			final String officeName = rs.getString("office");
			final String serailNo = rs.getString("serialNo");
			final BigDecimal clientBalance = rs.getBigDecimal("balance");
			final Long clientPoId = rs.getLong("clientPoId");

			ClientData clientData = ClientData.advancedSearchClient(id, name, accountNo, phone, status, officeName,
					serailNo, clientBalance);
			clientData.setclientPoId(clientPoId);
			return clientData;
		}
		
		public String schema() {
			StringBuilder stringBuilder = new StringBuilder(
					"c.id as id , c.account_no as accountNo,c.status_enum as status, c.phone as phone, o.name as office,c.po_id as clientPoId,");
			stringBuilder.append(
					" c.display_name as customerName,MAX(b.balance_amount) as balance,(SELECT coalesce(ba.serial_no, 'No Device') fROM b_allocation ba");
			stringBuilder.append(
					" where c.id = ba.client_id AND ba.status = 'allocated' order by item_master_id limit 1) serialNo");
			stringBuilder.append(
					" from m_client c join m_office o on c.office_id =o.id LEFT OUTER JOIN b_client_balance b ON b.client_id = c.id ");
			return stringBuilder.toString();
		}
		
	
	}
	
	

	private static final class typeSearchMapper implements RowMapper<ClientData> {

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");
			final String phone = rs.getString("phone");
			final String name = rs.getString("customerName");
			final Integer statusEnum = JdbcSupport.getInteger(rs, "status");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);
			final String serailNo = rs.getString("serialNo");

			ClientData clientData =ClientData.advancedSearchClient(id, name, accountNo, phone, status, null,
					serailNo, null);
			return clientData;
		}
			
			public String typeschema() {
				String query = " c.id AS id,c.account_no AS accountNo,c.status_enum AS status,c.phone AS phone,c.display_name AS customerName From m_client c";
				return query;
				}
		}


	@Override
	public ClientData retrieveOne(String columnName, String columnValue) {
		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);

		try {
			final AppUser currentUser = context.authenticatedUser();
			final String hierarchy = currentUser.getOffice().getHierarchy();
			String hierarchySearchString;

			// Change here..based on global configuration of restrictToHierarchy
			if (restrictToHierarchy.isEnabled()) {
				hierarchySearchString = hierarchy + "%";
			} else {
				hierarchySearchString = "%";
			}
			final ClientMapper clientMapper = new ClientMapper();
			final String sql = "select " + clientMapper.schema() + " where o.hierarchy like ? and c." + columnName
					+ " = '" + columnValue + "' ";
			return this.jdbcTemplate.queryForObject(sql, clientMapper, new Object[] { hierarchySearchString });
			/*
			 * final String clientGroupsSql = "select " +
			 * this.clientGroupsMapper.parentGroupsSchema();
			 * 
			 * final Collection<GroupGeneralData> parentGroups =
			 * this.jdbcTemplate.query(clientGroupsSql, this.clientGroupsMapper,new Object[]
			 * {}); return ClientData.setParentGroups(clientData, parentGroups);
			 */
		} catch (EmptyResultDataAccessException e) {
			throw new ClientNotFoundException(columnValue);
		}
	}

	@Override
	public ClientData retrieveClientForcrm(final String columnName, final String columnValue) {
		final ClientDataMapper mapper = new ClientDataMapper();
		StringBuilder sql = new StringBuilder("select " + mapper.schema());
		sql.append(" where c." + columnName + "='" + columnValue + "' ");

		return jdbcTemplate.queryForObject(sql.toString(), mapper, new Object[] {});
	}

	private static final class ClientDataMapper implements RowMapper<ClientData> {

		public String schema() {
			return " c.id as id,g.group_name as groupName,c.external_id as externalId,c.status_enum as statusEnum,"
					+ " c.office_id as officeId, o.name as officeName, c.is_indororp as entryType, mc.code_value as categoryType,"
					+ " c.activation_date as activationDate,c.image_key as imagekey, c.parent_id as parentId,"
					+ " c.po_id as poId,c.account_no as accountNo from m_client c "
					+ " join m_office o ON o.id = c.office_id left outer join b_group g ON g.id = c.group_id "
					+ " left outer join m_code_value mc ON mc.id = c.category_type ";

		}

		@Override
		public ClientData mapRow(ResultSet resultSet, int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");

			final String groupName = resultSet.getString("groupName");
			final Integer statusEnum = JdbcSupport.getInteger(resultSet, "statusEnum");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);
			final Long officeId = JdbcSupport.getLong(resultSet, "officeId");
			final String imageKey = resultSet.getString("imageKey");
			final String officeName = resultSet.getString("officeName");
			final LocalDate activationDate = JdbcSupport.getLocalDate(resultSet, "activationDate");
			final String poId = resultSet.getString("poId");
			final String accountNo = resultSet.getString("accountNo");

			return ClientData.temperory(groupName, status, officeId, officeName, id, activationDate, imageKey, poId,
					accountNo);

		}

	}

	/*
	 * @Override public Page<ClientData> retrieveAllClientsForLCO(SearchParameters
	 * searchParameters) {
	 * 
	 * 
	 * 
	 * final AppUser currentUser = context.authenticatedUser(); final
	 * LCOResourceMapper lcoMapper = new LCOResourceMapper(); final String hierarchy
	 * = currentUser.getOffice().getHierarchy();
	 * 
	 * 
	 * final String hierarchySearchString = hierarchy + "%";
	 * 
	 * final StringBuilder sqlBuilder = new StringBuilder(200);
	 * 
	 * sqlBuilder.append(lcoMapper.schema()); sqlBuilder.
	 * append(" Where co.order_status=1 and alloc.status='allocated' and im.item_class=1 and  o.hierarchy like '%.%' order by 1"
	 * );
	 * 
	 * final String extraCriteria =
	 * buildSqlStringFromClientCriteria(searchParameters);
	 * 
	 * if (StringUtils.isNotBlank(extraCriteria)) {
	 * sqlBuilder.append(" and (").append(extraCriteria).append(")"); }
	 * 
	 * if (searchParameters.isLimited()) {
	 * sqlBuilder.append(" limit ").append(searchParameters.getLimit()); }
	 * 
	 * if (searchParameters.isOffset()) {
	 * sqlBuilder.append(" offset ").append(searchParameters.getOffset()); }
	 * 
	 * final String sqlCountRows = "SELECT FOUND_ROWS()"; return
	 * this.paginationHelper.fetchPage(this.jdbcTemplate, sqlCountRows,
	 * sqlBuilder.toString(), new Object[] { }, lcoMapper);
	 * 
	 * }
	 * 
	 * private class LCOResourceMapper implements RowMapper<ClientData> {
	 * 
	 * 
	 * public String schema() {
	 * 
	 * return
	 * "Select c.id as id,co.id as orderId,alloc.serial_no as stbId,c.account_no as accountNo,"
	 * +
	 * " c.display_name as displayName,c.phone as phone,b.balance_amount AS balanceAmount, "
	 * + " cop.next_billable_day as startDate," +
	 * "date_add(cop.next_billable_day, INTERVAL 1 MONTH) as endDate "+
	 * " from m_client c JOIN m_office o ON o.id = c.office_id"+
	 * " LEFT OUTER JOIN b_client_balance b ON b.client_id = c.id"+
	 * " LEFT OUTER JOIN b_orders co ON co.client_id=c.id"+
	 * " LEFT OUTER JOIN b_order_price cop on cop.order_id=co.id"+
	 * " LEFT OUTER JOIN b_allocation alloc on alloc.client_id=c.id"+
	 * " LEFT OUTER JOIN b_item_master im on im.id=alloc.item_master_id";
	 * 
	 * }
	 */

	/*
	 * @Override public ClientData mapRow(ResultSet rs, int rowNum) throws
	 * SQLException {
	 * 
	 * final String accountNo = rs.getString("accountNo"); final BigDecimal
	 * clientBalance = rs.getBigDecimal("balanceAmount"); final Long id =
	 * JdbcSupport.getLong(rs, "id"); final String
	 * displayName=rs.getString("displayName"); final String phone =
	 * rs.getString("phone"); final Long orderId=rs.getLong("orderId"); final String
	 * stbId=rs.getString("stbId"); final LocalDate
	 * startDate=JdbcSupport.getLocalDate(rs,"startDate"); final LocalDate
	 * endDate=JdbcSupport.getLocalDate(rs,"endDate"); return
	 * ClientData.lcoClient(id,accountNo,displayName,phone,clientBalance,orderId,
	 * stbId,startDate,endDate); } }
	 */

	@Override
	public List<ClientCategoryData> retrievedParentsChild(final Long clientId) {

		try {
			this.context.authenticatedUser();
			final ParentChildMapper mapper = new ParentChildMapper();
			String sql = "Select " + mapper.parentChildSchema() + " where c.parent_id =?";
			return jdbcTemplate.query(sql.toString(), mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ParentChildMapper implements RowMapper<ClientCategoryData> {

		public String parentChildSchema() {
			return "c.id as id, c.account_no as accountNo, c.display_name as displayName, c.email as email,"
					+ " c.phone as phone, c.po_id as poId, c.status_enum as statusEnum from m_client c";
		}

		@Override
		public ClientCategoryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String accountNo = rs.getString("accountNo");
			final String displayName = rs.getString("displayName");
			final String email = rs.getString("email");
			final String phone = rs.getString("phone");
			final Long poId = rs.getLong("poId");
			final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
			final EnumOptionData status = ClientEnumerations.status(statusEnum);

			return new ClientCategoryData(id, null, null, accountNo, displayName, poId.toString(), email, phone,
					status);

		}
	}

	@Override
	public Long retriveMaxClientId() {

		return this.jdbcTemplate.queryForLong("select max(id) from m_client");
	}

	@Override
	public ClientData userdeviceinformation(Long officeId) {
		
		Configuration restrictToHierarchy = configurationRepository.findOneByName(ConfigurationConstants.Restrict_To_Hierarchy);

		final AppUser currentUser = context.authenticatedUser();

		String hierarchy = currentUser.getOffice().getHierarchy();
		String hierarchySearchString;

		// Change here..based on global configuration of restrictToHierarchy
		if (restrictToHierarchy.isEnabled()) {
			hierarchySearchString = hierarchy + "%";
		} else {
			hierarchySearchString = "%";
			//hierarchySearchString = hierarchy + "%";

		}
		// end of change

		try {
			UserdeviceinformationMapper userdeviceinformationMapper = new UserdeviceinformationMapper();
			UserdeviceinformationMapper1 userdeviceinformationMapper1 = new UserdeviceinformationMapper1();

			String sql=null;
			//it is for tstv allowing super user to show the dashboard without hierarchy restriction
			if(officeId.toString().equalsIgnoreCase("1")) {
			 sql = "SELECT " + userdeviceinformationMapper.Schema(hierarchySearchString);
				return jdbcTemplate.queryForObject(sql, userdeviceinformationMapper, new Object[] {});

			}else {
				//allowing to see the dashboard specific to office id
				 sql = "SELECT " + userdeviceinformationMapper1.Schema1(officeId);
					return jdbcTemplate.queryForObject(sql, userdeviceinformationMapper1, new Object[] {});

			}
		} catch (EmptyResultDataAccessException ex) {
			return null;
		}

	}

	private static final class UserdeviceinformationMapper implements RowMapper<ClientData> {

		public String Schema(String hierarchy) {
			return "sum(active) as c_active,sum(inactive) as c_inactive , sum(instock) as c_instock,sum(allocated) as c_allocated,sum(voucher_stock) as voucherStock,sum(provision_pending) as provisionPending  from"
					+ " (select a.id,IFNULL(b.client_active, 0) as active,IFNULL(b.client_inactive, 0) as inactive,"
					+ "IFNULL(b.in_stock, 0) as instock,IFNULL(b.stock_allocated, 0) as allocated, IFNULL(b.voucher_stock,0) as voucher_stock,IFNULL(b.provision_pending,0) as provision_pending"
					+ " from m_office a left join m_office_statistics b on a.id=b.office_id where a.hierarchy like '"
					+ hierarchy + "%')X";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final String c_active = rs.getString("c_active");
			final String c_inactive = rs.getString("c_inactive");
			final String c_instock = rs.getString("c_instock");
			final String c_allocated = rs.getString("c_allocated");
			final String voucherStock = rs.getString("voucherStock");
			final String provisionPending = rs.getString("provisionPending");

			ClientData clientData = ClientData.searchClient(null);
			clientData.setC_active(c_active);
			clientData.setC_allocated(c_allocated);
			clientData.setC_inactive(c_inactive);
			clientData.setC_instock(c_instock);
			clientData.setVoucherStock(voucherStock);
			clientData.setProvisionPending(provisionPending);
			return clientData;
		}

	}
	private static final class UserdeviceinformationMapper1 implements RowMapper<ClientData> {

		
		public String Schema1(Long officeId) {
			return " client_active as `clientActive`,in_stock as `boxStock`,voucher_stock as `voucherStock`,provision_pending as `provisionPending`"
					+ " from m_office_statistics where office_id="+officeId+"" ;
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final String c_active = rs.getString("clientActive");
			final String voucherStock = rs.getString("voucherStock");
			final String c_instock = rs.getString("boxStock");
			final String provisionPending = rs.getString("provisionPending");
			ClientData clientData = ClientData.searchClient(null);
			clientData.setC_active(c_active);
			clientData.setVoucherStock(voucherStock);
			clientData.setProvisionPending(provisionPending);
			clientData.setC_instock(c_instock);
			return clientData;
		}

	}

	@Override
	public List<ClientData> retriveAllPhonesAndEmails() {

		try {
			this.context.authenticatedUser();
			final PhoneEmailMapper mapper = new PhoneEmailMapper();
			String sql = "Select " + mapper.schema();
			return jdbcTemplate.query(sql.toString(), mapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class PhoneEmailMapper implements RowMapper<ClientData> {

		public String schema() {
			return "c.email as email, c.phone as phone from m_client c";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final String email = rs.getString("email");
			final String phone = rs.getString("phone");

			return ClientData.phonesAndEmails(phone, email);

		}
	}

	@Override
	public ClientData retriveClientDetailsForEvents(Long clientId) {

		try {
			this.context.authenticatedUser();
			final ClientEventMapper mapper = new ClientEventMapper();
			String sql = "Select " + mapper.schema() + " where c.id = ?";
			return jdbcTemplate.queryForObject(sql.toString(), mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientEventMapper implements RowMapper<ClientData> {
		public String schema() {
			return "c.id as id ,cu.username as username, cu.password as password,c.account_no as accountNo,concat(COALESCE(c.title, ' '),' ',c.firstname,' ', COALESCE(c.middlename, ''),' ',COALESCE(c.lastname,' '))"
					+ " as fullname,c.phone as phone,c.email as email, oa.email_id as officeEmail from m_client c join b_clientuser cu on c.id = cu.client_id inner join"
					+ " m_office o on c.office_id=o.id join b_office_address oa on oa.office_id = o.id";
		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final long id = rs.getLong("id");
			final String userName = rs.getString("username");
			final String password = rs.getString("password");
			final String accountNo = rs.getString("accountNo");
			final String fullName = rs.getString("fullName");
			final String email = rs.getString("email");
			final String phone = rs.getString("phone");
			ClientData clientData = ClientData.eventClientData(id, accountNo, fullName, userName, password, email,
					phone);
			clientData.setOfficeMail(rs.getString("officeEmail"));
			return clientData;
		}
	}

	@Override
	public ClientServiceData retriveServiceId(String Serialnumber) {

		try {
			this.context.authenticatedUser();
			final ClientEventMappe mapper = new ClientEventMappe();
			String sql = "Select " + mapper.schema() + " where id.serial_no = ?";
			return jdbcTemplate.queryForObject(sql.toString(), mapper, new Object[] { Serialnumber });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientEventMappe implements RowMapper<ClientServiceData> {

		public String schema() {
			return " cs.id, c.id as clientId, c.po_id as poId, cs.client_service_poid as clientServicePoId from b_allocation id join m_client c on c.id = id.client_id join b_client_service cs on cs.client_id = c.id and cs.id=id.clientservice_id ";
		}

		@Override
		public ClientServiceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final long id = rs.getLong("id");
			final long clientId = rs.getLong("clientId");
			final long poId = rs.getLong("poId");
			final long clientServicePoId = rs.getLong("clientServicePoId");
			ClientServiceData clientServiceData = new ClientServiceData(id, clientId, poId, clientServicePoId);
			return clientServiceData;
		}
	}

	@Override
	public ClientServiceData retriveClientServicesPoids(Long clientServiceId) {
		try {
			this.context.authenticatedUser();
			final ClientServiceMapperNew mapper = new ClientServiceMapperNew();
			final String sql = "select distinct" + mapper.schema() + " where cs.id = ?";
			return jdbcTemplate.queryForObject(sql.toString(), mapper, new Object[] { clientServiceId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientServiceMapperNew implements RowMapper<ClientServiceData> {

		public String schema() {

			return " cs.id as clientServiceId,cs.client_service_poid as clientServicePoId,c.po_id as clientPoid from b_client_service cs join m_client c on cs.client_id = c.id ";

		}

		@Override
		public ClientServiceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final long clientServiceId = rs.getLong("clientServiceId");
			final long clientServicePoId = rs.getLong("clientServicePoId");
			final long clientPoid = rs.getLong("clientPoid");
			ClientServiceData clientServiceData = new ClientServiceData(clientServiceId, clientServicePoId, clientPoid);
			return clientServiceData;
		}
	}

	// accountno

	@Override
	public List<ClientData> retriveAccountNo(String accountNo) {
		try {
			this.context.authenticatedUser();
			final ClientServiceMapperNewn mapper = new ClientServiceMapperNewn();
			final String sql = "select distinct " + mapper.schema()
					+ " where cm.account_no = ? and ca.client_id=cm.id ";
			return jdbcTemplate.query(sql.toString(), mapper, new Object[] { accountNo });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientServiceMapperNewn implements RowMapper<ClientData> {

		public String schema() {

			return "cm.firstname,cm.id,cm.lastname,cm.account_no,cm.email,cm.phone,cm.external_id as externalId, "
					+ "cm.home_phone_number as homePhoneNumber,cm.account_no as userName,cm.password,cm.activation_date as activationDate, "
					+ "cm.category_type as categoryType,cm.title,cm.id_key as idKey,cm.id_value as idValue,cm.office_id as officeId,ca.address_no as addressNo,ca.street, "
					+ "ca.city,ca.district,ca.state,ca.country,ca.address_key as addressKey from m_client as cm join b_client_address ca  ";

		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final String firstname = rs.getString("firstname");
			final String lastname = rs.getString("lastname");
			final String email = rs.getString("email");
			final String phone = rs.getString("phone");
			final String externalId = rs.getString("externalId");
			final String homePhoneNumber = rs.getString("homePhoneNumber");
			final String userName = rs.getString("userName");
			final String password = rs.getString("password");
			final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");
			final String categoryType = rs.getString("categoryType");
			final String title = rs.getString("title");
			final String idKey = rs.getString("idKey");
			final String idValue = rs.getString("idValue");
			final Long officeId = JdbcSupport.getLong(rs, "officeId");
			final String addressNo = rs.getString("addressNo");
			final String street = rs.getString("street");
			final String city = rs.getString("city");
			final String district = rs.getString("district");
			final String state = rs.getString("state");
			final String country = rs.getString("country");
			final String addressKey = rs.getString("addressKey");

			ClientData clientData = new ClientData(id, firstname, lastname, email, phone, externalId, homePhoneNumber,
					userName, password, activationDate, categoryType, title, idKey, idValue, officeId, addressNo,
					street, city, district, state, country, addressKey);
			return clientData;
		}
	}
	@Override
	public ClientBalanceData findClientBalanceByClientId(Long clientId) {
		try {
			final ClientBalanceMapper mapper = new ClientBalanceMapper();
			final String sql = "select distinct " + mapper.schema() + " where cb.client_id = ? ";
			return jdbcTemplate.queryForObject(sql.toString(), mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	@Override
	public ClientBalanceData findClientBalance(Long clientId) {
		try {
			this.context.authenticatedUser();
			final ClientBalanceMapper mapper = new ClientBalanceMapper();
			final String sql = "select distinct " + mapper.schema() + " where cb.client_id = ? ";
			return jdbcTemplate.queryForObject(sql.toString(), mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientBalanceMapper implements RowMapper<ClientBalanceData> {

		public String schema() {

			return " cb.id as id,cb.client_id as clientId,SUM(cb.balance_amount) as balanceAmount, "
					+ " cb.wallet_amount as walletAmount,cb.service_id as clientServiceId,cb.resource_id as resourceId "
					+ " from b_client_balance cb ";

		}

		@Override
		public ClientBalanceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long id = rs.getLong("id");
			final Long clientId = rs.getLong("clientId");
			final BigDecimal balanceAmount = rs.getBigDecimal("balanceAmount");
			final BigDecimal walletAmount = rs.getBigDecimal("walletAmount");
			final Long clientServiceId = rs.getLong("clientServiceId");
			final Long resourceId = rs.getLong("resourceId");

			ClientBalanceData clientBalanceData = new ClientBalanceData(id, clientId, balanceAmount, walletAmount,
					clientServiceId, resourceId);
			return clientBalanceData;
		}
	}

	@Override
	public ClientData currencyInfo(Long clientId) {
		try {
			this.context.authenticatedUser();
			final CurrencyMapper mapper = new CurrencyMapper();
			final String sql = "select distinct " + mapper.schema() + " where c.id = ? ";
			return jdbcTemplate.queryForObject(sql.toString(), mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	private static final class CurrencyMapper implements RowMapper<ClientData> {

		public String schema() {

			return "c.id as clientId,ca.country as country,cu.id as currencyId from m_client c join b_client_address ca on ca.client_id = c.id join m_currency cu on cu.country_name = ca.country ";

		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final Long clientId = rs.getLong("clientId");
			final String country = rs.getString("country");
			final Long currencyId = rs.getLong("currencyId");
			ClientData clientData = new ClientData(clientId, country, currencyId);

			return clientData;
		}
	}

	@Override
	public ClientData retriveClientDetails(String query, Long clientId) {
		try {
			this.context.authenticatedUser();
			final ClientDetailsMapper mapper = new ClientDetailsMapper();
			// final String sql = "select distinct " + mapper.schema()+" where c.id = ? ";
			return jdbcTemplate.queryForObject(query, mapper, new Object[] { clientId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private static final class ClientDetailsMapper implements RowMapper<ClientData> {

		public String schema() {

			return "c.id as clientId,c.email as email from m_client c ";

		}

		@Override
		public ClientData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long clientId = rs.getLong("clientId");
			final String email = rs.getString("email");

			ClientData clientData = new ClientData(null, null, null);

			clientData.setEmail(email);
			clientData.setClientId(clientId);

			return clientData;
		}
	}

	@Override
	public List<ClientData> retrieveClientsForLCO(Long officeId) {

		try {
			final AppUser currentUser = context.authenticatedUser();
			final String hierarchy = currentUser.getOffice().getHierarchy();
			final String hierarchySearchString = hierarchy + "%";
			final ClientLCOMapper clientLCOMapper = new ClientLCOMapper();
			StringBuilder sql = new StringBuilder("select " + clientLCOMapper.schema());
			sql.append(
					" where co.order_status=1 and alloc.status='allocated' and im.item_class=1 and  o.hierarchy like '%.%' and o.id=? order by 1");
			return jdbcTemplate.query(sql.toString(), clientLCOMapper, new Object[] { officeId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private class ClientLCOMapper implements RowMapper<ClientData> {

		public String schema() {

			return " c.id as id,o.id as officeId,co.id as orderId,alloc.serial_no as stbId,c.account_no as accountNo,"
					+ " c.display_name as displayName,c.phone as phone,b.balance_amount AS balanceAmount, "
					+ " cop.next_billable_day as startDate,"
					+ "date_add(cop.next_billable_day, INTERVAL 1 MONTH) as endDate "
					+ " from m_client c JOIN m_office o ON o.id = c.office_id"
					+ " LEFT OUTER JOIN b_client_balance b ON b.client_id = c.id"
					+ " LEFT OUTER JOIN b_orders co ON co.client_id=c.id"
					+ " LEFT OUTER JOIN b_order_price cop on cop.order_id=co.id"
					+ " LEFT OUTER JOIN b_allocation alloc on alloc.client_id=c.id"
					+ " LEFT OUTER JOIN b_item_master im on im.id=alloc.item_master_id";

		}

		@Override
		public ClientData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final String accountNo = rs.getString("accountNo");
			final BigDecimal clientBalance = rs.getBigDecimal("balanceAmount");
			final Long id = JdbcSupport.getLong(rs, "id");
			final Long officeId = JdbcSupport.getLong(rs, "id");
			final String displayName = rs.getString("displayName");
			final String phone = rs.getString("phone");
			final Long orderId = rs.getLong("orderId");
			final String stbId = rs.getString("stbId");
			final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
			final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
			ClientData clientData = ClientData.lcoClient(id, accountNo, displayName, phone, clientBalance, orderId,
					stbId, startDate, endDate);

			clientData.setOfficeId(officeId);

			return clientData;
		}
	}
	@Override
	public List<ClientData> retrieveRenewalClientsForLCO(Long officeId, 
			String fromDate, String toDate) {
		// TODO Auto-generated method stub
		try {
			final AppUser currentUser = context.authenticatedUser();
			//final String hierarchy = currentUser.getOffice().getHierarchy();
			//final String hierarchySearchString = hierarchy + "%";
			final RenewalClientLCOMapper renewalClientLCOMapper = new RenewalClientLCOMapper();
			StringBuilder sql = new StringBuilder("select  distinct" + renewalClientLCOMapper.schema());
			sql.append(
					" where co.order_status=1 and alloc.status='allocated' and im.item_class=1  and o.id=? and co.end_date >= DATE(NOW()) + INTERVAL datediff(?,?) DAY order by 1");
			return jdbcTemplate.query(sql.toString(), renewalClientLCOMapper, new Object[] { officeId,toDate,fromDate });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	private class RenewalClientLCOMapper implements RowMapper<ClientData> {

		public String schema() {

			return " c.id as id,o.id as officeId,co.id as orderId,alloc.serial_no as stbId,c.account_no as accountNo,"
					+ " c.display_name as displayName,c.phone as phone,b.balance_amount AS balanceAmount, "
					+ " co.start_date as startDate,"
					+ "co.end_date as endDate "
					+ " from m_client c JOIN m_office o ON o.id = c.office_id"
					+ " LEFT OUTER JOIN b_client_balance b ON b.client_id = c.id"
					+ " LEFT OUTER JOIN b_orders co ON co.client_id=c.id"
					+ " LEFT OUTER JOIN b_order_price cop on cop.order_id=co.id"
					+ " LEFT OUTER JOIN b_allocation alloc on alloc.client_id=c.id"
					+ " LEFT OUTER JOIN b_item_master im on im.id=alloc.item_master_id";

		}

		@Override
		public ClientData mapRow(ResultSet rs, int rowNum) throws SQLException {

			final String accountNo = rs.getString("accountNo");
			final BigDecimal clientBalance = rs.getBigDecimal("balanceAmount");
			final Long id = JdbcSupport.getLong(rs, "id");
			final Long officeId = JdbcSupport.getLong(rs, "officeId");
			final String displayName = rs.getString("displayName");
			final String phone = rs.getString("phone");
			final Long orderId = rs.getLong("orderId");
			final String stbId = rs.getString("stbId");
			final LocalDate startDate = JdbcSupport.getLocalDate(rs, "startDate");
			final LocalDate endDate = JdbcSupport.getLocalDate(rs, "endDate");
			ClientData clientData = ClientData.lcoClient(id, accountNo, displayName, phone, clientBalance, orderId,
					stbId, startDate, endDate);

			clientData.setOfficeId(officeId);

			return clientData;
		}
	}
}
