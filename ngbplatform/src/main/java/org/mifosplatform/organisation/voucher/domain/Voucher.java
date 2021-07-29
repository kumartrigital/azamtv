package org.mifosplatform.organisation.voucher.domain;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;

/**
 * Entity class, Used to Store the Voucher Group/Batch details from b_pin_master table.
 * 
 * @author ashokreddy
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "b_pin_master", uniqueConstraints = @UniqueConstraint(name = "batch_name", columnNames = { "batch_name" }))
public class Voucher extends AbstractAuditableCustom<AppUser, Long>  {
	
	
	@Column(name = "batch_name", nullable = false)
	private String batchName;
	
	@Column(name = "length", nullable = false)
	private Long length;

	@Column(name = "begin_with")
	private String beginWith;

	@Column(name = "pin_category", nullable = false)
	private String pinCategory;

	@Column(name = "quantity", nullable = false)
	private Long quantity;
	
	@Column(name = "serial_no")
	private Long serialNo;

	@Column(name = "pin_type")
	private String pinType;
	
	@Column(name = "pin_value")
	private String pinValue;

	@Column(name = "expiry_date")
	private Date expiryDate;
	
	@Column(name = "is_processed")
	private char isProcessed;
	
	@Column(name = "office_id", nullable = false)
	private Long officeId;
	
	@Column(name = "price_id")
	private Long priceId;
	
	@Column(name = "batch_type")
	private String batchType;
	
	/*@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "randomGenerator", orphanRemoval = true)
	private List<RandomGeneratorDetails> randomGeneratorDetails = new ArrayList<RandomGeneratorDetails>();*/
	
	/**
	 * Default/Zero-Parameerized Constructor. 
	 */
	public Voucher() {
		super();
	}

	public Voucher(final String batchName, final Long length, final String beginWith,
			final String pinCategory, final Long quantity, final Long serialNo,
			final String pinType, final String pinValue, final Date date,final Long priceId,final String batchType) {

		super();
		this.batchName = batchName;
		this.length = length;
		this.beginWith = beginWith;
		this.pinCategory = pinCategory;
		this.quantity = quantity;
		this.serialNo = serialNo;
		this.pinType = pinType;
		this.pinValue = pinValue;
		this.priceId = priceId;
		this.expiryDate = date;
		this.batchType = batchType;
		this.isProcessed = 'N';

	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	/**
	 * Using this method, we can get the input data from JsonCommand object.
	 * @param command
	 * @return
	 * @throws ParseException
	 */
	public static Voucher fromJson(final JsonCommand command) throws ParseException {
		  	final String batchName = command.stringValueOfParameterNamed("batchName");
		    final BigDecimal length = command.bigDecimalValueOfParameterNamed("length");
		    final String beginWith = command.stringValueOfParameterNamed("beginWith");
		    final String pinCategory = command.stringValueOfParameterNamed("pinCategory");
		    final BigDecimal quantity = command.bigDecimalValueOfParameterNamed("quantity");
		    final BigDecimal serialNo = command.bigDecimalValueOfParameterNamed("serialNo");
		    final String pinType = command.stringValueOfParameterNamed("pinType");
		    final Long pinVal = command.longValueOfParameterNamed("pinValue");
		    final LocalDate expiryDate = command.localDateValueOfParameterNamed("expiryDate");
		    Long priceId;
		    if(command.hasParameter("priceId")){
		    	priceId = command.longValueOfParameterNamed("priceId");
		    }else{
		    	priceId = (long) 0;
		    }
		    final String pinValue=String.valueOf(pinVal);
		    final String batchType = command.stringValueOfParameterNamed("batchType");
		    return new Voucher(batchName,length.longValue(),beginWith,pinCategory,quantity.longValue(),serialNo.longValue(),pinType,pinValue,expiryDate.toDate(),priceId,batchType);
	}

	public Long getPriceId() {
		return priceId;
	}

	public void setPriceId(Long priceId) {
		this.priceId = priceId;
	}

	public String getBatchName() {
		return batchName;
	}

	public Long getLength() {
		return length;
	}


	public String getBeginWith() {
		return beginWith;
	}


	public String getPinCategory() {
		return pinCategory;
	}


	public Long getQuantity() {
		return quantity;
	}


	public Long getSerialNo() {
		return serialNo;
	}


	public String getPinType() {
		return pinType;
	}


	public String getPinValue() {
		return pinValue;
	}


	public Date getExpiryDate() {
		return expiryDate;
	}


	public char getIsProcessed() {
		return isProcessed;
	}


	public void setIsProcessed(char isProcessed) {
		this.isProcessed = isProcessed;
	}

	public Long getOfficeId() {
		return officeId;
	}

	public void setOfficeId(Long officeId) {
		this.officeId = officeId;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}
	
	
	

}
