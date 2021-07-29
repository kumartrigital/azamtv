package org.mifosplatform.inview.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.inview.exception.PaywizardFailureException;
import org.mifosplatform.organisation.redemption.api.RedemptionApiResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class InviewWritePlatformServiceImpl implements InviewWritePlatformService {

	static JSONObject activation = new JSONObject();
	static JSONArray address = new JSONArray();
	static JSONArray plans = new JSONArray();
	static JSONArray requestMessage = new JSONArray();
	static JSONArray devices = new JSONArray();
	static JSONObject addressjson = new JSONObject();
	static JSONObject addressjsonBilling = new JSONObject();
	static JSONObject credentialsJson = new JSONObject();
	static JSONObject planJson = new JSONObject();
	static JSONObject deviceJson = new JSONObject();
	static JSONObject paymentInfo = new JSONObject();
	static JSONObject voucher = new JSONObject();
	static JSONObject voucherRedemptionJson = new JSONObject();
	static JSONObject topUpJson = new JSONObject();
	static JSONObject retrackJson = new JSONObject();
	static JSONObject requestMessageJson = new JSONObject();
	static JSONObject addMovieJson = new JSONObject();
	static String result;

	private final static Logger logger = LoggerFactory.getLogger(InviewWritePlatformServiceImpl.class);

	private final FromJsonHelper fromApiJsonHelper;
	private final RedemptionApiResource redemptionApiResource;
	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public InviewWritePlatformServiceImpl(final FromJsonHelper fromApiJsonHelper,
			final RedemptionApiResource redemptionApiResource, final FromJsonHelper fromJsonHelper) {

		this.fromApiJsonHelper = fromApiJsonHelper;
		this.redemptionApiResource = redemptionApiResource;
		this.fromJsonHelper = fromJsonHelper;

	}

	@Transactional
	@Override
	public String createClient(JsonCommand command, String voucherCode, String itemCode) {
		// TODO Auto-generated method stub
		try {
			String json = command.json();
			JSONObject activation = new JSONObject();

			String deviceId = null;
			String password = null;
			final JsonElement element = fromApiJsonHelper.parse(command.json());
			JsonArray deviceArray = fromJsonHelper.extractJsonArrayNamed("devices", element);
			for (JsonElement j : deviceArray) {
				JsonCommand deviceComm = new JsonCommand(null, j.toString(), j, fromJsonHelper, null, null, null, null,
						null, null, null, null, null, null, null, null);
				deviceId = deviceComm.stringValueOfParameterName("deviceId");
			}
			String jsonPayload = command.json();
			JSONObject jsonObject1 = new JSONObject(jsonPayload);
			JSONObject credJson = jsonObject1.getJSONObject("credentails");

			password = credJson.getString("password");

			activation.put("deviceId", deviceId);
			if (itemCode.equals("DTH")) {
				activation.put("voucherId", voucherCode);
			} else {
				activation.put("voucherId", deviceId);
			}
			activation.put("email", command.stringValueOfParameterName("email"));
			activation.put("username", deviceId);
			activation.put("password", password);
		 return registerCustomerWithPayWizard(activation);
		} catch (Exception e) {
			throw new PaywizardFailureException();
		}

	}

	private String registerCustomerWithPayWizard(final JSONObject activationJsonData) {
		try {
			// TODO Auto-generated method stub
			String customerRegistrationUrl = "http://45.63.98.216:9091/api/v1/paywizard/registration";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<String>(activationJsonData.toString(), headers);
			ResponseEntity<String> result = restTemplate.exchange(customerRegistrationUrl, HttpMethod.POST, request,
					String.class);
			// ResponseEntity<String> result =
			// restTemplate.postForObject(customerRegistrationUrl, request, String.class);
			System.out.println("Registration.customerRegistration()" + result.toString());
			if (result.getStatusCode().value() != 200) {
				throw new PaywizardFailureException();
			}
			return result.toString();

		} catch (Exception e) {
			throw new PaywizardFailureException();
		}

	}

	@Override
	public void topUpforPaywizard(JsonCommand command, Long clientId) {
		// TODO Auto-generated method stub
		try {

			final JsonElement element = fromApiJsonHelper.parse(command.json());
			topUpJson.put("username", fromApiJsonHelper.extractLongNamed("username", element));
			// topUpJson.put("password", fromApiJsonHelper.extractLongNamed("password",
			// element));
			topUpJson.put("voucherCode", fromApiJsonHelper.extractStringNamed("voucherCode", element));
			voucherRedemptionJson.put("clientId", clientId.toString());
			voucherRedemptionJson.put("pinNumber", "SM5803047024");
			try {
				String result = redemptionApiResource.createRedemption(voucherRedemptionJson.toString());
				System.out.println("redemption result " + result);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			topUpForPaywizardRestCall(topUpJson);
		} catch (Exception e) {
			throw new PaywizardFailureException();
		}

	}

	private String topUpForPaywizardRestCall(final JSONObject topUpJson) {
		try {
			// TODO Auto-generated method stub
			String topUpForPaywizardUrl = "http://45.63.98.216:9091/api/v1/paywizard/topup";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<String>(topUpJson.toString(), headers);
			result = restTemplate.postForObject(topUpForPaywizardUrl, request, String.class);
			System.out.println("TopUP.Paywizard()" + result.toString());

			return result;
		} catch (Exception ex) {
			throw new PaywizardFailureException();
		}
	}

	public String retrackForPaywizardRestCall(final String username) {
		try {
			// TODO Auto-generated method stub
			String retrackForPaywizardUrl = "http://45.63.98.216:9091/api/v1/paywizard/deviceEntitlement";
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			JSONObject json = new JSONObject();
			json.put("username", username);
			HttpEntity<String> request = new HttpEntity<String>(json.toString(), headers);
			String result = restTemplate.postForObject(retrackForPaywizardUrl, request, String.class);
			return result.toString();
		} catch (Exception ex) {
			throw new PaywizardFailureException();
		}
	}

	@Override
	public void addMovieForPaywizard(JsonCommand command) {
		// TODO Auto-generated method stub
		try {
			final JsonElement element = fromApiJsonHelper.parse(command.json());
			addMovieJson.put("eventId", fromApiJsonHelper.extractLongNamed("eventId", element));
			addMovieJson.put("formatType", fromApiJsonHelper.extractStringNamed("formatType", element));
			addMovieJson.put("optType", fromApiJsonHelper.extractStringNamed("optType", element));
			addMovieJson.put("locale", fromApiJsonHelper.extractStringNamed("locale", element));
			addMovieJson.put("clientId", fromApiJsonHelper.extractStringNamed("clientId", element));
			addMovieJson.put("dateFormat", fromApiJsonHelper.extractStringNamed("dateFormat", element));
			addMovieJson.put("eventBookedDate", fromApiJsonHelper.extractStringNamed("eventBookedDate", element));

			voucherRedemptionJson.put("clientId", fromApiJsonHelper.extractStringNamed("clientId", element));
			voucherRedemptionJson.put("pinNumber", "SM5803047024");
			try {
				String result = redemptionApiResource.createRedemption(voucherRedemptionJson.toString());
				System.out.println("redemption result " + result);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			addMovieForForPaywizardRestCall(addMovieJson);
		} catch (Exception e) {
			throw new PaywizardFailureException();
		}
	}

	private String addMovieForForPaywizardRestCall(final JSONObject addMovieJson) {
		try {
			// TODO Auto-generated method stub
			String addMovieForPaywizardUrl = "http://localhost:9091/api/v1/paywizard/addPVOD";

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<String> request = new HttpEntity<String>(addMovieJson.toString(), headers);
			result = restTemplate.postForObject(addMovieForPaywizardUrl, request, String.class);
			System.out.println("addMovie.Paywizard()" + result.toString());

			return result;
		} catch (Exception ex) {
			throw ex;
		}

	}

}
