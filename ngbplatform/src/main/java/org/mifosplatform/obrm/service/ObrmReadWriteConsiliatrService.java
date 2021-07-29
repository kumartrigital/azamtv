package org.mifosplatform.obrm.service;

import java.util.Map;

public interface ObrmReadWriteConsiliatrService {
	
	public Object obrmProcessCommandHandler(Map<String, Object> inputs);

	public String processObrmRequest(String opCodeString,  String sOAPMessage);
	
}
