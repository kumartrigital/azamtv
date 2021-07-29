package org.mifosplatform.portfolio.product.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.billing.emun.data.EnumValuesData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.portfolio.plan.data.ServiceData;
import org.mifosplatform.portfolio.product.domain.Product;
import org.mifosplatform.portfolio.product.domain.ProductDetailData;
import org.mifosplatform.portfolio.provisioncodemapping.data.ProvisionCodeMappingData;
import org.mifosplatform.portfolio.service.data.ServiceDetailData;

public class ProductData {
    
	private Long      id;
	private String    productCode;
	private String    productDescription;
	private String   productCategory;
	private Long      serviceId;
	private String    serviceCode;
	/*private Long      provisionId;
	private Date      validityStart;
	private Date      validityEnd;*/
	private String    productStatus;
	private Long productPoid;
	private int priority;
	private BigDecimal price;
	private String serviceType;
	private String contractPeriod;
	private String chargeCode;
	private Boolean isBouquet ;
	private String bouquet;
	
		//for template purpose
		private List<ProvisionCodeMappingData> provisionCodeMappingDatas;
		private List<ServiceData> serviceDatas;
		private List<EnumOptionData> status;
		private Collection<MCodeData> serviceParamsData;
		private Collection<ProductDetailData> productDetailDatas;
		private List<ServiceData> serviceCategorys;
		private Collection<ServiceDetailData> serviceDetailDatas;
		private Integer scaledAmount;
		private String locale;
		
		
	
	public ProductData(){
		
	}
	
	
	public ProductData(Long id, String productCode, String productDescription, String productCategory, Long serviceId,
			/*Long provisionId, Date validityStart, Date validityEnd,*/ String serviceCode, String status,Boolean isBouquet) {
		
		this.id = id;
		this.productCode = productCode;
		this.productDescription = productDescription;
		this.productCategory = productCategory;
		this.serviceId = serviceId;
		this.serviceCode = serviceCode;
		/*this.provisionId = provisionId;
		this.validityStart = validityStart;
		this.validityEnd = validityEnd;*/
		this.productStatus = status;
		this.isBouquet = isBouquet;
		if(isBouquet){
			bouquet="Yes";
		}else{
			bouquet="No";
		}
	}
	

	public ProductData(List<ProvisionCodeMappingData> provisionCodeMappingDatas, List<ServiceData> serviceDatas,
		   final List<EnumOptionData> status,final Collection<ProductDetailData> productDetailDatas,
		   final  List<ServiceData> serviceCategorys,final Collection<ServiceDetailData> serviceDetailDatas) {
		this.provisionCodeMappingDatas = provisionCodeMappingDatas;
		this.serviceDatas = serviceDatas;
		this.status = status;
		this.productDetailDatas = productDetailDatas;
		this.serviceCategorys = serviceCategorys;
		this.serviceDetailDatas = serviceDetailDatas;
	}


	public ProductData(String productCode, String description,
			String productCategory, Long serviceId, String status,
			Long productPoid, Integer priority,Boolean isBouquet) {
		// TODO Auto-generated constructor stub
		this.productCode=productCode;
		this.productDescription=productDescription;
		this.productCategory=productCategory;
		this.serviceId=serviceId;
		this.productStatus=status;
		this.setProductPoid(productPoid);
		this.priority=priority;
		this.isBouquet=isBouquet;
	}


	public ProductData(String productCode2, String description,Long poid, Integer priority2, BigDecimal price, String contractPeriod, String chargeCode) {
		this.productCode=productCode2;
		this.productDescription=description;
		this.productPoid=poid;
		this.priority=priority2;
		this.price=price;
		this.locale="en";
		this.contractPeriod = contractPeriod;
		this.chargeCode =chargeCode;
	}
	    
	public ProductData(String productCode, String description,
			String productCategory, Long serviceId, String status,
			Long productPoid, Integer priority, BigDecimal price,Boolean isBouquet) {
		// TODO Auto-generated constructor stub
		this.productCode=productCode;
		this.productDescription=description;
		this.productCategory=productCategory;
		this.serviceId=serviceId;
		this.productStatus=status;
		this.productPoid=productPoid;
		this.priority=priority;
		this.price=price;
		this.isBouquet=isBouquet;
	}

	public Long getId(){
		return id;
	}
	
	public String getProductCode(){
		return productCode;
	}
	
	public String getProductDescription(){
		return productDescription;
	}
	
	public String getProductCategory(){
		return productCategory;
	}

	public Long getServiceId(){
		return serviceId;
	}
	
	/*public Long getProvisionId(){
		return provisionId;
	}
	
	public Date getValidityStart(){
		return validityStart;
	}
	
	public Date getValidityEnd(){
		return validityEnd;
	}*/
	
	public String getProductStatus() {
		return productStatus;
	}
	
	public void setId(final Long id) {
		this.id = id;
	}

	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}

	public void setProductDescription(final String productDescription) {
		this.productDescription = productDescription;
	}

	/*public void setServiceType(final String serviceType) {
		this.serviceType = serviceType;
	}*/

	public void setProductStatus(final String productStatus) {
		this.productStatus = productStatus;
	}

	/*public void setServiceUnitType(final String serviceUnitType) {
		this.serviceUnitType = serviceUnitType;
	}

	public void setIsOptional(final String isOptional) {
		this.isOptional = isOptional;
	}

	public void setServiceTypes(final Collection<EnumValuesData> serviceTypes) {
		this.serviceTypes = serviceTypes;
	}

	public void setServiceUnitTypes(final List<EnumOptionData> serviceUnitTypes) {
		this.serviceUnitTypes = serviceUnitTypes;
	}*/


	public List<ServiceData> getServiceDatas() {
		return serviceDatas;
	}
	
	public List<ProvisionCodeMappingData> getProvisionCodeMappingDatas(){
		return provisionCodeMappingDatas;
	}

	public void setProvisionCodeMappingDatas(List<ProvisionCodeMappingData> provisionCodeMappingDatas) {
		
		this.provisionCodeMappingDatas = provisionCodeMappingDatas;
	}


	public void setServiceMasterDatas(List<ServiceData> serviceDatas) {
		
		this.serviceDatas = serviceDatas;
	
	}
	
	public void setStatus(final List<EnumOptionData> status) {
		this.status = status;
	}
	
	public List<EnumOptionData> getStatus() {
		return status;
	}


	public void setServiceParamsData(Collection<MCodeData> serviceParamsData) {
		this.serviceParamsData = serviceParamsData;
	}
	
	public Collection<MCodeData> getServiceParamsData() {
		return serviceParamsData;
	}


	public void setProductDetailData(Collection<ProductDetailData> productDetailDatas) {
		this.productDetailDatas = productDetailDatas;
		
	}
	
	public Collection<ProductDetailData> getProductDetailDatas() {
		return productDetailDatas;
	}

	/*public void setServiceCategorys(List<ProductData> serviceCategorys) {
		this.serviceCategorys = serviceCategorys;
	}*/


	public void setServiceCategorys(List<ServiceData> serviceCategorys) {
		this.serviceCategorys = serviceCategorys;
		
	}


	public void setServiceDetailData(Collection<ServiceDetailData> serviceDetailDatas) {
		this.serviceDetailDatas = serviceDetailDatas;		
	}
	
	public Collection<ServiceDetailData> getServiceDetailDatas() {
		return serviceDetailDatas;
	}

	public String getServiceCode(){
		return serviceCode;
	}

	/*public String getServiceCategory() {
		return serviceCategory;
	}


	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}*/

	public Long getProductPoid() {
		return productPoid;
	}


	public void setProductPoid(Long productPoid) {
		this.productPoid = productPoid;
	}
	

	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}
	public BigDecimal getPrice() {
		return price;
	}


	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Boolean getisBouquet() {
		return isBouquet;
	}


	public void setisBouquet(Boolean isBouquet) {
		this.isBouquet = isBouquet;
	}


	public Product toProduct(ProductData productData) {
		// TODO Auto-generated method stub
		return new Product(productData.getProductCode(), productData.getProductDescription(), productData.getProductCategory(),
				productData.getServiceId(), productData.getProductStatus(), productData.getProductPoid(), productData.getPriority());
	}


	
	
	
	
	
	
	
}
