package org.mifosplatform.organisation.broadcaster.data;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.finance.adjustment.data.AdjustmentData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.channel.data.ChannelData;

public class BroadcasterData {
	    
	private Long id;
	private String broadcasterCode;
	private String broadcasterName;
	private Long contactMobile;
	private Long contactNumber;
    private String contactName;
	private String email;
	private String address;
	private String pin;
	private List<ChannelData> channelDatas;


	public BroadcasterData(Long id, String broadcasterCode,String broadcasterName) {
		
		this.id = id;
		this.broadcasterCode = broadcasterCode;
		this.broadcasterName = broadcasterName;
	}



	public BroadcasterData(Long id, String broadcasterCode, String broadcasterName, Long contactMobile, Long contactNumber,
			String contactName, String email, String address, String pin) {
		this.id = id;
		this.broadcasterCode = broadcasterCode;
        this.broadcasterName = broadcasterName;
        this.contactMobile = contactMobile;
        this.contactNumber = contactNumber;
		this.contactName = contactName;
		this.email = email;
		this.address = address;
		this.pin = pin;
	}
		
		
		
		public Long getId() {
			return id;
		}
		
		
		public String getbroadcasterCode(){
			return broadcasterCode;
		}
		
		
		
		public String getbroadcasterName(){
			return broadcasterName;
		}
		
		
		
		
		public Long getcontactMobile() {
			return contactMobile;
		}
		
		
		
		public Long getcontactNumber(){
			return contactNumber;
		}
		
		
		
		public String getcontactName(){
			return contactName;
		}
		
		
		public String getemail(){
			return email;
		}
		
		
		
		public String getaddress(){
			return address;
		}
		
		
		
		public String getpin(){
			return pin;
		}
		
		public List<ChannelData> getChannelDatas() {
			return channelDatas;
		}



		public void setChannelDatas(List<ChannelData> channelDatas) {
			this.channelDatas = channelDatas;
		}
		
		
		public BroadcasterData( String broadcasterName,String pin,String email, Long contactMobile,Long contactNumber,String contactName,String broadcasterCode) {
			
			 this.broadcasterName = broadcasterName;
			 this.pin = pin;
			 this.email = email;
			 this.contactMobile = contactMobile;
			 this.contactNumber = contactNumber;
			 this.contactName = contactName;
			this.broadcasterCode = broadcasterCode;
			
		}
		public static BroadcasterData fromJsonToBroadcasterConfigCelcom(JsonCommand command) {
			
			final String broadcasterName=command.stringValueOfParameterNamed("broadcasterName");
			final String pin = command.stringValueOfParameterNamed("pin");
			final String email = command.stringValueOfParameterNamed("email");
			final Long contactMobile = command.longValueOfParameterNamed("contactMobile");
			final Long contactNumber =command.longValueOfParameterNamed("contactNumber");
			final String contactName=command.stringValueOfParameterNamed("contactName");
			final String broadcasterCode=command.stringValueOfParameterNamed("broadcasterCode");
			
	
			
			
		    return new BroadcasterData(broadcasterName,pin,email,contactMobile,contactNumber,contactName,broadcasterCode);
		}
		
		
		
			
		public String celcomRequestInputForBROADCASTERCONFIGCelcom(String userName) {
			
		
			StringBuilder sb = new StringBuilder("<COB_OP_UTILS_BCAST_CONFIG_inputFlist>");
			sb.append("<POID>0.0.0.1 /mso_broadcaster_info -1</POID>");
			sb.append("<ACCOUNT_OBJ>0.0.0.1 /account -1</ACCOUNT_OBJ>");
			sb.append("<NAME>"+this.broadcasterName+"</NAME>");
			sb.append("<COB_FLD_GST_REG_NO>"+this.pin+"</COB_FLD_GST_REG_NO>");
			sb.append("<COB_FLD_RMAIL>"+this.email+"</COB_FLD_RMAIL>");
			sb.append("<COB_FLD_RMN>"+this.contactMobile+"</COB_FLD_RMN>");
			sb.append("<PHONE>"+this.contactNumber+"</PHONE>");
			sb.append("<USER_NAME>"+this.contactName+"</USER_NAME>");
			sb.append("<CODE>"+this.broadcasterCode+"</CODE>");
			sb.append("</COB_OP_UTILS_BCAST_CONFIG_inputFlist>");
			
			return sb.toString();
			
		}

		
		
		
		
		
		
	}


