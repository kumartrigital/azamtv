package org.mifosplatform.provisioning.provisioning.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;

public class ModelProvisionMappingData {

	private Long id;
	private Long provisioningId;
	private String model;
	private String provisioningName;
	//private String modelName;
	private String make;
	private String itemType;
	
	private List<NetworkElementData> provisionDatas;
	//private Collection<MCodeData> itemModelDatas;
	
	public ModelProvisionMappingData(){
		
	}
	
	public ModelProvisionMappingData(Long id, Long provisioningId,
			String model, String provisioningName,String make,String itemType) {
		
		this.id = id;
		this.provisioningId = provisioningId;
		this.model = model;
		this.provisioningName = provisioningName;
		//this.modelName = modelName;
		this.make= make;
		this.itemType = itemType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProvisioningId() {
		return provisioningId;
	}

	public void setProvisioningId(Long provisioningId) {
		this.provisioningId = provisioningId;
	}

	public String getModel() {
		return model;
	}

	public void setModelId(String model) {
		this.model = model;
	}

	public List<NetworkElementData> getProvisionDatas() {
		return provisionDatas;
	}

	public void setProvisionDatas(List<NetworkElementData> provisionDatas) {
		this.provisionDatas = provisionDatas;
	}
	
	/*public Collection<MCodeData> getProvisionDatas() {
		return provisionDatas;
	}

	public void setProvisionDatas(Collection<MCodeData> provisionDatas) {
		this.provisionDatas = provisionDatas;
	}*/

	/*public Collection<MCodeData> getItemModelDatas() {
		return itemModelDatas;
	}*/

	/*public void setItemModelDatas(Collection<MCodeData> itemModelDatas) {
		this.itemModelDatas = itemModelDatas;
	}*/
	
	
	
}
