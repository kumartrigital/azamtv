package org.mifosplatform.cms.media.service;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;

public interface MediaAssetWritePlatformService {


	CommandProcessingResult createMediaAsset(JsonCommand command);

	CommandProcessingResult updateMediaAsset(JsonCommand command);

	CommandProcessingResult deleteMediaAsset(JsonCommand command);
	
	/**
	 * This method used for creating media attributes and locations
	 * Now we are not using
	 * whenever you required use it
	 * */
	CommandProcessingResult createMediaAssetLocationAttributes(JsonCommand command);

}
