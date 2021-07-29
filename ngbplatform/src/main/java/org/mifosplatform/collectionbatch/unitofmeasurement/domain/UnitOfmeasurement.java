package org.mifosplatform.collectionbatch.unitofmeasurement.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.collectionbatch.ratableusagemetric.domain.RatableUsageMetric;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;


@Entity
@Table(name = "r_uom")
public class UnitOfmeasurement extends AbstractPersistable<Long>{
	
	
	@Column(name="uom_name", nullable=false, length=100)
	private String Name;
	
	@Column(name="uom_description", nullable=false, length=100)
	private String Description;
	
	public UnitOfmeasurement()
	{
	
	}

	public UnitOfmeasurement(String Name,String Description) {

		this.Name = Name;
		this.Description = Description;
	}

	public static UnitOfmeasurement formJson(JsonCommand command) {
		
		/*Long uomId = command.longValueOfParameterNamed("uomId");*/
		String Name = command.stringValueOfParameterNamed("Name");
		String Description = command.stringValueOfParameterNamed("Description");
		
		
		return new UnitOfmeasurement( Name, Description);
	}


	public Map<String, Object> update(JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		
		final String NameNamedParamName = "Name";
		final String DescriptionNamedParamName = "Description";
		
		
		
		
		if(command.isChangeInStringParameterNamed(NameNamedParamName, this.Name)){
			final String newValue = command.stringValueOfParameterNamed(NameNamedParamName);
			actualChanges.put(NameNamedParamName, newValue);
			this.Name = StringUtils.defaultIfEmpty(newValue, null);
		}
		
		if(command.isChangeInStringParameterNamed(DescriptionNamedParamName, this.Description)){
			final String newValue = command.stringValueOfParameterNamed(DescriptionNamedParamName);
			actualChanges.put(DescriptionNamedParamName, newValue);
			this.Description = StringUtils.defaultIfEmpty(newValue,null);
		}
		
		return actualChanges;
	
	
	}

}
