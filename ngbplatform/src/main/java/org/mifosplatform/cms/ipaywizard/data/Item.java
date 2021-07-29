package org.mifosplatform.cms.ipaywizard.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

	@JsonProperty("@id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("isActive")
	private String isActive;

	@JsonProperty("type")
	private String type;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("pricing")
	private Pricing pricing;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Pricing getPricing() {
		return pricing;
	}

	public void setPricing(Pricing pricing) {
		this.pricing = pricing;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", isActive=" + isActive + ", type=" + type + ", description="
				+ description + ", pricing=" + pricing + "]";
	}
	
	

}
