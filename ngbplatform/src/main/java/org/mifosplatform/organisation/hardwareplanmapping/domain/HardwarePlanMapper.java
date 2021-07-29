package org.mifosplatform.organisation.hardwareplanmapping.domain;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_hw_plan_mapping")
public class HardwarePlanMapper extends AbstractPersistable<Long> {

	@Column(name = "item_class")
	private Long itemClass;

	@Column(name = "provisioning_id")
	private Long provisioningId;

	public HardwarePlanMapper() {
		// TODO Auto-generated constructor stub
	}

	public HardwarePlanMapper( final Long itemClass/*,final String planCode*/,final Long provisioningId) {

		this.itemClass = itemClass;
		this.provisioningId = provisioningId;

	}

	public Map<String, Object> update(final JsonCommand command) {
		
		final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		
		final String itemClassParamName = "itemClass";
		if (command.isChangeInLongParameterNamed(itemClassParamName, this.itemClass)) {
			final Long newValue = command.longValueOfParameterNamed(itemClassParamName);
			actualChanges.put(itemClassParamName, newValue);
			this.itemClass = newValue;
		}
		
		final String provisioningIdParamName = "provisioningId";
		if (command.isChangeInLongParameterNamed(provisioningIdParamName, this.provisioningId)) {
			final Long newValue = command.longValueOfParameterNamed(provisioningIdParamName);
			actualChanges.put(provisioningIdParamName, newValue);
			this.provisioningId = newValue;
		}

		return actualChanges;

	}

	public static HardwarePlanMapper fromJson(final JsonCommand command) {
		
		final Long itemClass = command.longValueOfParameterNamed("itemClass");
		final Long provisioningId = command.longValueOfParameterNamed("provisioningId");
		
		return new HardwarePlanMapper(itemClass,provisioningId);
	}

	/*public String getCode() {
		return planCode;
	}

	public String getPlanCode() {
		return planCode;
	}*/

	public Long getItemClass() {
		return itemClass;
	}

}
