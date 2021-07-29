package org.mifosplatform.portfolio.contract.data;

import java.util.List;

import org.mifosplatform.billing.chargecode.data.DurationTypeData;

public class SubscriptionData {

	private final Long id;

	private final String subscriptionPeriod;
	private final Long subscriptiontypeId;
    private final String Contractdata;
	private final String subscriptionType;
	private List<DurationTypeData> allowedperiods;

	private final Long units;
	private Long priceId;
	

	public SubscriptionData(final Long id, final String subscriptionPeriod,
			final String subscriptionType, final Long units,final Long subscriptiontypeid,final String day_name) {

		this.id = id;
		this.subscriptionPeriod = subscriptionPeriod;
		this.subscriptionType = subscriptionType;
		this.units = units;
		this.subscriptiontypeId=subscriptiontypeid;
	
		this.Contractdata=null;
	}

	public SubscriptionData(final Long id,final String data, final String subscriptionType,final Long priceId)
	{
		this.Contractdata=data;
		this.id=id;
		this.subscriptionPeriod=null;
		this.subscriptionType=subscriptionType;
		this.units=null;
		this.subscriptiontypeId=null;
		this.priceId = priceId;
		

	}
	

	public SubscriptionData(final List<DurationTypeData> datas, final SubscriptionData products) {
		this.id = products.getId();
		this.allowedperiods = datas;
		this.subscriptionPeriod = products.getSubscriptionPeriod();
		this.units = products.getUnits();
		this.subscriptionType = products.getSubscriptionType();
        this.subscriptiontypeId=products.getSubscriptiontypeId();
        this.Contractdata=null;
	}

	public SubscriptionData(final List<DurationTypeData> durationTypeData) {
		this.allowedperiods = durationTypeData;
		this.id=null;
		this.subscriptionPeriod=null;
		this.subscriptionType=null;
		this.units=null;
		this.subscriptiontypeId=null;
		this.Contractdata=null;
	}

	public List<DurationTypeData> getAllowedperiods() {
		return allowedperiods;
	}

	public void setAllowedperiods(final List<DurationTypeData> allowedperiods) {
		this.allowedperiods = allowedperiods;
	}

	public Long getId() {
		return id;
	}

	public String getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public Long getUnits() {
		return units;
	}

	public Long getSubscriptiontypeId() {
		return subscriptiontypeId;
	}

	public String getContractdata() {
		return Contractdata;
	}

	public Long getPriceId() {
		return priceId;
	}


}
