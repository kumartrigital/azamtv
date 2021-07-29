package org.mifosplatform.portfolio.service.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.client.data.ClientData;
import org.mifosplatform.provisioning.networkelement.data.NetworkElementData;

public class ServiceDetailData {
	
	private final Long id;
	private final Long paramName;
	private final String paramType;
	private final String paramValue;
	private final String codeParamName;
	private String detailValue;
	private boolean isDetail;
	private Date detailDate;
	private final String serviceCode;
	private final String paramCategory;
	
	private List<NetworkElementData> details;
	private ClientData clientDetails;
	
	

	public ServiceDetailData(final Long id, final Long paramName,final String paramType, 
			final String paramValue,final String codeParamName,final String serviceCode,final String paramCategory) {
             
		this.id=id;
		this.paramName = paramName;
		this.paramType = paramType;
		this.paramValue = paramValue;
		this.codeParamName = codeParamName;
		this.serviceCode = serviceCode;
		this.paramCategory=paramCategory;
	
	}

	public Long getId() {
		return id;
	}

	public Long getParamName() {
		return paramName;
	}

	public String getParamType() {
		return paramType;
	}

	public String getParamValue() {
		return paramValue;
	}

	public String getCodeParamName() {
		return codeParamName;
	}
	

	public void setDetailValue(String detailValue) {
		this.detailValue = detailValue;
	}

	public void setDetail(boolean isDetail) {
		this.isDetail = isDetail;
	}

	

	public String getDetailValue() {
		return detailValue;
	}

	public boolean isDetail() {
		return isDetail;
	}

	/*public Collection<MCodeData> getDetails() {
		return details;
	}

	public void setDetails(Collection<MCodeData> details) {
		this.details = details;
	}*/
	
	public List<NetworkElementData> getDetails() {
		return details;
	}

	public void setDetails(List<NetworkElementData> details) {
		this.details = details;
	}

	public Date getDetailDate() {
		return detailDate;
	}

	public void setDetailDate(Date detailDate) {
		this.detailDate = detailDate;
	}

	public String getServiceCode() {
		return serviceCode;
	}
	
	public String getparamCategory() {
		return paramCategory;
	}

	public ClientData getClientDetails() {
		return clientDetails;
	}
	
	public void setClientDetails(ClientData clientDetails) {
		this.clientDetails = clientDetails;
		
	}
	
	
	
	
}
