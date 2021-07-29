package org.mifosplatform.organisation.channel.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.logistics.supplier.domain.Supplier;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name="b_channel")
public class Channel extends AbstractPersistable<Long>{
	
	@Column(name="channel_name", nullable=false)
	private String channelName;
	
	@Column(name="channel_category", nullable=false)
	private String channelCategory;
	
	@Column(name="channel_type", nullable=false)
	private String channelType;
	
	@Column(name="is_local_channel", nullable=false)
	private char isLocalChannel ;
	
	@Column(name="is_hd_channel", nullable=false)
	private char isHdChannel;
	
	@Column(name="channel_sequence", nullable=false)
	private Long channelSequence;
	
	@Column(name="broadcaster_id", nullable=false)
	private Long broadcasterId;
	
	@Column(name="is_deleted")
	private char isDeleted;
	
	@Column(name="language", nullable=false)
	private String language;
	
	
	
	public Channel() {}
	
	public Channel(String channelName,String channelCategory,String channelType,boolean isLocalChannel,boolean isHdChannel,Long channelSequence,Long broadcasterId,String broadcasterName,String language ) {
		
		this.channelName = channelName;
		this.channelCategory = channelCategory;
		this.channelType = channelType;
		this.isLocalChannel = isLocalChannel?'Y':'N';
		this.isHdChannel = isHdChannel?'Y':'N';
		this.channelSequence = channelSequence;
		this.broadcasterId = broadcasterId;
		this.isDeleted = 'N';
		this.language = language;
	}
	
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelCategory() {
		return channelCategory;
	}

	public void setChannelCategory(String channelCategory) {
		this.channelCategory = channelCategory;
	}
   
	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
	
	public char getIsLocalChannel() {
		return isLocalChannel;
	}

	public void setIsLocalChannel(char isLocalChannel) {
		this.isLocalChannel = isLocalChannel;
	}
	
	public char getIsHdChannel() {
		return isHdChannel;
	}

	public void setIsHdChannel(char isHdChannel) {
		this.isHdChannel = isHdChannel;
	}
	
	public Long getChannelSequence() {
		return channelSequence;
	}
	
	public void setChannelSequence(Long channelSequence) {
		this.channelSequence = channelSequence;
	}
	
	public Long getBroadcasterId() {
		return broadcasterId;
	}
	
	public void setBroadcasterId(Long broadcasterId) {
		this.broadcasterId = broadcasterId;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	
	public static Channel formJson(JsonCommand command) {
		String channelName = command.stringValueOfParameterNamed("channelName");
		String channelCategory = command.stringValueOfParameterNamed("channelCategory");
		String channelType = command.stringValueOfParameterNamed("channelType");
		boolean isLocalChannel = command.booleanPrimitiveValueOfParameterNamed("isLocalChannel");
		boolean isHdChannel  = command.booleanPrimitiveValueOfParameterNamed("isHdChannel");
		Long channelSequence  = command.longValueOfParameterNamed("channelSequence");
		Long broadcasterId  = command.longValueOfParameterNamed("broadcasterId");
		String broadcasterName  = command.stringValueOfParameterNamed("broadcasterName");
		String language = command.stringValueOfParameterNamed("language");
		
		return new Channel(channelName, channelCategory, channelType,
				isLocalChannel, isHdChannel, channelSequence,broadcasterId,broadcasterName,language);
	}

	
	public Map<String, Object> update(JsonCommand command) {
		
final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String channelNameNamedParamName = "channelName";
		final String channelCategoryNamedParamName = "channelCategory";
		final String channelTypeNamedParamName = "channelType";
		final String isLocalChannelNamedParamName = "isLocalChannel";
		final String isHdChannelNamedParamName = "isHdChannel";
		final String channelSequenceNamedParamName = "channelSequence";
		final String broadcasterIdNamedParamName = "broadcasterId";
		final String languageNamedParamName = "language";

		
		if(command.isChangeInStringParameterNamed(channelNameNamedParamName, this.channelName)){
			final String newValue = command.stringValueOfParameterNamed(channelNameNamedParamName);
			actualChanges.put(channelNameNamedParamName, newValue);
			this.channelName = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		if(command.isChangeInStringParameterNamed(channelCategoryNamedParamName, this.channelCategory)){
			final String newValue = command.stringValueOfParameterNamed(channelCategoryNamedParamName);
			actualChanges.put(channelCategoryNamedParamName, newValue);
			this.channelCategory = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		
		
		if(command.isChangeInStringParameterNamed(channelTypeNamedParamName, this.channelType)){
			final String newValue = command.stringValueOfParameterNamed(channelTypeNamedParamName);
			actualChanges.put(channelTypeNamedParamName, newValue);
			this.channelType = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final boolean newvalueOfisLocalChannel = command.booleanPrimitiveValueOfParameterNamed(isLocalChannelNamedParamName);
		if(String.valueOf(newvalueOfisLocalChannel?'Y':'N').equalsIgnoreCase(String.valueOf(this.isLocalChannel))){
			actualChanges.put(isLocalChannelNamedParamName, newvalueOfisLocalChannel);
		}
		this.isLocalChannel = newvalueOfisLocalChannel?'Y':'N';
		
		
		final boolean newvalueOfisHdChannel = command.booleanPrimitiveValueOfParameterNamed(isHdChannelNamedParamName);
		if(String.valueOf(newvalueOfisHdChannel?'Y':'N').equalsIgnoreCase(String.valueOf(this.isHdChannel))){
			actualChanges.put(isHdChannelNamedParamName, newvalueOfisHdChannel);
		}
		this.isHdChannel = newvalueOfisHdChannel?'Y':'N';
		
		
		if(command.isChangeInLongParameterNamed(channelSequenceNamedParamName, this.channelSequence)){
			final Long newValue = command.longValueOfParameterNamed(channelSequenceNamedParamName);
			actualChanges.put(channelSequenceNamedParamName, newValue);
			this.channelSequence = newValue;
		}
		
		if(command.isChangeInLongParameterNamed(broadcasterIdNamedParamName, this.broadcasterId)){
			final Long newValue = command.longValueOfParameterNamed(broadcasterIdNamedParamName);
			actualChanges.put(broadcasterIdNamedParamName, newValue);
			this.broadcasterId = newValue;
		}
		
		if(command.isChangeInStringParameterNamed(languageNamedParamName, this.language)){
			final String newValue = command.stringValueOfParameterNamed(languageNamedParamName);
			actualChanges.put(languageNamedParamName, newValue);
			this.language = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		
		return actualChanges;
	
	
	}

	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void delete() {	
	this.isDeleted = 'Y';
	}
	

}
