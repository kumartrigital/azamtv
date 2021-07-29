package org.mifosplatform.freeradius.radius.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.mifosplatform.freeradius.radius.data.RadiusServiceData;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;


/**
 * @author hugo
 * 
 */
public interface RadiusReadPlatformService {

	String retrieveAllNasDetails();
	
	String createNas(String Json);

	String retrieveNasDetail(Long nasId);
	
	String deleteNasDetail(Long nasId);
	
	String retrieveAllRadServiceDetails(String attribute);

	CommandProcessingResult createRadService(String Json);

	RadiusServiceData retrieveRadServiceDetail(Long radServiceId);
	
	//String deleteRadService(Long radServiceId);

	String processRadiusDelete(String url, String encodePassword)
			throws ClientProtocolException, IOException;

    List<RadiusServiceData> retrieveRadServiceTemplateData(Long radServiceId);
	
   String processRadiusGet(String url, String encodePassword) throws ClientProtocolException, IOException;
   
   String processRadiusPost(String url, String encodePassword, String data) throws  IOException;

   String retrieveAllOnlineUsers(String attribute, String limit, String offset, String checkOnline, String userName, Long partner);

   String createReloadNases(String nasname);
   

}
