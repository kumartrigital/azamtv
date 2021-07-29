package org.mifosplatform.portfolio.client.service;

import java.util.List;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

public class ClientCategoryData {
	
	private final Long id;
	private final String categoryType;
	private final String billMode;
	private final String accountNo;
	private final String displayName;
	private final String displayLabel;
	private Boolean count;
	private List<ClientCategoryData> parentClientData;
	private Long poId;
	private String email;
	private String phone;
	private EnumOptionData status;
	
	public ClientCategoryData(final Long id,final String categoryType,final String billMode,final String accountNo,final String displayName,
			final List<ClientCategoryData> parentClientData,final Boolean count) {
           this.id=id;
           this.categoryType=categoryType;
           this.billMode = billMode;
           this.accountNo = accountNo;
           this.displayName = displayName;
           if(displayName!=null){
           this.displayLabel = generateLabelName();
           }else{
        	   this.displayLabel = null; 
           }
           this.count=count;
           this.setParentClientData(parentClientData);
	}

	public ClientCategoryData(final Long id,final String categoryType,final String billMode,final String accountNo,final String displayName,
			String poId, String email, String phone, EnumOptionData status) {
           this.id=id;
           this.categoryType=categoryType;
           this.billMode = billMode;
           this.accountNo = accountNo;
           this.displayName = displayName;
           if(displayName!=null){
           this.displayLabel = generateLabelName();
           }else{
        	   this.displayLabel = null; 
           }
           if(poId!=null)
           this.poId=Long.valueOf(poId);
           this.email=email;
           this.phone=phone;
           this.status=status;
	}
	
	private String generateLabelName() {
		 StringBuilder builder = new StringBuilder(this.displayName).append('[').append(this.accountNo).append(']');
		 //builder.append('[').append(this.accountNo).append(']');
		 return builder.toString();
	}

	public Long getId() {
		return id;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public String getBillMode() {
		return billMode;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDisplayLabel() {
		return displayLabel;
	}

	public Boolean getCount() {
		return count;
	}
    
	public Boolean setCount(Boolean count){
		return this.count=count;
	}

	public List<ClientCategoryData> getParentClientData() {
		return parentClientData;
	}
	
	public void setParentClientData(List<ClientCategoryData> parentClientData) {
		this.parentClientData = parentClientData;
	}
	
	public Long getPoId() {
		return poId;
	}

	public void setPoId(Long poId) {
		this.poId = poId;
	}

}
