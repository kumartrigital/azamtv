package org.mifosplatform.obrm.service;

import java.util.Map;

import org.apache.axis2.AxisFault;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.obrm.service.InfranetWebServiceServiceStub.Opcode;
import org.mifosplatform.obrm.service.InfranetWebServiceServiceStub.OpcodeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ObrmReadWriteConsiliatrServiceImpl implements ObrmReadWriteConsiliatrService {

	private final ObrmReadPlatformService obrmReadPlatformService;
	private final ObrmWritePlatformService obrmWritePlatformService;
	
	@Autowired
	public ObrmReadWriteConsiliatrServiceImpl(@Lazy final ObrmReadPlatformService obrmReadPlatformService,
			@Lazy final ObrmWritePlatformService obrmWritePlatformService) {
		
		this.obrmReadPlatformService = obrmReadPlatformService;
		this.obrmWritePlatformService = obrmWritePlatformService;
	}

	@Override
	public Object obrmProcessCommandHandler(Map<String, Object> inputs) {
		Object obj = null;
		switch(inputs.get("target").toString()){
			case "client360":
				obj = this.obrmReadPlatformService.retriveClientTotalData((String)inputs.get("key"),(String)inputs.get("value"));
				break;
				
			case "createclient":
				obj = this.obrmWritePlatformService.createClient((JsonCommand)inputs.get("json"));
				break;
			case "createOffice":
				obj=this.obrmWritePlatformService.createOffice((JsonCommand)inputs.get("json"));
				break;
		}
		return obj;
	}
	
	
	
	
	@Override
	public String processObrmRequest(String opCodeString,  String sOAPMessage){
		try {
			InfranetWebServiceServiceStub stub = new InfranetWebServiceServiceStub();
			Opcode opcode = new Opcode();
			opcode.setOpcode(opCodeString);
			opcode.setInputXML(sOAPMessage);
			opcode.setM_SchemaFile("?");
			OpcodeResponse opcodeResponse = stub.opcode(opcode);
			return this.parseJSON(opcodeResponse.getOpcodeReturn());

		} catch(AxisFault e){
			throw new PlatformDataIntegrityException("error.msg.obrm.not.work", e.getMessage(), e.getMessage(), e.getMessage());
		}catch (Exception e) {
			throw new PlatformDataIntegrityException("", e.getMessage());
		}
	}
	
	
	private String parseJSON(String result ) throws JSONException{
		JSONObject object = XML.toJSONObject(result);
		System.out.println(object.toString());
		return object.toString();
	}
	
	
}
