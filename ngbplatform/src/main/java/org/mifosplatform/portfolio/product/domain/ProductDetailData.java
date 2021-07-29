package org.mifosplatform.portfolio.product.domain;

import java.util.Collection;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class ProductDetailData {
	
	private final Long id;
	private final Long paramName;
	private final String paramType;
	private final String paramValue;
	private final String codeParamName;
	/*private String detailValue;
	private boolean isDetail;
	private Date detailDate;*/
	private final String productCode;
	private final String paramCategory;
	
	private Collection<MCodeData> details;
	
	

	public ProductDetailData(final Long id, final Long paramName,final String paramType, 
			final String paramValue,final String codeParamName,final String productCode,final String paramCategory) {
             
		this.id=id;
		this.paramName = paramName;
		this.paramType = paramType;
		this.paramValue = paramValue;
		this.codeParamName = codeParamName;
		this.productCode = productCode;
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
	

	/*public void setDetailValue(String detailValue) {
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
	}*/

	public Collection<MCodeData> getDetails() {
		return details;
	}

	public void setDetails(Collection<MCodeData> details) {
		this.details = details;
	}

	/*public Date getDetailDate() {
		return detailDate;
	}

	public void setDetailDate(Date detailDate) {
		this.detailDate = detailDate;
	}*/

	public String getProductCode() {
		return productCode;
	}
	
	public String getparamCategory() {
		return paramCategory;
	}
	
	
	
	
}
