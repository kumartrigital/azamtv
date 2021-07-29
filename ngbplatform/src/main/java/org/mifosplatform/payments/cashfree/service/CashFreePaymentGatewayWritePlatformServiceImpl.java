package org.mifosplatform.payments.cashfree.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import net.sf.json.JSON;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfiguration;
import org.mifosplatform.finance.paymentsgateway.domain.PaymentGatewayConfigurationRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.payments.cashfree.data.CashFreeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CashFreePaymentGatewayWritePlatformServiceImpl implements
		CashFreePaymentGatewayWritePlatformService {

	private final PlatformSecurityContext context;
	private final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository;

	@Autowired
	public CashFreePaymentGatewayWritePlatformServiceImpl(
			final PlatformSecurityContext context,
			final PaymentGatewayConfigurationRepository paymentGatewayConfigurationRepository) {
		this.context = context;
		this.paymentGatewayConfigurationRepository = paymentGatewayConfigurationRepository;
	}

	@Override
	public String makePayment(String json) {
		
		URL obj;
		try {
			JSONObject jsonObject = new JSONObject(json);
			PaymentGatewayConfiguration paymentGatewayConfiguration = this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
			String gatewayValues = paymentGatewayConfiguration.getValue();
			JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
			String appId = gatewayValuesObject.getString("appId");
			String secretKey = gatewayValuesObject.getString("secretKey");
			String returnURL = gatewayValuesObject.getString("returnUrl");
			String environment = gatewayValuesObject.getString("environment");
			String url = null;
			if(environment.equalsIgnoreCase("TEST")){
				url = "https://test.cashfree.com/api/v1/order/create";
			}else if(environment.equalsIgnoreCase("PROD")||environment.equalsIgnoreCase("PRODUCTION")){
				url = "https://api.cashfree.com/api/v1/order/create";
			}else{
				throw new PlatformDataIntegrityException("Environment Not defined Correctly", "Environment not defined correectly", "Environment not defined correctly");
			}
			obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

			conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
			conn.setRequestProperty("cache-control", "no-cache");

			conn.setDoOutput(true);

			conn.setRequestMethod("POST");

			String chars = "0123456789";
			long string_length = 6;
			String randomString = String.valueOf(new Date().getTime());
			for (int i=0; i<string_length; i++) {
				int  rnum =(int) Math.floor(Math.random() * chars.length());
				randomString += chars.substring(rnum,rnum+1);	
			}	
			System.out.println("transactionId "+randomString);
			
		    String data =  "appId="+appId+"&secretKey="+secretKey+"&orderId="+randomString+"&orderAmount="+jsonObject.getString("amount") +
		    		"&orderNote=Subscription&customerName="+jsonObject.getString("clientName")+"&customerPhone="+jsonObject.getString("clientphone")+"&" +
		    		"customerEmail="+jsonObject.getString("clientEmail")+"&sellerPhone=&" +
		    		"returnUrl="+jsonObject.getString("returnURL")+"&notifyUrl=&paymentModes=&pc=";
		    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		    out.write(data);
		    out.close();
		    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
		    Scanner scanner = new Scanner(inputStreamReader);
		    String output = null;
		    JSONObject outputJSON = null;
		    while(scanner.hasNext()){
		    	output = scanner.next();
		    	outputJSON = new JSONObject(output);
		    }
		    
		    scanner.close();
		    return output;
		} catch (MalformedURLException e) {
			return e.getMessage();
		} catch (IOException e) {
			return e.getMessage();
		} catch (JSONException e) {
			return e.getMessage();
		}

	}
	
	@Override
	 public CommandProcessingResult VerifyCredentials() {
		
		String url = "https://test.cashfree.com/api/v1/credentials/verify";
		
		 URL obj;
		try {
			PaymentGatewayConfiguration paymentGatewayConfiguration = this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
			String gatewayValues = paymentGatewayConfiguration.getValue();
			JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
			String appId = gatewayValuesObject.getString("appId");
			String secretKey = gatewayValuesObject.getString("secretKey");
			obj = new URL(url);
		    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
		    conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
			conn.setRequestProperty("cache-control", "no-cache");
			conn.setDoOutput(true);

			conn.setRequestMethod("POST");

			String data = "appId=" + appId + "&secretKey=" + secretKey;
			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream());
			out.write(data);
			out.close();
			InputStreamReader inputStreamReader = new InputStreamReader(
					conn.getInputStream());
			Scanner scanner = new Scanner(inputStreamReader);
			String output = null;
			JSONObject outputJSON = null;
			while (scanner.hasNext()) {
				output = scanner.next();
				System.out.println(output);
				outputJSON = new JSONObject(output);
			}
			scanner.close();
			return new CommandProcessingResult((long) -1);
			    
		 } catch (Exception e) {
				// TODO Auto-generated catch block
			   return new CommandProcessingResult((long) -1);
	     }

	}

	@Override //swapna
	public String getLink(String json){
	    String url = " https://test.cashfree.com/api/v1/order/info/link";

	    URL obj;
	    try {
	        JSONObject jsonObject = new JSONObject(json);
	        PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
	        String gatewayValues = paymentGatewayConfiguration.getValue();
	        JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
	        String appId = gatewayValuesObject.getString("appId");
	        String secretKey = gatewayValuesObject.getString("secretKey");
	        
	        
	        obj = new URL(url);
	        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

	        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	        conn.setRequestProperty("cache-control", "no-cache");
	        
	        conn.setDoOutput(true);

	        conn.setRequestMethod("POST");

	        
	        String data =  "appId="+appId+"&secretKey="+secretKey+"&orderId="+jsonObject.getString("orderId");
	        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
	        out.write(data);
	        out.close();
	        InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
	        Scanner scanner = new Scanner(inputStreamReader);
	        String output = null;
	        JSONObject outputJSON = null;;
	        while(scanner.hasNext()){
	            output = scanner.next();
	            System.out.println(output);
	            outputJSON = new JSONObject(output);
	        }
	        
	        scanner.close();
	        return output;
	    } catch (MalformedURLException e) {
	        // TODO Auto-generated catch block
	        return e.getMessage();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        return e.getMessage();
	    } catch (JSONException e) {
	        // TODO Auto-generated catch block
	        return e.getMessage();
	    }
	    
	}

	

@Override //swapna
public String settlements(String json) {
	String url = "https://test.cashfree.com/api/v1/settlements";

    URL obj;
	try {
		JSONObject jsonObject = new JSONObject(json);
		PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
		String gatewayValues = paymentGatewayConfiguration.getValue();
		JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
		String appId = gatewayValuesObject.getString("appId");
		String secretKey = gatewayValuesObject.getString("secretKey");
	   
		obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

	    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty("cache-control", "no-cache");
	    
	    conn.setDoOutput(true);

	    conn.setRequestMethod("POST");

	    
	    String data =  "appId="+appId+"&secretKey="+secretKey+
	    		"&startDate="+jsonObject.getString("startDate")+
	    		"&endDate="+jsonObject.getString("endDate")+
	    		"&lastId="+jsonObject.getString("lastId")+
	    		"&count="+jsonObject.getString("count");
	    
	    
	    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
	    out.write(data);
	    out.close();
	    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
	    Scanner scanner= new Scanner(inputStreamReader); 
	    String output = null;
	    JSONObject outputJSON = null;
	    while(scanner.hasNext()){
	    	output = scanner.next();
	    	System.out.println(output);
	    	outputJSON = new JSONObject(output);
	    }
	    
	    scanner.close();
	    return output;
	    
	 } catch (Exception e) {
		// TODO Auto-generated catch block
	    return e.getMessage();
	 }
}

@Override
public String FetchAllRefunds(String json) {
	
	String url = " https://test.cashfree.com/api/v1/refunds";
	
	URL obj;
	 try {
	
		 JSONObject jsonObject = new JSONObject(json);
		 PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
		 String gatewayValues = paymentGatewayConfiguration.getValue();
		 JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
		 String appId = gatewayValuesObject.getString("appId");
		 String secretKey = gatewayValuesObject.getString("secretKey");
		 
		 obj = new URL(url);
		 HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		 conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		 conn.setRequestProperty("cache-control", "no-cache");
		    
		 conn.setDoOutput(true);

		 conn.setRequestMethod("POST");
		 
		 String data =  "appId="+appId+"&secretKey="+secretKey+"&startDate="+jsonObject.getString("startDate")+"&endDate="+jsonObject.getString("endDate")+
				 "&lastId="+jsonObject.getString("lastId")+"&count="+jsonObject.getString("count");
		 System.out.println("data"+data);
		 
		 OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		 out.write(data);
		 out.close();
		 InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
		 Scanner scanner= new Scanner(inputStreamReader); 
		    String output = null;
		    JSONObject outputJSON = null;
		    while(scanner.hasNext()){
		    	output = scanner.next();
		    	System.out.println("output"+output);
		    	outputJSON = new JSONObject(output);
		    }
		    
		    scanner.close();
		    return output;
	
	 } catch (Exception e) {
			// TODO Auto-generated catch block
		  return e.getMessage();
     }
	
}

@Override
public String FetchSingleRefunds(String json) {
	
     String url = "https://test.cashfree.com/api/v1/refundStatus";
	
	 URL obj;
	 try {
	
		 JSONObject jsonObject = new JSONObject(json);
		 PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
		 String gatewayValues = paymentGatewayConfiguration.getValue();
		 JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
		 String appId = gatewayValuesObject.getString("appId");
		 String secretKey = gatewayValuesObject.getString("secretKey");
		 
		 obj = new URL(url);
		 HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		 conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		 conn.setRequestProperty("cache-control", "no-cache");
		    
		 conn.setDoOutput(true);

		 conn.setRequestMethod("POST");
		 
		 String data =  "appId="+appId+"&secretKey="+secretKey+ "&refundId="+jsonObject.getString("refundId");
	 
         System.out.println("data"+data);
		 
		 OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		 out.write(data);
		 out.close();
		 InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
		 Scanner scanner= new Scanner(inputStreamReader); 
		    String output = null;
		    JSONObject outputJSON = null;
		    while(scanner.hasNext()){
		    	output = scanner.next();
		    	System.out.println("output"+output);
		    	outputJSON = new JSONObject(output);
		    }
		    
		    scanner.close();
		    return output;
	
	 } catch (Exception e) {
			// TODO Auto-generated catch block
		  return e.getMessage();
     }
 }

@Override
public String createRefund(String json) {
	
	 String url = "https://test.cashfree.com/api/v1/order/refund";
	
	 URL obj;
	 try {
		 
		 JSONObject jsonObject = new JSONObject(json);
		 PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
		 String gatewayValues = paymentGatewayConfiguration.getValue();
		 JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
		 String appId = gatewayValuesObject.getString("appId");
		 String secretKey = gatewayValuesObject.getString("secretKey");
		 
		 obj = new URL(url);
		 HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		 conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		 conn.setRequestProperty("cache-control", "no-cache");
		    
		 conn.setDoOutput(true);

		 conn.setRequestMethod("POST");
		 
		 String data =  "appId="+appId+"&secretKey="+secretKey+"&orderId="+jsonObject.getString("orderId")+"&referenceId="+jsonObject.getString("referenceId")+
				 "&refundAmount="+jsonObject.getString("refundAmount")+"&refundNote="+jsonObject.getString("refundNote");
		 
		 System.out.println("data"+data);
		 OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		 out.write(data);
		 out.close();
		 InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
		 Scanner scanner= new Scanner(inputStreamReader); 
		    String output = null;
		    JSONObject outputJSON = null;
		    while(scanner.hasNext()){
		    	output = scanner.next();
		    	System.out.println("output"+output);
		    	outputJSON = new JSONObject(output);
		    }
		    
		    scanner.close();
		    return output;
		    
		 } catch (Exception e) {
			// TODO Auto-generated catch block
		    return e.getMessage();
		 }
}

@Override
public String createStatus(String json) {
	String url = "https://test.cashfree.com/api/v1/order/info/status";

    URL obj;
	try {
		JSONObject jsonObject = new JSONObject(json);
		PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
		String gatewayValues = paymentGatewayConfiguration.getValue();
		JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
		String appId = gatewayValuesObject.getString("appId");
		String secretKey = gatewayValuesObject.getString("secretKey");

		
		obj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

	    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty("cache-control", "no-cache");
	    
	    conn.setDoOutput(true);

	    conn.setRequestMethod("POST");

	    String data = "appId="+appId+"&secretKey="+secretKey+"&orderId="+jsonObject.getString("orderId");
	    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
	    out.write(data);
	    out.close();
	    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
	    Scanner scanner = new Scanner(inputStreamReader);
	    String output = null;
	    JSONObject outputJSON = null;;
	    while(scanner.hasNext()){
	    	output = scanner.next();
		    System.out.println(output);
	    	outputJSON = new JSONObject(output);
	    }
	    
	    scanner.close();
	    return output;
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		return e.getMessage();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		return e.getMessage();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		return e.getMessage();
	}

}



@Override
public String createTransactions(String json) {

 String url = "https://test.cashfree.com/api/v1/transactions";

URL obj;
try {
	JSONObject jsonObject = new JSONObject(json);
	PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
	String gatewayValues = paymentGatewayConfiguration.getValue();
	JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
	String appId = gatewayValuesObject.getString("appId");
	String secretKey = gatewayValuesObject.getString("secretKey");
	

	obj = new URL(url);
	HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("cache-control", "no-cache");
    
    conn.setDoOutput(true);

    conn.setRequestMethod("POST");
    

    String data = "appId="+appId+"&secretKey="+secretKey+"&startDate="+jsonObject.getString("startDate")+"&endDate="+jsonObject.getString("endDate") +
    		"&txStatus="+jsonObject.getString("txStatus")+
    		"&lastId="+jsonObject.getString("lastId")+
    		"&count="+jsonObject.getString("count");
  
    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
    out.write(data);
    out.close();
    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());   
    Scanner scanner = new Scanner(inputStreamReader);
    String output = null;
    JSONObject outputJSON = null;;
    while(scanner.hasNext()){
    	output = scanner.next();
	    System.out.println(output);
    	outputJSON = new JSONObject(output);
    }
    
    scanner.close();
    return output;
} catch (MalformedURLException e) {
	// TODO Auto-generated catch block
	return e.getMessage();
} catch (IOException e) {
	// TODO Auto-generated catch block
	return e.getMessage();
} catch (JSONException e) {
	// TODO Auto-generated catch block
	return e.getMessage();
}
}

	
	@Override
	// swapna
	public String settlement(String json) {
		String url = "https://test.cashfree.com/api/v1/settlement";

		URL obj;
		try {
			JSONObject jsonObject = new JSONObject(json);
			PaymentGatewayConfiguration paymentGatewayConfiguration = this.paymentGatewayConfigurationRepository
					.findOneByName("cashfree");
			String gatewayValues = paymentGatewayConfiguration.getValue();
			JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
			String appId = gatewayValuesObject.getString("appId");
			String secretKey = gatewayValuesObject.getString("secretKey");

			obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

			conn.setRequestProperty("Content-type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("cache-control", "no-cache");

			conn.setDoOutput(true);

			conn.setRequestMethod("POST");

			String data = "appId=" + appId + "&secretKey=" + secretKey
					+ "&settlementId=" + jsonObject.getString("settlementId")
					+ "&lastId=" + jsonObject.getString("lastId") + "&count="
					+ jsonObject.getString("count");

			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream());
			out.write(data);
			out.close();
			InputStreamReader inputStreamReader = new InputStreamReader(
					conn.getInputStream());
			Scanner scanner = new Scanner(inputStreamReader);
			String output = null;
			JSONObject outputJSON = null;
			;
			while (scanner.hasNext()) {
				output = scanner.next();
				System.out.println(output);
				outputJSON = new JSONObject(output);
			}

			scanner.close();
			return output;
		} catch (MalformedURLException e) {
			return e.getMessage();
		} catch (IOException e) {
			return e.getMessage();
		} catch (JSONException e) {
			return e.getMessage();
		}

	}

	@Override
	public CashFreeData generatePaymentToken(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			PaymentGatewayConfiguration paymentGatewayConfiguration = this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
			String gatewayValues = paymentGatewayConfiguration.getValue();
			JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
			String appId = gatewayValuesObject.getString("appId");
			String secretKey = gatewayValuesObject.getString("secretKey");
			String returnUrl = gatewayValuesObject.getString("returnUrl");
			String chars = "0123456789";
			long string_length = 6;
			String transactionId = String.valueOf(new Date().getTime());
			for (int i = 0; i < string_length; i++) {
				int rnum = (int) Math.floor(Math.random() * chars.length());
				transactionId += chars.substring(rnum, rnum + 1);
			}
			
			String data = "appId=" + appId + "&orderId=" + transactionId + "&orderAmount=" + jsonObject.getString("amount") +
					"&returnUrl=" + returnUrl + "&paymentModes=";
			  Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			  SecretKeySpec skspec = new SecretKeySpec(secretKey.getBytes(),"HmacSHA256");
			  sha256_HMAC.init(skspec);
			String  paymentToken = Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes()));
			CashFreeData cashFreeData = new CashFreeData(paymentToken, transactionId);
			return cashFreeData;
		} catch (JSONException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			return null;
		}

	}

	@Override
	public String PaymentEmail(String json) {
		String url = "https://test.cashfree.com/api/v1/order/email";
		URL obj;
		try {
			JSONObject jsonObject = new JSONObject(json);
			
			PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
			String gatewayValues = paymentGatewayConfiguration.getValue();
			JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
			String appId = gatewayValuesObject.getString("appId");
			String secretKey = gatewayValuesObject.getString("secretKey");
			
			obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		    conn.setRequestProperty("cache-control", "no-cache");
		    
		    conn.setDoOutput(true);

		    conn.setRequestMethod("POST");
		    
		    
		    String data =  "appId="+appId+"&secretKey="+secretKey+"&orderId="+jsonObject.getString("orderId");
		    System.out.println("data "+data);
		    		
		    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		    out.write(data);
		    out.close();
		    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
		    Scanner scanner = new Scanner(inputStreamReader);
		    String output = null;
		    JSONObject outputJSON = null;
		    while(scanner.hasNext()){
		    	output = scanner.next();
		    	System.out.println("output"+output);
		    	outputJSON = new JSONObject(output);
		    }
		    scanner.close();
		    return output;
		} catch (Exception e){
		return e.getMessage();
		}
		
	}

	@Override
	public String PaymentDetails(String json) {
		String url = "https://test.cashfree.com/api/v1/order/info";
		URL obj;
		try {
			JSONObject jsonObject = new JSONObject(json);
			
			PaymentGatewayConfiguration paymentGatewayConfiguration= this.paymentGatewayConfigurationRepository.findOneByName("cashfree");
			String gatewayValues = paymentGatewayConfiguration.getValue();
			JSONObject gatewayValuesObject = new JSONObject(gatewayValues);
			String appId = gatewayValuesObject.getString("appId");
			String secretKey = gatewayValuesObject.getString("secretKey");
			
			obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

		    conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		    conn.setRequestProperty("cache-control", "no-cache");
		    
		    conn.setDoOutput(true);

		    conn.setRequestMethod("POST");
		    
		    
		    String data =  "appId="+appId+"&secretKey="+secretKey+"&orderId="+jsonObject.getString("orderId");
		    System.out.println("data "+data);
		    		
		    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		    out.write(data);
		    out.close();
		    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream());
		    Scanner scanner = new Scanner(inputStreamReader);
		    String output = null;
		    JSONObject outputJSON = null;
		    while(scanner.hasNext()){
		    	output = scanner.next();
		    	System.out.println("output"+output);
		    	outputJSON = new JSONObject(output);
		    }
		    scanner.close();
		    return output;
		} catch (Exception e){
		return e.getMessage();
		}
		
	}
	
	
	
}

