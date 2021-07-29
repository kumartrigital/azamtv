/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.domain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.obrm.domain.OfficeTypeEnum;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.monetary.domain.ApplicationCurrency;
import org.mifosplatform.organisation.office.exception.CannotUpdateOfficeWithParentOfficeSameAsSelf;
import org.mifosplatform.organisation.office.exception.RootOfficeParentCannotBeUpdated;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "m_office", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }, name = "name_org"),
		@UniqueConstraint(columnNames = { "external_id" }, name = "externalid_org") })
public class Office extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1760184005999519057L;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_id")
	private final List<Office> children = new LinkedList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Office parent;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "hierarchy", nullable = true, length = 50)
	private String hierarchy;

	@Column(name = "opening_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date openingDate;

	@Column(name = "office_type", nullable = false)
	private String officeType;

	@Column(name = "external_id", length = 100)
	private String externalId;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "office", orphanRemoval = true)
	private OfficeAddress officeAddress;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "office", orphanRemoval = true)
	private OfficeAdditionalInfo officeAdditionalInfo;

	@Column(name = "po_id", length = 20)
	private String poId;

	@Column(name = "settlement_poId", length = 20)
	private String settlementpoId;

	@Column(name = "pancard_no", nullable = false, length = 20)
	private String panCardNo;

	@Column(name = "company_reg_no", nullable = true, length = 60)
	private String companyRegNo;

	@Column(name = "gst_reg_no", nullable = false, length = 20)
	private String gstRegNo;

	@Column(name = "commision_model", nullable = true, length = 1)
	private int commisionModel;

	@Column(name = "Subscriber_dues", nullable = true, length = 1)
	private Integer subscriberDues;

	@Column(name = "Payment_Type", nullable = true, length = 1)
	private Character payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DAS_Type", nullable = true)
	private CodeValue dasType;

	@Column(name = "client_id", nullable = true)
	private Long clientId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "currency_id", nullable = true)
	private ApplicationCurrency CurrencyId;

	public static Office headOffice(final String name, final LocalDate openingDate, final String externalId) {
		return new Office(null, name, openingDate, externalId, null);
	}

	public static Office fromJson(final Office parentOffice, final JsonCommand command) {

		final String name = command.stringValueOfParameterNamed("name");
		final LocalDate openingDate = command.localDateValueOfParameterNamed("openingDate");
		final String externalId = command.stringValueOfParameterNamed("externalId");
		final String officeType = command.stringValueOfParameterNamed("officeType");
		final String pancardNo = command.stringValueOfParameterNamed("pancardNo");
		final String companyRegNo = command.stringValueOfParameterNamed("companyRegNo");
		final String gstRegNo = command.stringValueOfParameterNamed("gstRegNo");
		final String commisionModel = command.stringValueOfParameterNamed("commisionModel");
		final boolean subscriberDues = command.booleanPrimitiveValueOfParameterNamed("subscriberdues");
		final String paymenttype = command.stringValueOfParameterNamed("payment");
		return new Office(parentOffice, name, openingDate, externalId, officeType, pancardNo, companyRegNo, gstRegNo,
				Integer.parseInt(commisionModel), subscriberDues, paymenttype);
	}

	public static Office fromJsonUpdateCrm(final Office parentOffice, final String json) {

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			final String name = jsonObject.optString("name");
			final String openingDateString = jsonObject.optString("openingDate");
			LocalDate openingDate = null;
			if (!("".equals(openingDateString))) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				openingDate = new LocalDate(sdf.parse(openingDateString));
			}
			final String externalId = jsonObject.optString("externalId");
			final String officeType = jsonObject.optString("officeType");
			final String pancardNo = jsonObject.optString("pancardNo");
			final String companyRegNo = jsonObject.optString("companyRegNo");
			final String gstRegNo = jsonObject.optString("gstRegNo");
			final String poId = jsonObject.optString("poId");
			return new Office(parentOffice, name, openingDate, externalId, officeType, poId, pancardNo, companyRegNo,
					gstRegNo);
		} catch (JSONException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

	protected Office() {
		this.openingDate = null;
		this.parent = null;
		this.name = null;
		this.externalId = null;
	}

	public List<Office> getChildren() {
		return children;
	}

	public Office getParent() {
		return parent;
	}

	public Date getOpeningDate() {
		return openingDate;
	}

	public String getOfficeType() {
		return officeType;
	}

	public String getExternalId() {
		return externalId;
	}

	public void updateExternalId(Long count) {
		if (this.officeType.equalsIgnoreCase("MSO")) {
			this.externalId = this.officeType + "-" + count;
		} else if (this.officeType.equalsIgnoreCase("DIST")) {
			this.externalId = this.officeType + this.parent.getId() + "-" + count;
		} else if (this.officeType.equalsIgnoreCase("LCO")) {
			this.externalId = this.officeType + this.parent.getId() + "-" + count;
		}
	}

	private Office(final Office parent, final String name, final LocalDate openingDate, final String externalId,
			final String officeType) {
		this.parent = parent;
		this.openingDate = openingDate.toDateMidnight().toDate();
		if (parent != null) {
			this.parent.addChild(this);
		}

		if (StringUtils.isNotBlank(name)) {
			this.name = name.trim();
		} else {
			this.name = null;
		}
		if (StringUtils.isNotBlank(externalId)) {
			this.externalId = externalId.trim();
		} else {
			this.externalId = null;
		}

		this.officeType = officeType;
	}

	private Office(final Office parent, final String name, final LocalDate openingDate, final String externalId,
			final String officeType, String panCardNo, String companyRegNo, String gstRegNo, int commisionModel,
			boolean subscriberDues, String payment) {
		this.parent = parent;
		this.openingDate = openingDate.toDateMidnight().toDate();
		if (parent != null) {
			this.parent.addChild(this);
		}

		if (StringUtils.isNotBlank(name)) {
			this.name = name.trim();
		} else {
			this.name = null;
		}
		if (StringUtils.isNotBlank(externalId)) {
			this.externalId = externalId.trim();
		} else {
			this.externalId = null;
		}

		this.officeType = officeType;
		this.panCardNo = panCardNo;
		this.companyRegNo = companyRegNo;
		this.gstRegNo = gstRegNo;
		this.commisionModel = commisionModel;
		if (subscriberDues) {
			this.subscriberDues = 1;
		} else {
			this.subscriberDues = 0;
		}
		if (payment.equalsIgnoreCase("Advance")) {
			this.payment = '1';
		} else if (payment.equalsIgnoreCase("Arrear")) {
			this.payment = '2';
		} else if (payment.equalsIgnoreCase("Prepaid")) {
			this.payment = '3';
		}
	}

	private Office(final Office parent, final String name, final LocalDate openingDate, final String externalId,
			final String officeType, String poId, String panCardNo, String companyRegNo, String gstRegNo) {
		this.parent = parent;
		if (openingDate != null)
			this.openingDate = openingDate.toDateMidnight().toDate();
		if (parent != null) {
			this.parent.addChild(this);
		}

		if (StringUtils.isNotBlank(name)) {
			this.name = name.trim();
		} else {
			this.name = null;
		}
		if (StringUtils.isNotBlank(externalId)) {
			this.externalId = externalId.trim();
		} else {
			this.externalId = null;
		}
		this.poId = poId;
		this.officeType = officeType;
		this.panCardNo = panCardNo;
		this.companyRegNo = companyRegNo;
		this.gstRegNo = gstRegNo;
	}

	private void addChild(final Office office) {
		this.children.add(office);
	}

	public Map<String, Object> update(final JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(7);

		final String dateFormatAsInput = command.dateFormat();
		final String localeAsInput = command.locale();

		final String parentIdParamName = "parentId";

		if (command.parameterExists(parentIdParamName) && this.parent == null) {
			throw new RootOfficeParentCannotBeUpdated();
		}

		if (this.parent != null && command.isChangeInLongParameterNamed(parentIdParamName, this.parent.getId())) {
			final Long newValue = command.longValueOfParameterNamed(parentIdParamName);
			actualChanges.put(parentIdParamName, newValue);
		}

		final String openingDateParamName = "openingDate";
		if (command.isChangeInLocalDateParameterNamed(openingDateParamName, getOpeningLocalDate())) {
			final String valueAsInput = command.stringValueOfParameterNamed(openingDateParamName);
			actualChanges.put(openingDateParamName, valueAsInput);
			actualChanges.put("dateFormat", dateFormatAsInput);
			actualChanges.put("locale", localeAsInput);

			final LocalDate newValue = command.localDateValueOfParameterNamed(openingDateParamName);
			this.openingDate = newValue.toDate();
		}

		final String nameParamName = "name";
		if (command.isChangeInStringParameterNamed(nameParamName, this.name)) {
			final String newValue = command.stringValueOfParameterNamed(nameParamName);
			actualChanges.put(nameParamName, newValue);
			this.name = newValue;
		}

		final String officeTypeParam = "officeType";
		if (command.isChangeInStringParameterNamed(officeTypeParam, this.officeType)) {

			final String newValue = command.stringValueOfParameterNamed(officeTypeParam);
			actualChanges.put(officeTypeParam, newValue);
			this.officeType = StringUtils.defaultIfEmpty(newValue, null);

		}

		final String externalIdParamName = "externalId";
		if (command.isChangeInStringParameterNamed(externalIdParamName, this.externalId)) {
			final String newValue = command.stringValueOfParameterNamed(externalIdParamName);
			actualChanges.put(externalIdParamName, newValue);
			this.externalId = StringUtils.defaultIfEmpty(newValue, null);
		}

		final String partnernameParamName = "partnerName";
		if (command.isChangeInStringParameterNamed(partnernameParamName, this.name)) {
			final String newValue = command.stringValueOfParameterNamed(partnernameParamName);
			actualChanges.put(partnernameParamName, newValue);
			this.name = newValue;
		}

		final String pancardNonameParamName = "pancardNo";
		if (command.isChangeInStringParameterNamed(pancardNonameParamName, this.panCardNo)) {
			final String newValue = command.stringValueOfParameterNamed(pancardNonameParamName);
			actualChanges.put(pancardNonameParamName, newValue);
			this.panCardNo = newValue;
		}

		final String companyRegNonameParamName = "companyRegNo";
		if (command.isChangeInStringParameterNamed(companyRegNonameParamName, this.companyRegNo)) {
			final String newValue = command.stringValueOfParameterNamed(companyRegNonameParamName);
			actualChanges.put(companyRegNonameParamName, newValue);
			this.companyRegNo = newValue;
		}

		final String commisionModelnameParamName = "commisionModel";
		if (command.isChangeInIntegerParameterNamed(commisionModelnameParamName, this.commisionModel)) {
			final int newValue = command.integerValueOfParameterNamed(commisionModelnameParamName);
			actualChanges.put(commisionModelnameParamName, newValue);
			this.commisionModel = newValue;
		}

		final String gstRegNonameParamName = "gstRegNo";
		if (command.isChangeInStringParameterNamed(gstRegNonameParamName, this.gstRegNo)) {
			final String newValue = command.stringValueOfParameterNamed(gstRegNonameParamName);
			actualChanges.put(gstRegNonameParamName, newValue);
			this.gstRegNo = newValue;
		}
		if (command.hasParameter("payment")) {
			if (this.payment == null) {
				this.payment = '1';
			}
			final String paymentParamName = "payment";
			String paymentString = null;
			if (this.payment == '1') {
				paymentString = "Advance";
			} else if (this.payment == '2') {
				paymentString = "Arrear";
			} else if (this.payment == '3') {
				paymentString = "Prepaid";
			}
			if (command.isChangeInStringParameterNamed(paymentParamName, paymentString)) {

				final String newValue = command.stringValueOfParameterNamed(paymentParamName);
				actualChanges.put(paymentParamName, newValue);
				if (newValue.equalsIgnoreCase("Advance")) {
					this.payment = '1';
				} else if (newValue.equalsIgnoreCase("Arrear")) {
					this.payment = '2';
				} else if (newValue.equalsIgnoreCase("Prepaid")) {
					this.payment = '3';
				}

			}
		}

		final String subscriberduesParamName = "subscriberdues";
		boolean subscriberduesboolean = false;
		if (this.subscriberDues == 1) {
			subscriberduesboolean = true;
		} else {
			subscriberduesboolean = false;
		}
		if (command.isChangeInBooleanParameterNamed(subscriberduesParamName, subscriberduesboolean)) {

			final boolean newValue = command.booleanPrimitiveValueOfParameterNamed(subscriberduesParamName);
			actualChanges.put(subscriberduesParamName, newValue);

			if (newValue) {
				this.subscriberDues = 1;
			} else {
				this.subscriberDues = 0;
			}

		}

		final String dasTypeParamName = "dasType";
		if (this.dasType != null && command.isChangeInLongParameterNamed(dasTypeParamName, this.dasType.getId())) {
			final Long newValue = command.longValueOfParameterNamed(dasTypeParamName);
			actualChanges.put(dasTypeParamName, newValue);
		}

		return actualChanges;
	}

	public boolean isOpeningDateBefore(final LocalDate baseDate) {
		return getOpeningLocalDate().isBefore(baseDate);
	}

	public boolean isOpeningDateAfter(final LocalDate activationLocalDate) {
		return getOpeningLocalDate().isAfter(activationLocalDate);
	}

	private LocalDate getOpeningLocalDate() {
		LocalDate openingLocalDate = null;
		if (this.openingDate != null) {
			openingLocalDate = LocalDate.fromDateFields(this.openingDate);
		} else {
			System.out.println("Hello");
		}
		return openingLocalDate;
	}

	public void update(final Office newParent) {

		if (this.parent == null) {
			throw new RootOfficeParentCannotBeUpdated();
		}

		if (this.identifiedBy(newParent.getId())) {
			throw new CannotUpdateOfficeWithParentOfficeSameAsSelf(this.getId(), newParent.getId());
		}

		this.parent = newParent;
		generateHierarchy();
	}

	public boolean identifiedBy(final Long id) {
		return getId().equals(id);
	}

	public void generateHierarchy() {

		if (parent != null) {
			this.hierarchy = this.parent.hierarchyOf(getId());
		} else {
			this.hierarchy = ".";
		}
	}

	private String hierarchyOf(final Long id) {
		return this.hierarchy + id.toString() + ".";
	}

	public String getName() {
		return this.name;
	}

	public String getHierarchy() {
		return hierarchy;
	}

	public boolean hasParentOf(final Office office) {
		boolean isParent = false;
		if (this.parent != null) {
			isParent = this.parent.equals(office);
		}
		return isParent;
	}

	public boolean doesNotHaveAnOfficeInHierarchyWithId(final Long officeId) {
		return !this.hasAnOfficeInHierarchyWithId(officeId);
	}

	private boolean hasAnOfficeInHierarchyWithId(final Long officeId) {

		boolean match = false;

		if (identifiedBy(officeId)) {
			match = true;
		}

		if (!match) {
			List<Office> children = this.getChildren();
			for (final Office child : children) {
				final boolean result = child.hasAnOfficeInHierarchyWithId(officeId);

				if (result) {
					match = result;
					break;
				}
			}
		}

		return match;
	}

	public static Office fromPartner(final Office parentOffice, final JsonCommand command) {

		final String name = command.stringValueOfParameterNamed("partnerName");
		final LocalDate openingDate = DateUtils.getLocalDateOfTenant();
		final String externalId = command.stringValueOfParameterNamed("externalId");
		final String officeType = command.stringValueOfParameterNamed("officeType");
		return new Office(parentOffice, name, openingDate, externalId, officeType);
	}

	public OfficeAddress getOfficeAddress() {
		return officeAddress;
	}

	public void setParent(Office parent) {
		this.parent = parent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}

	public void setOfficeType(String officeType) {
		this.officeType = officeType;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setOfficeAddress(OfficeAddress officeAddress) {
		this.officeAddress = officeAddress;
	}

	public OfficeAdditionalInfo getOfficeAdditionalInfo() {
		return officeAdditionalInfo;
	}

	public void setOfficeAdditionalInfo(OfficeAdditionalInfo officeAdditionalInfo) {
		this.officeAdditionalInfo = officeAdditionalInfo;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}

	public String getSettlementPoId() {
		return settlementpoId;
	}

	public void setSettlementPoId(String settlementPoId) {
		this.settlementpoId = settlementPoId;
	}

	public String getPanCardNo() {
		return panCardNo;
	}

	public void setPanCardNo(String panCardNo) {
		this.panCardNo = panCardNo;
	}

	public String getCompanyRegNo() {
		return companyRegNo;
	}

	public void setCompanyRegNo(String companyRegNo) {
		this.companyRegNo = companyRegNo;
	}

	public String getGstRegNo() {
		return gstRegNo;
	}

	public void setGstRegNo(String gstRegNo) {
		this.gstRegNo = gstRegNo;
	}

	public int getCommisionModel() {
		return commisionModel;
	}

	public void setCommisionModel(int commisionModel) {
		this.commisionModel = commisionModel;
	}

	public CodeValue getDasType() {
		return dasType;
	}

	public void setDasType(CodeValue dasType) {
		this.dasType = dasType;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String obrmRequestInput() {

		final Long accountNo = Calendar.getInstance().getTimeInMillis();

		StringBuilder sb = new StringBuilder("<COB_OP_CUST_CREATE_COMPANY_inputFlist>");
		sb.append("<poId>0.0.0.1 /account -1 0</poId>");
		sb.append("<PROGRAM_NAME>CRM|COBADMIN</PROGRAM_NAME>");
		sb.append("<LOGIN>" + accountNo + "</LOGIN>");
		sb.append("<PASSWD_CLEAR>" + accountNo + "</PASSWD_CLEAR>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("<NAMEINFO elem=\"1\">");
		sb.append("<LAST_NAME>" + this.name + "</LAST_NAME>");
		sb.append("<MIDDLE_NAME></MIDDLE_NAME>");
		sb.append("<FIRST_NAME>" + this.name + "</FIRST_NAME>");
		sb.append("<SALUTATION>M/s.</SALUTATION>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>" + this.officeAddress.getPhoneNumber() + "</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<EMAIL_ADDR>" + this.officeAddress.getEmail() + "</EMAIL_ADDR>");
		sb.append("<COUNTRY>" + this.officeAddress.getCountry() + "</COUNTRY>");
		sb.append("<ZIP>" + this.officeAddress.getzip() + "</ZIP>");
		sb.append("<STATE>" + this.officeAddress.getState() + "</STATE>");
		sb.append("<CITY>" + this.officeAddress.getCity() + "</CITY>");
		sb.append("<ADDRESS>" + this.officeAddress.getAddressName() + "</ADDRESS>");
		sb.append("<COB_FLD_AREA>" + this.officeAddress.getAddressName() + "</COB_FLD_AREA>");
		sb.append("<COB_FLD_LOCATION>" + this.officeAddress.getCity() + "</COB_FLD_LOCATION>");
		sb.append("<COB_FLD_STREET>" + this.officeAddress.getAddressName() + "</COB_FLD_STREET>");
		sb.append("<COB_FLD_BUILDING>" + this.officeAddress.getAddressName() + "</COB_FLD_BUILDING>");
		sb.append("<COB_FLD_LANDMARK>" + this.officeAddress.getAddressName() + "</COB_FLD_LANDMARK>");
		sb.append("<COB_FLD_DISTRICT>" + this.officeAddress.getCity() + "</COB_FLD_DISTRICT>");
		sb.append("</NAMEINFO>");
		sb.append("<ACCTINFO elem=\"0\">");
		sb.append("<poId>0.0.0.1 /account -1 0</poId>");
		sb.append("<BUSINESS_TYPE>" + OfficeTypeEnum.getValue(this.officeType.toString()) + "</BUSINESS_TYPE>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<AAC_SOURCE></AAC_SOURCE>");
		sb.append("<ACCOUNT_NO>" + accountNo + "</ACCOUNT_NO>");
		sb.append("<BAL_INFO elem=\"0\">");
		sb.append("</BAL_INFO>");
		sb.append("</ACCTINFO>");
		sb.append("<PROFILES elem=\"0\">");
		sb.append("<INHERITED_INFO>");
		sb.append("<COB_FLD_BUSINESS_EXT>");
		sb.append("<COB_FLD_REG_NO>1223345</COB_FLD_REG_NO>");
		sb.append("<COB_FLD_RELATION_TYPE>0</COB_FLD_RELATION_TYPE>");
		sb.append("<COB_FLD_DAS_TYPE>DAS-I</COB_FLD_DAS_TYPE>");
		sb.append("<COB_FLD_ERP_ACCT_ID>ERP-0001</COB_FLD_ERP_ACCT_ID>");
		sb.append("<COB_FLD_AGREEMENT_START>1523464544</COB_FLD_AGREEMENT_START>");
		sb.append("<COB_FLD_AGREEMENT_END>1523464544</COB_FLD_AGREEMENT_END>");
		sb.append("<COB_FLD_AGREEMENT_ID>AGR-0001</COB_FLD_AGREEMENT_ID>");
		sb.append("<COB_FLD_VAT_TAX_NO>VAT-0001</COB_FLD_VAT_TAX_NO>");
		sb.append("<COB_FLD_ENT_TAX_NO>ENT-001</COB_FLD_ENT_TAX_NO>");
		sb.append("<COB_FLD_PAN_NO>DIQPS8859L</COB_FLD_PAN_NO>");
		sb.append("<COB_FLD_ST_REG_NO>ST-001</COB_FLD_ST_REG_NO>");
		sb.append("<COB_FLD_CIN_NO>CIN093298390</COB_FLD_CIN_NO>");
		sb.append("<COB_FLD_TIN_NO>TIN0890023</COB_FLD_TIN_NO>");
		sb.append("<COB_FLD_GST_REG_NO>GSTIN032342908</COB_FLD_GST_REG_NO>");
		if (this.parent.getId() != 1) {
			sb.append("<PARENT>0.0.0.1 /account " + this.parent.getPoId() + " 0</PARENT>");
		}
		sb.append("<COB_FLD_COMMISSION_MODEL>1</COB_FLD_COMMISSION_MODEL>");
		sb.append("<COB_FLD_RMAIL>" + this.officeAddress.getEmail() + "</COB_FLD_RMAIL>");
		sb.append("<COB_FLD_RMN>" + this.officeAddress.getPhoneNumber() + "</COB_FLD_RMN>");
		sb.append("<COB_FLD_CONTACT_PREF>1</COB_FLD_CONTACT_PREF>");
		sb.append("</COB_FLD_BUSINESS_EXT>");
		sb.append("</INHERITED_INFO>");
		sb.append("<PROFILE_OBJ>0.0.0.1 /profile/business_ext -1 0</PROFILE_OBJ>");
		sb.append("</PROFILES>");
		sb.append("</COB_OP_CUST_CREATE_COMPANY_inputFlist>");

		System.out.println(sb.toString());

		/*
		 * StringBuilder sb = new
		 * StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		 * ); sb.append("<COB_OP_CUST_CREATE_COMPANY_inputFlist>");
		 * sb.append("<poId>0.0.0.1 /account -1 0</poId>");
		 * sb.append("<PROGRAM_NAME>CRM|COBADMIN</PROGRAM_NAME>");
		 * sb.append("<LOGIN>COBAPPMSO1</LOGIN>");
		 * sb.append("<PASSWD_CLEAR>COBAPPMSO1</PASSWD_CLEAR>");
		 * sb.append("<FLAGS>0</FLAGS><NAMEINFO elem=\"1\">");
		 * sb.append("<LAST_NAME>"+this.name+
		 * "</LAST_NAME><MIDDLE_NAME></MIDDLE_NAME><FIRST_NAME>"+this.name+
		 * "</FIRST_NAME>"); sb.append("<SALUTATION>Mr.</SALUTATION>");
		 * sb.append("<PHONES elem=\"5\"><PHONE>"+this.officeAddress.getPhoneNumber()+
		 * "</PHONE><TYPE>5</TYPE></PHONES>");
		 * sb.append("<EMAIL_ADDR>"+this.officeAddress.getEmail()+"</EMAIL_ADDR>");
		 * sb.append("<COUNTRY>"+this.officeAddress.getCountry()+"</COUNTRY>");
		 * sb.append("<ZIP>"+this.officeAddress.getzip()+"</ZIP>");
		 * sb.append("<STATE>"+this.officeAddress.getState()+"</STATE><CITY>"+this.
		 * officeAddress.getCity()+"</CITY>");
		 * sb.append("<ADDRESS>"+this.officeAddress.getAddressName()+
		 * "</ADDRESS></NAMEINFO>");
		 * sb.append("<ACCTINFO elem=\"0\"><poId>0.0.0.1 /account -1 0</poId>");
		 * sb.append("<BUSINESS_TYPE>31000000</BUSINESS_TYPE>");
		 * sb.append("<CURRENCY>INR</CURRENCY><AAC_SOURCE></AAC_SOURCE>");
		 * sb.append("<ACCOUNT_NO>"+Calendar.getInstance().getTimeInMillis()+
		 * "</ACCOUNT_NO>"); sb.append("<BAL_INFO elem=\"0\"></BAL_INFO></ACCTINFO>");
		 * sb.
		 * append("<PROFILES elem=\"0\"><INHERITED_INFO><BUSINESS_EXT><REG_NO>1223345</REG_NO>"
		 * ); sb.append("<RELATION_TYPE>0</RELATION_TYPE><DAS_TYPE>DAS-I</DAS_TYPE>");
		 * sb.append(
		 * "<ERP_ACCT_ID>ERP-0001</ERP_ACCT_ID><AGREEMENT_START>1523464544</AGREEMENT_START>"
		 * ); sb.append(
		 * "<AGREEMENT_END>1523464544</AGREEMENT_END><AGREEMENT_ID>AGR-0001</AGREEMENT_ID>"
		 * );
		 * sb.append("<VAT_TAX_NO>VAT-0001</VAT_TAX_NO><ENT_TAX_NO>ENT-001</ENT_TAX_NO>"
		 * ); sb.append(
		 * "<PAN_NO>DIQPS8859L</PAN_NO><ST_REG_NO>ST-001</ST_REG_NO><CIN_NO>CIN093298390</CIN_NO><TIN_NO>TIN0890023</TIN_NO>"
		 * ); sb.append(
		 * "<GST_REG_NO>GSTIN032342908</GST_REG_NO><COMMISSION_MODEL>1</COMMISSION_MODEL></BUSINESS_EXT>"
		 * ); sb.append("<NAMEINFO elem=\"1\"><RMAIL>"+this.officeAddress.getEmail()+
		 * "</RMAIL><RMN>"+this.officeAddress.getPhoneNumber()+"</RMN>");
		 * sb.append("<CONTACT_PREF>1</CONTACT_PREF><AREA>HARLUR</AREA>");
		 * sb.append("<LOCATION>"+this.officeAddress.getCity()+"</LOCATION><STREET>"+
		 * this.officeAddress.getState()+"</STREET><BUILDING>ICICI BUILDING</BUILDING>"
		 * ); sb.append("<LANDMARK>ICICI ATM</LANDMARK></NAMEINFO>"); sb.
		 * append("</INHERITED_INFO><PROFILE_OBJ>0.0.0.1 /profile/business_ext -1 0</PROFILE_OBJ></PROFILES>"
		 * ); sb.append("</COB_OP_CUST_CREATE_COMPANY_inputFlist>");
		 */

		/*
		 * StringBuilder sb = new
		 * StringBuilder("<MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");
		 * sb.append("<MSO_FLD_CATV_ACCOUNT_OBJ/>"); sb.append("<MSO_FLD_ROLES/>");
		 * sb.append("<ACCTINFO elem=\"0\">");
		 * sb.append("<MSO_FLD_AREA></MSO_FLD_AREA>");
		 * sb.append("<MSO_FLD_CONTACT_PREF>0</MSO_FLD_CONTACT_PREF>");
		 * sb.append("<MSO_FLD_ET_ZONE/>");
		 * sb.append("<MSO_FLD_RMAIL>"+this.officeAddress.getEmail()+"</MSO_FLD_RMAIL>")
		 * ; sb.append("<MSO_FLD_RMN>"+this.officeAddress.getPhoneNumber()+
		 * "</MSO_FLD_RMN>"); sb.append("<MSO_FLD_VAT_ZONE/>");
		 * sb.append("<BAL_INFO elem=\"0\"/>");
		 * sb.append("<BUSINESS_TYPE>13000000</BUSINESS_TYPE>");
		 * sb.append("<CURRENCY>356</CURRENCY>");
		 * sb.append("<poId>0.0.0.1 /account -1 0</poId>"); sb.append("</ACCTINFO>");
		 * sb.append("<FLAGS>0</FLAGS>");
		 * sb.append("<LOGIN>"+this.officeAddress.getPhoneNumber()+"</LOGIN>");
		 * sb.append("<NAMEINFO elem=\"1\">");
		 * sb.append("<MSO_FLD_AREA_NAME>HighTechCity - Madhapur</MSO_FLD_AREA_NAME>");
		 * sb.append("<MSO_FLD_BUILDING_NAME>sapphire</MSO_FLD_BUILDING_NAME>");
		 * sb.append("<MSO_FLD_DISTRICT_NAME>Hyderabad</MSO_FLD_DISTRICT_NAME>");
		 * sb.append("<MSO_FLD_LANDMARK>PNB</MSO_FLD_LANDMARK>");
		 * sb.append("<MSO_FLD_LOCATION_NAME>loc</MSO_FLD_LOCATION_NAME>");
		 * sb.append("<MSO_FLD_STREET_NAME>21 North</MSO_FLD_STREET_NAME>");
		 * sb.append("<ADDRESS>32</ADDRESS>");
		 * sb.append("<CITY>"+this.officeAddress.getCity()+"</CITY>");
		 * sb.append("<COMPANY>"+this.name+"+</COMPANY>");
		 * sb.append("<COUNTRY>"+this.officeAddress.getCountry()+"</COUNTRY>");
		 * sb.append("<EMAIL_ADDR>"+this.officeAddress.getEmail()+"</EMAIL_ADDR>");
		 * sb.append("<FIRST_NAME>test</FIRST_NAME>"); sb.append("<LASTSTAT_CMNT/>");
		 * sb.append("<LASTSTAT_CMNT/>"); sb.append("<LAST_NAME>one</LAST_NAME>");
		 * sb.append("<MIDDLE_NAME/>"); sb.append("<PHONES elem=\"1\">");
		 * sb.append("<PHONE>"+this.officeAddress.getPhoneNumber()+"</PHONE>");
		 * sb.append("<TYPE>1</TYPE>"); sb.append("</PHONES>");
		 * sb.append("<PHONES elem=\"5\">");
		 * sb.append("<PHONE>"+this.officeAddress.getPhoneNumber()+"</PHONE>");
		 * sb.append("<TYPE>5</TYPE>"); sb.append("</PHONES>");
		 * sb.append("<PHONES elem=\"2\">");
		 * sb.append("<PHONE>"+this.officeAddress.getPhoneNumber()+"</PHONE>");
		 * sb.append("<TYPE>2</TYPE>"); sb.append("</PHONES>");
		 * sb.append("<SALUTATION>Mr.</SALUTATION>");
		 * sb.append("<STATE>"+this.officeAddress.getState()+"</STATE>");
		 * sb.append("<ZIP>"+this.officeAddress.getzip()+"</ZIP>");
		 * sb.append("</NAMEINFO>"); sb.append("<PASSWD_CLEAR>XXXX</PASSWD_CLEAR>");
		 * sb.append("<poId>0.0.0.1 /plan -1 0</poId>");
		 * sb.append("<PROFILES elem=\"0\">"); sb.append("<INHERITED_INFO>");
		 * sb.append("<MSO_FLD_WHOLESALE_INFO>");
		 * sb.append("<MSO_FLD_AGREEMENT_NO>LCO-AGR-001</MSO_FLD_AGREEMENT_NO>");
		 * sb.append("<MSO_FLD_COMMISION_MODEL>0</MSO_FLD_COMMISION_MODEL>");
		 * sb.append("<MSO_FLD_COMMISION_SERVICE>1</MSO_FLD_COMMISION_SERVICE>");
		 * sb.append("<MSO_FLD_DAS_TYPE>DAS-I</MSO_FLD_DAS_TYPE>");
		 * sb.append("<MSO_FLD_ENT_TAX_NO>E001</MSO_FLD_ENT_TAX_NO>");
		 * sb.append("<MSO_FLD_ERP_CONTROL_ACCT_ID>1972</MSO_FLD_ERP_CONTROL_ACCT_ID>");
		 * sb.append("<MSO_FLD_FROM_DATE>1519669800</MSO_FLD_FROM_DATE>");
		 * sb.append("<MSO_FLD_PAN_NO>ASDFG1872A</MSO_FLD_PAN_NO>");
		 * sb.append("<MSO_FLD_POSTAL_REG_NO>PR001</MSO_FLD_POSTAL_REG_NO>");
		 * sb.append("<MSO_FLD_PP_TYPE>0</MSO_FLD_PP_TYPE>");
		 * sb.append("<MSO_FLD_PREF_DOM>1</MSO_FLD_PREF_DOM>");
		 * sb.append("<MSO_FLD_ST_REG_NO>S001</MSO_FLD_ST_REG_NO>");
		 * sb.append("<MSO_FLD_TO_DATE>1546194600</MSO_FLD_TO_DATE>");
		 * sb.append("<MSO_FLD_VAT_TAX_NO>V001</MSO_FLD_VAT_TAX_NO>");
		 * sb.append("<PARENT>0.0.0.1 /account "+this.parent.getpoId()+" 7</PARENT>");
		 * sb.append("</MSO_FLD_WHOLESALE_INFO>"); sb.append("</INHERITED_INFO>");
		 * sb.append("<PROFILE_OBJ>0.0.0.1 /profile/wholesale -1 0</PROFILE_OBJ>");
		 * sb.append("</PROFILES>");
		 * sb.append("<PROGRAM_NAME>OAP|testcsrone</PROGRAM_NAME>");
		 * sb.append("<USERID>0.0.0.1 /account 452699 0</USERID>");
		 * sb.append("</MSO_OP_CUST_REGISTER_CUSTOMER_inputFlist>");
		 */

		return sb.toString();
	}

	public String celcomRequestInput(String userName) {

		String relationType = "Primary Points";
		if (relationType.equals(this.officeAddress.getbusinessType())) {
			relationType = "0";
		} else {
			relationType = "1";
		}
		char paymentBusinnesType = this.payment;
		if (officeType.equalsIgnoreCase("MSO") || officeType.equalsIgnoreCase("DIST")) {
			paymentBusinnesType = '0';
		}
		int officetype = OfficeTypeEnum.getValue(this.officeType.toString());
		int businessType = officetype + Integer.parseInt(relationType + paymentBusinnesType);
		// final Long accountNo = Calendar.getInstance().getTimeInMillis();

		StringBuilder sb = new StringBuilder("<COB_OP_CUST_CREATE_COMPANY_inputFlist>");
		sb.append("<poId>0.0.0.1 /account -1 0</poId>");
		sb.append("<PROGRAM_NAME>CRM|" + userName + "</PROGRAM_NAME>");
		sb.append("<LOGIN>" + this.externalId + "</LOGIN>");
		sb.append("<PASSWD_CLEAR>" + this.externalId + "</PASSWD_CLEAR>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("<NAMEINFO elem=\"1\">");
		sb.append("<LAST_NAME>" + this.name + "</LAST_NAME>");
		sb.append("<MIDDLE_NAME></MIDDLE_NAME>");
		sb.append("<FIRST_NAME>" + this.name + "</FIRST_NAME>");
		sb.append("<SALUTATION>M/s.</SALUTATION>");
		sb.append("<COMPANY>" + this.name + "</COMPANY>");
		sb.append("<PHONES elem=\"5\">");
		sb.append("<PHONE>" + this.officeAddress.getPhoneNumber() + "</PHONE>");
		sb.append("<TYPE>5</TYPE>");
		sb.append("</PHONES>");
		sb.append("<EMAIL_ADDR>" + this.officeAddress.getEmail() + "</EMAIL_ADDR>");
		sb.append("<COUNTRY>" + this.officeAddress.getCountry() + "</COUNTRY>");
		sb.append("<ZIP>" + this.officeAddress.getzip() + "</ZIP>");
		sb.append("<STATE>" + this.officeAddress.getState() + "</STATE>");
		sb.append("<CITY>" + this.officeAddress.getCity() + "</CITY>");
		sb.append("<ADDRESS>" + this.officeAddress.getAddressName() + "</ADDRESS>");
		sb.append("<COB_FLD_AREA>" + this.officeAddress.getAddressName() + "</COB_FLD_AREA>");
		sb.append("<COB_FLD_LOCATION></COB_FLD_LOCATION>");
		sb.append("<COB_FLD_STREET></COB_FLD_STREET>");
		sb.append("<COB_FLD_BUILDING></COB_FLD_BUILDING>");
		sb.append("<COB_FLD_LANDMARK></COB_FLD_LANDMARK>");
		sb.append("<COB_FLD_DISTRICT>" + this.officeAddress.getDistrict() + "</COB_FLD_DISTRICT>");
		sb.append("</NAMEINFO>");
		sb.append("<ACCTINFO elem=\"0\">");
		sb.append("<poId>0.0.0.1 /account -1 0</poId>");
		sb.append("<BUSINESS_TYPE>" + businessType + "</BUSINESS_TYPE>");
		sb.append("<CURRENCY>356</CURRENCY>");
		sb.append("<AAC_SOURCE></AAC_SOURCE>");
		sb.append("<ACCOUNT_NO>" + this.externalId + "</ACCOUNT_NO>");
		sb.append("<BAL_INFO elem=\"0\">");
		sb.append("</BAL_INFO>");
		sb.append("</ACCTINFO>");
		sb.append("<PROFILES elem=\"0\">");
		sb.append("<INHERITED_INFO>");
		sb.append("<COB_FLD_BUSINESS_EXT>");
		sb.append("<COB_FLD_REG_NO>1223345</COB_FLD_REG_NO>");
		sb.append("<COB_FLD_RELATION_TYPE>" + relationType + "</COB_FLD_RELATION_TYPE>");
		sb.append("<COB_FLD_DAS_TYPE>" + this.dasType.getLabel() + "</COB_FLD_DAS_TYPE>");
		sb.append("<COB_FLD_ERP_ACCT_ID>ERP-0001</COB_FLD_ERP_ACCT_ID>");
		sb.append("<COB_FLD_AGREEMENT_START>" + (System.currentTimeMillis()) / 1000 + "</COB_FLD_AGREEMENT_START>");
		sb.append("<COB_FLD_AGREEMENT_END>10445221800</COB_FLD_AGREEMENT_END>");// 31 Dec 2300
		sb.append("<COB_FLD_AGREEMENT_ID>AGR-0001</COB_FLD_AGREEMENT_ID>");
		sb.append("<COB_FLD_VAT_TAX_NO>VAT-0001</COB_FLD_VAT_TAX_NO>");
		sb.append("<COB_FLD_ENT_TAX_NO>ENT-001</COB_FLD_ENT_TAX_NO>");
		sb.append("<COB_FLD_PAN_NO>" + this.panCardNo + "</COB_FLD_PAN_NO>");
		sb.append("<COB_FLD_ST_REG_NO>ST-001</COB_FLD_ST_REG_NO>");
		sb.append("<COB_FLD_CIN_NO>" + this.companyRegNo + "</COB_FLD_CIN_NO>");
		sb.append("<COB_FLD_TIN_NO>TIN0890023</COB_FLD_TIN_NO>");
		sb.append("<COB_FLD_GST_REG_NO>" + this.gstRegNo + "</COB_FLD_GST_REG_NO>");
		if (this.parent.getId() != 1) {
			sb.append("<PARENT>0.0.0.1 /account " + this.parent.getPoId() + " 0</PARENT>");
		}
		sb.append("<COB_FLD_COMMISSION_MODEL>" + this.commisionModel + "</COB_FLD_COMMISSION_MODEL>");
		sb.append("<COB_FLD_CUST_PAY_TYPE>" + this.payment + "</COB_FLD_CUST_PAY_TYPE>");
		sb.append("<COB_FLD_ON_BEHALF_INV>" + this.subscriberDues + "</COB_FLD_ON_BEHALF_INV>");
		sb.append("<COB_FLD_RMAIL>" + this.officeAddress.getEmail() + "</COB_FLD_RMAIL>");
		sb.append("<COB_FLD_RMN>" + this.officeAddress.getPhoneNumber() + "</COB_FLD_RMN>");
		sb.append("<COB_FLD_CONTACT_PREF>1</COB_FLD_CONTACT_PREF>");
		sb.append("</COB_FLD_BUSINESS_EXT>");
		sb.append("</INHERITED_INFO>");
		sb.append("<PROFILE_OBJ>0.0.0.1 /profile/business_ext -1 0</PROFILE_OBJ>");
		sb.append("</PROFILES>");
		sb.append("</COB_OP_CUST_CREATE_COMPANY_inputFlist>");

		System.out.println(sb.toString());

		return sb.toString();
	}

	public String celcomRequestInputUpdateCrm(String userName, Map<String, Object> changes,
			Map<String, Object> addressChanges, String officeMovement) {

		Map<String, Object> businessProfileMap = new HashMap<String, Object>();
		if (changes.containsKey("businessType")) {
			businessProfileMap.put("businessType", changes.get("businessType"));
			changes.remove("businessType");
		}
		if (changes.containsKey("parentId")) {
			businessProfileMap.put("parentId", changes.get("parentId"));
			changes.remove("parentId");
		}
		if (changes.containsKey("pancardNo")) {
			businessProfileMap.put("pancardNo", changes.get("pancardNo"));
			changes.remove("pancardNo");
		}
		if (changes.containsKey("companyRegNo")) {
			businessProfileMap.put("companyRegNo", changes.get("companyRegNo"));
			changes.remove("companyRegNo");
		}
		if (changes.containsKey("gstRegNo")) {
			businessProfileMap.put("gstRegNo", changes.get("gstRegNo"));
			changes.remove("gstRegNo");
		}
		if (changes.containsKey("commisionModel")) {
			businessProfileMap.put("commisionModel", changes.get("commisionModel"));
			changes.remove("commisionModel");
		}
		String relationType = "Primary Points";
		if (relationType.equals(this.officeAddress.getbusinessType())) {
			relationType = "0";
		} else {
			relationType = "1";
		}
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_UPDATE_CUSTOMER_inputFlist>");
		sb.append("<poId>0.0.0.1 /account " + this.poId + " 0</poId>");
		sb.append("<PROGRAM_NAME>CRM|" + userName + "</PROGRAM_NAME>");
		if (!(changes.isEmpty()) || (!addressChanges.isEmpty())) {
			sb.append("<NAMEINFO elem=\"1\">");
			sb.append("<LAST_NAME>" + this.name + "</LAST_NAME>");
			sb.append("<FIRST_NAME>" + this.name + "</FIRST_NAME>");
			sb.append("<PHONES elem=\"5\">");
			sb.append("<PHONE>" + this.officeAddress.getPhoneNumber() + "</PHONE>");
			sb.append(" <TYPE>5</TYPE>");
			sb.append("</PHONES>");
			sb.append("<EMAIL_ADDR>" + this.officeAddress.getEmail() + "</EMAIL_ADDR>");
			sb.append("<COUNTRY>" + this.officeAddress.getCountry() + "</COUNTRY>");
			sb.append("<ZIP>" + this.officeAddress.getzip() + "</ZIP>");
			sb.append("<STATE>" + this.officeAddress.getState() + "</STATE>");
			sb.append("<CITY>" + this.officeAddress.getCity() + "</CITY>");
			sb.append("<ADDRESS>" + this.officeAddress.getAddressName() + "</ADDRESS>");
			sb.append("<COB_FLD_AREA>" + this.officeAddress.getAddressName() + "</COB_FLD_AREA>");
			sb.append("<COB_FLD_DISTRICT>" + this.officeAddress.getDistrict() + "</COB_FLD_DISTRICT>");
			sb.append("</NAMEINFO>");
		}

		/*
		 * sb.append("<NAMEINFO elem=\"2\">");
		 * sb.append("<LAST_NAME>"+this.name+"</LAST_NAME>");
		 * sb.append("<MIDDLE_NAME></MIDDLE_NAME>");
		 * sb.append("<FIRST_NAME>"+this.name+"</FIRST_NAME>");
		 * sb.append("<PHONES elem=\"5\">");
		 * sb.append("<PHONE>"+this.officeAddress.getPhoneNumber()+"</PHONE>");
		 * sb.append("<TYPE>5</TYPE>"); sb.append("</PHONES>");
		 * sb.append("<EMAIL_ADDR>"+this.officeAddress.getEmail()+"</EMAIL_ADDR>");
		 * sb.append("<COUNTRY>"+this.officeAddress.getCountry()+"</COUNTRY>");
		 * sb.append("<ZIP>"+this.officeAddress.getzip()+"</ZIP>");
		 * sb.append("<STATE>"+this.officeAddress.getState()+"</STATE>");
		 * sb.append("<CITY>"+this.officeAddress.getCity()+"</CITY>");
		 * sb.append("<ADDRESS>"+this.officeAddress.getAddressName()+"</ADDRESS>");
		 * sb.append("<COB_FLD_AREA>"+this.officeAddress.getAddressName()+
		 * "</COB_FLD_AREA>");
		 * sb.append("<COB_FLD_DISTRICT>"+this.officeAddress.getDistrict()+
		 * "</COB_FLD_DISTRICT>"); sb.append("</NAMEINFO>");
		 */
		if (!(businessProfileMap.isEmpty())) {
			sb.append("<PROFILES elem=\"0\">");
			sb.append("<INHERITED_INFO>");
			sb.append("<COB_FLD_BUSINESS_EXT>");
			if (businessProfileMap.containsKey("businessType")) {
				sb.append("<COB_FLD_RELATION_TYPE>" + relationType + "</COB_FLD_RELATION_TYPE>");
			}
			if (businessProfileMap.containsKey("pancardNo")) {
				sb.append("<COB_FLD_PAN_NO>" + this.panCardNo + "</COB_FLD_PAN_NO>");
			}
			if (businessProfileMap.containsKey("companyRegNo")) {
				sb.append("<COB_FLD_CIN_NO>" + this.companyRegNo + "</COB_FLD_CIN_NO>");
			}
			if (businessProfileMap.containsKey("gstRegNo")) {
				sb.append("<COB_FLD_GST_REG_NO>" + this.gstRegNo + "</COB_FLD_GST_REG_NO>");
			}
			if (businessProfileMap.containsKey("parentId")) {
				sb.append("<PARENT>0.0.0.1 /account " + this.parent.getPoId() + " 0</PARENT>");
				if (officeMovement.equals("1")) {
					sb.append("<COB_FLD_FRANCHISE_OBJ>0.0.0.1 /account " + this.parent.getPoId()
							+ " 0</COB_FLD_FRANCHISE_OBJ> ");
				} else if (officeMovement.equals("2")) {
					sb.append("<COB_FLD_MSO_OBJ>0.0.0.1 /account " + this.parent.getPoId() + " 0</COB_FLD_MSO_OBJ>");
				} else if (officeMovement.equals("3")) {
					sb.append("<COB_FLD_FRANCHISE_OBJ>0.0.0.1 /account " + this.parent.getPoId()
							+ " 0</COB_FLD_FRANCHISE_OBJ>");
				}
			}
			if (businessProfileMap.containsKey("commisionModel")) {
				sb.append("<COB_FLD_COMMISSION_MODEL>" + this.commisionModel + "</COB_FLD_COMMISSION_MODEL>");
			}
			/*
			 * sb.append("<COB_FLD_RMAIL>"+this.officeAddress.getEmail()+"</COB_FLD_RMAIL>")
			 * ; sb.append("<COB_FLD_RMN>"+this.officeAddress.getPhoneNumber()+
			 * "</COB_FLD_RMN>");
			 */sb.append("<COB_FLD_CONTACT_PREF>1</COB_FLD_CONTACT_PREF>");
			sb.append("</COB_FLD_BUSINESS_EXT>");
			sb.append("</INHERITED_INFO>");
			sb.append("</PROFILES>");
		}
		sb.append("</COB_OP_CUST_UPDATE_CUSTOMER_inputFlist>");

		return sb.toString();
	}

	/**
	 * Build BRM request to set CREDIT LIMIT.
	 * 
	 * @param creditLimit
	 * @param username
	 * @return
	 */
	public String buildCreditLimitUpdateRequest(BigDecimal creditLimit, String username) {
		StringBuilder sb = new StringBuilder("<COB_OP_CUST_SET_CREDIT_LIMIT_inputFlist>");
		sb.append("<POID>0.0.0.1 /account " + this.poId + " 0</POID>");
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/settlement/prepaid " + this.settlementpoId + "</SERVICE_OBJ>");
		sb.append("<PROGRAM_NAME>CRM|" + username + "</PROGRAM_NAME>");
		sb.append("<DESCR>LCO Credit Limit Set</DESCR>");
		sb.append("<LIMIT elem=\"356\">");
		sb.append("<CREDIT_LIMIT>" + creditLimit + "</CREDIT_LIMIT>");
		sb.append("</LIMIT>");
		sb.append("</COB_OP_CUST_SET_CREDIT_LIMIT_inputFlist>");
		return sb.toString();
	}

	public Character getPayment() {
		return payment;
	}

	public void setPayment(Character payment) {
		this.payment = payment;
	}

	public ApplicationCurrency getCurrencyId() {
		return CurrencyId;
	}

	public void setCurrencyId(ApplicationCurrency currencyId) {
		CurrencyId = currencyId;
	}

}
