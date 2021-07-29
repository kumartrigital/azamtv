package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.taxmaster.data.TaxMappingRateData;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ChargeTaxCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 *
 */
@Service
public class GenerateDisconnectionCharges {

	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final GenerateCharges generateCharges;
	

	@Autowired
	public GenerateDisconnectionCharges(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			  final GenerateCharges generateCharges) {
		
		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.generateCharges = generateCharges;
	}

	BigDecimal pricePerMonth=BigDecimal.ZERO;
	BigDecimal price=BigDecimal.ZERO;
	LocalDate startDate=null;
	LocalDate endDate=null;
	LocalDate billEndDate=null;
	LocalDate invoiceTillDate=null;
	LocalDate nextbillDate=null;
	ChargeData billingOrderCommand=null;

	public boolean isChargeTypeNRC(final BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("NRC")) {
			chargeType = true;
		}
		return chargeType;
	}

	public boolean isChargeTypeRC(final BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("RC")) {
			chargeType = true;
		}
		return chargeType;
	}

	public boolean isChargeTypeUC(final BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("UC")) {
			chargeType = true;
		}
		return chargeType;
	}

	// Monthly Bill
	public ChargeData getReverseMonthyBill(final BillingOrderData billingOrderData,
			final DiscountMasterData discountMasterData, final LocalDate disconnectionDate) {
		
	    BigDecimal discountAmount = BigDecimal.ZERO;
	    BigDecimal netAmount=BigDecimal.ZERO;
		BigDecimal disconnectionCreditForMonths = BigDecimal.ZERO;
		BigDecimal disconnectionCreditPerday = BigDecimal.ZERO;
		BigDecimal disconnectionCreditForDays = BigDecimal.ZERO;
		List<ChargeTaxCommand> listOfTaxes =new ArrayList<ChargeTaxCommand>();
		int numberOfDays = 0;
		int totalDays = 0;
		
		billEndDate = new LocalDate(billingOrderData.getBillEndDate());
		price = billingOrderData.getPrice();
		
		if (billingOrderData.getInvoiceTillDate() == null) { //with out invoice direct disconnect order
			
			System.out.println("---- RC ----");
			this.startDate = new LocalDate(billingOrderData.getBillStartDate());
			this.endDate = disconnectionDate;
			this.invoiceTillDate = disconnectionDate;
			this.nextbillDate = invoiceTillDate.plusDays(1);
			LocalDate nextDurationDate	=startDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);
		    numberOfDays = Days.daysBetween(startDate, invoiceTillDate).getDays()+1;
			
			if(billingOrderData.getChargeDuration()==12){
			   int maxDaysInYear =startDate.dayOfYear().withMaximumValue().getDayOfYear();
			   netAmount = price.divide(new BigDecimal(maxDaysInYear),RoundingMode.HALF_UP);
			}else if(billingOrderData.getChargeDuration()!=1){
			   totalDays = Days.daysBetween(startDate, nextDurationDate).getDays()+1;
			   netAmount = price.divide(new BigDecimal(totalDays),RoundingMode.HALF_UP);
			}else{
			   int maxDaysOfMonth = startDate.dayOfMonth().withMaximumValue().getDayOfMonth();
			   netAmount = price.divide(new BigDecimal(maxDaysOfMonth),RoundingMode.HALF_UP);
			}
			price=netAmount.multiply(new BigDecimal(numberOfDays));
			
			if(discountMasterData !=null && BigDecimal.ZERO.compareTo(discountMasterData.getDiscountRate()) < 0){

		           if(billingOrderData.getBillStartDate().after(discountMasterData.getDiscountStartDate().toDate())
		        		   ||billingOrderData.getBillStartDate().equals(discountMasterData.getDiscountStartDate().toDate())){

		    		if ("percentage".equalsIgnoreCase(discountMasterData.getDiscountType())){
		    			discountAmount = price.multiply(discountMasterData.getDiscountRate().divide(new BigDecimal(100),RoundingMode.HALF_UP));
			               price = price.subtract(discountAmount);
		    		 }else if("flat".equalsIgnoreCase(discountMasterData.getDiscountType())){
		    		     price = price.subtract(discountMasterData.getDiscountRate());
		              }
		           }
			}
			if(price.compareTo(BigDecimal.ZERO) !=0){
			   listOfTaxes = this.calculateTax(billingOrderData, price,disconnectionDate);
			}
			
		}else { // If BillItem till date not equal to null
		  
			System.out.println("---- DC ----");
		   if(discountMasterData != null && BigDecimal.ZERO.compareTo(discountMasterData.getDiscountRate()) < 0 ){	
			   
	        if((discountMasterData.getDiscountStartDate().toDate().after(billingOrderData.getBillStartDate())
		    		   || discountMasterData.getDiscountStartDate().toDate().equals(billingOrderData.getBillStartDate())) &&
		       (billingOrderData.getInvoiceTillDate().before(this.generateCharges.getDiscountEndDateIfNull(discountMasterData, 
		    		    new LocalDate(billingOrderData.getInvoiceTillDate())))
		    			|| billingOrderData.getInvoiceTillDate().equals(this.generateCharges.getDiscountEndDateIfNull(discountMasterData, 
		    					new LocalDate(billingOrderData.getInvoiceTillDate()))))	){

    		if ("percentage".equalsIgnoreCase(discountMasterData.getDiscountType())){
    			discountAmount = price.multiply(discountMasterData.getDiscountRate().divide(new BigDecimal(100),RoundingMode.HALF_UP));
	               price = price.subtract(discountAmount);
    		 }else if("flat".equalsIgnoreCase(discountMasterData.getDiscountType())){
    		     price = price.subtract(discountMasterData.getDiscountRate());
              }
             }
		   }
		   
			this.startDate = disconnectionDate;
			this.endDate = new LocalDate(billingOrderData.getInvoiceTillDate());
			invoiceTillDate = new LocalDate(billingOrderData.getInvoiceTillDate());
			int maxDaysOfMonth =startDate.dayOfMonth().withMaximumValue().getDayOfMonth();
			int  maximumDaysInYear = startDate.dayOfYear().withMaximumValue().getDayOfYear();
			LocalDate nextDurationDate=startDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);
			totalDays =Days.daysBetween(startDate, nextDurationDate).getDays()+1;
			int numberOfMonths = Months.monthsBetween(disconnectionDate, invoiceTillDate).getMonths();
		
			if (billingOrderData.getBillingAlign().equalsIgnoreCase("N")) {
				LocalDate tempBillEndDate = invoiceTillDate.minusMonths(numberOfMonths);
				numberOfDays = Days.daysBetween(disconnectionDate, tempBillEndDate).getDays();
				
			} else if (billingOrderData.getBillingAlign().equalsIgnoreCase("Y")) {
                if(numberOfMonths>0){
				 LocalDate tempBillEndDate = invoiceTillDate.minusMonths(numberOfMonths).dayOfMonth().withMaximumValue();
				 numberOfDays = Days.daysBetween(disconnectionDate, tempBillEndDate).getDays();
               }else{
					/*LocalDate tempBillEndDate = invoiceTillDate.minusMonths(numberOfMonths).dayOfMonth().withMaximumValue()*/;
					numberOfDays = Days.daysBetween(disconnectionDate, invoiceTillDate).getDays();
				}
			}
            //calculate amount for one month
			if (numberOfMonths != 0) {
			   if(billingOrderData.getChargeDuration()==12){
				    netAmount=price.divide(new BigDecimal(billingOrderData.getChargeDuration()),RoundingMode.HALF_UP);
				    disconnectionCreditForMonths = netAmount.multiply(new BigDecimal(numberOfMonths));
			     }else if(billingOrderData.getChargeDuration()!=1){
					netAmount=price.divide(new BigDecimal(billingOrderData.getChargeDuration()),RoundingMode.HALF_UP);
					disconnectionCreditForMonths = netAmount.multiply(new BigDecimal(numberOfMonths));
			     }else{
				    disconnectionCreditForMonths = price.multiply(new BigDecimal(numberOfMonths));//monthly
			   }
			}//calculate amount for one Day
			 if(billingOrderData.getChargeDuration()==12){
			        disconnectionCreditPerday = price.divide(new BigDecimal(maximumDaysInYear),RoundingMode.HALF_UP);
			 }else if(billingOrderData.getChargeDuration()!=1){
			        disconnectionCreditPerday = price.divide(new BigDecimal(totalDays),RoundingMode.HALF_UP);
			 }else{
				   disconnectionCreditPerday = price.divide(new BigDecimal(maxDaysOfMonth),RoundingMode.HALF_UP);
			 }
			if (numberOfDays != 0) {
				disconnectionCreditForDays = disconnectionCreditPerday.multiply(new BigDecimal(numberOfDays));
			}
			price = disconnectionCreditForMonths.add(disconnectionCreditForDays);//final case
			
			this.startDate=invoiceTillDate;
			this.endDate = disconnectionDate;
			this.invoiceTillDate =disconnectionDate;
			this.nextbillDate = invoiceTillDate.plusDays(1);
			billingOrderData.setChargeType("DC");
			listOfTaxes = this.calculateTax(billingOrderData, price,disconnectionDate);
		 }
		
		 return this.createChargeData(billingOrderData, startDate, endDate, invoiceTillDate, 
				                   nextbillDate, price, listOfTaxes,discountMasterData);
	}

	// Reverse Weekly Bill
	public ChargeData getReverseWeeklyBill(final BillingOrderData billingOrderData,final DiscountMasterData discountMasterData, 
			                            final  LocalDate disconnectionDate) {
		
		BigDecimal disconnectionCreditForWeeks = BigDecimal.ZERO;
		BigDecimal disconnectionCreditPerday = BigDecimal.ZERO;
		BigDecimal disconnectionCreditForDays = BigDecimal.ZERO;
		BigDecimal discountAmount = BigDecimal.ZERO;
		BigDecimal netAmount=BigDecimal.ZERO;
		int numberOfDays = 0;
		
		billEndDate = new LocalDate(billingOrderData.getBillEndDate());
		price = billingOrderData.getPrice();

		 if (billingOrderData.getInvoiceTillDate() == null) {//with out invoice direct disconnect order

				this.startDate = new LocalDate(billingOrderData.getBillStartDate());
				this.endDate = disconnectionDate;
				this.invoiceTillDate = disconnectionDate;
				this.nextbillDate = invoiceTillDate.plusDays(1);
				Integer billingDays = 7 * billingOrderData.getChargeDuration();
				numberOfDays = Days.daysBetween(startDate, nextbillDate).getDays();
			    netAmount=price.divide(new BigDecimal(billingDays),RoundingMode.HALF_UP);
				price=netAmount.multiply(new BigDecimal(numberOfDays));
						
		}else{//if invoice till date not null or after invoice disconnect order
			

		  if(discountMasterData != null && BigDecimal.ZERO.compareTo(discountMasterData.getDiscountRate()) < 0){

	       if((discountMasterData.getDiscountStartDate().toDate().after(billingOrderData.getBillStartDate())
	    		   || discountMasterData.getDiscountStartDate().toDate().equals(billingOrderData.getBillStartDate())) &&
	    		   (billingOrderData.getInvoiceTillDate().before(this.generateCharges.getDiscountEndDateIfNull(discountMasterData, 
			    		    new LocalDate(billingOrderData.getInvoiceTillDate())))
			    			|| billingOrderData.getInvoiceTillDate().equals(this.generateCharges.getDiscountEndDateIfNull(discountMasterData, 
			    					new LocalDate(billingOrderData.getInvoiceTillDate()))))){

		    		if ("percentage".equalsIgnoreCase(discountMasterData.getDiscountType())){
		    			   discountAmount = price.multiply(discountMasterData.getDiscountRate().divide(new BigDecimal(100),RoundingMode.HALF_UP));
			               price = price.subtract(discountAmount);
		    		}else if("flat".equalsIgnoreCase(discountMasterData.getDiscountType())){
		    			  price = price.subtract(discountMasterData.getDiscountRate());
		              }
		           }

		  }

			this.startDate = disconnectionDate;
			this.endDate = new LocalDate(billingOrderData.getInvoiceTillDate());
			invoiceTillDate = new LocalDate(billingOrderData.getInvoiceTillDate());
			int numberOfWeeks = Weeks.weeksBetween(disconnectionDate, invoiceTillDate).getWeeks();

			if (billingOrderData.getBillingAlign().equalsIgnoreCase("N")) {
		     	LocalDate tempBillEndDate = invoiceTillDate.minusWeeks(numberOfWeeks);
				numberOfDays = Days.daysBetween(disconnectionDate, tempBillEndDate).getDays();
				
			} else if (billingOrderData.getBillingAlign().equalsIgnoreCase("Y")) {
				LocalDate tempBillEndDate = invoiceTillDate.minusWeeks(numberOfWeeks).dayOfWeek().withMaximumValue();
				numberOfDays = Days.daysBetween(disconnectionDate, tempBillEndDate).getDays();
			}

			if (numberOfWeeks != 0) {
				 if(billingOrderData.getChargeDuration() == 2) {
					netAmount=price.divide(new BigDecimal(billingOrderData.getChargeDuration()),RoundingMode.HALF_UP);
					disconnectionCreditForWeeks = netAmount.multiply(new BigDecimal(numberOfWeeks));
				}else{
				disconnectionCreditForWeeks = price.multiply(new BigDecimal(numberOfWeeks));	
			   }
			}
			disconnectionCreditPerday =this.getWeeklyPricePerDay(billingOrderData.getChargeDuration(),price);
			if (numberOfDays != 0) {
				disconnectionCreditForDays = disconnectionCreditPerday.multiply(new BigDecimal(numberOfDays));
			}
			price = disconnectionCreditForWeeks.add(disconnectionCreditForDays);
			billingOrderData.setChargeType("DC");

			this.startDate=invoiceTillDate;
			this.endDate = disconnectionDate;
			this.invoiceTillDate =disconnectionDate;
			this.nextbillDate = invoiceTillDate.plusDays(1);

		}
		List<ChargeTaxCommand> listOfTaxes = this.calculateTax(billingOrderData, price,disconnectionDate);

		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, 
				                      nextbillDate, price, listOfTaxes,discountMasterData);
	}

	// Per day weekly price
	public BigDecimal getWeeklyPricePerDay(Integer chargeDuration,BigDecimal price) {
		Integer billingDays = 7 * chargeDuration; 
		return price.divide(new BigDecimal(billingDays),RoundingMode.HALF_UP);
	}

	// Daily Bill
	public ChargeData getDailyBill(final BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		startDate = new LocalDate(billingOrderData.getBillStartDate());
		endDate = startDate;
		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);
		price = billingOrderData.getPrice();

		List<ChargeTaxCommand> listOfTaxes = this.calculateTax(billingOrderData, price,null);

		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,
	                                         discountMasterData);

	}
	
	// Reverse One Time Bill
	public ChargeData getReverseOneTimeBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {
            
		  List<ChargeTaxCommand> listOfTaxes = new ArrayList<ChargeTaxCommand>();
			LocalDate startDate = new LocalDate(billingOrderData.getBillStartDate());
			LocalDate  endDate = startDate;
			LocalDate  invoiceTillDate = startDate;
			LocalDate   nextbillDate = invoiceTillDate;
			BigDecimal price = billingOrderData.getPrice();//totalPrice
			  if(discountMasterData !=null && BigDecimal.ZERO.compareTo(discountMasterData.getDiscountAmount()) < 0){	
				   BigDecimal discountedPrice = price.subtract(discountMasterData.getDiscountAmount());
				   listOfTaxes = this.calculateTax(billingOrderData,discountedPrice,startDate);
		        }else{
		           listOfTaxes = this.calculateTax(billingOrderData,price,startDate);
		        }

		return this.createChargeData(billingOrderData, startDate,endDate,invoiceTillDate,nextbillDate,price,
					listOfTaxes,discountMasterData);
		}
	
	

	// Tax Calculation
	public List<ChargeTaxCommand> calculateTax(final BillingOrderData billingOrderData, final BigDecimal billPrice, LocalDate disconnectionDate) {

		List<TaxMappingRateData> taxMappingRateDatas = chargingOrderReadPlatformService.retrieveTaxMappingData(billingOrderData.getClientId(),billingOrderData.getChargeCode());
		if(taxMappingRateDatas.isEmpty()){
			
			 taxMappingRateDatas = chargingOrderReadPlatformService.retrieveDefaultTaxMappingData(billingOrderData.getClientId(),billingOrderData.getChargeCode());
		}
		List<ChargeTaxCommand> invoiceTaxCommand = this.generateInvoiceTax(taxMappingRateDatas, billPrice, billingOrderData,disconnectionDate);
		return invoiceTaxCommand;
	}
	
	// Generate BillItem Tax
	public List<ChargeTaxCommand> generateInvoiceTax(final List<TaxMappingRateData> taxMappingRateDatas, final BigDecimal price,
			BillingOrderData billingOrderData,LocalDate disconnectionDate) {
		
		   BigDecimal taxRate= null;
		   BigDecimal taxAmount = null;
		   String taxCode = null;
		   
		 List<ChargeTaxCommand> invoiceTaxCommands = new ArrayList<ChargeTaxCommand>();
		 ChargeTaxCommand invoiceTaxCommand = null;
		 
		if (taxMappingRateDatas != null) {

			for (TaxMappingRateData taxMappingRateData : taxMappingRateDatas) {

				if ("Percentage".equalsIgnoreCase(taxMappingRateData.getTaxType())) {
					
					taxRate = taxMappingRateData.getRate();
					taxCode = taxMappingRateData.getTaxCode();
					 if(billingOrderData.getTaxInclusive().compareTo(Integer.valueOf(1))==0){  /*(2990 * 11) / (100 + 11)*/
	                      	 taxAmount=price.multiply(taxRate).divide(new BigDecimal(100).add(taxRate),Integer.parseInt(this.generateCharges.roundingDecimal()), RoundingMode.HALF_UP);
	                     }else{
	                    	 taxAmount = price.multiply(taxRate.divide(new BigDecimal(100))).setScale(Integer.parseInt(this.generateCharges.roundingDecimal()),RoundingMode.HALF_UP);
	                    }
				} else if("Flat".equalsIgnoreCase(taxMappingRateData.getTaxType())) {
					
					taxRate = taxMappingRateData.getRate();
					taxCode = taxMappingRateData.getTaxCode();
					if("RC".equalsIgnoreCase(billingOrderData.getChargeType())||"NRC".equalsIgnoreCase(billingOrderData.getChargeType())){
					      taxAmount =taxRate;
					}else{
					BigDecimal numberOfMonthsPrice = BigDecimal.ZERO;
					BigDecimal numberOfDaysPrice = BigDecimal.ZERO;
					BigDecimal pricePerDay = BigDecimal.ZERO;
					BigDecimal pricePerMonth = BigDecimal.ZERO;
					LocalDate durationDate=disconnectionDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);
					int totalDays = Days.daysBetween(disconnectionDate, durationDate).getDays();
				    int numberOfMonths = Months.monthsBetween(disconnectionDate,new LocalDate(billingOrderData.getInvoiceTillDate())).getMonths();
				    int maximumDaysInMonth = disconnectionDate.dayOfMonth().withMaximumValue().getDayOfMonth();
			        int  maximumDaysInYear = new LocalDate().dayOfYear().withMaximumValue().getDayOfYear();
					int numberOfDays = 0;
					if(numberOfMonths !=0){
						LocalDate tempDate = new LocalDate(billingOrderData.getInvoiceTillDate()).minusMonths(numberOfMonths);
						numberOfDays = Days.daysBetween(disconnectionDate, tempDate).getDays();	
					}else{
						numberOfDays = Days.daysBetween(disconnectionDate,new LocalDate(billingOrderData.getInvoiceTillDate())).getDays();
					}
					if(billingOrderData.getDurationType().equalsIgnoreCase("month(s)")){
					     if(billingOrderData.getChargeDuration()==12){
					    	 
						       pricePerMonth = taxRate.divide(new BigDecimal(billingOrderData.getChargeDuration()),RoundingMode.HALF_UP);
					           numberOfMonthsPrice = pricePerMonth.multiply(new BigDecimal(numberOfMonths));
				    	       pricePerDay = taxRate.divide(new BigDecimal(maximumDaysInYear),RoundingMode.HALF_UP);
				    	       numberOfDaysPrice = pricePerDay.multiply(new BigDecimal(numberOfDays));
				    	       
				      }else if(billingOrderData.getChargeDuration()!=1){
				    	  
						       pricePerMonth = taxRate.divide(new BigDecimal(billingOrderData.getChargeDuration()),RoundingMode.HALF_UP);
						       numberOfMonthsPrice = pricePerMonth.multiply(new BigDecimal(numberOfMonths));
				    	       pricePerDay = taxRate.divide(new BigDecimal(totalDays),RoundingMode.HALF_UP);
				    	       numberOfDaysPrice = pricePerDay.multiply(new BigDecimal(numberOfDays));
				      }else{
				    	       numberOfMonthsPrice = taxRate.multiply(new BigDecimal(numberOfMonths));
						       pricePerDay = taxRate.divide(new BigDecimal(maximumDaysInMonth),RoundingMode.HALF_UP);
						       numberOfDaysPrice = pricePerDay.multiply(new BigDecimal(numberOfDays));
				       }
					} else if(billingOrderData.getDurationType().equalsIgnoreCase("week(s)")){ 
						
							  Integer billingDays = 7*billingOrderData.getChargeDuration();
				    	      pricePerDay = taxRate.divide(new BigDecimal(billingDays),RoundingMode.HALF_UP);
				    	      numberOfDaysPrice = pricePerDay.multiply(new BigDecimal(numberOfDays));
				      
					}  
					taxAmount = numberOfDaysPrice.add(numberOfMonthsPrice).setScale(Integer.parseInt(this.generateCharges.roundingDecimal()),RoundingMode.HALF_UP);
					// taxAmount = taxFlat;
				}
				}
			
				invoiceTaxCommand = new ChargeTaxCommand(billingOrderData.getClientId(), null, null,
						                  taxCode, billingOrderData.getTaxInclusive(), taxRate, taxAmount,price);
				invoiceTaxCommands.add(invoiceTaxCommand);
			}

	    }
		return invoiceTaxCommands;

	}

	// create billing order command
	public ChargeData createChargeData(BillingOrderData billingOrderData, LocalDate chargeStartDate,
			LocalDate chargeEndDate, LocalDate invoiceTillDate,LocalDate nextBillableDate, BigDecimal billPrice,
			List<ChargeTaxCommand> listOfTaxes,DiscountMasterData discountMasterData) {

		 BigDecimal price = billPrice.setScale(Integer.parseInt(this.generateCharges.roundingDecimal()),RoundingMode.HALF_UP);
		
		return new ChargeData(billingOrderData.getClientOrderId(),
				billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), chargeStartDate.toDate(),
				nextBillableDate.toDate(), chargeEndDate.toDate(),
				billingOrderData.getBillingFrequency(),
				billingOrderData.getChargeCode(),
				billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),
				billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,
				billingOrderData.getStartDate(), billingOrderData.getEndDate(),
				discountMasterData, billingOrderData.getTaxInclusive(), billingOrderData.getIsAggregate(),billingOrderData.getCurrencyId(),billingOrderData.getChargeOwner());
	}
	
}