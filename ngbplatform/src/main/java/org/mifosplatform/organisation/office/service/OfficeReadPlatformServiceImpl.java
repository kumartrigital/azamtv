/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.mifosplatform.celcom.domain.PaymentTypeEnum;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.finance.officebalance.data.OfficeBalanceData;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.core.api.ApiParameterHelper;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.service.CurrencyReadPlatformService;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.organisation.office.data.OfficeTransactionData;
import org.mifosplatform.organisation.office.exception.OfficeNotFoundException;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.portfolio.client.domain.ClientEnumerations;
import org.mifosplatform.portfolio.client.domain.ClientStatus;
import org.mifosplatform.portfolio.client.service.ClientCategoryData;
import org.mifosplatform.portfolio.group.service.SearchParameters;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service("employeeSearchService")
public class OfficeReadPlatformServiceImpl implements OfficeReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final CurrencyReadPlatformService currencyReadPlatformService;
	private final static String NAMEDECORATEDBASEON_HIERARCHY = "concat(substring('........................................', 1, ((LENGTH(o.hierarchy) - LENGTH(REPLACE(o.hierarchy, '.', '')) - 1) * 1)), o.name)";
	private final PaginationHelper<OfficeData> paginationHelper = new PaginationHelper<OfficeData>();

	@Autowired
	public OfficeReadPlatformServiceImpl(final PlatformSecurityContext context,
			final CurrencyReadPlatformService currencyReadPlatformService,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.currencyReadPlatformService = currencyReadPlatformService;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public Page<OfficeData> retrieveOfficeDetails(SearchSqlQuery searchOffice) {
		
		OfficeMapper OfficeMapper = new OfficeMapper();
		
		final StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(OfficeMapper.officeSchema());
        sqlBuilder.append(" where o.id IS NOT NULL ");
        
        String sqlSearch = searchOffice.getSqlSearch();
        String extraCriteria = null;
        if (sqlSearch != null) {
	    	sqlSearch=sqlSearch.trim();
	    	extraCriteria = " and ( o.id like '%"+sqlSearch+"%' OR" 
	    			+ " o.name like '%"+sqlSearch+"%' OR"
	    			+ " o.external_id like '%"+sqlSearch+"%' OR"
	    			+ " o.opening_date like '%"+sqlSearch+"%' OR"
	    			+ " o.hierarchy like '%"+sqlSearch+"%' OR"
	    			+ " parent.id like '%"+sqlSearch+"%' OR"
	    			+ " parent.name like '%"+sqlSearch+"%' OR"
	    			+ " o.office_type like '%"+sqlSearch+"%' OR"
	    			+ " b.balance_amount like '%"+sqlSearch+"%' OR"
	    			+ " od.city like '%"+sqlSearch+"%' OR"
	    			+ " od.state like '%"+sqlSearch+"%' OR"
	    			+ " od.country like '%"+sqlSearch+"%' OR"
	    			+ " od.email_id like '%"+sqlSearch+"%' OR"
	    			+ " od.phone_number like '%"+sqlSearch+"%' OR"
	    			+ " od.office_number like '%"+sqlSearch+"%' OR"
	    			+ " od.address_name like '%"+sqlSearch+"%' OR"
	    			+ " od.contact_person like '%"+sqlSearch+"%' OR"
	    			+ " od.zip like '%"+sqlSearch+"%' OR"
	    			+ " od.business_type like '%"+sqlSearch+"%' OR"
	    			+ " od.district like '%"+sqlSearch+"%' OR"
	    			+ " o.po_id like '%"+sqlSearch+"%' OR"
	    			+ " o.pancard_no like '%"+sqlSearch+"%' OR"
	    			+ " o.company_reg_no like '%"+sqlSearch+"%' OR"
	    			+ " o.commision_model like '%"+sqlSearch+"%' OR"
	    			+ " o.gst_reg_no like '%"+sqlSearch+"%' OR"
	    			+ " o.payment_type like '%"+sqlSearch+"%' OR"
	    			+ " o.Subscriber_dues like '%"+sqlSearch+"%' OR"
	    			+ " b.credit_limit like '%"+sqlSearch+"%' OR"
	    			+ " o.client_id like '%"+sqlSearch+"%' OR"
	    			+ " cs.id like '%"+sqlSearch+"%' OR"
	    			+ " c.po_id like '%"+sqlSearch+"%' OR"
	    			+ " cs.client_service_poid like '%"+sqlSearch+"%')";
	    }
        
        if (null != extraCriteria) {
            sqlBuilder.append(extraCriteria);
        }
       // sqlBuilder.append(" and is_deleted = 'N' ");

        if (searchOffice.isLimited()) {
            sqlBuilder.append(" limit ").append(searchOffice.getLimit());
        }

        if (searchOffice.isOffset()) {
            sqlBuilder.append(" offset ").append(searchOffice.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
		        new Object[] {}, OfficeMapper);
		
	}
	
	private static final class OfficeMapper implements RowMapper<OfficeData> {

		public String officeSchema() {
			return "o.id AS id,o.name AS name," + NAMEDECORATEDBASEON_HIERARCHY
					+ "AS nameDecorated,o.external_id AS externalId,o.opening_date AS openingDate,o.hierarchy AS hierarchy,"
					+ "parent.id AS parentId,parent.name AS parentName,o.office_type as officeType, ifnull(b.balance_amount,0 )as balance, b.credit_limit as credit,"
					+ "od.state as state,od.city as city,od.country as country,od.phone_number as phoneNumber,od.office_number as officeNumber, "
					+ "od.email_id as email,od.address_name as addressName,od.contact_person as contactPerson,od.zip as zip,od.business_type as businessType,"
					+ "od.district as district,o.pancard_no as pancardNo,o.company_reg_no as companyRegNo,o.commision_model as commisionModel,"
					+ "o.gst_reg_no as gstRegNo, o.po_id as poId, o.payment_type as paymentType,	o.Subscriber_dues as subsciberDues,cv.code_value as dasType,o.settlement_poId as settlementPoId, " 
					+ "o.client_id as clientId,cs.id as clientServiceId,c.po_id as clientPoId,cs.client_service_poid as clientServicePoId "
					+ "FROM m_office o LEFT JOIN m_office AS parent ON parent.id = o.parent_id "
					+ "Left join m_office_balance b on o.id = b.office_id  Left join b_office_address od ON o.id = od.office_id  Left join m_code_value cv on " 
					+ "cv.id = o.das_type Left join b_client_service cs on cs.client_id = o.client_id Left join m_client c on c.id = o.client_id ";
		}

		@Override
		public OfficeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String name = resultSet.getString("name");
			final String nameDecorated = resultSet.getString("nameDecorated");
			final String externalId = resultSet.getString("externalId");
			final LocalDate openingDate = JdbcSupport.getLocalDate(resultSet, "openingDate");
			final String hierarchy = resultSet.getString("hierarchy");
			final Long parentId = JdbcSupport.getLong(resultSet, "parentId");
			final String parentName = resultSet.getString("parentName");
			final String officeType = resultSet.getString("officeType");
			final BigDecimal balance = resultSet.getBigDecimal("balance");
			final String city = resultSet.getString("city");
			final String state = resultSet.getString("state");
			final String country = resultSet.getString("country");
			final String email = resultSet.getString("email");
			final String phoneNumber = resultSet.getString("phoneNumber");
			final String officeNumber = resultSet.getString("officeNumber");
			final String addressName = resultSet.getString("addressName");
			final String contactPerson = resultSet.getString("contactPerson");
			final String zip = resultSet.getString("zip");
			final String businessType = resultSet.getString("businessType");
			final String district = resultSet.getString("district");
			final String poId = resultSet.getString("poId");
			final String pancardNo = resultSet.getString("pancardNo");
			final String companyRegNo = resultSet.getString("companyRegNo");
			final int commisionModel = resultSet.getInt("commisionModel");
			final String gstRegNo = resultSet.getString("gstRegNo");
			final Integer paymentTypeEnum = resultSet.getInt("paymentType");
			final int subscriberDuesInt = resultSet.getInt("subsciberDues");
			final String dasType = resultSet.getString("dasType");
			final String settlementPoId = resultSet.getString("settlementPoId");
			final BigDecimal credit = resultSet.getBigDecimal("credit");
			final Long clientId = resultSet.getLong("clientId");
			final Long clientServiceId = resultSet.getLong("clientServiceId");
			final String clientPoId = resultSet.getString("clientPoId");
			final String clientServicePoId = resultSet.getString("clientServicePoId");
			boolean subscriberDues= false;
			if(subscriberDuesInt==1){
				subscriberDues=true;
			}
			/*return new OfficeData(id, name, nameDecorated, externalId, openingDate, hierarchy, parentId, parentName,
					null, null, officeType, balance, city, state, country, email, phoneNumber, officeNumber,addressName, contactPerson, zip, 
					businessType, district, poId, pancardNo, companyRegNo, commisionModel, gstRegNo, paymentTypeEnum, subscriberDues, dasType, settlementPoId);*/
			OfficeData officedata = new OfficeData(id, name, nameDecorated, externalId, openingDate, hierarchy, parentId, parentName,
					null, null, officeType, balance, city, state, country, email, phoneNumber, officeNumber,addressName, contactPerson, zip, 
					businessType, district, poId, pancardNo, companyRegNo, commisionModel, gstRegNo, paymentTypeEnum, subscriberDues, dasType, settlementPoId);
			officedata.setCredit(credit);
			officedata.setClientId(clientId);
			officedata.setClientServiceId(clientServiceId);
			officedata.setClientPoId(clientPoId);
			officedata.setClientServicePoId(clientServicePoId);
			return officedata;
		}
	}

	private static final class OfficeDropdownMapper implements RowMapper<OfficeData> {

		public String schema() {
			return " o.id as id, " + NAMEDECORATEDBASEON_HIERARCHY
					+ " as nameDecorated, o.name as name,o.po_id as poId,o.external_id AS externalId,o.office_type as officetype from m_office o ";
		}

		@Override
		public OfficeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String name = resultSet.getString("name");
			final String nameDecorated = resultSet.getString("nameDecorated");
			final String externalId = resultSet.getString("externalId");
			final String poId = resultSet.getString("poId");
			final String officeType = resultSet.getString("officeType");

			OfficeData officeData= OfficeData.dropdown(id, name, nameDecorated, externalId, officeType);
			officeData.setPoId(poId);
			return officeData;
		}
	}

	private static final class OfficeTransactionMapper implements RowMapper<OfficeTransactionData> {

		public String schema() {
			return " ot.id as id, ot.transaction_date as transactionDate, ot.from_office_id as fromOfficeId, fromoff.name as fromOfficeName, "
					+ " ot.to_office_id as toOfficeId, tooff.name as toOfficeName, ot.transaction_amount as transactionAmount, ot.description as description, "
					+ " ot.currency_code as currencyCode, rc.decimal_places as currencyDigits, "
					+ " rc.name as currencyName, rc.internationalized_name_code as currencyNameCode, rc.display_symbol as currencyDisplaySymbol "
					+ " from m_office_transaction ot "
					+ " left join m_office fromoff on fromoff.id = ot.from_office_id "
					+ " left join m_office tooff on tooff.id = ot.to_office_id "
					+ " join m_currency rc on rc.`code` = ot.currency_code";
		}

		@Override
		public OfficeTransactionData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final LocalDate transactionDate = JdbcSupport.getLocalDate(resultSet, "transactionDate");
			final Long fromOfficeId = JdbcSupport.getLong(resultSet, "fromOfficeId");
			final String fromOfficeName = resultSet.getString("fromOfficeName");
			final Long toOfficeId = JdbcSupport.getLong(resultSet, "toOfficeId");
			final String toOfficeName = resultSet.getString("toOfficeName");
			final String currencyCode = resultSet.getString("currencyCode");
			final String currencyName = resultSet.getString("currencyName");
			final String currencyNameCode = resultSet.getString("currencyNameCode");
			final String currencyDisplaySymbol = resultSet.getString("currencyDisplaySymbol");
			final Integer currencyDigits = JdbcSupport.getInteger(resultSet, "currencyDigits");

			final CurrencyData currencyData = new CurrencyData(id, currencyCode, currencyName, currencyDigits,
					currencyDisplaySymbol, currencyNameCode, null, null, null);

			final BigDecimal transactionAmount = resultSet.getBigDecimal("transactionAmount");
			final String description = resultSet.getString("description");

			return OfficeTransactionData.instance(id, transactionDate, fromOfficeId, fromOfficeName, toOfficeId,
					toOfficeName, currencyData, transactionAmount, description);
		}
	}

	@Override
	public Collection<OfficeData> retrieveAllOffices() {

		final AppUser currentUser = context.authenticatedUser();

		String hierarchy = currentUser.getOffice().getHierarchy();
		String hierarchySearchString = hierarchy + "%";

		final OfficeMapper officeMapper = new OfficeMapper();
		final String sql = "select " + officeMapper.officeSchema() + " where o.hierarchy like ? group by o.name order by o.hierarchy,o.opening_date";

		return this.jdbcTemplate.query(sql, officeMapper, new Object[] { hierarchySearchString });
	}

	@Override
	public Collection<OfficeData> retrieveAllOfficesForDropdown() {
		final AppUser currentUser = context.authenticatedUser();

		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final OfficeDropdownMapper officeDropdownMap = new OfficeDropdownMapper();
		final String sql = "select " + officeDropdownMap.schema() + "where o.hierarchy like ? order by o.name";

		return this.jdbcTemplate.query(sql, officeDropdownMap, new Object[] { hierarchySearchString });
	}
	
	public Collection<OfficeData> retrieveAllOfficesForDropdown(String officename, boolean getAll) {
		
		final AppUser currentUser = context.authenticatedUser();

		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final OfficeDropdownMapper officeDropdownMap = new OfficeDropdownMapper();
		String where="";
		if(officename!=null||!getAll){
			where="where";
		}
		String sqlJoin = "";
		if(officename!=null){
			if(!getAll){
				sqlJoin="and";
			}
			sqlJoin= sqlJoin+" o.name like '%" + officename + "%'";
		}
		String heirarchyJoin ="";
		if(!getAll){
			heirarchyJoin=" o.hierarchy like  '"+hierarchySearchString+"'";
		}
		final String sql = "select " + officeDropdownMap.schema() + " "+where+" "+heirarchyJoin+" "+sqlJoin+" order by o.name limit 7 ";

		
		

		return this.jdbcTemplate.query(sql, officeDropdownMap, new Object[] {});
	}

	
	
	@Override
	public OfficeData retrieveOffice(final Long officeId) {

		try {
			context.authenticatedUser();

			final OfficeMapper officeMapper = new OfficeMapper();
			final String sql = "select " + officeMapper.officeSchema() + " where o.id = ? group by o.name ";

			return this.jdbcTemplate.queryForObject(sql, officeMapper, new Object[] { officeId });
		} catch (EmptyResultDataAccessException e) {
			throw new OfficeNotFoundException(officeId);
		}
	}

	@Override
	public OfficeData retrieveNewOfficeTemplate() {

		context.authenticatedUser();

		return OfficeData.template(null, DateUtils.getLocalDateOfTenant(), null);
	}

	@Override
	public Collection<OfficeData> retrieveAllowedParents(final Long officeId) {

		context.authenticatedUser();
		final Collection<OfficeData> filterParentLookups = new ArrayList<OfficeData>();

		if (isNotHeadOffice(officeId)) {
			final Collection<OfficeData> parentLookups = retrieveAllOfficesForDropdown();

			for (final OfficeData office : parentLookups) {
				if (!office.hasIdentifyOf(officeId)) {
					filterParentLookups.add(office);
				}
			}
		}

		return filterParentLookups;
	}

	private boolean isNotHeadOffice(final Long officeId) {
		return !Long.valueOf(1).equals(officeId);
	}

	@Override
	public Collection<OfficeTransactionData> retrieveAllOfficeTransactions() {

		final AppUser currentUser = context.authenticatedUser();

		String hierarchy = currentUser.getOffice().getHierarchy();
		String hierarchySearchString = hierarchy + "%";

		OfficeTransactionMapper officeTransactionMap = new OfficeTransactionMapper();
		String sql = "select " + officeTransactionMap.schema()
				+ " where (fromoff.hierarchy like ? or tooff.hierarchy like ?) order by ot.transaction_date, ot.id";

		return this.jdbcTemplate.query(sql, officeTransactionMap,
				new Object[] { hierarchySearchString, hierarchySearchString });
	}

	@Override
	public OfficeTransactionData retrieveNewOfficeTransactionDetails() {
		context.authenticatedUser();

		final Collection<OfficeData> parentLookups = retrieveAllOfficesForDropdown();
		final Collection<CurrencyData> currencyOptions = currencyReadPlatformService.retrieveAllowedCurrencies();

		return OfficeTransactionData.template(DateUtils.getLocalDateOfTenant(), parentLookups, currencyOptions);
	}

	@Override
	public List<OfficeData> retrieveAgentTypeData() {

		final AppUser currentUser = context.authenticatedUser();

		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final OfficeDropdownMapper officeDropdownMap = new OfficeDropdownMapper();
		final String sql = "select " + officeDropdownMap.schema()
				+ ", m_code_value c  WHERE o.office_type = c.id AND c.code_value = 'agent' AND o.hierarchy LIKE ? "
				+ " ORDER BY o.name";

		return this.jdbcTemplate.query(sql, officeDropdownMap, new Object[] { hierarchySearchString });

	}

	@Override
	public Collection<FinancialTransactionsData> retreiveOfficeFinancialTransactionsData(final Long officeId) {

		context.authenticatedUser();
		final OfficeFinancialTransactionMapper mapper = new OfficeFinancialTransactionMapper();
		final String sql = "select v.* from  office_fin_trans_vw v where v.office_id=" + officeId
				+ " order by  transDate desc ";
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class OfficeFinancialTransactionMapper implements RowMapper<FinancialTransactionsData> {

		@Override
		public FinancialTransactionsData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
			final Long officeId = resultSet.getLong("office_id");
			final Long transactionId = resultSet.getLong("TransId");
			final String transactionType = resultSet.getString("TransType");
			final BigDecimal debitAmount = resultSet.getBigDecimal("Dr_amt");
			final BigDecimal creditAmount = resultSet.getBigDecimal("Cr_amt");
			final String userName = resultSet.getString("username");
			final String transactionCategory = resultSet.getString("tran_type");
			final boolean flag = resultSet.getBoolean("flag");
			final LocalDate transDate = JdbcSupport.getLocalDate(resultSet, "TransDate");
			final String receiptid = resultSet.getString("receiptid");
			final Long office_poid=resultSet.getLong("office_poid");
			final String cancelRemark=resultSet.getString("cancelRemark");
			final String isDeleted=resultSet.getString("isDeleted");

			
			FinancialTransactionsData financialTransactionsData = new FinancialTransactionsData(officeId,null, transactionId, transDate, transactionType, debitAmount,
					creditAmount, null, userName, transactionCategory, flag, null, null);
			
			financialTransactionsData.setReceiptNo(receiptid);
			financialTransactionsData.setOfficePoid(office_poid);
			financialTransactionsData.setCancelRemark(cancelRemark);
			financialTransactionsData.setisDeleted(isDeleted);
			return financialTransactionsData;
			
		}
	}

	@Override
	public Collection<OfficeData> retrieveAllOfficesForSearch(String query) {
		final AppUser currentUser = context.authenticatedUser();

		final String hierarchy = currentUser.getOffice().getHierarchy();
		final String hierarchySearchString = hierarchy + "%";

		final OfficeDropdownMapper officeDropdownMap = new OfficeDropdownMapper();
		final String sql = "select distinct " + officeDropdownMap.schema()
				+ "where o.hierarchy like ? and o.name like '" + query + "%' order by o.name limit 7 ";

		return this.jdbcTemplate.query(sql, officeDropdownMap, new Object[] { hierarchySearchString });
	}

	@Override
	public Page<OfficeData> retrieveAllLCOs(final SearchParameters searchParameters) {
		final AppUser currentUser = context.authenticatedUser();
		final OfficeMapper officeMapper = new OfficeMapper();
		final String hierarchy = currentUser.getOffice().getHierarchy();

		final String hierarchySearchString = hierarchy + "%";

		final StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append("select SQL_CALC_FOUND_ROWS ");

		sqlBuilder.append(officeMapper.officeSchema());
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
				new Object[] { hierarchySearchString }, officeMapper);
	}

	private String buildSqlStringFromClientCriteria(final SearchParameters searchParameters) {

		final String sqlSearch = searchParameters.getSqlSearch();
		final Long officeId = searchParameters.getOfficeId();
		final String externalId = searchParameters.getExternalId();
		final String displayName = searchParameters.getName();
		final String firstname = searchParameters.getFirstname();
		final String lastname = searchParameters.getLastname();
		final String hierarchy = searchParameters.getHierarchy();
		final String groupName = searchParameters.getGroupName();
		final String status = searchParameters.getStatus();

		String extraCriteria = "";
		if (sqlSearch != null) {
			extraCriteria = " and (" + sqlSearch + ")";

			extraCriteria = " and ( display_name like '%" + sqlSearch + "%' OR c.phone like '%" + sqlSearch
					+ "%' OR c.account_no like '%" + sqlSearch + "%' OR c.email like '%" + sqlSearch + "%'"
					+ " OR IFNULL(( Select min(serial_no) from b_allocation ba where c.id=ba.client_id and ba. is_deleted = 'N'),'No Hardware') LIKE '%"
					+ sqlSearch + "%' )";

			// extraCriteria = " and " + sqlCondition + " = " + sqlSearch ;

		}

		if (officeId != null) {
			extraCriteria += " and office_id = " + officeId;
		}

		if (externalId != null) {
			extraCriteria += " and external_id like " + ApiParameterHelper.sqlEncodeString(externalId);
		}

		if (displayName != null) {
			extraCriteria += " and concat(ifnull(firstname, ''), if(firstname > '',' ', '') , ifnull(lastname, '')) like "
					+ ApiParameterHelper.sqlEncodeString(displayName);
		}

		if (firstname != null) {
			extraCriteria += " and firstname like " + ApiParameterHelper.sqlEncodeString(firstname);
		}

		if (lastname != null) {
			extraCriteria += " and lastname like " + ApiParameterHelper.sqlEncodeString(lastname);
		}

		if (hierarchy != null) {
			extraCriteria += " and o.hierarchy like " + ApiParameterHelper.sqlEncodeString(hierarchy + "%");
		}

		if (groupName != null) {
			extraCriteria += " and g.group_name = " + ApiParameterHelper.sqlEncodeString(groupName);
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
    public List<PaymentTypeEnum> retrievePaymentTypeEnum() {
        List<PaymentTypeEnum> PaymentType = new ArrayList<PaymentTypeEnum>();
        for(int i=0;i<=2;i++){
            PaymentType.add(PaymentTypeEnum.fromInt(i));
        }
        return PaymentType;
	}
	
	@Override
	public Long retriveMaxCountId(String officeType, String hierarchy) {
		
		return this.jdbcTemplate.queryForLong("select count(id)+1 as count from m_office where office_type='"+officeType+"' and hierarchy like '"+hierarchy+"%'");
	}

	@Override
	public OfficeData retriveOfficeDetail(Long clientId) {

			final OfficeMappers officeMappers = new OfficeMappers();
			final String sql = " select distinct  " + officeMappers.officeSchema() + " where c.id = ?";

			return this.jdbcTemplate.queryForObject(sql, officeMappers, new Object[] { clientId });
		
		
	}
	
	private static final class OfficeMappers implements RowMapper<OfficeData> {

		public String officeSchema() {
			return " o.id as officeId,c.id as clientId,oa.business_type as businessType,o.Subscriber_dues as subscriberDues from m_client c "+
                   " join m_office o on o.id=c.office_id join b_office_address oa on oa.office_id = o.id ";
		}

		@Override
		public OfficeData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long officeId = resultSet.getLong("officeId");
			final Long clientId = resultSet.getLong("clientId");
			final String businessType = resultSet.getString("businessType");
			final Long subscriberDue = resultSet.getLong("subscriberDues");
			boolean subscriberDues= false;
			if(subscriberDue==1){
				subscriberDues=true;
			}
			
			OfficeData officeData = new OfficeData(officeId,clientId,businessType,subscriberDues);
			return officeData;
		}
	}

	@Override
	public OfficeBalanceData retriveOfficebalanceDetail(Long officeId) {
		try {
			context.authenticatedUser();
			final OfficeMapperss officeMapperss = new OfficeMapperss();
			final String sql = " select distinct " + officeMapperss.officeSchema() + " where o.id = ?";
	
			return this.jdbcTemplate.queryForObject(sql, officeMapperss, new Object[] { officeId });
		} catch (EmptyResultDataAccessException e) {
			throw new PlatformDataIntegrityException("There is no office balance with this office Id","There is no office balance with this office Id","There is no office balance with this office Id");
		}
	
	}
	private static final class OfficeMapperss implements RowMapper<OfficeBalanceData> {

		public String officeSchema() {
			return " ob.office_id as officeId,ob.balance_amount as balance, o.Subscriber_dues as subscriberDues from m_office_balance ob join m_office o on o.id = ob.office_id ";
		}

		@Override
		public OfficeBalanceData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long officeId = resultSet.getLong("officeId");
			final BigDecimal balance = resultSet.getBigDecimal("balance");
			final Long subscriberDue = resultSet.getLong("subscriberDues");
			boolean subscriberDues= false;
			if(subscriberDue==1){
				subscriberDues=true;
			}
			OfficeBalanceData officeBalanceData = new OfficeBalanceData(null,officeId,balance);
			officeBalanceData.setSubscriberDues(subscriberDues);
			return officeBalanceData;
		}
	}

	
	
}