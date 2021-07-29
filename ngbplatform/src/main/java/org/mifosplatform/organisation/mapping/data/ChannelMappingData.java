package org.mifosplatform.organisation.mapping.data;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.salescataloge.data.SalesCatalogeData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.product.data.ProductData;
import org.mifosplatform.portfolio.service.data.ServiceMasterData;

public class ChannelMappingData {
	
	private Long   id;
	private Long productId;
	private Long channelId;
	private String productCode;
	private String productDescription;
	private String channelName;
	private LocalDate fromDate;
	private LocalDate toDate;
	private String paramvalue;
	private String paramName;




	//for template purpose
	private List<ChannelData> channelDatas;
	private List<ServiceData> productDatas;
	
	
	private List<ChannelData> availableChannels;
	private List<ChannelData> selectedChannels;
	
    public ChannelMappingData() {
    
    }
	
	public ChannelMappingData(Long id, Long productId, Long channelId,String productCode,String productDescription,String channelName,LocalDate fromDate,LocalDate toDate) {
		this.id = id;
		this.productId = productId;
		this.channelId = channelId;
		this.productCode = productCode;
		this.productDescription =productDescription;
		this.channelName = channelName;
		this.fromDate=fromDate;
		this.toDate=toDate;
		
	}

	public ChannelMappingData(final List<ChannelData> channelDatas,final List<ServiceData> productDatas) {
		this.channelDatas = channelDatas;
		this.productDatas = productDatas;
	}
	
	public Long getId() {
		return id;
	}
	

	public Long getProductId() {
		return productId;
	}
	

	public Long getchannelId() {
		return channelId;
	}
	

	public String getProductCode(){
		return productCode;
	}
	
	public String getProductDescription(){
		return productDescription;
	}
	public String getchannelName(){
		return channelName;
	}
	
	public List<ServiceData> getProductDatas() {
		return productDatas;
	}
	
	public List<ChannelData> getChannelDatas() {
		return channelDatas;
	}
	
	public void setProductDatas(List<ServiceData> productDatas) {
		this.productDatas = productDatas;		
	}

	public void setChannelDatas(List<ChannelData> channelDatas) {
		this.channelDatas = channelDatas;
		
	}
	
	public LocalDate getFromDate(){
		return fromDate;
	}
	public LocalDate getToDate(){
		return toDate;
	}

	public List<ChannelData> getAvailableChannels() {
		return availableChannels;
	}

	public List<ChannelData> getSelectedChannels() {
		return selectedChannels;
	}

	public void setAvailaableChannels(List<ChannelData> availableChannels) {
		this.availableChannels = availableChannels;
	}

	public void setSelectedChannels(List<ChannelData> selectedChannels) {
		this.selectedChannels = selectedChannels;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	public ChannelMappingData(String productCode,String productDescription,String paramvalue,String paramName,String channelName) {
	
		this.productCode = productCode;
		this.productDescription =productDescription;
		this.paramvalue = paramvalue;
		this.paramName=paramName;
		this.channelName=channelName;
		
	}
	
	
	
	
		
	public String celcomRequestInputForPRODUCTCONFIGCelcom(String userName, List<ChannelMappingData> channelMappingDataList) {
		
		
		
	
		StringBuilder sb = new StringBuilder("<COB_OP_UTILS_BCAST_CONFIG_inputFlist>");
		sb.append("<POID>0.0.0.1 /mso_pack_channel_map</POID>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account -1</ACCOUNT_OBJ>");
		sb.append("<PRODUCT_OBJ>"+this.productCode+"</PRODUCT_OBJ>");
		sb.append("<PRODUCT_NAME>"+this.productDescription+"</PRODUCT_NAME>");
		for(ChannelMappingData channelMappingData : channelMappingDataList){
			if(channelMappingData.getParamName().equalsIgnoreCase("ABV")){
				sb.append("<COB_FLD_ABV_CODE>"+channelMappingData.getParamvalue()+"</COB_FLD_ABV_CODE>");
			}else if(channelMappingData.getParamName().equalsIgnoreCase("IRDETO")){
				sb.append("<COB_FLD_IRDETO_CODE>"+channelMappingData.getParamvalue()+"</COB_FLD_IRDETO_CODE>");
			}else if (channelMappingData.getParamName().equalsIgnoreCase("CISCO")){
				sb.append("<COB_FLD_CISCO_CODE>"+channelMappingData.getParamvalue()+"</COB_FLD_CISCO_CODE>");
			}else if(channelMappingData.getParamName().equalsIgnoreCase("GOSPEL")){
				sb.append("<COB_FLD_GOSPELL_CODE>"+channelMappingData.getParamvalue()+"</COB_FLD_GOSPELL_CODE>");
			}
		}
		sb.append("<COB_FLD_CHANNELS elem=0>");
		sb.append("<COB_FLD_CHANNEL_NAME>"+this.channelName+"</COB_FLD_CHANNEL_NAME>");
		sb.append("</COB_FLD_CHANNELS>");
		sb.append("</COB_OP_UTILS_BCAST_CONFIG_inputFlist>");
		
		return sb.toString();
		
	}
	

	/*public void setServiceMasterDatas(List<ServiceMasterData> retrieveServiceMastersForDropdown) {
		this.serviceMasterDatas = serviceMasterDatas;
		
	}*/
	public String getParamvalue() {
		return paramvalue;
	}

	public void setParamvalue(String paramvalue) {
		this.paramvalue = paramvalue;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	
	
	
}
