package org.mifosplatform.portfolio.property.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "b_property_defination", uniqueConstraints = @UniqueConstraint(name = "property_code_constraint", columnNames = { "property_code" }))
public class PropertyMaster  extends AbstractAuditableCustom<AppUser, Long> {

	private static final long serialVersionUID = 1L;

	@Column(name = "property_type_id")
	private Long propertyTypeId;

	@Column(name = "property_code")
	private String propertyCode;

	@Column(name = "unit_code")
	private String unitCode;

	@Column(name = "floor")
	private String floor;

	@Column(name = "building_code")
	private String buildingCode;

	@Column(name = "parcel")
	private String parcel;

	@Column(name = "precinct")
	private String precinct;

	@Column(name = "street")
	private String street;

	@Column(name = "po_box")
	private String poBox;

	@Column(name = "state")
	private String state;

	@Column(name = "country")
	private String country;

	@Column(name = "status")
	private String status;

	@Column(name = "client_id")
	private Long clientId;

	@Column(name = "is_deleted")
	private char isDeleted = 'N';
	
	@Column(name = "district")
	private String district;
	
	public PropertyMaster(){
		
	}
	
	
	public PropertyMaster(final Long propertyTypeId, final String propertyCode,final String unitCode, final String floor, final String buildingCode,
			final String parcel, final String precinct, final String poBox, final String street,final String state, final String country, String status,final String district) {
        
		this.propertyTypeId = propertyTypeId;
		this.propertyCode = propertyCode;
		this.unitCode = unitCode;
	    this.floor = floor;
	    this.buildingCode = buildingCode;
	    this.parcel = parcel;
	    this.precinct = precinct;
	    this.poBox = poBox;
	    this.street = street;
	    this.state = state;
	    this.status = status;
	    this.country = country;
	    this.status = CodeNameConstants.CODE_PROPERTY_VACANT;
	    this.district = district;
	}


	public static PropertyMaster fromJson(final JsonCommand command) {

		final String propertyCode = command.stringValueOfParameterNamed("propertyCode");
		final Long propertyTypeId = command.longValueOfParameterNamed("propertyType");
		final String unitCode = command.stringValueOfParameterNamed("unitCode");
		final String floor = command.stringValueOfParameterNamed("floor");
		final String buildingCode = command.stringValueOfParameterNamed("buildingCode");
		final String parcel = command.stringValueOfParameterNamed("parcel");
		final String precinct = command.stringValueOfParameterNamed("precinct");
		final String poBox = command.stringValueOfParameterNamed("poBox");
		final String street = command.stringValueOfParameterNamed("street");
		final String state = command.stringValueOfParameterNamed("state");
		final String country = command.stringValueOfParameterNamed("country");
		final String district = command.stringValueOfParameterNamed("district");
		
		return new PropertyMaster(propertyTypeId,propertyCode,unitCode,floor,buildingCode,parcel,precinct,poBox,street,state,country,district,CodeNameConstants.CODE_PROPERTY_VACANT);

	}
	
	
	
	public Map<String, Object> update(final JsonCommand command) {
		
		final Map<String, Object> actualChanges = new ConcurrentHashMap<String, Object>(1);
		
		final String propertyCodeParamName = "propertyCode";
		if (command.isChangeInStringParameterNamed(propertyCodeParamName,this.propertyCode)) {
			final String newValue = command.stringValueOfParameterNamed(propertyCodeParamName);
			actualChanges.put(propertyCodeParamName, newValue);
			this.propertyCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String propertyTypeId = "propertyType";
		if (command.isChangeInLongParameterNamed(propertyTypeId,this.propertyTypeId)) {
			final Long newValue = command.longValueOfParameterNamed(propertyTypeId);
			actualChanges.put(propertyTypeId, newValue);
			this.propertyTypeId = newValue;
		}
		
		final String unitCodeParamName = "unitCode";
		if (command.isChangeInStringParameterNamed(unitCodeParamName,this.unitCode)) {
			final String newValue = command.stringValueOfParameterNamed(unitCodeParamName);
			actualChanges.put(unitCodeParamName, newValue);
			this.unitCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String floorParamName = "floor";
		if (command.isChangeInStringParameterNamed(floorParamName,this.floor)) {
			final String newValue = command.stringValueOfParameterNamed(floorParamName);
			actualChanges.put(floorParamName, newValue);
			this.floor =StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String buildingCodeParamName = "buildingCode";
		if (command.isChangeInStringParameterNamed(buildingCodeParamName,this.buildingCode)) {
			final String newValue = command.stringValueOfParameterNamed(buildingCodeParamName);
			actualChanges.put(buildingCodeParamName, newValue);
			this.buildingCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String parcelParamName = "parcel";
		if (command.isChangeInStringParameterNamed(parcelParamName,this.parcel)) {
			final String newValue = command.stringValueOfParameterNamed(parcelParamName);
			actualChanges.put(parcelParamName, newValue);
			this.parcel = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String precinctParamName = "precinct";
		if (command.isChangeInStringParameterNamed(precinctParamName,this.precinct)) {
			final String newValue = command.stringValueOfParameterNamed(precinctParamName);
			actualChanges.put(precinctParamName, newValue);
			this.precinct = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String poBoxParamName = "poBox";
		if (command.isChangeInStringParameterNamed(poBoxParamName,this.poBox)) {
			final String newValue = command.stringValueOfParameterNamed(poBoxParamName);
			actualChanges.put(poBoxParamName, newValue);
			this.poBox = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String streetParamName = "street";
		if (command.isChangeInStringParameterNamed(streetParamName,this.street)) {
			final String newValue = command.stringValueOfParameterNamed(streetParamName);
			actualChanges.put(streetParamName, newValue);
			this.street = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String stateParamName = "state";
		if (command.isChangeInStringParameterNamed(stateParamName,this.state)) {
			final String newValue = command.stringValueOfParameterNamed(stateParamName);
			actualChanges.put(stateParamName, newValue);
			this.state = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String countryParamName = "country";
		if (command.isChangeInStringParameterNamed(propertyCodeParamName,this.country)) {
			final String newValue = command.stringValueOfParameterNamed(countryParamName);
			actualChanges.put(countryParamName, newValue);
			this.country = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		final String districtParamName = "district";
		if (command.isChangeInStringParameterNamed(districtParamName,this.district)) {
			final String newValue = command.stringValueOfParameterNamed(districtParamName);
			actualChanges.put(districtParamName, newValue);
			this.district = StringUtils.defaultIfEmpty(newValue, null);
		}
		
	
		return actualChanges;
	}


	public Long getPropertyTypeId() {
		return propertyTypeId;
	}


	public void setPropertyTypeId(Long propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}


	public String getPropertyCode() {
		return propertyCode;
	}


	public void setPropertyCode(String propertyCode) {
		this.propertyCode = propertyCode;
	}


	public String getUnitCode() {
		return unitCode;
	}


	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}


	public String getFloor() {
		return floor;
	}


	public void setFloor(String floor) {
		this.floor = floor;
	}


	public String getBuildingCode() {
		return buildingCode;
	}


	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}


	public String getParcel() {
		return parcel;
	}


	public void setParcel(String parcel) {
		this.parcel = parcel;
	}


	public String getPrecinct() {
		return precinct;
	}


	public void setPrecinct(String precinct) {
		this.precinct = precinct;
	}


	public String getStreet() {
		return street;
	}


	public void setStreet(String street) {
		this.street = street;
	}


	public String getPoBox() {
		return poBox;
	}


	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public Long getClientId() {
		return clientId;
	}


	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}


	public void delete() {
		
		if (this.isDeleted == 'N') {
			this.isDeleted = 'Y';
			this.propertyCode = this.propertyCode+"_"+this.getId();
		}
	}


	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	/*public String getDistrict() {
		// TODO Auto-generated method stub
		return null;
	}*/


}
