package org.mifosplatform.organisation.address.data;

public class EntityTypecommand {
	
	private final Long entityId;
	private final String entityName;
	private final Long parentEntityId;
	private final String entityCode;

	public EntityTypecommand(final Long entityId, final String entityName,
			final String entityCode, final Long parentEntityId) {
    
		this.entityId=entityId;
		this.entityCode=entityCode;
		this.entityName=entityName;
		this.parentEntityId=parentEntityId;
	
	
	}

	public Long getEntityId() {
		return entityId;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public String getEntityName() {
		return entityName;
	}

	public Long getParentEntityId() {
		return parentEntityId;
	}
	
	

}
