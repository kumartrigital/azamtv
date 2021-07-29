package org.mifosplatform.portfolio.addons.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * @author hugo
 *
 */
@Entity
@Table(name = "b_addons_service_price")
public class AddonsPrices {

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@ManyToOne
    @JoinColumn(name="adservice_id")
    private AddonServices addons;

	@Column(name ="service_id", length=50)
    private Long serviceId;

	@Column(name = "price", nullable = false)
	private BigDecimal price;
	
	@Column(name = "is_deleted", nullable = false)
	private char isDeleted = 'N';


	public AddonsPrices()
	{
		  // This constructor is intentionally empty. Nothing special is needed here.
	}
	
	
	public AddonsPrices(Long serviceId, BigDecimal price) {
		
		this.serviceId=serviceId;
		this.price=price;
	}


	public static AddonsPrices fromJson(JsonElement jsonElement, FromJsonHelper fromJsonHelper) {
		
		final Long serviceId = fromJsonHelper.extractLongNamed("serviceId", jsonElement);
		final BigDecimal price = fromJsonHelper.extractBigDecimalWithLocaleNamed("price", jsonElement);
		
		return new AddonsPrices(serviceId,price);
	}


	public void update(AddonServices addonServices) {
 
		this.addons=addonServices;
	}


	public void delete() {
		if(this.isDeleted != 'Y'){
			 this.isDeleted ='Y';
		}
		
	}
}