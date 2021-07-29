package org.mifosplatform.portfolio.client.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;

import antlr.StringUtils;

@Entity
@Table(name = "m_client_billprofile")
public class ClientBillProfileInfo{
	
	@Column(name = "bill_day_of_month")
	private  Long  billDayOfMonth;
	
	@Column(name = "bill_currency")
	private  Long  billCurrency;
	
	@Column(name = "bill_frequency")
	private  Long  billFrequency;
	
	@Column(name = "bill_segment")
	private String  billSegment;
	
	@Column(name = "next_bill_day")
	@Temporal(TemporalType.DATE)
	private  Date  nextBillDay;

	@Column(name = "last_bill_day")
	@Temporal(TemporalType.DATE)
	private  Date  lastBillDay;
	
	@Column(name = "last_bill_no")
	private  Long  lastBillNo;
	
	@Column(name = "payment_type")
	private  Long  paymentType;
	
	@Column(name = "bill_suppression_flag")
	private  char billSuppressionFlag;
	
	@Column(name = "bill_suppression_id")
	private  Long billSuppressionId;
	
	@Id
	@Column(name = "client_id", unique = true, nullable = false)
	private  Long clientId;
	
	@Column(name = "first_bill")
	private  char  firstBill;
	
	@Column(name = "hot_bill")
	private  char  hotBill;
	
	 public ClientBillProfileInfo(){
	 
	 }
	 public ClientBillProfileInfo(Long  billDayOfMonth,Long  billCurrency, Long  billFrequency,Long clientId,Date nextBillDay){
		  
		 this.billDayOfMonth=billDayOfMonth;
		 this.billCurrency=billCurrency;
		 this.billFrequency=billFrequency;
		 this.clientId = clientId;
		 this.nextBillDay = nextBillDay;
		 this.billSuppressionFlag = 'N';
		 this.firstBill = 'N';
		 this.hotBill = 'N';
		/* this.isDeleted = 'N';*/
	 }
	 
	public Long getLastBillNo() {
		return lastBillNo;
	}
	public void setLastBillNo(Long lastBillNo) {
		this.lastBillNo = lastBillNo;
	}
	public Long getBillDayOfMonth() {
		return billDayOfMonth;
	}
	public void setBillDayOfMonth(Long billDayOfMonth) {
		this.billDayOfMonth = billDayOfMonth;
	}
	public Long getBillCurrency() {
		return billCurrency;
	}
	public void setBillCurrency(Long billCurrency) {
		this.billCurrency = billCurrency;
	}
	public Long getBillFrequency() {
		return billFrequency;
	}
	public void setBillFrequency(Long billFrequency) {
		this.billFrequency = billFrequency;
	}
	public String getBillSegment() {
		return billSegment;
	}
	public void setBillSegment(String billSegment) {
		this.billSegment = billSegment;
	}
	public Date getNextBillDay() {
		return nextBillDay;
	}
	public void setNextBillDay(Date nextBillDay) {
		this.nextBillDay = nextBillDay;
	}
	public Date getLastBillDay() {
		return lastBillDay;
	}
	public void setLastBillDay(Date lastBillDay) {
		this.lastBillDay = lastBillDay;
	}
	public Long getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(Long paymentType) {
		this.paymentType = paymentType;
	}
	public char getBillSuppressionFlag() {
		return billSuppressionFlag;
	}
	public void setBillSuppressionFlag(char billSuppressionFlag) {
		this.billSuppressionFlag = billSuppressionFlag;
	}
	public Long getBillSuppressionId() {
		return billSuppressionId;
	}
	public void setBillSuppressionId(Long billSuppressionId) {
		this.billSuppressionId = billSuppressionId;
	}
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public char getFirstBill() {
		return firstBill;
	}
	public void setFirstBill(char firstBill) {
		this.firstBill = firstBill;
	}
	public char getHotBill() {
		return hotBill;
	}
	public void setHotBill(char hotBill) {
		this.hotBill = hotBill;
	}
	public void delete() {

	/*if (this.isDeleted == 'N') {
		this.isDeleted = 'Y';
		this.country = this.country+"_"+this.getId();
	}*/
	}

	public static ClientBillProfileInfo fromJson(JSONObject object,Long clientId) throws JSONException{
	         
		ClientBillProfileInfo clientBillProfileInfo = null;
		
		try {
			Long billDayOfMonth = object.getLong("billDayOfMonth");
			Long billCurrency = object.getLong("billCurrency");
			Long billFrequency=object.getLong("billFrequency");
			String nextBillDaye=object.getString("nextBillDay");
			
          DateFormat formatter;
          Date nextBillDay;
          formatter = new SimpleDateFormat("dd MM yyyy");
          nextBillDay = formatter.parse(nextBillDaye);
			
			clientBillProfileInfo = new ClientBillProfileInfo(billDayOfMonth, billCurrency, billFrequency,clientId,nextBillDay);
			
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return clientBillProfileInfo;
	}
		 
		 
	
	    public Map<String, Object> update(JsonCommand command) {

		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		final  String billDayOfMonthNamedParamName = "billDayOfMonth";
		final  String billCurrencyNamedParamName = "billCurrency";
		final  String billFrequencyNamedParamName = "billFrequency";
		final  String billSegmentNamedParamName = "billSegment";
		final  String nextBillDayNamedParamName = "nextBillDay";
		final  String lastBillDayNamedParamName = "lastBillDay";
		final  String lastBillNoNamedParamName = "lastBillNo";
		final  String paymentTypeNamedParamName = "paymentType";
		final  String billSuppressionFlagNamedParamName = "billSuppressionFlag";
		final  String billSuppressionIdNamedParamName = "billSuppressionId";
		//final  String clientIdNamedParamName = "clientId";
		final  String firstBillNamedParamName = "firstBill";
		final  String hotBillNamedParamName = "hotBill";
		
		
		

		    if(command.isChangeInLongParameterNamed(billDayOfMonthNamedParamName, this.billDayOfMonth)){
			final Long newValue = command.longValueOfParameterNamed(billDayOfMonthNamedParamName);
			actualChanges.put(billDayOfMonthNamedParamName, newValue);
			this.billDayOfMonth = newValue;
		     }
			
			if(command.isChangeInLongParameterNamed(billCurrencyNamedParamName, this.billCurrency)){
				final Long newValue = command.longValueOfParameterNamed(billCurrencyNamedParamName);
				actualChanges.put(billCurrencyNamedParamName, newValue);
				this.billCurrency = newValue;
			}
			if(command.isChangeInLongParameterNamed(billFrequencyNamedParamName, this.billFrequency)){
				final Long newValue = command.longValueOfParameterNamed(billFrequencyNamedParamName);
				actualChanges.put(billFrequencyNamedParamName, newValue);
				this.billFrequency = newValue;
			}
			if(command.isChangeInStringParameterNamed(billSegmentNamedParamName, this.billSegment)){
				final String newValue = command.stringValueOfParameterNamed(billSegmentNamedParamName);
				actualChanges.put(billSegmentNamedParamName, newValue);
				this.billSegment = newValue;
			}
			
			if (command.isChangeInLocalDateParameterNamed(nextBillDayNamedParamName,
                this.nextBillDay != null?new LocalDate(this.nextBillDay):null)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(nextBillDayNamedParamName);
				actualChanges.put(nextBillDayNamedParamName, newValue);
				this.nextBillDay=newValue.toDate();
			}

			if (command.isChangeInLocalDateParameterNamed(lastBillDayNamedParamName,
					this.lastBillDay != null?new LocalDate(this.lastBillDay):null)) {
				final LocalDate newValue = command.localDateValueOfParameterNamed(lastBillDayNamedParamName);
				actualChanges.put(lastBillDayNamedParamName, newValue);
				this.lastBillDay=newValue.toDate();
			}
			
		    if(command.isChangeInLongParameterNamed(lastBillNoNamedParamName, this.lastBillNo)){
			final Long newValue = command.longValueOfParameterNamed(lastBillNoNamedParamName);
			actualChanges.put(lastBillNoNamedParamName, newValue);
			this.lastBillNo = newValue;
		     }
			
			if (command.isChangeInLongParameterNamed(paymentTypeNamedParamName,this.paymentType)) {
				final Long newValue = command.longValueOfParameterNamed(paymentTypeNamedParamName);
				actualChanges.put(paymentTypeNamedParamName, newValue);
				this.paymentType=newValue;
			}

			final boolean newvalueOfbillSuppressionFlag = command.booleanPrimitiveValueOfParameterNamed(billSuppressionFlagNamedParamName );
			if(String.valueOf(newvalueOfbillSuppressionFlag?'Y':'N').equalsIgnoreCase(String.valueOf(this.billSuppressionFlag))){
				actualChanges.put(billSuppressionFlagNamedParamName , newvalueOfbillSuppressionFlag);
			}
			this.billSuppressionFlag = newvalueOfbillSuppressionFlag?'Y':'N';
			
			
			if (command.isChangeInLongParameterNamed(billSuppressionIdNamedParamName,this.billSuppressionId)) {
				final Long newValue = command.longValueOfParameterNamed( billSuppressionIdNamedParamName);
				actualChanges.put(billSuppressionIdNamedParamName, newValue);
				this.billSuppressionId=newValue;
			}
			
			/*if (command.isChangeInLongParameterNamed(clientIdNamedParamName,this.clientId)) {
				final Long newValue = command.longValueOfParameterNamed(clientIdNamedParamName);
				actualChanges.put(clientIdNamedParamName, newValue);
				this.clientId=newValue;
			}*/
			
			final boolean newvalueOffirstBill = command.booleanPrimitiveValueOfParameterNamed(firstBillNamedParamName);
			if(String.valueOf(newvalueOffirstBill?'Y':'N').equalsIgnoreCase(String.valueOf(this.firstBill))){
				actualChanges.put(firstBillNamedParamName , newvalueOffirstBill);
			}
			this.firstBill = newvalueOffirstBill?'Y':'N';
			
			final boolean newvalueOfhotBill = command.booleanPrimitiveValueOfParameterNamed(hotBillNamedParamName);
			if(String.valueOf(newvalueOfhotBill?'Y':'N').equalsIgnoreCase(String.valueOf(this.hotBill))){
				actualChanges.put(hotBillNamedParamName , newvalueOfhotBill);
			}
			this.hotBill = newvalueOfhotBill?'Y':'N';
				
				return actualChanges;
		 }
		 
		 
		 
	 }
		 
		 
	
		 
	 
	
	
	


