import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;


public class TestStringd {

	
	public static void main(String []arg) throws JSONException, IOException {
		
	/*String str="select a.id as clientId from m_client a, b_orders b where a.id=b.client_id and b.order_status  = 1 and (b.next_billable_day is null or b.next_billable_day<=Now())";
	if(str.toLowerCase().matches("now()".toLowerCase())){
		str=str.replace("now()", "hi");
	}
	System.out.println(str);*/
		
		JSONObject object = new JSONObject();
		object.put("planCode", "PLN06");
		object.put("isPrepaid", "Y");
		object.put("chargevariant", 2);
		object.put("serviceCode", "none");
		object.put("discountId", 1);
		object.put("chargeCode", "HSC");
		
		object.put("duration", "6 Months");
		object.put("price", "10");
		object.put("locale", "en");
		
		/*object.put("planCode", "Prepaid");
		object.put("isPrepaid", "Y");
		object.put("chargevariant", 2);
		object.put("serviceCode", "none");
		object.put("discountId", 1);
		object.put("chargeCode", "HSC");
		
		object.put("duration", "Half Yearly");
		object.put("price", "10");
		object.put("locale", "en");*/
		
		
		//for(int i = 17; i <= 199; i++){
			object.put("priceregion", 17);
			//YWRtaW46YXNpYW5ldEAxMjM=		YmlsbGluZzpwYXNzd29yZA==
			//https://saas.openbillingsystem.com:8443/obsplatform/api/v1/prices/7
			processRadiusPost("https://saas.openbillingsystem.com:8443/obsplatform/api/v1/prices/7", "YWRtaW46YXNpYW5ldEAxMjM=", object.toString());
		//}
	}
	
	public static  String processRadiusPost(String url, String encodePassword, String data) throws IOException{
		
		HttpClient httpClient = new DefaultHttpClient();
		httpClient = wrapClient(httpClient);
		StringEntity se = new StringEntity(data.trim());
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader("Authorization", "Basic " + encodePassword);
		postRequest.setHeader("X-Obs-Platform-TenantId", "default");
		postRequest.setHeader("Content-Type", "application/json");
		postRequest.setEntity(se);
		HttpResponse response = httpClient.execute(postRequest);

		if (response.getStatusLine().getStatusCode() == 404) {
			return "ResourceNotFoundException";

		} else if (response.getStatusLine().getStatusCode() == 401) {
			return "UnauthorizedException"; 

		} else if (response.getStatusLine().getStatusCode() != 200) {
			System.out.println("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		} else{
			System.out.println("Execute Successfully:" + response.getStatusLine().getStatusCode());
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
		String output,output1="";
		
		while ((output = br.readLine()) != null) {
			output1 = output1 + output;
		}
		
		System.out.println("**********************  "+output1+"  **********************");
		br.close();
		
		return output1;
		
	}
	
	public static HttpClient wrapClient(HttpClient base) {

		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@SuppressWarnings("unused")
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				@SuppressWarnings("unused")
				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			return null;
		}
	}


}
