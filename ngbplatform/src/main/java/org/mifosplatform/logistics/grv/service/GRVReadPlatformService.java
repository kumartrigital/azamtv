package org.mifosplatform.logistics.grv.service;

import java.util.List;

import org.mifosplatform.logistics.grv.data.GRVData;

public interface GRVReadPlatformService {
	
	List<GRVData> retriveGrvIds();
	
	List<String> retriveSerialNumbersForItems(Long grvId);

	GRVData retriveSingleGrvData(Long grvId);

	String retriveSerialNumbersFromGrv(String serialNumber, Long grvId);
	
}
