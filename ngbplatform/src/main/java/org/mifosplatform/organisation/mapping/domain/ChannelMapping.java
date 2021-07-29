package org.mifosplatform.organisation.mapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.useradministration.domain.AppUser;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_prd_ch_mapping")
public class ChannelMapping extends AbstractPersistable<Long>{
	
	@Column(name="product_id", nullable=false)
	private Long productId;
	
	@Column(name="channel_id", nullable=false)
	private Long channelId;
	
	@Column(name="is_deleted")
	private char isDeleted;

	public ChannelMapping() {}
	
	
	
	public ChannelMapping(Long productId, Long channelId) {
		this.productId = productId;
		this.channelId = channelId;
		this.isDeleted = 'N';
	}
	
	public ChannelMapping(Long id) {
		this.productId = id;
	}
	

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}
	

	public Long getChannelId() {
		return channelId;
	}

	public void setChannelId(Long channelId) {
		this.channelId = channelId;
	}
	

	public static ChannelMapping formJson(JsonCommand command) {
		
		Long productId = command.longValueOfParameterNamed("productId");
		
		return new ChannelMapping(productId);
	}
	
	

	/*public Map<String, Object> update(JsonCommand command) {
		
final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String productIdNamedParamName = "productId";
		final String channelIdNamedParamName = "channelId";
		
		
		
		
		if(command.isChangeInLongParameterNamed(productIdNamedParamName, this.productId)){
			final Long newValue = command.longValueOfParameterNamed(productIdNamedParamName);
			actualChanges.put(productIdNamedParamName, newValue);
			this.productId = newValue;
		}
		

		if(command.isChangeInLongParameterNamed(channelIdNamedParamName, this.channelId)){
			final Long newValue = command.longValueOfParameterNamed(channelIdNamedParamName);
			actualChanges.put(channelIdNamedParamName, newValue);
			this.channelId = newValue;
		}
		
		return actualChanges;
	
	}*/

	public char getIsDeleted() {
		return isDeleted;
	}
	
	public void setIsDeleted(char isDeleted) {
		this.isDeleted = isDeleted;
	}

	/*public void delete() {
		this.isDeleted = 'Y';
		
	}*/
	
	public void delete() {
		if(this.isDeleted != 'Y'){
			this.isDeleted = 'Y';
		}
	}

}
