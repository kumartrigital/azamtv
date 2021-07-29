package org.mifosplatform.finance.chargeorder.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifosplatform.billing.discountmaster.data.DiscountMasterData;
import org.mifosplatform.billing.taxmaster.data.TaxMappingRateData;
import org.mifosplatform.finance.chargeorder.data.BillingOrderData;
import org.mifosplatform.finance.chargeorder.data.ChargeData;
import org.mifosplatform.finance.chargeorder.data.ChargeTaxCommand;
import org.mifosplatform.infrastructure.configuration.domain.Configuration;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationConstants;
import org.mifosplatform.infrastructure.configuration.domain.ConfigurationRepository;
import org.mifosplatform.portfolio.plan.domain.Plan;
import org.mifosplatform.portfolio.plan.domain.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenerateCharges {

	private final ChargingOrderReadPlatformService chargingOrderReadPlatformService;
	private final ConfigurationRepository globalConfigurationRepository;
	private final PlanRepository planRepository;

	@Autowired
	public GenerateCharges(final ChargingOrderReadPlatformService chargingOrderReadPlatformService,
			final ConfigurationRepository globalConfigurationRepository,final PlanRepository planRepository) {
		
		this.chargingOrderReadPlatformService = chargingOrderReadPlatformService;
		this.globalConfigurationRepository = globalConfigurationRepository;
		this.planRepository = planRepository;
	}

	//ChargeData chargeData = null;

	public boolean isChargeTypeNRC(BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("NRC")) {
			chargeType = true;
		}
		return chargeType;
	}

	public boolean isChargeTypeRC(BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("RC")) {
			chargeType = true;
		}
		return chargeType;
	}

	public boolean isChargeTypeUC(BillingOrderData billingOrderData) {
		boolean chargeType = false;
		if (billingOrderData.getChargeType().equals("UC")) {
			chargeType = true;
		}
		return chargeType;
	}

	// prorata monthly bill
	public ChargeData getProrataMonthlyFirstBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

	
		BigDecimal pricePerDay = BigDecimal.ZERO;
		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;
		
		startDate = new LocalDate(billingOrderData.getBillStartDate());
		LocalDate durationDate = startDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);
		LocalDate monthStartDate = startDate.dayOfMonth().withMinimumValue();
		int totalDays = Days.daysBetween(startDate, durationDate).getDays() + 1;
		Plan plan = this.planRepository.findOne(billingOrderData.getPlanId());

		// check startDate is monthStartDate
		if (startDate.equals(monthStartDate)) {
			endDate = startDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);// durationDate
		} else {
			endDate = startDate.dayOfMonth().withMaximumValue();
		}

		if (endDate.toDate().before(billingOrderData.getBillEndDate()) || endDate.toDate().equals(billingOrderData.getBillEndDate())) {
			
			price = billingOrderData.getPrice();  //.setScale(Integer.parseInt(roundingDecimal()));

			if (billingOrderData.getChargeDuration() == 12 && !startDate.equals(monthStartDate)) {
				
				int maximumDaysInYear = new LocalDate().dayOfYear().withMaximumValue().getDayOfYear();
				pricePerDay = price.divide(new BigDecimal(maximumDaysInYear),RoundingMode.HALF_UP);

			}else if(!startDate.equals(monthStartDate)) {
				
				pricePerDay = price.divide(new BigDecimal(totalDays),RoundingMode.HALF_UP);;

			}

			int currentDay = startDate.getDayOfMonth();
			int endOfMonth = startDate.dayOfMonth().withMaximumValue().getDayOfMonth();
			int onlymonthyTotalDays = endOfMonth - currentDay + 1;
			
			if (onlymonthyTotalDays < endOfMonth) {
				price = pricePerDay.multiply(new BigDecimal(onlymonthyTotalDays));
			}

			// plan with No prorata and not start day of month
			if (plan.getBillRule() == 300 && startDate.compareTo(monthStartDate) > 0 && plan.isPrepaid() == 'N') {
				price = BigDecimal.ZERO;
			} else if(plan.getBillRule() == 200 && plan.isPrepaid() == 'N'){
				price =billingOrderData.getPrice();
			}

		} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
			endDate = new LocalDate(billingOrderData.getBillEndDate());
			price = getDisconnectionCredit(startDate, endDate,billingOrderData.getPrice(),billingOrderData.getDurationType(),billingOrderData.getChargeDuration());
		}
		
		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);

		// check promotion or discount is apply or not --Tax is calculated on
		// Net charges if those applied..
		List<ChargeTaxCommand> listOfTaxes=this.calculateDiscountAndTax(billingOrderData,discountMasterData,startDate,endDate,price);
		
		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,discountMasterData,billingOrderData.getChargeOwner());
	}
	
	// NextMonth Bill after prorata
	public ChargeData getNextMonthBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;

		startDate = new LocalDate(billingOrderData.getNextBillableDate());
		endDate = new LocalDate(billingOrderData.getInvoiceTillDate()).plusMonths(billingOrderData.getChargeDuration()).dayOfMonth().withMaximumValue();
		
		if (endDate.toDate().before(billingOrderData.getBillEndDate()) || endDate.toDate().equals(billingOrderData.getBillEndDate())) {
			price = billingOrderData.getPrice();
			
		} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
			
			endDate = new LocalDate(billingOrderData.getBillEndDate());
			price = getDisconnectionCredit(startDate, endDate,billingOrderData.getPrice(),billingOrderData.getDurationType(),billingOrderData.getChargeDuration());
		}

		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);

		List<ChargeTaxCommand> listOfTaxes=this.calculateDiscountAndTax(billingOrderData,discountMasterData,startDate,endDate,price);
		
		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,discountMasterData,billingOrderData.getChargeOwner());

	}

	// Monthly Bill
	public ChargeData getMonthyBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;

		if (billingOrderData.getInvoiceTillDate() == null) {
			
			startDate = new LocalDate(billingOrderData.getBillStartDate());
			endDate = startDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);
			price = billingOrderData.getPrice();
			
		} else if (billingOrderData.getInvoiceTillDate() != null) {
			startDate = new LocalDate(billingOrderData.getNextBillableDate());
			endDate = startDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);
			
		}
		   if (endDate.toDate().before(billingOrderData.getBillEndDate()) || endDate.toDate().equals(billingOrderData.getBillEndDate())) {
				price = billingOrderData.getPrice();
			
			} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
				
				endDate = new LocalDate(billingOrderData.getBillEndDate());
				price = getDisconnectionCredit(startDate, endDate,billingOrderData.getPrice(),billingOrderData.getDurationType(),billingOrderData.getChargeDuration());
			}
		

		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);

		// check promotion or discount is apply or not --Tax is calculated on
		// Net charges if those applied..
		List<ChargeTaxCommand> listOfTaxes=this.calculateDiscountAndTax(billingOrderData,discountMasterData,startDate,endDate,price);

		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,discountMasterData,billingOrderData.getChargeOwner());

	}

	// Pro rate Weekly Bill
	public ChargeData getProrataWeeklyFirstBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;

		startDate = new LocalDate(billingOrderData.getBillStartDate());
		endDate = startDate.dayOfWeek().withMaximumValue();
		LocalDate weekStartDate = startDate.dayOfWeek().withMinimumValue();
		Plan plan = this.planRepository.findOne(billingOrderData.getPlanId());

		int totalDays = 0;

		totalDays = Days.daysBetween(startDate, endDate).getDays() + 1;
		BigDecimal weeklyPricePerDay = getWeeklyPricePerDay(billingOrderData);

		Integer billingDays = 7 * billingOrderData.getChargeDuration();

		if (totalDays < billingDays) {
			price = weeklyPricePerDay.multiply(new BigDecimal(totalDays));
			if (plan.getBillRule() == 300 && !startDate.equals(weekStartDate)) {
				price = BigDecimal.ZERO;
			}
		} else if (totalDays == billingDays) {
			price = billingOrderData.getPrice();
		}

		invoiceTillDate = endDate;
		nextbillDate = endDate.plusDays(1);

		List<ChargeTaxCommand> listOfTaxes=this.calculateDiscountAndTax(billingOrderData,discountMasterData,startDate,endDate,price);
		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,
				discountMasterData,billingOrderData.getChargeOwner());

	}

	public ChargeData getNextWeeklyBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;

		startDate = new LocalDate(billingOrderData.getNextBillableDate());
		endDate = startDate.plusWeeks(billingOrderData.getChargeDuration())
				.minusDays(1);

		if (endDate.toDate().before(billingOrderData.getBillEndDate())
				|| endDate.toDate()
						.compareTo(billingOrderData.getBillEndDate()) == 0) {
			price = billingOrderData.getPrice();
		} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
			endDate = new LocalDate(billingOrderData.getBillEndDate());
			price = getDisconnectionCredit(startDate, endDate,
					billingOrderData.getPrice(),
					billingOrderData.getDurationType(),
					billingOrderData.getChargeDuration());
		}

		invoiceTillDate = endDate;
		nextbillDate = endDate.plusDays(1);

		List<ChargeTaxCommand> listOfTaxes=this.calculateDiscountAndTax(billingOrderData,discountMasterData,startDate,endDate,price);
		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,
				discountMasterData,billingOrderData.getChargeOwner());

	}

	// Weekly Bill
	public ChargeData getWeeklyBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;

		if (billingOrderData.getInvoiceTillDate() == null) {
			
			startDate = new LocalDate(billingOrderData.getBillStartDate());
			endDate = startDate.plusWeeks(billingOrderData.getChargeDuration()).minusDays(1);
			price = billingOrderData.getPrice();
		
		} else if (billingOrderData.getInvoiceTillDate() != null) {
			startDate = new LocalDate(billingOrderData.getNextBillableDate());
			endDate = startDate.plusWeeks(billingOrderData.getChargeDuration()).minusDays(1);
			
			if (endDate.toDate().before(billingOrderData.getBillEndDate()) || endDate.toDate().compareTo(billingOrderData.getBillEndDate()) == 0) {
				price = billingOrderData.getPrice();
			
			} else if (endDate.toDate().after(billingOrderData.getBillEndDate())) {
				
				endDate = new LocalDate(billingOrderData.getBillEndDate());
				price = getDisconnectionCredit(startDate, endDate,billingOrderData.getPrice(),billingOrderData.getDurationType(),
						billingOrderData.getChargeDuration());
			}
		}

		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);
		List<ChargeTaxCommand> listOfTaxes=this.calculateDiscountAndTax(billingOrderData,discountMasterData,startDate,endDate,price);

		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,
				discountMasterData,billingOrderData.getChargeOwner());
	}

	// One Time Bill
	public ChargeData getOneTimeBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		LocalDate endDate=null;
		LocalDate invoiceTillDate=null;
		LocalDate nextbillDate=null;
		List<ChargeTaxCommand> listOfTaxes = new ArrayList<ChargeTaxCommand>();

		LocalDate startDate = new LocalDate(billingOrderData.getBillStartDate());
		BigDecimal price = billingOrderData.getPrice();
		
		if(billingOrderData.getStartDate()!=null){
			endDate = startDate.plusMonths(billingOrderData.getChargeDuration()).minusDays(1);
			invoiceTillDate = endDate;
			nextbillDate = invoiceTillDate.plusDays(1);
			
		}else{
			endDate = startDate;
		    invoiceTillDate = startDate;
		    nextbillDate = invoiceTillDate;
		}

		if(discountMasterData !=null && BigDecimal.ZERO.compareTo(discountMasterData.getDiscountAmount()) <= 1){

			listOfTaxes = this.calculateTax(billingOrderData,discountMasterData.getDiscountedChargeAmount());
		} else {
			
			listOfTaxes = this.calculateTax(billingOrderData,billingOrderData.getPrice());
		}

		return this.createChargeData(billingOrderData, startDate,
				endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,
				discountMasterData,billingOrderData.getChargeOwner());
	}

	// Disconnection credit price
	protected BigDecimal getDisconnectionCredit(LocalDate startDate,LocalDate endDate, BigDecimal amount, String durationType,
			Integer chargeDuration) {

		LocalDate durationDate = startDate.plusMonths(chargeDuration).minusDays(1);
		int divisibleDays = Days.daysBetween(startDate, durationDate).getDays() + 1;
		int maxDaysOfMonth = startDate.dayOfMonth().withMaximumValue().getDayOfMonth();
		int maximumDaysInYear = new LocalDate().dayOfYear().withMaximumValue().getDayOfYear();
		BigDecimal pricePerDay = BigDecimal.ZERO;

		int totalDays = 0;
		if (startDate.isEqual(endDate)) {
			totalDays = 0;
		} else {
			// int numberOfMonths =
			// Months.monthsBetween(startDate,endDate).getMonths();
			// LocalDate tempBillEndDate = endDate.minusMonths(numberOfMonths);
			totalDays = Days.daysBetween(startDate, endDate).getDays() + 1;
		}

		if (durationType.equalsIgnoreCase("month(s)")) {

			if (chargeDuration == 12) {
				pricePerDay = amount.divide(new BigDecimal(maximumDaysInYear),RoundingMode.HALF_UP);

			} else if(chargeDuration != 1){
				pricePerDay = amount.divide(new BigDecimal(divisibleDays),RoundingMode.HALF_UP);

			} else {
				pricePerDay = amount.divide(new BigDecimal(maxDaysOfMonth),RoundingMode.HALF_UP);
			}
		} else if (durationType.equalsIgnoreCase("week(s)")) {

			Integer billingDays = 7 * chargeDuration;

			pricePerDay = amount.divide(new BigDecimal(billingDays),RoundingMode.HALF_UP);
	}
		

		return pricePerDay.multiply(new BigDecimal(totalDays));

	}

	// order cancelled bill
	public ChargeData getCancelledOrderBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {
		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;
		if (billingOrderData.getInvoiceTillDate() == null)
			startDate = new LocalDate(billingOrderData.getStartDate());
		else
			startDate = new LocalDate(billingOrderData.getNextBillableDate());

		endDate = new LocalDate(billingOrderData.getBillEndDate());
		price = this.getDisconnectionCredit(startDate, endDate,billingOrderData.getPrice(),billingOrderData.getDurationType(), null);

		nextbillDate = new LocalDate().plusYears(1000);
		invoiceTillDate = endDate;
		List<ChargeTaxCommand> listOfTaxes = this.calculateTax(billingOrderData, price);

		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,discountMasterData,billingOrderData.getChargeOwner());

	}

	// Per day weekly price
	public BigDecimal getWeeklyPricePerDay(BillingOrderData billingOrderData) {
		Integer billingDays = 7 * billingOrderData.getChargeDuration();

		return billingOrderData.getPrice().divide(new BigDecimal(billingDays),RoundingMode.HALF_UP);
	}

	// Daily Bill
	public ChargeData getDailyBill(BillingOrderData billingOrderData,DiscountMasterData discountMasterData) {

		LocalDate startDate = null;
		LocalDate endDate = null;
		BigDecimal price = null;
		LocalDate invoiceTillDate = null;
		LocalDate nextbillDate = null;
		
		if (billingOrderData.getNextBillableDate() == null) {
			startDate = new LocalDate(billingOrderData.getBillStartDate());
			endDate = startDate;
			
		} else {

			startDate = new LocalDate(billingOrderData.getNextBillableDate());
			endDate = startDate;
		}
		
		invoiceTillDate = endDate;
		nextbillDate = invoiceTillDate.plusDays(1);
		price = billingOrderData.getPrice();
		List<ChargeTaxCommand> listOfTaxes = this.calculateTax(billingOrderData, price);
		return this.createChargeData(billingOrderData, startDate,endDate, invoiceTillDate, nextbillDate, price, listOfTaxes,discountMasterData,billingOrderData.getChargeOwner());

	}
	
	public List<ChargeTaxCommand> calculateDiscountAndTax(BillingOrderData billingOrderData,DiscountMasterData discountMasterData, LocalDate startDate,
			LocalDate endDate, BigDecimal price) {

		List<ChargeTaxCommand> listOfTaxes = new ArrayList<>();

		if (this.isDiscountApplicable(startDate, discountMasterData, endDate)) {

			discountMasterData = this.calculateDiscount(discountMasterData,price);

			// Tax is calculated on netChages..whenever customer has tax exemption false and discount applicabled
			if (!billingOrderData.isTaxExemption())
				listOfTaxes = this.calculateTax(billingOrderData,discountMasterData.getDiscountedChargeAmount());

		} else if (this.isPromotionAtMiddleOfMonth(startDate,discountMasterData, endDate, billingOrderData)) {

			BigDecimal promoPrice = this.getDisconnectionCredit(discountMasterData.getDiscountEndDate().plusDays(1),
					endDate, price, billingOrderData.getDurationType(),billingOrderData.getChargeDuration());
			
			 discountMasterData.setDiscountAmount(price.subtract(promoPrice).setScale(Integer.parseInt(roundingDecimal()),RoundingMode.HALF_UP));
			 discountMasterData.setDiscountedChargeAmount(promoPrice.setScale(Integer.parseInt(roundingDecimal()),RoundingMode.HALF_UP));

			if (!billingOrderData.isTaxExemption())
				listOfTaxes = this.calculateTax(billingOrderData, promoPrice);

		} else {
			// Tax is calculated on charges ..whenever customer has tax exemption false
			if (!billingOrderData.isTaxExemption())
				listOfTaxes = this.calculateTax(billingOrderData, price);
		}

		return listOfTaxes;
	}

	// Tax Calculation
	public List<ChargeTaxCommand> calculateTax(BillingOrderData billingOrderData, BigDecimal billPrice) {

		// Get State level taxes
		List<TaxMappingRateData> taxMappingRateDatas = chargingOrderReadPlatformService.retrieveTaxMappingData(billingOrderData.getClientId(),billingOrderData.getChargeCode());
		if (taxMappingRateDatas.isEmpty()) {
				taxMappingRateDatas = chargingOrderReadPlatformService.retrieveDefaultTaxMappingData(billingOrderData.getClientId(),billingOrderData.getChargeCode());
		}
		
		List<ChargeTaxCommand> ChargeTaxCommand = generateInvoiceTax(taxMappingRateDatas, billPrice,billingOrderData.getClientId(),billingOrderData.getTaxInclusive());
		return ChargeTaxCommand;
	}

	// Generate BillItem Tax
	public List<ChargeTaxCommand> generateInvoiceTax(List<TaxMappingRateData> taxMappingRateDatas, BigDecimal price,Long clientId,Integer isTaxInclusive) {

		BigDecimal taxRate = BigDecimal.ZERO;
		BigDecimal taxAmount = BigDecimal.ZERO;
		String taxCode = null;
		
		List<ChargeTaxCommand> ChargeTaxCommands = new ArrayList<ChargeTaxCommand>();
		ChargeTaxCommand ChargeTaxCommand = null;

		if (taxMappingRateDatas != null && !taxMappingRateDatas.isEmpty()) {

			for (TaxMappingRateData taxMappingRateData : taxMappingRateDatas) {

				if ("Percentage".equalsIgnoreCase(taxMappingRateData.getTaxType())) {
					
					taxRate = taxMappingRateData.getRate();
					taxCode = taxMappingRateData.getTaxCode();
					  if(isTaxInclusive.compareTo(Integer.valueOf(1))==0){  /*(2990 * 11) / (100 + 11)*/
                      	   taxAmount= price.multiply(taxRate).divide(new BigDecimal(100).add(taxRate),Integer.parseInt(roundingDecimal()), RoundingMode.HALF_UP);
                       }else{
					       taxAmount = price.multiply(taxRate.divide(new BigDecimal(100))).setScale(Integer.parseInt(roundingDecimal()), RoundingMode.HALF_UP);
                    }
				} else if ("Flat".equalsIgnoreCase(taxMappingRateData.getTaxType())) {
					
					taxRate = taxMappingRateData.getRate();
					taxCode = taxMappingRateData.getTaxCode();
					// taxAmount =taxFlat;
					if (price.compareTo(taxRate) < 0) {
						taxAmount = BigDecimal.ZERO;
					} else {
						taxAmount = taxRate;
					}
				}

				ChargeTaxCommand = new ChargeTaxCommand(clientId, null, null,taxCode, isTaxInclusive, taxRate, taxAmount,price);
				ChargeTaxCommands.add(ChargeTaxCommand);
			}

		}else{
			ChargeTaxCommand = new ChargeTaxCommand(clientId, null, null,taxCode, isTaxInclusive, taxRate, taxAmount,price);
			ChargeTaxCommands.add(ChargeTaxCommand);
		}
		return ChargeTaxCommands;

	}

	// Discount Applicable Logic
	public Boolean isDiscountApplicable(LocalDate chargeStartDate,DiscountMasterData discountMasterData, LocalDate chargeEndDate) {
		
		boolean isDiscountApplicable = false;

		if (discountMasterData != null) {
			if ((chargeStartDate.toDate().after(discountMasterData.getDiscountStartDate().toDate()) 
					|| chargeStartDate.toDate().equals(discountMasterData.getDiscountStartDate().toDate()))
					&& (chargeEndDate.toDate().before(this.getDiscountEndDateIfNull(discountMasterData,chargeEndDate)) 	
					||	chargeEndDate.toDate().equals(this.getDiscountEndDateIfNull(discountMasterData,chargeEndDate)))) {

				isDiscountApplicable = true;
			}
		}

		return isDiscountApplicable;

	}

	// Discount End Date calculation if null
	public Date getDiscountEndDateIfNull(DiscountMasterData discountMasterData,LocalDate chargeEndDate) {
		LocalDate discountEndDate = discountMasterData.getDiscountEndDate();
		if (discountMasterData.getDiscountEndDate() == null) {
			discountEndDate = chargeEndDate;
		}
		return discountEndDate.toDate();

	}

	// if is percentage
	public boolean isDiscountPercentage(DiscountMasterData discountMasterData) {
		boolean isDiscountPercentage = false;
		if ("percentage".equalsIgnoreCase(discountMasterData.getDiscountType())) {
			isDiscountPercentage = true;
		}
		return isDiscountPercentage;
	}

	// if is flat
	public boolean isDiscountFlat(DiscountMasterData discountMasterData) {
		boolean isDiscountFlat = false;
		if ("Flat".equalsIgnoreCase(discountMasterData.getDiscountType())) {
			isDiscountFlat = true;
		}
		return isDiscountFlat;
	}

	// Discount calculation
	public DiscountMasterData calculateDiscount(DiscountMasterData discountMasterData,BigDecimal chargePrice) {
		
		BigDecimal discountAmount=BigDecimal.ZERO;

		if (isDiscountPercentage(discountMasterData)) {

			discountAmount = this.calculateDiscountPercentage(discountMasterData.getDiscountRate(), chargePrice);
			chargePrice = this.chargePriceNotLessThanZero(chargePrice,discountAmount);
			discountMasterData.setDiscountAmount(discountAmount);
			discountMasterData.setDiscountedChargeAmount(chargePrice);

		}

		if (isDiscountFlat(discountMasterData)) {

			BigDecimal netFlatAmount = this.calculateDiscountFlat(discountMasterData.getDiscountRate(), chargePrice);
			discountAmount = chargePrice.subtract(netFlatAmount).setScale(Integer.parseInt(roundingDecimal()),RoundingMode.HALF_UP);
			netFlatAmount = this.chargePriceNotLessThanZero(chargePrice,discountAmount);
			discountMasterData.setDiscountAmount(discountAmount);
			discountMasterData.setDiscountedChargeAmount(netFlatAmount);
		}
		return discountMasterData;

	}

	// Dicount Percent calculation
	public BigDecimal calculateDiscountPercentage(BigDecimal discountRate,BigDecimal chargePrice) {

		return chargePrice.multiply(discountRate.divide(new BigDecimal(100))).setScale(Integer.parseInt(roundingDecimal()), RoundingMode.HALF_UP);
	}

	// Discount Flat calculation
	public BigDecimal calculateDiscountFlat(BigDecimal discountRate,BigDecimal chargePrice) {

		BigDecimal calculateDiscountFlat = BigDecimal.ZERO;
		// if chargePrice is Zero then discount is Zero
		if (chargePrice.compareTo(BigDecimal.ZERO) == 1) {
			calculateDiscountFlat = chargePrice.subtract(discountRate);
		}
		return calculateDiscountFlat;

	}

	// To check price not less than zero
	public BigDecimal chargePriceNotLessThanZero(BigDecimal chargePrice,BigDecimal discountPrice) {

		chargePrice = chargePrice.subtract(discountPrice);
		if (chargePrice.compareTo(discountPrice) < 0) {
			chargePrice = BigDecimal.ZERO;
		}
		return chargePrice;
	}

	// create billing order command
	public ChargeData createChargeData(BillingOrderData billingOrderData, LocalDate chargeStartDate,LocalDate chargeEndDate, LocalDate invoiceTillDate,
			LocalDate nextBillableDate, BigDecimal billPrice,List<ChargeTaxCommand> listOfTaxes,DiscountMasterData discountMasterData,String chargeOwner) {
		         
		      BigDecimal price = billPrice.setScale(Integer.parseInt(roundingDecimal()),RoundingMode.HALF_UP);
		      
		 return new ChargeData(billingOrderData.getClientOrderId(),billingOrderData.getOderPriceId(),
				billingOrderData.getClientId(), chargeStartDate.toDate(),nextBillableDate.toDate(), chargeEndDate.toDate(),
				billingOrderData.getBillingFrequency(),billingOrderData.getChargeCode(),billingOrderData.getChargeType(),
				billingOrderData.getChargeDuration(),billingOrderData.getDurationType(), invoiceTillDate.toDate(),
				price, billingOrderData.getBillingAlign(), listOfTaxes,billingOrderData.getStartDate(), billingOrderData.getEndDate(),
				discountMasterData, billingOrderData.getTaxInclusive(), billingOrderData.getIsAggregate(),billingOrderData.getCurrencyId(),chargeOwner);
	}

	
	//rounding amount
	public String roundingDecimal() {

		final Configuration property = this.globalConfigurationRepository.findOneByName(ConfigurationConstants.CONFIG_PROPERTY_ROUNDING);
		
		if(property != null && property.isEnabled()){
		
		  return property.getValue();
		}else{ 
			return String.valueOf(2);
		}
		
	}
	
	/**
	 * @param startDate
	 * @param discountMasterData
	 * @param endDate
	 * @param billingOrderData
	 * @return boolean
	 */
	public boolean isPromotionAtMiddleOfMonth(LocalDate startDate,DiscountMasterData discountMasterData, 
			LocalDate chargeEndDate,BillingOrderData billingOrderData) {

		boolean isPromotionAtMiddleOfMonth = false;

		if (discountMasterData != null && discountMasterData.getDiscountEndDate() != null) {

			if (chargeEndDate.getYear() == discountMasterData.getDiscountEndDate().getYear() && 
					chargeEndDate.getMonthOfYear() == discountMasterData.getDiscountEndDate().getMonthOfYear()) {
				isPromotionAtMiddleOfMonth = true;
			}
		}

		return isPromotionAtMiddleOfMonth;
	}

}
