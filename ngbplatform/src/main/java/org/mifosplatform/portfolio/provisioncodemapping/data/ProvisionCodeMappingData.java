package org.mifosplatform.portfolio.provisioncodemapping.data;

import java.util.List;

import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.portfolio.product.data.ProductData;

public class ProvisionCodeMappingData {
	
	private Long     id;
	private String   provisionCode;
	private String   networkCode;
	private String   provisionValue;
	
	
	public ProvisionCodeMappingData(){
		
	}
	
	public ProvisionCodeMappingData(Long id, String provisionCode, String networkCode, String provisionValue  ) {
	
		this.id = id;
		this.provisionCode = provisionCode;
		this.networkCode = networkCode;
		this.provisionValue = provisionValue;
		

	}
	
	public ProvisionCodeMappingData(Long id, String provisionCode) {
		this.id = id;
		this.provisionCode = provisionCode;

	}
	

	public Long getId(){
		return id;
	}
	
	public String getProvisinCode(){
		return provisionCode;
	}
	
	public String getNetworkCode(){
		return networkCode;
	}
	
	public String getProvisionValue(){
		return provisionValue;
	}
	
	
}
