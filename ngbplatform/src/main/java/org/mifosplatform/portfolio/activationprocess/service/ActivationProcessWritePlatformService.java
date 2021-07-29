/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.activationprocess.service;

import org.json.JSONObject;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.portfolio.activationprocess.exception.NINNOTVerificationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public interface ActivationProcessWritePlatformService {

	CommandProcessingResult activationProcess(JsonCommand command);

	CommandProcessingResult selfRegistrationProcess(JsonCommand command);

	CommandProcessingResult createSimpleActivation(JsonCommand command);

	CommandProcessingResult createLeaseDetails(JsonCommand command);

	CommandProcessingResult createClientSimpleActivation(JsonCommand command, Long clientId);

	CommandProcessingResult createCustomerActivation(JsonCommand command);

	CommandProcessingResult createServiceActivationWithoutDevice(JsonCommand command, Long clientId);

	CommandProcessingResult validateKey_NIN(JsonCommand command, Long NINId);

	CommandProcessingResult validateMobileAndNIN(JsonCommand command);

	CommandProcessingResult ResendOtpMessage(JsonCommand command);

	String saveImage(String imageBase64Encoder);

	Boolean photoVerification(String nin, String path);

	ResponseEntity<String> OTP_MESSAGE(String mobileNumber, String otp);

	public static ResponseEntity<String> OTP_MESSAGEs(String mobileNo, String Otp) {
		ResponseEntity<String> result = null;

		try {
			RestTemplate restTemplate = new RestTemplate();

			String OTP_ENDPOINT = "https://termii.com/api/sms/send";
			HttpHeaders headers = new HttpHeaders();

			headers.add("Content-Type", "application/json");
			headers.add("Cookie", "termii-sms=64Eq2KHTk3xNkRKHRaxrWA5WbfBp4lMNxHpMcx4Y");

			System.out.println(OTP_ENDPOINT);
			JSONObject requestpayload = new JSONObject();
			requestpayload.put("to", "234" + mobileNo);
			requestpayload.put("from", "Moreplex tv");
			requestpayload.put("sms", "otp verification " + Otp);
			requestpayload.put("type", "plain");
			requestpayload.put("channel", "generic");
			requestpayload.put("api_key", "TLISRdXrYknFK30dLcvfmtqGTdXHozF1QY0hhAe1JBcJDqRLr2Mwej3Q5We7J1");

			HttpEntity<String> request = new HttpEntity<>(requestpayload.toString(), headers);
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()");
			result = restTemplate.postForEntity(OTP_ENDPOINT, request, String.class);
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()" + result);
		} catch (HttpClientErrorException e) {
			e.printStackTrace();
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()" + result);
			throw new NINNOTVerificationException("something went wrong " + e.getLocalizedMessage());

		} catch (Exception e) {
			System.out.println("ActivationProcessWritePlatformService.OTP_MESSAGEs()" + result);
			e.printStackTrace();
			throw new NINNOTVerificationException("something went wrong " + e.getLocalizedMessage());

		}
		return result;
	}

}
