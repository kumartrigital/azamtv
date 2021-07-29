package org.mifosplatform.logistics.item.domain;

import org.mifosplatform.infrastructure.core.api.JsonCommand;

public class ItemMasterData {
	public String oldStbId;
	
	public String newStbId;
	public String oldScId;
	public String newScId;
	public String clientPoId;
	public String clientServicePoId;
	
	public ItemMasterData() {
		// TODO Auto-generated constructor stub
	}
	
	public ItemMasterData(String clientPoId,  String clientServicePoId,String newStbId, String newScId){
		this.clientPoId=clientPoId;
		this.clientServicePoId=clientServicePoId;
		this.newStbId=newStbId;
		this.newScId=newScId;
	}
	
	
	public String celcomRequestInputForSwapDevice(String userName) {

		StringBuilder sb = new StringBuilder("<COB_OP_CUST_UPDATE_SERVICE_inputFlist>");
		sb.append("<POID>0.0.0.1 /account "+this.clientPoId+" 0</POID>");
		sb.append("<ACCOUNT_OBJ>0.0.0.1 /account "+this.clientPoId+" 0</ACCOUNT_OBJ>");
		sb.append("<SERVICE_OBJ>0.0.0.1 /service/tv "+this.clientServicePoId+" 0</SERVICE_OBJ>");
		sb.append("<PROGRAM_NAME>CRM|"+userName+"</PROGRAM_NAME>");
		sb.append("<SERVICES elem=\"0\">");
		sb.append("<POID>0.0.0.1 /service/tv "+this.clientServicePoId+" 1</POID>");
		if(this.newStbId!=null){
			sb.append("<ALIAS_LIST elem=\"0\">");
			sb.append("<NAME>"+this.newStbId+"</NAME>");
			sb.append("</ALIAS_LIST>");
		}
		if(this.newScId!=null){
			sb.append("<ALIAS_LIST elem=\"1\">");
			sb.append("<NAME>"+this.newScId+"</NAME>");
			sb.append("</ALIAS_LIST>");
		}
		sb.append("</SERVICES>");
		sb.append("</COB_OP_CUST_UPDATE_SERVICE_inputFlist>");
		
		return sb.toString();
	}
}
