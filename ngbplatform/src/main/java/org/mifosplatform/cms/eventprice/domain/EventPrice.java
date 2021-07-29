package org.mifosplatform.cms.eventprice.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mifosplatform.annotation.ComparableFields;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.infrastructure.core.api.JsonCommand;


/**
 * Domain for {@link EventPricing}
 * 
 * @author pavani
 * @author Rakesh
 */
@Entity
@Table(name = "b_mod_pricing")
@ComparableFields(on={"formatType", "optType", "clientType", "discountId", "price"})
public class EventPrice {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	/*@Column(name = "event_id")
	private Integer eventId;*/
	
	@Column(name = "format_type")
	private String formatType;
	
	@Column(name = "opt_type")
	private String optType;
	
	@Column(name = "client_typeid")
	private Long clientType;
	
	@Column(name = "discount_id")
	private Integer discountId;
	
	@Column(name = "price")
	private Double price;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "event_id", insertable = true, updatable = true, nullable = true, unique = true)
	private EventMaster eventId;
	
	
	@Column(name = "currencyId")
	private String currencyId;
	
	@Column(name = "is_deleted")
	private char isDeleted;
	
	public EventPrice() {
		
	}
	
	public static EventPrice fromJson(final JsonCommand command, final EventMaster eventMaster) {
		
		final String formatType = command.stringValueOfParameterNamed("formatType");
		final String optType = command.stringValueOfParameterNamed("optType");
		final Long clientType = command.longValueOfParameterNamed("clientType");
		final Integer discountId = command.integerValueOfParameterNamed("discountId");
		final String priceString  = command.stringValueOfParameterNamed("price");
		final Double price = Double.parseDouble(priceString);
		final String currencyId=command.stringValueOfParameterNamed("currencyId");
		
		return new EventPrice(eventMaster, formatType, optType, clientType, discountId, price,currencyId);
	}
		
	public EventPrice(final EventMaster eventId, final String formatType, final String optType, final Long clientType,
					  final Integer discountId, final Double price,final String currencyId) {
		this.eventId = eventId;
		this.formatType = formatType;
		this.optType = optType;
		this.clientType = clientType;
		this.discountId = discountId;
		this.price = price;
		this.isDeleted ='n';
		this.currencyId=currencyId;
		
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @return the eventId
	 */
	public EventMaster getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(final EventMaster eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the formatType
	 */
	public String getFormatType() {
		return formatType;
	}

	/**
	 * @param formatType the formatType to set
	 */
	public void setFormatType(final String formatType) {
		this.formatType = formatType;
	}

	/**
	 * @return the optType
	 */
	public String getOptType() {
		return optType;
	}

	/**
	 * @param optType the optType to set
	 */
	public void setOptType(final String optType) {
		this.optType = optType;
	}

	/**
	 * @return the clientType
	 */
	public Long getClientType() {
		return clientType;
	}

	/**
	 * @param clientType the clientType to set
	 */
	public void setClientType(final Long clientType) {
		this.clientType = clientType;
	}

	/**
	 * @return the discountId
	 */
	public Integer getDiscountId() {
		return discountId;
	}

	/**
	 * @param discountId the discountId to set
	 */
	public void setDiscountId(final Integer discountId) {
		this.discountId = discountId;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(final Double price) {
		this.price = price;
	}

	/**
	 * @return the isDeleted
	 */
	public char getIsDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted the isDeleted to set
	 */
	public void setIsDeleted(final char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	
	
}
