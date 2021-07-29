package org.mifosplatform.finance.chargeorder.data;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BillDetailsData {

	private Long id;
	private Long clientId;
	private String billPeriod;
	private BigDecimal previousBalance;
	private BigDecimal chargeAmount;
	private BigDecimal adjustmentAmount;
	private BigDecimal taxAmount;
	private BigDecimal paidAmount;
	private BigDecimal dueAmount;
	private LocalDate billDate;
	private LocalDate dueDate;
	private String promotionalMessage;
	private String billNo;
	private String date;
	private String transaction;
	private BigDecimal amount;
	private String payments;
	private String isPaid;
	private BigDecimal invoiceAmount;
	private String poId;
	private Date startDate;
	private Date endDate;
	private List<BillDetailsData> billDataList;
	
	
	public BillDetailsData(List<BillDetailsData> billDataList){
		this.billDataList=billDataList;
	}
	
	
	public BillDetailsData(final Long id, final Long clientId,final LocalDate dueDate, final String transactionType,
			final BigDecimal dueAmount, final BigDecimal amount, final LocalDate transDate) {

		this.id = id;
		this.dueDate = dueDate;
		this.transaction = transactionType;
		this.dueAmount = dueAmount;
		this.amount = amount;
		this.clientId = clientId;

	}

	public BillDetailsData(final Long id, final LocalDate billDate, final LocalDate dueDate,final BigDecimal amount, 
			final String isPaid, final BigDecimal invoiceAmount,final BigDecimal adjustmentAmount) {

		this.id = id;
		this.billDate = billDate;
		this.dueDate = dueDate;
		this.amount = amount;
		this.isPaid = isPaid;
		this.invoiceAmount=invoiceAmount;
		this.adjustmentAmount = adjustmentAmount;
	}

	public BillDetailsData(Long id, LocalDate billDate, LocalDate dueDate, BigDecimal amount) {
		this.id = id ;
		this.billDate = billDate;
		this.dueDate = dueDate;
		this.amount = amount;
	
	}
	
	
	public  BillDetailsData(Long id, LocalDate billDate, LocalDate dueDate, BigDecimal amount, String poId) {
		this.id = id ;
		this.billDate = billDate;
		this.dueDate = dueDate;
		this.amount = amount;
	    this.poId=poId;
	}

	public BillDetailsData(String poId, Date startDate, Date endDate, Double totalamount) {
		this.poId=poId;
		this.billDate= new LocalDate(startDate);
		this.dueDate= new LocalDate(endDate);
		this.amount= new BigDecimal(totalamount);
	}

	public Long getId() {
		return id;
	}

	public String getPoId() {
		return poId;
	}
	public void setPoId(String poId) {
		this.poId = poId;
	}

	
	public Long getClientId() {
		return clientId;
	}

	public String getBillPeriod() {
		return billPeriod;
	}

	public BigDecimal getPreviousBalance() {
		return previousBalance;
	}

	public String getPromotionalMessage() {
		return promotionalMessage;
	}

	public String getBillNo() {
		return billNo;
	}

	public String getDate() {
		return date;
	}

	public String getTransaction() {
		return transaction;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getPayments() {
		return payments;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public BigDecimal getAdjustmentAmount() {
		return adjustmentAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public BigDecimal getDueAmount() {
		return dueAmount;
	}

	public LocalDate getBillDate() {
		return billDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public String getMessage() {
		return promotionalMessage;
	}

	public String getIsPaid() {
		return isPaid;
	}

	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}
  
	public static String celcomRequestInput(String clientPoId) {
		StringBuffer sb = new StringBuffer("<COB_OP_AR_GET_BILL_DETAILS_inputFlist>");
		
		sb.append("<POID>0.0.0.1 /account "+clientPoId+" 0</POID>");
		sb.append("<PROGRAM_NAME>COB_OP_AR_GET_BILL_DETAILS</PROGRAM_NAME>"); 	
		sb.append("<START_T>"+(System.currentTimeMillis()/1000-2592000)+"</START_T>"); 
		sb.append("<END_T>"+System.currentTimeMillis()/1000+"</END_T>");
		sb.append("</COB_OP_AR_GET_BILL_DETAILS_inputFlist>"); 
		return sb.toString();
	
	}

	public static List<BillDetailsData> fromJson(String result) {
		List<BillDetailsData> billDataList= new ArrayList<BillDetailsData>();      
		BillDetailsData  billData;
		try {
			JSONObject object=new JSONObject(result);
			object = object.optJSONObject("brm:COB_OP_AR_GET_BILL_DETAILS_outputFlist");
			JSONArray bills = null;
			JSONObject brmBills = null;
			JSONObject billObject = null;
			String poId=null;
			String startDateBrm=null;
			String endDateBrm=null;
			Double totalamount=null;
			bills = object.optJSONArray("brm:BILLS");
			if(bills==null){
				bills = new JSONArray("["+object.optJSONObject("brm:BILLS")+"]");
			}
				for(int i=0;i<bills.length();i++){
					brmBills = bills.getJSONObject(i);
					poId = String.valueOf(brmBills.optString("brm:POID"));
					String returnValue = poId;
					if(returnValue !=null){
						String[] args = returnValue.split(" ");
						poId = args[2];
					}
					startDateBrm =brmBills.optString("brm:START_T");
					Date startDate=new SimpleDateFormat("yyyy-MM-dd").parse(startDateBrm);
					endDateBrm =brmBills.optString("brm:END_T");
					Date endDate=new SimpleDateFormat("yyyy-MM-dd").parse(endDateBrm);
					totalamount = brmBills.optDouble("brm:CURRENT_TOTAL");
					billData = new BillDetailsData(poId, startDate, endDate, totalamount);
					billDataList.add(billData);
					}
				
				
				
				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return billDataList;
	}
	
	
		
	
}

