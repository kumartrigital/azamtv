package org.mifosplatform.organisation.address.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_district",uniqueConstraints=@UniqueConstraint( name="district_code",columnNames ={ "district_code" }))
public class District extends AbstractPersistable<Long>{

	
	private static final long serialVersionUID = 1L;

	@Column(name="district_code")
	private String districtCode;

	@Column(name="district_name")
	private String districtName;

	@Column(name ="parent_code")
	private Long parentCode;

	@Column(name = "is_delete")
	private char isDeleted='N';
	
	
	public District(){
		
	}
	
	public District(final String entityCode, final String entityName, final Long parentEntityId) {
		  this.districtCode=entityCode;
		  this.districtName=entityName;
		  this.parentCode=parentEntityId;

		}
	
	public static District fromJson(final JsonCommand command) {
		 final String districtCode = command.stringValueOfParameterNamed("entityCode");
		    final String districtName = command.stringValueOfParameterNamed("entityName");
		    final Long parentEntityId = command.longValueOfParameterNamed("parentEntityId");
		    return new District(districtCode,districtName, parentEntityId);
		    
	}
	
	public Map<String, Object> update(final JsonCommand command) {
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		final String districtCodeParamName="entityCode";
		if (command.isChangeInStringParameterNamed(districtCodeParamName,this.districtCode)){
			final String newValue = command.stringValueOfParameterNamed(districtCodeParamName);
			actualChanges.put(districtCodeParamName, newValue);
			this.districtCode = StringUtils.defaultIfEmpty(newValue, null);
		}
		final String districtNameParamName="entityName";
		if (command.isChangeInStringParameterNamed( districtNameParamName,this.districtName)){
			final String newValue = command.stringValueOfParameterNamed( districtNameParamName);
			actualChanges.put( districtNameParamName, newValue);
			this.districtName = StringUtils.defaultIfEmpty(newValue, null);
		}
		final String parentCodeParam = "parentEntityId";
	    if (command.isChangeInLongParameterNamed(parentCodeParam, this.parentCode)) {
	        final Long newValue = command.longValueOfParameterNamed(parentCodeParam);
	        actualChanges.put(parentCodeParam, newValue);
	        this.parentCode = newValue;
	    }
	    return actualChanges;
	}

	public void delete() {
		// TODO Auto-generated method stub
		if(this.isDeleted == 'N'){
			this.isDeleted='Y';
			this.districtCode = this.districtCode+"_"+this.getId();
		}
	}

	
	
}
