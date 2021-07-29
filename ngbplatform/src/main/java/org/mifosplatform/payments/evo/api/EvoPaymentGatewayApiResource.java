package org.mifosplatform.payments.evo.api;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.ToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.ClientAdditionalData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/evo")
@Component
@Scope("singleton")

/**
 * 
 * @author raghu
 *
 */
public class EvoPaymentGatewayApiResource {
	
	private final PlatformSecurityContext context;
	private final ToApiJsonSerializer<ClientAdditionalData> jsonSerializer;

	@Autowired
	public EvoPaymentGatewayApiResource(final PlatformSecurityContext context,final ToApiJsonSerializer<ClientAdditionalData> jsonSerializer) {
		this.context=context;
		this.jsonSerializer = jsonSerializer;
	}

	@POST
	@Path("{method}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String blowfishEncrpt(final String apiRequestBodyAsJson,@PathParam("method") final String method){
	     try{
	    	 context.authenticatedUser();
	    	 JSONObject jsonData  = new JSONObject(apiRequestBodyAsJson);
	    	 Blowfish bf		  = new Blowfish("9b_JY3m=2t*Pa)T8",new HexCoder());
	    	 String encrypt_decrypt_String = "";
	    	 if(method.equalsIgnoreCase("encrypt")){
	    		 encrypt_decrypt_String = bf.encrypt(jsonData.getString("text"), jsonData.getInt("length"));
	    	 }else if(method.equalsIgnoreCase("decrypt")){
	    		 encrypt_decrypt_String = bf.decrypt(jsonData.getString("text"), jsonData.getInt("length"));
	    	 }
	    	 JSONObject jsonObj = new JSONObject();
	    	 jsonObj.put("blowfishData", encrypt_decrypt_String);
	    	 return this.jsonSerializer.serialize(jsonObj);
		}catch (Exception e) {
		       // return e.getMessage();
			throw new PlatformDataIntegrityException(e.getMessage(),e.getLocalizedMessage(),"length",e.getStackTrace().getClass());
	    }
	}
	
	@POST
	@Path("/amount")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public String amountCal(final String apiRequestBodyAsJson){
	     try{
	    	 context.authenticatedUser();
	    	 JSONObject jsonData  = new JSONObject(apiRequestBodyAsJson);
	    	 double amount = jsonData.getDouble("amount");
	    	 Long clientId = jsonData.getLong("clientId");
	    	 String merchantId = jsonData.getString("merchantId");
	    	 String currencyType = jsonData.getString("currencyType");
	    	 
	    	 
	    	// String data = "*174*pg_57966*100*GBP";
	    	 
	    	 String HmacKey = "f]7C8bW[c!4ET9x?5j)XH=6e2Fo*Gw(3";
	    	 
	    	 JSONObject jsonObj = new JSONObject();
	    	 jsonObj.put("amount", amount*100);
	    	 
	    	 String data = "*"+clientId+"*"+merchantId+"*"+jsonObj.getInt("amount")+"*"+currencyType; 
	    	
	    	 Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
	    	 SecretKeySpec secret_key = new SecretKeySpec(HmacKey.getBytes("UTF-8"), "HmacSHA256");
	    	 sha256_HMAC.init(secret_key);
	    	 jsonObj.put("macValue", Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes("UTF-8"))));
	    	 
	    	 return this.jsonSerializer.serialize(jsonObj);
		}catch (Exception e) {
		       // return e.getMessage();
			throw new PlatformDataIntegrityException(e.getMessage(),e.getLocalizedMessage(),"amount",e.getStackTrace().getClass());
	    }
	}

}
