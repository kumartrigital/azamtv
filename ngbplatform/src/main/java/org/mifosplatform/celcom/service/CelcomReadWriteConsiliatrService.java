package org.mifosplatform.celcom.service;

import java.util.Map;

public interface CelcomReadWriteConsiliatrService {
	
	public Object celcomProcessCommandHandler(Map<String, Object> inputs);

	public String processCelcomRequest(String opCodeString,  String sOAPMessage);
	
}
