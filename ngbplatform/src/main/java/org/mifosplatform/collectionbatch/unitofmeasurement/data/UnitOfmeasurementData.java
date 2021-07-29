package org.mifosplatform.collectionbatch.unitofmeasurement.data;

public class UnitOfmeasurementData {
	
	private Long id;
	private String Name;
	private String Description;
	private Long uomId;
	private String uomName;
	
	
	
public UnitOfmeasurementData(Long id,String Name,String Description) {
		
		this.id = id;
		this.Name = Name;
		this.Description = Description;
	}

public UnitOfmeasurementData(Long uomId, String uomName) {
	
	this.uomId=uomId;
	this.uomName=uomName;
	
}

public Long getUomId() {
	return uomId;
}

public void setUomId(Long uomId) {
	this.uomId = uomId;
}

public String getUomName() {
	return uomName;
}

public void setUomName(String uomName) {
	this.uomName = uomName;
}

public Long getId() {
	return id;
 }

public void setId(Long id) {
	this.id = id;
 }

public String getName() {
	return Name;
 }

public void setName(String name) {
	Name = name;
 }

public String getDescription() {
	return Description;
 }

public void setDescription(String description) {
	Description = description;
 }

}
	


