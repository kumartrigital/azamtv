/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.data;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.chargecode.data.BillFrequencyCodeData;
import org.mifosplatform.billing.selfcare.domain.SelfCare;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.logistics.onetimesale.data.OneTimeSaleData;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.office.data.OfficeData;
import org.mifosplatform.portfolio.client.service.ClientCategoryData;
import org.mifosplatform.portfolio.client.service.GroupData;
import org.mifosplatform.portfolio.clientservice.data.ClientServiceData;
import org.mifosplatform.portfolio.group.data.GroupGeneralData;
import org.mifosplatform.portfolio.order.data.OrderData;

/**
 * Immutable data object representing client data.
 */
final public class ClientData implements Comparable<ClientData> {

	private final Long id;
	private String accountNo;
	private String externalId;

	private EnumOptionData status;
	private Boolean active;
	private LocalDate activationDate;

	private String firstname;
	private String middlename;
	private String lastname;
	private String fullname;
	private String displayName;
	private String officeMail;

	private Long officeId;
	private final String officeName;

	private final String imageKey;
	@SuppressWarnings("unused")
	private final Boolean imagePresent;
	private String email;
	private String phone;
	private String homePhoneNumber;
	private String addressNo;
	private String street;
	private String city;
	private String state;
	private String country;
	private String zip;
	private BigDecimal balanceAmount;
	private BigDecimal walletAmount;
	private String hwSerialNumber;
	private String taxExemption;
	private String groupName;
	private BigDecimal paidAmount;
	private BigDecimal lastBillAmount;
	private Date lastPaymentDate;
	private String district;
	private BigDecimal overDue;
	private Date nextBillDate;
	private String AccountType;
	private String officeType;
	private String officePoId;
	private Date lastBillDate;

	private String c_active;
	private String c_inactive;
	private String c_instock;
	private String c_allocated;
	private String officeHierarchy;
	private String selfcarePassword;
	private String currencyCode;
	private String voucherStock;
	private String provisionPending;

	public String getVoucherStock() {
		return voucherStock;
	}

	public void setVoucherStock(String voucherStock) {
		this.voucherStock = voucherStock;
	}

	public String getProvisionPending() {
		return provisionPending;
	}

	public void setProvisionPending(String provisionPending) {
		this.provisionPending = provisionPending;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	private Collection<MCodeData> idProofs;

	public String getAccountType() {
		return this.AccountType;
	}

	public void setAccountType(String accountType) {
		this.AccountType = accountType;
	}

	// associations
	private final Collection<GroupGeneralData> groups;

	// template
	private final Collection<OfficeData> officeOptions;
	private final Collection<ClientCategoryData> clientCategoryDatas;
	private final String categoryType;
	private AddressData addressTemplateData;
	private final List<String> hardwareDetails;
	private PaymentGatewayConfiguration configurationProperty;
	private Configuration loginConfigurationProperty;
	private PaymentGatewayConfiguration configurationPropertyforIos;
	private final String currency;

	private final Collection<GroupData> groupNameDatas;
	private final Collection<CodeValueData> closureReasons;
	private Boolean balanceCheck;
	private final String entryType;
	private SelfCare selfcare;
	private final String userName;
	private final String clientPassword;
	private final String title;
	private final String parentId;
	private String officePOID;
	private ClientAdditionalData clientAdditionalData;
	private List<ClientServiceData> clientServiceData;
	private List<OrderData> orderData;
	private List<Balance> balance;

	private List<OneTimeSaleData> oneTimeSaleData;
	private String poId;
	Collection<MCodeData> businessTypes;
	private Collection<MCodeData> preferences;
	private String billMode;
	private Long orderId;
	private String stbId;
	private LocalDate startDate;
	private LocalDate endDate;
	/* private List<AddressData> addressdata; */
	private List<EnumOptionData> addressOptionsData;
	private List<String> countryData, stateData, cityData;

	public List<String> citiesData;
	public String addressType;
	private List<AddressData> datas;
	private List<AddressData> addressData;

	/* private Collection<ClientCategoryData> ClientCategoryData; */
	private final Collection<OfficeData> allowedParents;
	private Collection<OfficeData> officeData;
	private Collection<ClientCategoryData> ClientCategoryDatas;
	private String parentInfo;
	private String idKey;
	private String idValue;
	private Long clientPoId;
	private String poid;
	private String clientServicePoId;
	private Long currencyId;

	private Long chargeCycleId;
	private String chargeCycle;
	private String addressKey;
	private Long clientId;
	private BigDecimal nonCurrencyAmount;
	private List<ClientData> clientDatas;

	/*
	 * public static ClientData temp() { return new ClientData(null, null,null,
	 * null, null, null, null, null, null, null, null, null, null, null, null, null,
	 * null,null,null,null, null, null, null, null, null, null,
	 * null,null,null,null,null,null,null,null,null,null,null,
	 * null,null,null,null,null,null,null, null,null,null,null,null); }
	 */
	public static ClientData phonesAndEmails(String phone, String email) {
		return new ClientData(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, email, phone, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null);

	}

	public static ClientData template(final Long officeId, final LocalDate joinedDate,
			final Collection<OfficeData> officeOptions, Collection<ClientCategoryData> categoryDatas,
			Collection<GroupData> groupDatas, List<CodeValueData> closureReasons) {
		return new ClientData(null, null, null, officeId, null, null, null, null, null, null, null, null, joinedDate,
				null, officeOptions, null, categoryDatas, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, groupDatas, closureReasons, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null);
	}

	public static ClientData template(final Long officeId, final LocalDate joinedDate,
			final Collection<OfficeData> officeOptions, Collection<ClientCategoryData> categoryDatas,
			List<CodeValueData> closureReasons) {
		return new ClientData(null, null, null, officeId, null, null, null, null, null, null, null, null, joinedDate,
				null, officeOptions, null, categoryDatas, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, closureReasons, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null);
	}

	public static ClientData templateOnTop(final ClientData clientData, final List<OfficeData> allowedOffices,
			Collection<ClientCategoryData> categoryDatas, Collection<GroupData> groupDatas,
			List<String> allocationDetailsDatas, String balanceCheck) {

		return new ClientData(clientData.accountNo, clientData.groupName, clientData.status, clientData.officeId,
				clientData.officeName, clientData.id, clientData.firstname, clientData.middlename, clientData.lastname,
				clientData.fullname, clientData.displayName, clientData.externalId, clientData.activationDate,
				clientData.imageKey, allowedOffices, clientData.groups, categoryDatas, clientData.categoryType,
				clientData.email, clientData.phone, clientData.homePhoneNumber, clientData.addressNo, clientData.street,
				clientData.city, clientData.state, clientData.country, clientData.zip, clientData.balanceAmount,
				allocationDetailsDatas, clientData.hwSerialNumber, clientData.currency, groupDatas, null, balanceCheck,
				clientData.taxExemption, clientData.entryType, clientData.walletAmount, null, null, null,
				clientData.title, clientData.paidAmount, clientData.lastBillAmount, clientData.lastPaymentDate, null,
				null, null, null, clientData.poId, clientData.allowedParents);
	}

	public static ClientData setParentGroups(final ClientData clientData,
			final Collection<GroupGeneralData> parentGroups) {
		return new ClientData(clientData.accountNo, clientData.groupName, clientData.status, clientData.officeId,
				clientData.officeName, clientData.id,

				clientData.firstname, clientData.middlename, clientData.lastname, clientData.fullname,
				clientData.displayName, clientData.externalId, clientData.activationDate, clientData.imageKey,
				clientData.officeOptions, parentGroups, clientData.clientCategoryDatas, clientData.categoryType,
				clientData.email, clientData.phone, clientData.homePhoneNumber, clientData.addressNo, clientData.street,
				clientData.city, clientData.state, clientData.country, clientData.zip, clientData.balanceAmount,
				clientData.hardwareDetails, clientData.hwSerialNumber, clientData.currency, clientData.groupNameDatas,
				null, null, clientData.taxExemption, clientData.entryType, clientData.walletAmount, null, null, null,
				clientData.title, clientData.paidAmount, clientData.lastBillAmount, clientData.lastPaymentDate, null,
				null, null, null, clientData.poId, clientData.allowedParents);

	}

	public static ClientData clientIdentifier(final Long id, final String accountNo, final EnumOptionData status,
			final String firstname, final String middlename, final String lastname, final String fullname,
			final String displayName, final Long officeId, final String officeName) {

		return new ClientData(accountNo, null, status, officeId, officeName, id, firstname, middlename, lastname,
				fullname, displayName, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null);
	}

	public static ClientData lookup(final Long id, final String displayName, final Long officeId,
			final String officeName) {
		return new ClientData(null, null, null, officeId, officeName, id, null, null, null, null, displayName, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null);

	}

	public static ClientData walletAmount(final Long id, final String accountNo, final BigDecimal walletAmount,
			final String hwSerialNumber) {
		return new ClientData(accountNo, null, null, null, null, id, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				hwSerialNumber, null, null, null, null, null, null, walletAmount, null, null, null, null, null, null,
				null, null, null, null, null, null, null);

	}

	public static ClientData instance(final String accountNo, final String groupName, final EnumOptionData status,
			final Long officeId, final String officeName, final Long id, final String firstname,
			final String middlename, final String lastname, final String fullname, final String displayName,
			final String externalId, final LocalDate activationDate, final String imageKey, final String categoryType,
			final String email, final String phone, final String homePhoneNumber, final String addrNo,
			final String street, final String city, final String state, final String country, final String zip,
			final BigDecimal balanceAmount, final String hwSerialNumber, final String currency,
			final String taxExemption, String entryType, final BigDecimal walletAmount, final String userName,
			final String clientPassword, final String parentId, String title, final BigDecimal paidAmount,
			final BigDecimal lastBillAmount, final Date lastPaymentDate, final String poId) {

		return new ClientData(accountNo, groupName, status, officeId, officeName, id, firstname, middlename, lastname,
				fullname, displayName, externalId, activationDate, imageKey, null, null, null, categoryType, email,
				phone, homePhoneNumber, addrNo, street, city, state, country, zip, balanceAmount, null, hwSerialNumber,
				currency, null, null, null, taxExemption, entryType, walletAmount, userName, clientPassword, parentId,
				title, paidAmount, lastBillAmount, lastPaymentDate, null, null, null, null, poId, null);

	}

	public static ClientData temperory(final String groupName, final EnumOptionData status, final Long officeId,
			final String officeName, final Long id, final LocalDate activationDate, final String imageKey,
			final String poId, String accountNo) {

		ClientData data = new ClientData(accountNo, groupName, status, officeId, officeName, id, null, null, null, null,
				null, null, activationDate, imageKey, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null);
		data.setPoId(poId);

		return data;
	}

	public static ClientData searchClient(final Long id) {
		return new ClientData(null, null, null, null, null, id, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null);

	}

	public static ClientData advancedSearchClient(final Long id, final String name, final String accountNo,
			final String phone, final EnumOptionData status, final String officeName, final String serialNo,
			final BigDecimal clientBalance) {
		return new ClientData(accountNo, null, status, null, officeName, id, null, null, null, null, name, null, null,
				null, null, null, null, null, null, phone, null, null, null, null, null, null, null, clientBalance,
				null, serialNo, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null);

	}
	

	public static ClientData lcoClient(final Long id, final String accountNo, final String displayName,
			final String phone, final BigDecimal clientBalance, final Long orderId, final String stbId,
			final LocalDate startDate, final LocalDate endDate) {
		return new ClientData(accountNo, null, null, null, null, id, null, null, null, null, displayName, null, null,
				null, null, null, null, null, null, phone, null, null, null, null, null, null, null, clientBalance,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, orderId,
				stbId, startDate, endDate, null, null);

	}

	public static ClientData eventClientData(final Long id, final String accountNo, final String fullName,
			final String userName, final String password, String email, String phone) {
		return new ClientData(accountNo, null, null, null, null, id, null, null, null, fullName, null, null, null, null,
				null, null, null, null, email, phone, null, null, null, null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, userName, password, null, null, null, null, null, null, null, null,
				null, null, null);

	}

	private ClientData(final String accountNo, final String groupName, final EnumOptionData status, final Long officeId,
			final String officeName, final Long id, final String firstname, final String middlename,
			final String lastname, final String fullname, final String displayName, final String externalId,
			final LocalDate activationDate, final String imageKey, final Collection<OfficeData> allowedOffices,
			final Collection<GroupGeneralData> groups, Collection<ClientCategoryData> clientCategoryDatas,
			final String categoryType, final String email, final String phone, final String homePhoneNumber,
			final String addrNo, final String street, final String city, final String state, final String country,
			final String zip, BigDecimal balanceAmount, final List<String> hardwareDetails, final String hwSerialNumber,
			final String currency, Collection<GroupData> groupNameDatas, List<CodeValueData> closureReasons,
			String balanceCheck, final String taxExemption, String entryType, final BigDecimal walletAmount,
			final String userName, final String clientPassword, final String parentId, final String title,
			final BigDecimal paidAmount, final BigDecimal lastBillAmount, final Date lastPaymentDate,
			final Long orderId, final String stbId, final LocalDate startDate, final LocalDate endDate,
			final String poId, final Collection<OfficeData> allowedParents) {

		this.accountNo = accountNo;
		this.groupName = groupName;
		this.status = status;
		if (status != null) {
			active = status.getId().equals(300L);
		} else {
			active = null;
		}
		this.officeId = officeId;
		this.officeName = officeName;
		this.id = id;
		this.firstname = StringUtils.defaultIfEmpty(firstname, null);
		this.middlename = StringUtils.defaultIfEmpty(middlename, null);
		this.lastname = StringUtils.defaultIfEmpty(lastname, null);
		this.fullname = StringUtils.defaultIfEmpty(fullname, null);
		this.displayName = StringUtils.defaultIfEmpty(displayName, null);
		this.externalId = StringUtils.defaultIfEmpty(externalId, null);
		this.activationDate = activationDate;
		this.walletAmount = walletAmount;
		this.imageKey = imageKey;
		this.title = title;
		this.paidAmount = paidAmount;
		this.lastBillAmount = lastBillAmount;
		this.lastPaymentDate = lastPaymentDate;
		if (imageKey != null) {
			this.imagePresent = Boolean.TRUE;
		} else {
			this.imagePresent = null;
		}
		this.closureReasons = closureReasons;

		// associations
		this.groups = groups;

		// template
		this.officeOptions = allowedOffices;
		this.clientCategoryDatas = clientCategoryDatas;
		this.groupNameDatas = groupNameDatas;
		this.categoryType = categoryType;
		this.email = email;
		this.phone = phone;
		this.homePhoneNumber = homePhoneNumber;
		this.addressNo = StringUtils.defaultIfEmpty(addrNo, null);
		this.street = StringUtils.defaultIfEmpty(street, null);
		this.city = StringUtils.defaultIfEmpty(city, null);
		this.state = StringUtils.defaultIfEmpty(state, null);
		this.country = StringUtils.defaultIfEmpty(country, null);
		this.zip = StringUtils.defaultIfEmpty(zip, null);
		if (balanceAmount == null) {
			balanceAmount = BigDecimal.ZERO;
		}
		this.balanceAmount = balanceAmount != null ? balanceAmount : BigDecimal.ZERO;
		this.hardwareDetails = hardwareDetails;
		this.hwSerialNumber = hwSerialNumber;
		this.currency = currency;
		this.taxExemption = taxExemption;
		this.entryType = entryType;
		if (balanceCheck != null && balanceCheck.equalsIgnoreCase("Y")) {
			this.setBalanceCheck(true);
		} else {
			this.setBalanceCheck(false);
		}
		this.userName = userName;
		this.clientPassword = clientPassword;
		this.parentId = parentId;
		this.preferences = preferences;
		this.orderId = orderId;
		this.stbId = stbId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.poId = poId;
		this.allowedParents = allowedParents;

	}

	public ClientData(final Long id, final String firstname, final String lastname, final String email,
			final String phone, final String externalId, final String homePhoneNumber, final String userName,
			final String password, final LocalDate activationDate, final String categoryType, final String title,
			final String idKey, final String idValue, final Long officeId, final String addressNo, final String street,
			final String city, final String district, final String state, final String country, String addressKey) {

		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.phone = phone;
		this.externalId = externalId;
		this.homePhoneNumber = homePhoneNumber;
		this.userName = userName;
		this.clientPassword = password;
		this.activationDate = activationDate;
		this.categoryType = categoryType;
		this.title = title;
		this.idKey = idKey;
		this.idValue = idValue;
		this.officeId = officeId;
		this.addressNo = addressNo;
		this.street = street;
		this.city = city;
		this.district = district;
		this.state = state;
		this.country = country;
		this.addressKey = addressKey;
		this.groups = null;
		this.officeOptions = null;
		this.clientCategoryDatas = null;
		this.hardwareDetails = null;
		this.currency = null;
		this.groupNameDatas = null;
		this.closureReasons = null;
		this.entryType = null;
		this.parentId = null;
		this.allowedParents = null;
		this.officeName = null;
		this.imageKey = null;
		if (imageKey != null) {
			this.imagePresent = Boolean.TRUE;
		} else {
			this.imagePresent = null;
		}

	}

	public ClientData(Long clientId, String country, Long currencyId) {
		this.country = country;
		this.clientId = clientId;
		this.currencyId = currencyId;
		this.id = null;
		this.firstname = null;
		this.lastname = null;
		this.email = null;
		this.phone = null;
		this.externalId = null;
		this.homePhoneNumber = null;
		this.userName = null;
		this.clientPassword = null;
		this.activationDate = null;
		this.categoryType = null;
		this.title = null;
		this.idKey = null;
		this.idValue = null;
		this.officeId = null;
		this.addressNo = null;
		this.street = null;
		this.city = null;
		this.district = null;
		this.state = null;
		this.country = null;
		this.addressKey = null;
		this.groups = null;
		this.officeOptions = null;
		this.clientCategoryDatas = null;
		this.hardwareDetails = null;
		this.currency = null;
		this.groupNameDatas = null;
		this.closureReasons = null;
		this.entryType = null;
		this.parentId = null;
		this.allowedParents = null;
		this.officeName = null;
		this.imageKey = null;
		if (imageKey != null) {
			this.imagePresent = Boolean.TRUE;
		} else {
			this.imagePresent = null;
		}
	}

	public ClientData(List<ClientData> clientDatas) {
		this.clientDatas = clientDatas;
		this.clientCategoryDatas = null;
		this.id = null;
		this.firstname = null;
		this.lastname = null;
		this.email = null;
		this.phone = null;
		this.externalId = null;
		this.homePhoneNumber = null;
		this.userName = null;
		this.clientPassword = null;
		this.activationDate = null;
		this.categoryType = null;
		this.title = null;
		this.idKey = null;
		this.idValue = null;
		this.officeId = null;
		this.addressNo = null;
		this.street = null;
		this.city = null;
		this.district = null;
		this.state = null;
		this.country = null;
		this.addressKey = null;
		this.groups = null;
		this.officeOptions = null;
		this.hardwareDetails = null;
		this.currency = null;
		this.groupNameDatas = null;
		this.closureReasons = null;
		this.entryType = null;
		this.parentId = null;
		this.allowedParents = null;
		this.officeName = null;
		this.imageKey = null;
		if (imageKey != null) {
			this.imagePresent = Boolean.TRUE;
		} else {
			this.imagePresent = null;
		}

	}



	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}

	public Long id() {
		return this.id;
	}

	public String displayName() {
		return this.displayName;
	}

	public Long officeId() {
		return this.officeId;
	}

	public String officeName() {
		return this.officeName;
	}

	public String imageKey() {
		return this.imageKey;
	}

	public boolean imageKeyDoesNotExist() {
		return !imageKeyExists();
	}

	public Boolean isActive() {
		return active;
	}

	private boolean imageKeyExists() {
		return StringUtils.isNotBlank(this.imageKey);
	}

	public BigDecimal getOverDue() {
		return overDue;
	}

	public void setOverDue(BigDecimal overDue) {
		this.overDue = overDue;
	}

	public Date getNextBillDate() {
		return nextBillDate;
	}

	public void setNextBillDate(Date nextBillDate) {
		this.nextBillDate = nextBillDate;
	}

	@Override
	public int compareTo(final ClientData obj) {
		if (obj == null) {
			return -1;
		}
		return new CompareToBuilder() //
				.append(this.id, obj.id) //
				.append(this.displayName, obj.displayName) //
				.toComparison();
	}

	public String getPoid() {
		return poid;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		ClientData rhs = (ClientData) obj;
		return new EqualsBuilder() //
				.append(this.id, rhs.id) //
				.append(this.displayName, rhs.displayName) //
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37) //
				.append(this.id) //
				.append(this.displayName) //
				.toHashCode();
	}

	// TODO - kw - look into removing usage of the getters below
	public String getExternalId() {
		return this.externalId;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public String getAddressKey() {
		return addressKey;
	}

	public String getLastname() {
		return this.lastname;
	}

	public LocalDate getActivationDate() {
		return this.activationDate;
	}

	public void setAddressTemplate(AddressData data) {
		this.setAddressTemplateData(data);

	}

	public PaymentGatewayConfiguration getConfigurationProperty() {
		return configurationProperty;
	}

	public void setConfigurationProperty(PaymentGatewayConfiguration paypalconfigurationProperty) {
		this.configurationProperty = paypalconfigurationProperty;
	}

	public void setConfigurationPropertyForIos(PaymentGatewayConfiguration paypalconfigurationPropertyForIos) {
		this.setConfigurationPropertyforIos(paypalconfigurationPropertyForIos);
	}

	public void setBalanceCheck(boolean isEnabled) {

		this.balanceCheck = isEnabled;
	}

	public SelfCare getSelfcare() {
		return selfcare;
	}

	public void setSelfcare(SelfCare selfcare) {
		this.selfcare = selfcare;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public AddressData getAddressTemplateData() {
		return addressTemplateData;
	}

	public void setAddressTemplateData(AddressData addressTemplateData) {
		this.addressTemplateData = addressTemplateData;
	}

	public PaymentGatewayConfiguration getConfigurationPropertyforIos() {
		return configurationPropertyforIos;
	}

	public void setConfigurationPropertyforIos(PaymentGatewayConfiguration paypalconfigurationPropertyForIos) {
		this.configurationPropertyforIos = paypalconfigurationPropertyForIos;
	}

	public Collection<CodeValueData> getClosureReasons() {
		return closureReasons;
	}

	public Boolean getBalanceCheck() {
		return balanceCheck;
	}

	public void setBalanceCheck(Boolean balanceCheck) {
		this.balanceCheck = balanceCheck;
	}

	public void setConfigurationProperty(Configuration configurationProperty) {
		this.loginConfigurationProperty = configurationProperty;

	}

	public void setClientAdditionalData(ClientAdditionalData clientAdditionalData) {

		this.clientAdditionalData = clientAdditionalData;
	}

	public String getGroupName() {
		return groupName;
	}

	public Long getId() {
		return id;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;

	}

	public String getOfficeName() {
		return officeName;
	}

	public EnumOptionData getStatus() {
		return status;
	}

	public String getOfficePOID() {
		return officePOID;
	}

	public Configuration getLoginConfigurationProperty() {
		return loginConfigurationProperty;
	}

	public void setLoginConfigurationProperty(Configuration loginConfigurationProperty) {
		this.loginConfigurationProperty = loginConfigurationProperty;
	}

	public Boolean getActive() {
		return active;
	}

	public String getMiddlename() {
		return middlename;
	}

	public String getFullname() {
		return fullname;
	}

	public String getImageKey() {
		return imageKey;
	}

	public Boolean getImagePresent() {
		return imagePresent;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public String getHomePhoneNumber() {
		return homePhoneNumber;
	}

	public String getAddressNo() {
		return addressNo;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getZip() {
		return zip;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public BigDecimal getWalletAmount() {
		return walletAmount;
	}

	public String getHwSerialNumber() {
		return hwSerialNumber;
	}

	public String getTaxExemption() {
		return taxExemption;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public BigDecimal getLastBillAmount() {
		return lastBillAmount;
	}

	public Date getLastPaymentDate() {
		return lastPaymentDate;
	}

	public Date getLastBillDate() {
		return lastBillDate;
	}

	public Collection<GroupGeneralData> getGroups() {
		return groups;
	}

	public Collection<OfficeData> getOfficeOptions() {
		return officeOptions;
	}

	public Collection<ClientCategoryData> getClientCategoryDatas() {
		return clientCategoryDatas;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public List<String> getHardwareDetails() {
		return hardwareDetails;
	}

	public String getCurrency() {
		return currency;
	}

	public Collection<GroupData> getGroupNameDatas() {
		return groupNameDatas;
	}

	public String getEntryType() {
		return entryType;
	}

	public String getUserName() {
		return userName;
	}

	public String getClientPassword() {
		return clientPassword;
	}

	public String getTitle() {
		return title;
	}

	public String getParentId() {
		return parentId;
	}

	public ClientAdditionalData getClientAdditionalData() {
		return clientAdditionalData;
	}

	public void setOfficePOID(String officePOID) {
		this.officePOID = officePOID;
	}

	public List<ClientServiceData> getClientServiceData() {
		return clientServiceData;
	}

	public void setClientServiceData(List<ClientServiceData> clientServiceData) {
		this.clientServiceData = clientServiceData;
	}

	public List<OrderData> getOrderData() {
		return orderData;
	}

	public void setOrderData(List<OrderData> orderData) {
		this.orderData = orderData;
	}

	/*
	 * public List<AddressData> getAddressdata() { return addressdata; }
	 * 
	 * public void setAddressdata(List<AddressData> addressdata) { this.addressdata
	 * = addressdata; }
	 */

	public List<OneTimeSaleData> getOneTimeSaleData() {
		return oneTimeSaleData;
	}

	public void setOneTimeSaleData(List<OneTimeSaleData> oneTimeSaleData) {
		this.oneTimeSaleData = oneTimeSaleData;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getBillMode() {
		return billMode;
	}

	public void setBillMode(String billMode) {
		this.billMode = billMode;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setStatus(EnumOptionData status) {
		this.status = status;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActivationDate(LocalDate activationDate) {
		this.activationDate = activationDate;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setHomePhoneNumber(String homePhoneNumber) {
		this.homePhoneNumber = homePhoneNumber;
	}

	public void setAddressNo(String addressNo) {
		this.addressNo = addressNo;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public void setWalletAmount(BigDecimal walletAmount) {
		this.walletAmount = walletAmount;
	}

	public void setHwSerialNumber(String hwSerialNumber) {
		this.hwSerialNumber = hwSerialNumber;
	}

	public void setTaxExemption(String taxExemption) {
		this.taxExemption = taxExemption;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public void setLastBillAmount(BigDecimal lastBillAmount) {
		this.lastBillAmount = lastBillAmount;
	}

	public void setLastPaymentDate(Date lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public void setLastBillDate(Date lastBillDate) {
		this.lastBillDate = lastBillDate;
	}

	public String getParentInfo() {
		return parentInfo;
	}

	public void setParentInfo(String parentInfo) {
		this.parentInfo = parentInfo;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String obrmRequestInput() {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		stringBuffer.append("<MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>");
		stringBuffer.append("<ACCOUNT_NO>" + this.accountNo + "</ACCOUNT_NO>");
		// stringBuffer.append("<ACCOUNT_NO>CR-"+1080534620+"</ACCOUNT_NO>");
		stringBuffer.append("<FLAGS>0</FLAGS>");
		stringBuffer.append("<PROGRAM_NAME>OAP|csradmin</PROGRAM_NAME>");
		// stringBuffer.append("<POID>0.0.0.1 / "+this.poId+" 0</POID>");
		stringBuffer.append("<POID>0.0.0.1 / 0 0</POID>");

		// stringBuffer.append("<USERID>"+userId+"</USERID>");
		stringBuffer.append("<USERID>0.0.0.1 /account 416152 8</USERID>");
		stringBuffer.append("</MSO_OP_CUST_GET_CUSTOMER_INFO_inputFlist>");
		return stringBuffer.toString();
	}

	public String celcomRequestInput(int flag) {

		StringBuffer sb = new StringBuffer("<COB_OP_CUST_CUSTOMER_RETRIEVAL_inputFlist>");

		sb.append("<POID>0.0.0.1 /account -1 0</POID>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account " + this.poId + " 1</ACCOUNT_OBJ>");
		sb.append("<PROGRAM_NAME>COB|celcom</PROGRAM_NAME>");
		sb.append("<ACCOUNT_NO>" + this.accountNo + "</ACCOUNT_NO>");
		sb.append("<FLAGS>" + flag + "</FLAGS>");
		sb.append("</COB_OP_CUST_CUSTOMER_RETRIEVAL_inputFlist>");
		return sb.toString();

	}

	public static ClientData fromJson(String result, ClientData clientData) throws JSONException {

		String firstname = null, middlename = null, lastname = null, email = null, phone = null, homePhoneNumber = null,
				addressNo = null, city = null, state = null, country = null, zipcode = null, fullname = null,
				displayName = null, entryType = null, title = null;
		BigDecimal balanceAmount = null;

		try {
			JSONObject object = new JSONObject(result);
			object = object.optJSONObject("brm:MSO_OP_CUST_GET_CUSTOMER_INFO_outputFlist");
			JSONObject nameInfoObj = null;
			if (object.optJSONArray("brm:NAMEINFO") != null) {
				nameInfoObj = object.optJSONArray("brm:NAMEINFO").optJSONObject(0);
			} else {
				JSONArray array = new JSONArray("[" + object.getString("brm:NAMEINFO") + "]");
				nameInfoObj = array.optJSONObject(0);
			}
			JSONObject creditProfInfoObject = object.optJSONObject("brm:MSO_FLD_CREDIT_PROFILE");
			JSONObject officeInfoObject = object.optJSONObject("brm:MSO_FLD_ORG_STRUCTURE");

			// from client
			final String groupName = clientData.getGroupName();
			final Long id = clientData.getId();
			final EnumOptionData status = clientData.getStatus();
			final LocalDate activationDate = clientData.getActivationDate();
			final String imageKey = clientData.getImageKey();
			final String userName = clientData.getUserName();
			final String clientpassword = clientData.getClientPassword();
			final String parentId = clientData.getParentId();

			Long officeId = Long.valueOf(0);
			String officeName = null;
			/*
			 * if(clientData.getOfficePOID().equalsIgnoreCase(object.getString("brm:PARENT")
			 * )){ officeId = clientData.getOfficeId(); officeName =
			 * clientData.getOfficeName(); }
			 */

			if (officeInfoObject != null) {
				if ("0.0.0.1 /account 104790 7".equalsIgnoreCase(officeInfoObject.optString("brm:PARENT"))) {
					officeId = clientData.getOfficeId();
					officeName = clientData.getOfficeName();
				}
			}
			// from object
			final String accountNo = object.optString("brm:ACCOUNT_NO");
			final String externalId = object.optString("brm:POID");
			final String categoryType = object.optString("brm:BUSINESS_TYPE");

			if (nameInfoObj != null) {
				firstname = nameInfoObj.optString("brm:FIRST_NAME");
				middlename = nameInfoObj.optString("brm:MIDDLE_NAME");
				lastname = nameInfoObj.optString("brm:LAST_NAME");
				email = nameInfoObj.optString("brm:EMAIL_ADDR");
				phone = nameInfoObj.optJSONObject("brm:PHONES").getString("brm:PHONE");
				homePhoneNumber = phone;
				addressNo = nameInfoObj.optString("brm:ADDRESS");
				city = nameInfoObj.optString("brm:CITY");
				state = nameInfoObj.optString("brm:STATE");
				country = nameInfoObj.optString("brm:COUNTRY");
				zipcode = nameInfoObj.optString("brm:ZIP");
				fullname = (new StringBuilder(firstname + " " + lastname)).toString();
				displayName = fullname;
				entryType = nameInfoObj.optString("brm:COUNTRY");
				title = nameInfoObj.optString("brm:SALUTATION");
			}

			final String street = city;

			if (creditProfInfoObject != null) {
				balanceAmount = BigDecimal.valueOf(creditProfInfoObject.optLong("brm:CURRENT_BAL"));
			}

			final String hwSerialNumber = null;
			final String currency = null;
			final String taxExemption = null;
			final BigDecimal walletAmount = null;
			final BigDecimal paidAmount = null;
			final BigDecimal lastBillAmount = null;
			final Date lastPaymentDate = null;

			return ClientData.instance(accountNo, groupName, status, officeId, officeName, id, firstname, middlename,
					lastname, fullname, displayName, externalId, activationDate, imageKey, categoryType, email, phone,
					homePhoneNumber, addressNo, street, city, state, country, zipcode, balanceAmount, hwSerialNumber,
					currency, taxExemption, entryType, walletAmount, userName, clientpassword, parentId, title,
					paidAmount, lastBillAmount, lastPaymentDate, null);
		} catch (Exception e) {
			return null;
		}
	}

	public static ClientData fromCelcomJson(String result, ClientData clientData) throws JSONException {

		String firstname = null, middlename = null, lastname = null, email = null, phone = null, homePhoneNumber = null,
				addressNo = null, city = null, state = null, country = null, zipcode = null, fullname = null,
				displayName = null, entryType = null, title = null;
		BigDecimal balanceAmount = null;

		try {
			JSONObject object = new JSONObject(result);
			object = object.optJSONObject("brm:COB_OP_CUST_CUSTOMER_RETRIEVAL_outputFlist");
			JSONObject nameInfoObj = null;
			if (object.optJSONArray("brm:NAMEINFO") != null) {
				nameInfoObj = object.optJSONArray("brm:NAMEINFO").optJSONObject(0);
			} else {
				JSONArray array = new JSONArray("[" + object.getString("brm:NAMEINFO") + "]");
				nameInfoObj = array.optJSONObject(0);
			}
			JSONObject billInfoObj = object.optJSONObject("brm:BILLINFO");

			// from object
			/* clientData.setAccountNo(object.optString("brm:ACCOUNT_NO")); */
			/* final String categoryType = object.optString("brm:BUSINESS_TYPE"); */

			if (nameInfoObj != null) {
				/*
				 * clientData.setFirstname(nameInfoObj.optString("brm:FIRST_NAME"));
				 * clientData.setMiddlename(nameInfoObj.optString("brm:MIDDLE_NAME"));
				 * clientData.setLastname(nameInfoObj.optString("brm:LAST_NAME"));
				 * clientData.setEmail(nameInfoObj.optString("brm:EMAIL_ADDR"));
				 * clientData.setPhone(nameInfoObj.optJSONObject("brm:PHONES").getString(
				 * "brm:PHONE"));
				 * clientData.setHomePhoneNumber(nameInfoObj.optJSONObject("brm:PHONES").
				 * getString("brm:PHONE"));
				 * clientData.setAddressNo(nameInfoObj.optString("brm:ADDRESS"));
				 * clientData.setCity(nameInfoObj.optString("brm:CITY"));
				 * clientData.setState(nameInfoObj.optString("brm:STATE"));
				 * clientData.setCountry(nameInfoObj.optString("brm:COUNTRY"));
				 * clientData.setZip(nameInfoObj.optString("brm:ZIP"));
				 * clientData.setFullname((new
				 * StringBuilder(nameInfoObj.optString("brm:FIRST_NAME")+" "+nameInfoObj.
				 * optString("brm:LAST_NAME"))).toString());
				 * clientData.setDisplayName(clientData.getFullname()); entryType
				 * =nameInfoObj.optString("brm:COUNTRY");
				 */
			}

			/* final String street = city; */

			/*
			 * if(billInfoObj!=null){ clientData.setBalanceAmount(new
			 * BigDecimal(billInfoObj.optString("brm:CURRENT_BAL")));
			 * clientData.setLastBillAmount(new
			 * BigDecimal(billInfoObj.optString("brm:PENDINGBILL_DUE")));
			 * clientData.setOverDue(new
			 * BigDecimal(billInfoObj.optString("brm:OPENBILL_DUE")));
			 * clientData.setLastPaymentDate(retriveDate(billInfoObj.optString(
			 * "brm:LAST_BILL_T")));
			 * clientData.setNextBillDate(retriveDate(billInfoObj.optString(
			 * "brm:NEXT_BILL_T"))); }
			 */

			if (billInfoObj != null) {
				if (!billInfoObj.optString("brm:CURRENT_BAL").equals("")
						&& !billInfoObj.optString("brm:CURRENT_BAL").equalsIgnoreCase(null)) {
					clientData.setBalanceAmount(new BigDecimal(billInfoObj.optString("brm:CURRENT_BAL")));
				}
				if (!billInfoObj.optString("brm:TOTAL_DUE").equals("")
						&& !billInfoObj.optString("brm:TOTAL_DUE").equalsIgnoreCase(null)) {
					clientData.setLastBillAmount(new BigDecimal(billInfoObj.optString("brm:TOTAL_DUE")));
				}
				clientData.setLastBillDate(retriveDate(billInfoObj.optString("brm:LAST_BILL_T")));
				if (!billInfoObj.optString("brm:AMOUNT").equals("")
						&& !billInfoObj.optString("brm:AMOUNT").equalsIgnoreCase(null)) {
					clientData.setPaidAmount(new BigDecimal(billInfoObj.optString("brm:AMOUNT")));
				}
				clientData.setLastPaymentDate(retriveDate(billInfoObj.optString("brm:LAST_POSTED_T")));
				clientData.setNextBillDate(retriveDate(billInfoObj.optString("brm:NEXT_BILL_T")));
			}

			return clientData;
		} catch (Exception e) {
			throw new PlatformDataIntegrityException("parse.exception", e.getMessage(), e.getMessage(), e.getMessage());
		}
	}

	private static Date retriveDate(String optString) {
		Date returnValue = null;
		try {
			if (optString != null) {
				String arg[] = optString.split("T");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				returnValue = format.parse(arg[0]);
			}
			return returnValue;
		} catch (ParseException e) {
			throw new PlatformDataIntegrityException("parse.exception", e.getMessage(), e.getMessage(), e.getMessage());
		}
	}

	/*
	 * public Collection<MCodeData> getPreferences() { return preferences; }
	 */

	public void setPreferences(Collection<MCodeData> prefernces) {
		this.preferences = prefernces;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Collection<MCodeData> getBusinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(Collection<MCodeData> businessTypes) {
		this.businessTypes = businessTypes;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getStbId() {
		return stbId;
	}

	public void setStbId(String stbId) {
		this.stbId = stbId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public Collection<MCodeData> getPreferences() {
		return preferences;
	}

	public List<EnumOptionData> getAddressOptionsData() {
		return addressOptionsData;
	}

	public void setAddressOptionsData(List<EnumOptionData> addressOptionsData) {
		this.addressOptionsData = addressOptionsData;
	}

	public List<String> getCityData() {
		return cityData;
	}

	public void setCityData(List<String> cityData) {
		this.cityData = cityData;
	}

	public void setAddressData(List<AddressData> addressData) {
		this.datas = addressData;

	}

	public List<AddressData> getAddressData() {
		return addressData;
	}

	public List<AddressData> getDatas() {
		return datas;
	}

	public void setDatas(List<AddressData> datas) {
		this.datas = datas;

	}

	public Collection<OfficeData> getAllowedParents() {
		return allowedParents;
	}

	/*
	 * public void setParentClientData(Collection<ClientCategoryData>
	 * parentClientData) { this.parentClientData = parentClientData; }
	 */

	public void setOfficeData(Collection<OfficeData> officeData) {

		this.officeData = officeData;
	}

	public void setClientCategoryDatas(Collection<ClientCategoryData> ClientCategoryDatas) {

		this.ClientCategoryDatas = ClientCategoryDatas;
	}

	public String getOfficeType() {
		return officeType;
	}

	public void setOfficeType(String officeType) {
		this.officeType = officeType;
	}

	public String getOfficePoId() {
		return officePoId;
	}

	public void setOfficePoId(String officePoId) {
		this.officePoId = officePoId;
	}

	public String getC_active() {
		return c_active;
	}

	public void setC_active(String c_active) {
		this.c_active = c_active;
	}

	public String getC_inactive() {
		return c_inactive;
	}

	public void setC_inactive(String c_inactive) {
		this.c_inactive = c_inactive;
	}

	public Collection<MCodeData> getIdProofs() {
		return idProofs;
	}

	public void setIdProofs(Collection<MCodeData> idProofs) {
		this.idProofs = idProofs;
	}

	public String getOfficeMail() {
		return officeMail;
	}

	public void setOfficeMail(String officeMail) {
		this.officeMail = officeMail;
	}

	public String getIdKey() {
		return idKey;
	}

	public void setIdKey(String idKey) {
		this.idKey = idKey;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}

	public void setclientPoId(Long clientPoId) {
		this.clientPoId = clientPoId;

	}

	public String getClientServicePoId() {
		return clientServicePoId;
	}

	public void setClientServicePoId(String clientServicePoId) {
		this.clientServicePoId = clientServicePoId;
	}

	public String getC_instock() {
		return c_instock;
	}

	public void setC_instock(String c_instock) {
		this.c_instock = c_instock;
	}

	public String getC_allocated() {
		return c_allocated;
	}

	public void setC_allocated(String c_allocated) {
		this.c_allocated = c_allocated;
	}

	public Long getChargeCycleId() {
		return chargeCycleId;
	}

	public void setChargeCycleId(Long chargeCycleId) {
		this.chargeCycleId = chargeCycleId;
	}

	public String getChargeCycle() {
		return chargeCycle;
	}

	public void setChargeCycle(String chargeCycle) {
		this.chargeCycle = chargeCycle;
	}

	public void setOfficeHierarchy(String officeHierarchy) {
		this.officeHierarchy = officeHierarchy;
	}

	public String getSelfcarePassword() {
		return selfcarePassword;
	}

	public void setSelfcarePassword(String selfcarePassword) {
		this.selfcarePassword = selfcarePassword;
	}

	public BigDecimal getNonCurrencyAmount() {
		return nonCurrencyAmount;
	}

	public void setNonCurrencyAmount(BigDecimal nonCurrencyAmount) {
		this.nonCurrencyAmount = nonCurrencyAmount;
	}

	public List<ClientData> getClientDatas() {
		return clientDatas;
	}

	public void setClientDatas(List<ClientData> clientDatas) {
		this.clientDatas = clientDatas;
	}

	public List<Balance> getBalance() {
		return balance;
	}

	public void setBalance(List<Balance> balance) {
		this.balance = balance;
	}

}
