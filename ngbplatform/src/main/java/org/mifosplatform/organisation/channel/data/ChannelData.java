package org.mifosplatform.organisation.channel.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.channel.domain.LanguageEnum;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class ChannelData {

	private Long   id;
	private String channelName;
	private String channelCategory;
	private String language;
	private String   channelType;
	private Boolean   isLocalChannel;
    private Boolean isHdChannel;
	private Long   channelSequence;
	private Long   broadcasterId;
	private String   broadcasterName;

	//for template purpose
	private List<BroadcasterData> broadcasterDatas;
	private List<LanguageEnum> languageEnum;
	Collection<MCodeData> channelCategorys;
	public ChannelData() {
	}

	public ChannelData(Long id, String channelName, String channelCategory,String language, String channelType, Boolean isLocalChannel,
			Boolean isHdChannel, Long channelSequence, Long broadcasterId, String broadcasterName) {
		
		this.id = id;
		this.channelName = channelName;
		this.channelCategory = channelCategory;
		this.language = language;
		this.channelType = channelType;
		this.isLocalChannel = isLocalChannel;
		this.isHdChannel = isHdChannel;
		this.channelSequence = channelSequence;
		this.broadcasterId = broadcasterId;
		this.broadcasterName = broadcasterName;
		
	}
	
	public ChannelData(Long id, String channelName) {
		this.id = id;
		this.channelName = channelName;

	}
	
	public ChannelData(Long id, String channelName,String channelType) {
		this.id = id;
		this.channelName = channelName;
		this.channelType = channelType;

	}

	
	public ChannelData(final List<BroadcasterData> broadcasterDatas,final  List<LanguageEnum> languageEnum) {
		this.broadcasterDatas = broadcasterDatas;
		this.languageEnum =languageEnum;
	}


	public Long getId() {
		return id;
	}
	
	
	public String getchannelName(){
		return channelName;
	}
	

	public String getchannelCategory(){
		return channelCategory;
	}
	public String getlanguage(){
		return language;
	}
	
	public String getchannelType(){
		return channelType;
	}
	

	public Boolean getisLocalChannel(){
		return isLocalChannel;
	}
	
	public Boolean getisHdChannel(){
		return isHdChannel;
	}
	
	public Long getchannelSequence(){
		return channelSequence;
	}
    
	public Long getbroadcasterId(){
		return broadcasterId;
	}
	
	public String getbroadcasterName(){
		return broadcasterName;
	}


	public List<BroadcasterData> getBroadcasterDatas() {
		return broadcasterDatas;
	}


	public void setBroadcasterDatas(List<BroadcasterData> broadcasterDatas) {
		this.broadcasterDatas = broadcasterDatas;
	}

	public Collection<MCodeData> getChannelCategorys() {
		return channelCategorys;
	}

	public void setChannelCategorys(Collection<MCodeData> channelCategorys) {
		this.channelCategorys = channelCategorys;
	}

	public void setLanguageEnum(List<LanguageEnum> languageEnum) {
		
     this.languageEnum = languageEnum;
		
	}
	
	public List<LanguageEnum> getLanguageEnum() {
		return languageEnum;
	}
	
	public ChannelData(String channelName,String broadcasterName,Boolean isHdChannel,String channelCategory,String language, String channelType) {
		
	
		this.channelName = channelName;
		this.broadcasterName = broadcasterName;
		this.isHdChannel = isHdChannel;
		this.channelCategory = channelCategory;
		this.language = language;
		this.channelType = channelType;
	
	}
	
	
	public static ChannelData fromJsonToChannelConfigCelcom(JsonCommand command) {
		
		final String channelName=command.stringValueOfParameterNamed("channelName");
		final String broadcasterName = command.stringValueOfParameterNamed("broadcasterName");
		final Boolean isHdChannel = command.booleanPrimitiveValueOfParameterNamed("isHdChannel");
		final String channelCategory = command.stringValueOfParameterNamed("channelCategory");
		final String language =command.stringValueOfParameterNamed("language");
		final String channelType=command.stringValueOfParameterNamed("channelType");
		

		
		
	    return new ChannelData(channelName,broadcasterName,isHdChannel,channelCategory,language,channelType);
	}
	
	
	
		
	public String celcomRequestInputForCHANNELCONFIGCelcom(String userName) {
		
	
		StringBuilder sb = new StringBuilder("<COB_OP_UTILS_BCAST_CONFIG_inputFlist>");
		sb.append("<POID>0.0.0.1 /mso_channel_info -1</POID>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account -1</ACCOUNT_OBJ>");
		sb.append("<COB_FLD_CHANNEL_NAME>"+this.channelName+"</COB_FLD_CHANNEL_NAME>");
		sb.append("<COB_FLD_BCAST_NAME>"+this.broadcasterName+"</COB_FLD_BCAST_NAME>");
		sb.append("<COB_FLD_CHANNEL_TYPE>"+this.isHdChannel+"</COB_FLD_CHANNEL_TYPE>");
		sb.append("<COB_FLD_CHANNEL_CATEGORY>"+this.channelCategory+"</COB_FLD_CHANNEL_CATEGORY>");
		sb.append("<COB_FLD_LANGUAGE>"+this.language+"</COB_FLD_LANGUAGE>");
		sb.append("<COB_FLD_CHANNEL_PAYTYPE>"+this.channelType+"</COB_FLD_CHANNEL_PAYTYPE>");
		sb.append("<COB_FLD_REGION>ALL</COB_FLD_REGION>");
		sb.append("<FLAGS>0</FLAGS>");
		sb.append("</COB_OP_UTILS_BCAST_CONFIG_inputFlist>");
		
		return sb.toString();
		
	}

	
	
	
	
	
}
