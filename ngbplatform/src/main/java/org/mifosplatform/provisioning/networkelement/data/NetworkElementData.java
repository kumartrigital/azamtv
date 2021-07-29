package org.mifosplatform.provisioning.networkelement.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.provisioning.networkelement.domain.StatusTypeEnum;



public class NetworkElementData {


		private Long   id;
		private String systemcode;
		private String systemname;
		private String status;
		private boolean isGroupSupported;
		private List<StatusTypeEnum> statusTypeEnumList;
		

		public NetworkElementData(List<StatusTypeEnum> statusTypeEnumList)
		{
			this.statusTypeEnumList=statusTypeEnumList;
		}

		
	public NetworkElementData(Long id, String systemcode, String  systemname,String status, String isGroupSupported) {
		
		this.id = id;
		this.systemcode = systemcode;
		this.systemname = systemname;
		this.status = status;
		if(isGroupSupported==null||isGroupSupported.equalsIgnoreCase("N")){
			this.setGroupSupported(false);
		}else{
			this.setGroupSupported(true);
		}
	}
	
	
	public NetworkElementData(Long id, String systemcode) {
		this.id = id;
		this.systemcode = systemcode;

	}

	public Long getId() {
		return id;
	}


	public String getsystemcode(){
		return systemcode;
	}


	public String getsystemname(){
		return systemname;
	}
	public String getstatus(){
		return  status;
	}


	public boolean isGroupSupported() {
		return isGroupSupported;
	}


	public void setGroupSupported(boolean isGroupSupported) {
		this.isGroupSupported = isGroupSupported;
	}

	}

