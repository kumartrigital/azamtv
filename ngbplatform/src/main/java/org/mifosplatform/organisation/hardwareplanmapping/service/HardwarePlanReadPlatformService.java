package org.mifosplatform.organisation.hardwareplanmapping.service;

import java.util.List;

import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwareMappingDetailsData;
import org.mifosplatform.organisation.hardwareplanmapping.data.HardwarePlanData;
import org.mifosplatform.portfolio.plan.data.PlanCodeData;

public interface HardwarePlanReadPlatformService {

	List<HardwarePlanData> retrieveAllHardwareMappings(Long itemClass);

	HardwarePlanData retrieveSingleHardwareMapping(Long id);
}
