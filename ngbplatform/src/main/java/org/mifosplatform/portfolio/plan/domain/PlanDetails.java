package org.mifosplatform.portfolio.plan.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.plan.data.PlanData;
import org.mifosplatform.portfolio.product.domain.Product;

/**
 * @author hugo
 *
 */
@Entity
@Table(name = "b_plan_detail")
public class PlanDetails {

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@ManyToOne
    @JoinColumn(name="plan_id")
    private Plan plan;

	@Column(name ="product_id", length=50)
    private Long productId;

	@Column(name ="service_id", length=50)
    private Long serviceId;
	
	@Column(name = "is_deleted", nullable = false)
	private char isDeleted = 'N';

	@Column(name ="deal_poid", length=10)
    private Long dealPoid;

	public PlanDetails()
	{
		  // This constructor is intentionally empty. Nothing special is needed here.
	}
	public PlanDetails(final Long productId,final Long serviceId)
	{

		this.productId=productId;
	    this.serviceId=serviceId;
		this.plan=null;

	}

	public PlanDetails(final String serviceCode, Plan plan)
	{
		this.plan=plan;
	}
	
	public PlanDetails(final Long productId,final Long serviceId, Long dealPoid)
	{

		this.productId=productId;
	    this.serviceId=(long) 0;
		this.plan=null;
		this.dealPoid=dealPoid;
		
	}

	public Long getId() {
		return id;
	}
	public char getIsDeleted() {
		return isDeleted;
	}
	public Long getProductId() {
		return productId;
	}

	public Long getServiceId() {
		return serviceId;
	}
	public char isIsDeleted() {
		return isDeleted;
	}

	public Plan getPlan() {
		return plan;
	}

	public Long getDealPoid() {
		return dealPoid;
	}
	public void setDealPoid(Long dealPoid) {
		this.dealPoid = dealPoid;
	}
	public void update(final Plan plan){
		this.plan=plan;
	}
	
	public void delete() {
		this.isDeleted='Y';
	}
	
	public static PlanDetails fromJson(final JsonCommand command) {
		
		    final Long productId = command.longValueOfParameterNamed("productId");
		    final Long serviceId = command.longValueOfParameterNamed("serviceId");
		    return new PlanDetails(productId,serviceId);
	}

	public Map<String, Object> update(final Plan plan, final JsonCommand command){
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		/*final String serviceCodeName = "serviceCode";
			if (command.isChangeInStringParameterNamed(serviceCodeName, this.serviceCode)) {
				final String newValue = command.stringValueOfParameterNamed(serviceCodeName);
	            actualChanges.put(serviceCodeName, newValue);
	            this.serviceCode = StringUtils.defaultIfEmpty(newValue, null);
	        }*/
		final String serviceIdNamedParamName = "serviceId";
		if(command.isChangeInLongParameterNamed(serviceIdNamedParamName, this.serviceId)){
			final Long newValue = command.longValueOfParameterNamed(serviceIdNamedParamName);
			actualChanges.put(serviceIdNamedParamName, newValue);
			this.serviceId = newValue;
		}
	        return actualChanges;
	}
	
	public PlanDetails update(PlanDetails planDetails) {
		this.productId=planDetails.getProductId();
	    this.serviceId=planDetails.getServiceId();;
		this.plan=null;
		this.dealPoid=planDetails.getDealPoid();
		return new PlanDetails(productId, serviceId, dealPoid);
	}
	
	public static Set<PlanDetails> fromJsonPlanDetails(JSONObject plan) {
		Set<PlanDetails> planDetailsSet = new HashSet<PlanDetails>();
		try{
			JSONArray dealsArray;
			if(plan.optJSONArray("brm:DEALS")!=null){
				dealsArray = plan.optJSONArray("brm:DEALS");
			}else{
				dealsArray = new JSONArray("["+plan.getString("brm:DEALS")+"]");
			}
			for(int j=0;j<dealsArray.length();j++){
				JSONObject deals=dealsArray.getJSONObject(j);
				String dealCode=deals.optString("brm:POID");
				String[] dealCodeArray=dealCode.split(" ");
				Long code=Long.parseLong(dealCodeArray[2]);
				Long poid=code;
				JSONObject product=deals.optJSONObject("brm:PRODUCTS");
				String productPoidObject=product.optString("brm:POID");
				String[] productPoidArray=productPoidObject.split(" ");
				Long serviceId=Long.parseLong(productPoidArray[2]);
				PlanDetails planDetails= new PlanDetails(code,serviceId,poid);
				planDetailsSet.add(planDetails);
			}
		}catch(JSONException e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage());
		}
		return planDetailsSet;
	}
}