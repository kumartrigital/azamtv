package org.mifosplatform.organisation.voucher.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.office.data.OfficeData;

/**
 * The class <code>VoucherData</code> is a Bean class, contains only getter and setter methods to store and retrieve data.
 *
 *  @author ashokreddy
 */

public class VoucherData {
	
	private Long id;
	private String batchName;
	private Long officeId;
	private Long length;
	private String pinCategory;
	private String pinType;
	private Long quantity;
	private String serialNo;
	private LocalDate expiryDate;
	private String beginWith;
	private String pinValue;
    private List<EnumOptionData> pinCategoryData;
	private List<EnumOptionData> pinTypeData;
	private VoucherPinConfigValueData voucherPinConfigValues;
	private String isProcessed;
	private String planCode;
	private Collection<OfficeData> offices;
	private Long priceId;
	
	private String pinNo;
	
	private String status;
	private Long clientId;
	private Collection<MCodeData> reasondatas;
	private Collection<MCodeData> valueMCodeDatas;
	private String batchType;
	private String promotionDescription;
	private Long saleRefNo;
	private BigDecimal pinPrice;
	
	public VoucherData(final String batchName, final Long officeId,
			final Long length, final String pinCategory, final String pinType, final Long quantity,
			final String serial, final Date expiryDate, final String beginWith,
			final String pinValue, final Long id, final String planCode,final String batchType,
			final String promotionDescription, final String isProcessed, 
			final String pinNo, final String status, final Long clientId) {

		this.batchName=batchName;
		this.officeId=officeId;
		this.length=length;
		this.pinCategory=pinCategory;
		this.pinType=pinType;
		this.quantity=quantity;
		this.beginWith=beginWith;
		this.serialNo=serial;
		this.expiryDate=new LocalDate(expiryDate);
		this.pinValue=pinValue;
		this.id=id;
		this.planCode=planCode;
		this.batchType=batchType;
		this.promotionDescription=promotionDescription;
		this.isProcessed=isProcessed;
		this.pinNo = pinNo;
		this.status = status;
		this.clientId = clientId;
	}

	/**
	 * Default/Zero-Parameterized Constructor.
	 */
	public VoucherData() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param pinCategoryData
	 * 			value containg the List of VoucherCategory types.
	 *  Category Like ALPHA/NUMERIC/ALPHANUMERIC
	 *  
	 * @param pinTypeData
	 * 			value containg the List of VoucherPin types. Like VALUE and PRODUCT
	 * @param offices 
	 */
	public VoucherData(final List<EnumOptionData> pinCategoryData,
			final List<EnumOptionData> pinTypeData, Collection<OfficeData> offices,VoucherPinConfigValueData voucherPinConfigValueData,
			Collection<MCodeData> valueMCodeDatas) {
		
		this.pinCategoryData=pinCategoryData;
		this.pinTypeData=pinTypeData;
		this.offices = offices;
		this.voucherPinConfigValues = voucherPinConfigValueData;
		this.valueMCodeDatas = valueMCodeDatas;
		
	}

	public VoucherData(String pinType, String pinValue, Date expiryDate, Long officeId) {
		
		this.pinType = pinType;
		this.pinValue = pinValue;
		this.expiryDate = new LocalDate(expiryDate);
		this.officeId = officeId;
		
	}
public VoucherData(String pinType, String pinValue, Date expiryDate,String serialNum,String pinNum,String status){
		
		this.pinType = pinType;
		this.pinValue = pinValue;
		this.expiryDate = new LocalDate(expiryDate);
		this.serialNo = serialNum;
		this.pinNo = pinNum;
		this.status = status;
		
	}
	public VoucherData(Long saleRefNo, String serial, String pinNum, String status, Long officeId) {
		// TODO Auto-generated constructor stub
		this.saleRefNo = saleRefNo;
		this.serialNo = serial;
		this.pinNo = pinNum;
		this.status = status;
		this.officeId = officeId;
	}
	public VoucherData(Long id,String batchName) {
		
		this.id = id;
		this.batchName = batchName;
	}
	public VoucherData(Long priceId) {
		this.priceId = priceId;
	}

	public VoucherData(Collection<MCodeData> reasondatas) {
        this.reasondatas = reasondatas;
	}

	

	public VoucherData(String pinType, String pinValue, Date expiryDate, Long officeId, BigDecimal pinPrice) {
		// TODO Auto-generated constructor stub
		this.pinPrice = pinPrice;
		this.pinType = pinType;
		this.pinValue = pinValue;
		this.expiryDate = new LocalDate(expiryDate);
		this.officeId = officeId;
	}

	public List<EnumOptionData> getPinCategoryData() {
		return pinCategoryData;
	}

	public void setPinCategoryData(List<EnumOptionData> pinCategoryData) {
		this.pinCategoryData = pinCategoryData;
	}

	public List<EnumOptionData> getPinTypeData() {
		return pinTypeData;
	}

	public void setPinTypeData(List<EnumOptionData> pinTypeData) {
		this.pinTypeData = pinTypeData;
	}

	public String getBatchName() {
		return batchName;
	}
	
	public Long getSaleRefNo() {
		return saleRefNo;
	}

	public void setSaleRefNo(Long saleRefNo) {
		this.saleRefNo = saleRefNo;
	}

	public Long getId() {
		return id;
	}

	public String getBeginWith() {
		return beginWith;
	}
    
	public String getPinValue() {
		return pinValue;
	}

	public void setPinValue(String pinValue) {
		this.pinValue = pinValue;
	}

	public void setBeginWith(String beginWith) {
		this.beginWith = beginWith;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getPinCategory() {
		return pinCategory;
	}

	public void setPinCategory(String pinCategory) {
		this.pinCategory = pinCategory;
	}

	public String getPinType() {
		return pinType;
	}

	public void setPinType(String pinType) {
		this.pinType = pinType;
	}

	public Long getQuantity() {
		return quantity;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public String getIsProcessed() {
		return isProcessed;
	}

	public void setIsProcessed(String isProcessed) {
		this.isProcessed = isProcessed;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public Collection<OfficeData> getOffices() {
		return offices;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public String getPromotionDescription() {
		return promotionDescription;
	}

	public void setPromotionDescription(String promotionDescription) {
		this.promotionDescription = promotionDescription;
	}
	
	public String getPinNo() {
		return pinNo;
	}

	public void setPinNo(String pinNo) {
		this.pinNo = pinNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	


}
