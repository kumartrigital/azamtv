/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.office.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.celcom.domain.OfficeTypeEnum;
import org.mifosplatform.celcom.domain.PaymentTypeEnum;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.organisation.address.data.AddressData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

/**
 * Immutable data object for office data.
 */
public class OfficeData {

    private final Long id;
    private final String name;
    private final String nameDecorated;
    private final String externalId;
    private final LocalDate openingDate;
    private final String hierarchy;
    private final Long parentId;
    private final String parentName;
    private final String officeType;
    private final BigDecimal balance;
    
    private List<String> countryData;
	private List<String> statesData;
	private List<String> citiesData;
	private String city; 
	private String state; 
	private String country; 
	private String email; 
	private String phoneNumber;
	private String officeNumber;
	private String addressName;
	
	private String contactPerson;
	private String zip;
	private String businessType;
	private String district;
	private String poId;
	private String balanceAmount;
	private int payment;
	private boolean subscriberDues;
	private String pancardNo;
	private String companyRegNo;
	private int commisionModel;
	private String gstRegNo;
	private PaymentTypeEnum paymentEnum;
 
	private final Collection<OfficeData> allowedParents;
    private final Collection<CodeValueData> officeTypes;
    private Collection<CodeValueData> segmentTypes;
    private List<PaymentTypeEnum> paymentTypeEnum;
    private String dasTypeValue;
    private String walletAmount;
    private String settlementPoId;
    private BigDecimal credit;
    private Long clientId;
    private Long clientServiceId;
    private String clientPoId;
	private String clientServicePoId;

	
	private AddressData addressData;
    Collection<MCodeData> businessTypes;
	
    

    public static OfficeData dropdown(final Long id, final String name, final String nameDecorated, final String externalId,final String officeType) {
    	

        return new OfficeData(id, name,nameDecorated, externalId, null, null, null, null, null,null,officeType,null,null,null,null,null,null,null,null,null,null,null,null);
    }
    
    public static OfficeData externalId(final String externalId) {
    	

        return new OfficeData(null, null,null, externalId, null, null, null, null, null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

    public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	public String getCompanyRegNo() {
		return companyRegNo;
	}

	public void setCompanyRegNo(String companyRegNo) {
		this.companyRegNo = companyRegNo;
	}

	public int getCommisionModel() {
		return commisionModel;
	}

	public void setCommisionModel(int commisionModel) {
		this.commisionModel = commisionModel;
	}

	public String getGstRegNo() {
		return gstRegNo;
	}

	public void setGstRegNo(String gstRegNo) {
		this.gstRegNo = gstRegNo;
	}

	public PaymentTypeEnum getPaymentEnum() {
		return paymentEnum;
	}

	public void setPaymentEnum(PaymentTypeEnum paymentEnum) {
		this.paymentEnum = paymentEnum;
	}

	public static OfficeData template(final Collection<OfficeData> parentLookups, final LocalDate defaultOpeningDate, final Collection<CodeValueData> officeTypes) {
    	
        return new OfficeData(null, null, null, null, defaultOpeningDate, null, null, null, parentLookups,officeTypes,null,null,null,null,null,null,null,null,null,null,null,null,null);
    }

    public static OfficeData appendedTemplate(final OfficeData office, final Collection<OfficeData> allowedParents, final Collection<CodeValueData> codeValueDatas) {
    	
        OfficeData officeData= new OfficeData(office.id, office.name, office.nameDecorated, office.externalId, office.openingDate, office.hierarchy,
                office.parentId, office.parentName, allowedParents,codeValueDatas,office.officeType,office.balance,office.city,office.state,office.country,office.email,office.phoneNumber,
                office.officeNumber,office.addressName,office.contactPerson,office.zip,office.businessType,office.district, office.poId,office.getPancardNo(),
                office.getCompanyRegNo(), office.getCommisionModel(), office.getGstRegNo(), office.getPayment(), office.getSubscriberDues(), office.getDasTypeValue(),  office.settlementPoId);
        return officeData;
    }

    public String getPancardNo() {
		return pancardNo;
	}

	public void setPancardNo(String pancardNo) {
		this.pancardNo = pancardNo;
	}

	public OfficeData(final Long id, final String name,final String nameDecorated, final String externalId, final LocalDate openingDate,
            final String hierarchy, final Long parentId, final String parentName, final Collection<OfficeData> allowedParents, 
            final Collection<CodeValueData> codeValueDatas, final String officeType, BigDecimal balance,final String city,
            final String state,final String country,final String email,final String phoneNumber,final String officeNumber,final String addressName,final String contactPerson,final String zip,final String businessType,final String district) {
    	
        this.id = id;
        this.name = name;
        this.nameDecorated = nameDecorated;
        this.externalId = externalId;
        this.openingDate = openingDate;
        this.hierarchy = hierarchy;
        this.parentName = parentName;
        this.parentId = parentId;
        this.allowedParents = allowedParents;
        this.officeTypes = codeValueDatas;
        this.officeType = officeType;
        this.balance=balance;
        this.city=city;
        this.state=state;
        this.country=country;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.officeNumber=officeNumber;
        this.addressName=addressName;
        this.contactPerson=contactPerson;
        this.zip=zip;
        this.businessType=businessType;
        this.district=district;
        
    }
    
	
    public OfficeData(final Long id, final String name,final String nameDecorated, final String externalId, final LocalDate openingDate,
            final String hierarchy, final Long parentId, final String parentName, final Collection<OfficeData> allowedParents, 
            final Collection<CodeValueData> codeValueDatas, final String officeType, BigDecimal balance,final String city,
            final String state,final String country,final String email,final String phoneNumber,final String officeNumber,final String addressName,
            final String contactPerson,final String zip,final String businessType,final String district,final String poId,final String pancardNo, 
            final String companyRegNo, final int commisionModel, final String gstRegNo, final int paymentType, final Boolean subscriberDues, String dasTypeValue,
            final String settlementPoId) {
    	
        this.id = id;
        this.name = name;
        this.nameDecorated = nameDecorated;
        this.externalId = externalId;
        this.openingDate = openingDate;
        this.hierarchy = hierarchy;
        this.parentName = parentName;
        this.parentId = parentId;
        this.allowedParents = allowedParents;
        this.officeTypes = codeValueDatas;
        this.officeType = officeType;
        this.balance=balance;
        this.city=city;
        this.state=state;
        this.country=country;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.officeNumber=officeNumber;
        this.addressName=addressName;
        this.contactPerson=contactPerson;
        this.zip=zip;
        this.businessType=businessType;
        this.district=district;
        this.pancardNo=pancardNo;
        this.companyRegNo=companyRegNo;
        this.commisionModel=commisionModel;
        this.gstRegNo=gstRegNo;
        this.poId=poId;
        this.payment =  paymentType;
        this.subscriberDues = subscriberDues;
        this.settlementPoId = settlementPoId;
        this.setDasTypeValue(dasTypeValue);
    }
    
    public OfficeData(final Long id, final String name,final String nameDecorated, final String externalId, final LocalDate openingDate,
            final String hierarchy, final Long parentId, final String parentName, final Collection<OfficeData> allowedParents, 
            final Collection<CodeValueData> codeValueDatas, final String officeType, BigDecimal balance,final String city,
            final String state,final String country,final String email,final String phoneNumber,final String officeNumber,final String addressName,
            final String contactPerson,final String zip,final String businessType,final String district, final String poId) {
    	
        this.id = id;
        this.name = name;
        this.nameDecorated = nameDecorated;
        this.externalId = externalId;
        this.openingDate = openingDate;
        this.hierarchy = hierarchy;
        this.parentName = parentName;
        this.parentId = parentId;
        this.allowedParents = allowedParents;
        this.officeTypes = codeValueDatas;
        this.officeType = officeType;
        this.balance=balance;
        this.city=city;
        this.state=state;
        this.country=country;
        this.email=email;
        this.phoneNumber=phoneNumber;
        this.officeNumber=officeNumber;
        this.addressName=addressName;
        this.contactPerson=contactPerson;
        this.zip=zip;
        this.businessType=businessType;
        this.district=district;
        this.poId=poId;
    }


	public OfficeData(final Long id,final Long clientId,final String businessType,final boolean subscriberDue) {
		this.id = id;
        this.name = null;
        this.nameDecorated = null;
        this.externalId = null;
        this.openingDate = null;
        this.hierarchy = null;
        this.parentName = null;
        this.parentId = null;
        this.allowedParents = null;
        this.officeTypes = null;
        this.officeType = null;
        this.balance=null;
        this.city=null;
        this.state=null;
        this.country=null;
        this.email=null;
        this.phoneNumber=null;
        this.officeNumber=null;
        this.addressName=null;
        this.contactPerson=null;
        this.zip=null;
        this.businessType=businessType;
        this.district=null;
        this.poId=null;
        this.clientId = clientId;
        this.subscriberDues = subscriberDue;
	}

	public String getPoId() {
		return poId;
	}

	public void setPoId(String poId) {
		this.poId = poId;
	}
    
	public String getOfficeType() {
		return officeType;
	}

	public boolean hasIdentifyOf(final Long officeId) {
    	
        return this.id.equals(officeId);
    }

	public Collection<OfficeData> getAllowedParents() {
		return allowedParents;
	}

	public Collection<CodeValueData> getOfficeTypes() {
		return officeTypes;
	}

	public void setCountryData(List<String> countryData) {
		this.countryData = countryData;
	}

	public void setStatesData(List<String> statesData) {
		this.statesData = statesData;
	}

	public void setCitiesData(List<String> citiesData) {
		this.citiesData = citiesData;
	}

	public void setAddressData(AddressData addressData) {
		this.addressData = addressData;
	}
	
	public Collection<MCodeData> getBussinessTypes() {
		return businessTypes;
	}

	public void setBusinessTypes(Collection<MCodeData> businessTypes) {
		this.businessTypes = businessTypes;
	}
	
	
	 
	 public String getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(String balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public List<PaymentTypeEnum> getPaymentTypeEnum() {
		return paymentTypeEnum;
	}

	public void setPaymentTypeEnum(List<PaymentTypeEnum> paymentTypeEnum) {
		this.paymentTypeEnum = paymentTypeEnum;
	}
	
	
	   public boolean getSubscriberDues() {
			return subscriberDues;
		}

		public void setSubscriberDues(boolean subscriberDues) {
			this.subscriberDues = subscriberDues;
		}

		public int getPayment() {
			return payment;
		}

		public void setPayment(int payment) {
			this.payment = payment;
		}
	public String celcomRequestInput(int flag) {
			
			StringBuffer sb = new StringBuffer("<COB_OP_CUST_CUSTOMER_RETRIEVAL_inputFlist>");
			
			sb.append("<POID>0.0.0.1 /account -1 0</POID>");
			sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.poId+" 1</ACCOUNT_OBJ>");
			sb.append("<PROGRAM_NAME>COB|celcom</PROGRAM_NAME>");
			sb.append("<FLAGS>"+flag+"</FLAGS>");
			sb.append("</COB_OP_CUST_CUSTOMER_RETRIEVAL_inputFlist>");
			return sb.toString();
		
		}

	public Collection<CodeValueData> getSegmentTypes() {
		return segmentTypes;
	}

	public void setSegmentTypes(Collection<CodeValueData> segmentTypes) {
		this.segmentTypes = segmentTypes;
	}

	public String getDasTypeValue() {
		return dasTypeValue;
	}

	public void setDasTypeValue(String dasTypeValue) {
		this.dasTypeValue = dasTypeValue;
	}
	
	public String getWalletAmount() {
		return walletAmount;
	}

	public void setWalletAmount(String walletAmount) {
		this.walletAmount = walletAmount;
		
	}

	public String getSettlementPoId() {
		return settlementPoId;
	}

   public void setSettlementPoId(String settlementPoId) {
		this.settlementPoId = settlementPoId;
	}
	public String getExternalId() {
		return externalId;
	}
	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}
	
	public Long getClientId() {
		return clientId;
	}
	
	public void setClientId(Long clientId){
		this.clientId = clientId;
	}
	
	public Long getClientServiceId() {
		return clientServiceId;
	}
	
	public void setClientServiceId(Long clientServiceId){
		this.clientServiceId = clientServiceId;
	}
	
	public String getClientPoId() {
		return clientPoId;
	}
	
	public void setClientPoId(String clientPoId){
		this.clientPoId = clientPoId;
	}
	
	public String getClientServicePoId() {
		return clientServicePoId;
	}

	public void setClientServicePoId(String clientServicePoId) {
		this.clientServicePoId = clientServicePoId;
		
	}
	
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
		
	}

	public Long getId() {
		return id;
	}

    
}