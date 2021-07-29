package org.mifosplatform.infrastructure.configuration.service;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.mifosplatform.infrastructure.configuration.data.LicenseData;
import org.mifosplatform.infrastructure.configuration.exception.InvalidLicenseKeyException;
import org.mifosplatform.infrastructure.configuration.exception.LicenseKeyNotFoundException;
import org.mifosplatform.infrastructure.core.domain.MifosPlatformTenant;
import org.mifosplatform.infrastructure.security.service.TenantDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LicenseUpdateServiceImpl implements LicenseUpdateService {

	private final TenantDetailsService tenantDetailsService;
	
@Autowired
public LicenseUpdateServiceImpl(final TenantDetailsService tenantDetailsService){
 
	this.tenantDetailsService=tenantDetailsService;	
	
}
	
@Transactional
@Override
public void updateLicenseKey(ServletRequest request,MifosPlatformTenant tenant) {

	  String licenseKey= request.getParameter("key");
	  if(licenseKey == null){
		  throw new LicenseKeyNotFoundException("License key not found");
		  
	  }
	  
	 boolean isValid = this.checkIfKeyIsValid(licenseKey,tenant);
	 if(!isValid){
		 throw new InvalidLicenseKeyException("License key Exipired.");
	 }
	 
	 this.tenantDetailsService.updateLicenseKey(licenseKey);
	  return;
}

@Override
public boolean checkIfKeyIsValid(String licenseKey, MifosPlatformTenant tenant) {
	
	 boolean isValid=false;
	try {
		LicenseData licenseData=this.getLicenseDetails(licenseKey);
         if(licenseData.getKeyDate().after(new Date()) && tenant.getName().equalsIgnoreCase(licenseData.getClientName())){
        	 isValid = true;
         }
         return isValid;
	} catch (Exception exception){
		exception.printStackTrace();
		return false;
	}
}

@Override
public LicenseData getLicenseDetails(String licensekey) {

	final  String algorithm = "AES";
	final byte[] keyValue=new String("hugoadminhugoadm").getBytes();
	
	try {
		Key key = new SecretKeySpec(keyValue, algorithm);
		Cipher chiper = Cipher.getInstance(algorithm);
		chiper.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = Base64.decodeBase64(licensekey);
		byte[] decValue = chiper.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		String[] decryptedValues =decryptedValue.split("=");
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy");
		Date keyDate=dateFormat.parse(decryptedValues[1]);
	return new LicenseData(keyDate,decryptedValues[0]);
	
	} catch (Exception exception){
		exception.printStackTrace();
		return null;
	}
}



}
