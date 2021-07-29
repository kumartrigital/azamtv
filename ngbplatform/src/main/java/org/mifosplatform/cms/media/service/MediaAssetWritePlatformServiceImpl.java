package org.mifosplatform.cms.media.service;

import java.util.Collection;
import java.util.Map;

import org.mifosplatform.cms.media.domain.MediaAsset;
import org.mifosplatform.cms.media.serialization.MediaAssetCommandFromApiJsonDeserializer;
import org.mifosplatform.cms.mediadetails.domain.MediaAssetRepository;
import org.mifosplatform.cms.mediadetails.domain.MediaassetAttributes;
import org.mifosplatform.cms.mediadetails.domain.MediaassetLocation;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.mcodevalues.api.CodeNameConstants;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;
import org.mifosplatform.organisation.mcodevalues.service.MCodeReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

@Service
public class MediaAssetWritePlatformServiceImpl implements MediaAssetWritePlatformService {

	private final PlatformSecurityContext context;
	private final MediaAssetCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final FromJsonHelper fromApiJsonHelper;
	private final MediaAssetRepository assetRepository;
	private final MCodeReadPlatformService mCodeReadPlatformService;

	@Autowired
	public MediaAssetWritePlatformServiceImpl(final PlatformSecurityContext context,
			final FromJsonHelper fromApiJsonHelper, final MediaAssetRepository assetRepository,
			final MediaAssetCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final MCodeReadPlatformService mCodeReadPlatformService) {
		this.context = context;
		this.assetRepository = assetRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.mCodeReadPlatformService = mCodeReadPlatformService;

	}

	@Override
	public CommandProcessingResult createMediaAsset(final JsonCommand command) {

		try {

			// this.context.authenticatedUser();
			final String mediaTypeCheck = command.stringValueOfParameterNamed("mediaTypeCheck");
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final MediaAsset mediaAsset = MediaAsset.fromJson(command);

			/**
			 * Condition for create add media
			 */
			if ("ADDMEDIA".equalsIgnoreCase(mediaTypeCheck)) {

				final String language = command.stringValueOfParameterNamed("languageId");
				Long languageId = null;
				final Collection<MCodeData> codeValuedatasForLanguages = this.mCodeReadPlatformService
						.getCodeValue(CodeNameConstants.CODE_ASSET_LANGUAGE);
				for (final MCodeData codeValuedatasForLanguage : codeValuedatasForLanguages) {

					if (codeValuedatasForLanguage.getmCodeValue().equalsIgnoreCase(language)) {
						languageId = codeValuedatasForLanguage.getId();
					}

				}
				final String formatType = command.stringValueOfParameterNamed("formatType");
				final String location = command.stringValueOfParameterNamed("location");
				final MediaassetLocation mediaassetLocation = new MediaassetLocation(languageId, formatType, location);
				mediaAsset.addMediaLocations(mediaassetLocation);
			}

			/**
			 * Condition for create advance media
			 * 
			 */
			if ("ADVANCEMEDIA".equalsIgnoreCase(mediaTypeCheck)) {

				final JsonArray mediaassetAttributesArray = command.arrayOfParameterNamed("mediaassetAttributes")
						.getAsJsonArray();
				String[] mediaassetAttributes = null;
				mediaassetAttributes = new String[mediaassetAttributesArray.size()];
				for (int i = 0; i < mediaassetAttributesArray.size(); i++) {
					mediaassetAttributes[i] = mediaassetAttributesArray.get(i).toString();
					// JsonObject temp = mediaassetAttributesArray.getAsJsonObject();
				}

				// For Media Attributes
				for (final String mediaassetAttribute : mediaassetAttributes) {

					final JsonElement element = fromApiJsonHelper.parse(mediaassetAttribute);
					final String mediaAttributeType = fromApiJsonHelper.extractStringNamed("attributeType", element);
					final String mediaattributeName = fromApiJsonHelper.extractStringNamed("attributeName", element);
					final String mediaattributeValue = fromApiJsonHelper.extractStringNamed("attributevalue", element);
					final String mediaattributeNickname = fromApiJsonHelper.extractStringNamed("attributeNickname",
							element);
					final String mediaattributeImage = fromApiJsonHelper.extractStringNamed("attributeImage", element);
					final MediaassetAttributes attributes = new MediaassetAttributes(mediaAttributeType,
							mediaattributeName, mediaattributeValue, mediaattributeNickname, mediaattributeImage);
					mediaAsset.add(attributes);
				}

				final JsonArray mediaassetLocationsArray = command.arrayOfParameterNamed("mediaAssetLocations")
						.getAsJsonArray();
				String[] mediaassetLocations = null;
				mediaassetLocations = new String[mediaassetLocationsArray.size()];

				for (int i = 0; i < mediaassetLocationsArray.size(); i++) {
					mediaassetLocations[i] = mediaassetLocationsArray.get(i).toString();
				}

				// For Media Locations
				for (final String mediaassetLocationData : mediaassetLocations) {

					final JsonElement element = fromApiJsonHelper.parse(mediaassetLocationData);
					final Long languageId = fromApiJsonHelper.extractLongNamed("languageId", element);
					final String formatType = fromApiJsonHelper.extractStringNamed("formatType", element);
					final String location = fromApiJsonHelper.extractStringNamed("location", element);
					final MediaassetLocation mediaassetLocation = new MediaassetLocation(languageId, formatType,
							location);
					mediaAsset.addMediaLocations(mediaassetLocation);
				}
			}

			String overview = command.stringValueOfParameterName("overview");
			mediaAsset.setOverview(overview);
			this.assetRepository.save(mediaAsset);
			return new CommandProcessingResult(mediaAsset.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command, final DataIntegrityViolationException dve) {

	}

	@Override
	public CommandProcessingResult updateMediaAsset(final JsonCommand command) {
		try {
			// this.context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final MediaAsset mediaAsset = retriveMessageBy(command.entityId());

			/*
			 * mediaAsset.getMediaassetAttributes().clear();
			 */

			mediaAsset.getMediaassetLocations().clear();

			final Map<String, Object> changes = mediaAsset.updateAssetDetails(command);

			final String language = command.stringValueOfParameterNamed("languageId");
			Long languageId = null;
			final Collection<MCodeData> codeValuedatasForLanguages = this.mCodeReadPlatformService
					.getCodeValue(CodeNameConstants.CODE_ASSET_LANGUAGE);
			for (final MCodeData codeValuedatasForLanguage : codeValuedatasForLanguages) {

				if (codeValuedatasForLanguage.getmCodeValue().equalsIgnoreCase(language)) {
					languageId = codeValuedatasForLanguage.getId();
				}
			}
			final String formatType = command.stringValueOfParameterNamed("formatType");
			final String location = command.stringValueOfParameterNamed("location");
			final MediaassetLocation mediaassetLocation = new MediaassetLocation(languageId, formatType, location);
			mediaAsset.addMediaLocations(mediaassetLocation);

			/**
			 * The following code is previous one used for media attributes and locations
			 * Now deprecated Whenever you need uncomment it
			 */
			/*
			 * final JsonArray mediaassetAttributesArray=command.arrayOfParameterNamed(
			 * "mediaassetAttributes").getAsJsonArray();
			 * 
			 * String[] assetAttributes =null; assetAttributes=new
			 * String[mediaassetAttributesArray.size()]; for(int i=0;
			 * i<mediaassetAttributesArray.size();i++){ assetAttributes[i]
			 * =mediaassetAttributesArray.get(i).toString(); } //For Media Attributes for
			 * (String mediaassetAttribute : assetAttributes) {
			 * 
			 * final JsonElement element = fromApiJsonHelper.parse(mediaassetAttribute);
			 * final String mediaAttributeType =
			 * fromApiJsonHelper.extractStringNamed("attributeType", element); final String
			 * mediaattributeName = fromApiJsonHelper.extractStringNamed("attributeName",
			 * element); final String mediaattributeValue =
			 * fromApiJsonHelper.extractStringNamed("attributevalue", element); final String
			 * mediaattributeNickname=
			 * fromApiJsonHelper.extractStringNamed("attributeNickname", element); final
			 * String mediaattributeImage=
			 * fromApiJsonHelper.extractStringNamed("attributeImage", element);
			 * MediaassetAttributes attributes=new
			 * MediaassetAttributes(mediaAttributeType,mediaattributeName,
			 * mediaattributeValue,mediaattributeNickname,mediaattributeImage);
			 * mediaAsset.add(attributes);
			 * 
			 * }
			 * 
			 * final JsonArray
			 * mediaassetLocationsArray=command.arrayOfParameterNamed("mediaAssetLocations")
			 * .getAsJsonArray();
			 * 
			 * String[] mediaassetLocations =null; mediaassetLocations=new
			 * String[mediaassetLocationsArray.size()]; for(int i=0;
			 * i<mediaassetLocationsArray.size();i++){
			 * 
			 * mediaassetLocations[i] =mediaassetLocationsArray.get(i).toString();
			 * 
			 * } //For Media Attributes for (String mediaassetLocationData :
			 * mediaassetLocations) {
			 * 
			 * final JsonElement element = fromApiJsonHelper.parse(mediaassetLocationData);
			 * final Long languageId = fromApiJsonHelper.extractLongNamed("languageId",
			 * element); final String formatType =
			 * fromApiJsonHelper.extractStringNamed("formatType", element); final String
			 * location = fromApiJsonHelper.extractStringNamed("location", element);
			 * MediaassetLocation mediaassetLocation = new
			 * MediaassetLocation(languageId,formatType,location);
			 * mediaAsset.addMediaLocations(mediaassetLocation); }
			 */
			if (!changes.isEmpty()) {
				this.assetRepository.save(mediaAsset);
			}
			return new CommandProcessingResult(mediaAsset.getId());

		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}

	private MediaAsset retriveMessageBy(final Long assetId) {
		final MediaAsset mediaAsset = this.assetRepository.findOne(assetId);
		return mediaAsset;
	}

	@Override
	public CommandProcessingResult deleteMediaAsset(final JsonCommand command) {
		context.authenticatedUser();
		final MediaAsset mediaAsset = retriveMessageBy(command.entityId());
		if (mediaAsset == null) {
			throw new DataIntegrityViolationException(command.entityId().toString());
		}
		mediaAsset.isDelete();
		this.assetRepository.save(mediaAsset);
		return new CommandProcessingResult(mediaAsset.getId());
	}

	/**
	 * This method used for creating media attributes and locations Now we are not
	 * using whenever you requires use it
	 */
	@Override
	public CommandProcessingResult createMediaAssetLocationAttributes(final JsonCommand command) {

		context.authenticatedUser();
		this.fromApiJsonDeserializer.validateForCreateLocationAttributes(command.json());
		final MediaAsset mediaAsset = retriveMessageBy(command.entityId());
		final String mediaDetailType = command.stringValueOfParameterNamed("mediaDetailType");
		/**
		 * This is for media Attributes
		 */
		if ("ATTRIBUTES".equalsIgnoreCase(mediaDetailType)) {

			final JsonArray mediaassetAttributesArray = command.arrayOfParameterNamed("mediaassetAttributes")
					.getAsJsonArray();
			String[] mediaassetAttributes = null;
			mediaassetAttributes = new String[mediaassetAttributesArray.size()];
			for (int i = 0; i < mediaassetAttributesArray.size(); i++) {
				mediaassetAttributes[i] = mediaassetAttributesArray.get(i).toString();
				// JsonObject temp = mediaassetAttributesArray.getAsJsonObject();
			}
			// For Media Attributes
			for (final String mediaassetAttribute : mediaassetAttributes) {

				final JsonElement element = fromApiJsonHelper.parse(mediaassetAttribute);
				final String mediaAttributeType = fromApiJsonHelper.extractStringNamed("attributeType", element);
				final String mediaattributeName = fromApiJsonHelper.extractStringNamed("attributeName", element);
				final String mediaattributeValue = fromApiJsonHelper.extractStringNamed("attributevalue", element);
				final String mediaattributeNickname = fromApiJsonHelper.extractStringNamed("attributeNickname",
						element);
				final String mediaattributeImage = fromApiJsonHelper.extractStringNamed("attributeImage", element);
				final MediaassetAttributes attributes = new MediaassetAttributes(mediaAttributeType, mediaattributeName,
						mediaattributeValue, mediaattributeNickname, mediaattributeImage);
				mediaAsset.add(attributes);
			}
		}
		/**
		 * This is for media Locations
		 */
		if ("LOCATIONS".equalsIgnoreCase(mediaDetailType)) {

			final JsonArray mediaassetLocationsArray = command.arrayOfParameterNamed("mediaAssetLocations")
					.getAsJsonArray();
			String[] mediaassetLocations = null;
			mediaassetLocations = new String[mediaassetLocationsArray.size()];
			for (int i = 0; i < mediaassetLocationsArray.size(); i++) {

				mediaassetLocations[i] = mediaassetLocationsArray.get(i).toString();
			}

			// For Media Locations
			for (final String mediaassetLocationData : mediaassetLocations) {

				final JsonElement element = fromApiJsonHelper.parse(mediaassetLocationData);
				final Long languageId = fromApiJsonHelper.extractLongNamed("languageId", element);
				final String formatType = fromApiJsonHelper.extractStringNamed("formatType", element);
				final String location = fromApiJsonHelper.extractStringNamed("location", element);
				final MediaassetLocation mediaassetLocation = new MediaassetLocation(languageId, formatType, location);
				mediaAsset.addMediaLocations(mediaassetLocation);
			}
		}
		this.assetRepository.save(mediaAsset);

		return new CommandProcessingResult(mediaAsset.getId());
	}

}
