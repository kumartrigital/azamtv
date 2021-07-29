/**
 * 
 */
package org.mifosplatform.cms.eventmaster.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.mifosplatform.cms.eventmaster.domain.EventDetails;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.eventmaster.domain.EventMasterRepository;
import org.mifosplatform.cms.eventmaster.serialization.EventMasterFromApiJsonDeserializer;
import org.mifosplatform.cms.media.data.MediaAssetData;
import org.mifosplatform.cms.media.domain.MediaAsset;
import org.mifosplatform.cms.media.service.MediaAssetReadPlatformService;
import org.mifosplatform.cms.mediadetails.domain.MediaAssetRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.workflow.eventaction.data.ActionDetaislData;
import org.mifosplatform.workflow.eventaction.service.ActionDetailsReadPlatformService;
import org.mifosplatform.workflow.eventaction.service.ActiondetailsWritePlatformService;
import org.mifosplatform.workflow.eventaction.service.EventActionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;

/**
 * {@link Service} Class for {@link EventMaster} Write Service
 * implements {@link EventMasterWritePlatformService}
 * 
 * @author pavani
 * @author Rakesh
 */
@Service
public class EventMasterWritePlatformServiceImpl implements
		EventMasterWritePlatformService {
	
	private final PlatformSecurityContext context;
	private final MediaAssetRepository assetRepository;
	private final EventMasterRepository eventMasterRepository;
	private final EventMasterFromApiJsonDeserializer apiJsonDeserializer;
	private final MediaAssetReadPlatformService assetReadPlatformService;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	private final ActiondetailsWritePlatformService actiondetailsWritePlatformService;
	
	@Autowired
	public EventMasterWritePlatformServiceImpl (final PlatformSecurityContext context, final EventMasterRepository eventMasterRepository, 
	       final EventMasterFromApiJsonDeserializer apiJsonDeserializer, final MediaAssetRepository assetRepository, 
	       final ActiondetailsWritePlatformService actiondetailsWritePlatformService,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
	       final MediaAssetReadPlatformService assetReadPlatformService) {
		
		this.context = context;
		this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService=actiondetailsWritePlatformService;
		this.assetRepository = assetRepository;
		this.apiJsonDeserializer = apiJsonDeserializer;
		this.eventMasterRepository = eventMasterRepository;
		this.assetReadPlatformService = assetReadPlatformService;
	}

	@Transactional
	@Override
	public CommandProcessingResult createEventMaster(final JsonCommand command) {
		try {
		//	this.context.authenticatedUser();
			//final Long createdbyId = context.authenticatedUser().getId();
			this.apiJsonDeserializer.validateForCreate(command.json());
			
			final EventMaster eventMaster = EventMaster.fromJsom(command);		
			final JsonArray array = command.arrayOfParameterNamed("mediaData").getAsJsonArray();
			String[] media  = null;
			media = new String[array.size()];
			for(int i = 0 ; i < array.size() ; i++) {
				media[i] = array.get(i).getAsString();
			}
			for(final String mediaId : media) {
				final Long id = Long.valueOf(mediaId);
				final MediaAsset mediaAsset = this.assetRepository.findOne(id);
				final EventDetails detail = new EventDetails(mediaAsset.getId());
				eventMaster.addMediaDetails(detail);
			}
			//eventMaster.setCreatedbyId(createdbyId);
			this.eventMasterRepository.save(eventMaster);
			
		  if("Live Event".equalsIgnoreCase(eventMaster.getEventCategory())){
            final List<ActionDetaislData> actionDetailsDatas=this.actionDetailsReadPlatformService.retrieveActionDetails(EventActionConstants.EVENT_CREATE_LIVE_EVENT);
            if(!actionDetailsDatas.isEmpty()){
            this.actiondetailsWritePlatformService.AddNewActions(actionDetailsDatas,Long.valueOf(0),eventMaster.getId().toString(),null);
            }
			 }
			
			return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withEntityId(eventMaster.getId()).build();
		} catch(DataIntegrityViolationException dve) {
			
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (ParseException e) {
			e.printStackTrace();
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	@SuppressWarnings("unused")
	@Transactional
	@Override
	public CommandProcessingResult updateEventMaster(JsonCommand command) {
		try {
			this.context.authenticatedUser();
			this.apiJsonDeserializer.validateForCreate(command.json());
			
			final EventMaster oldEvent = this.eventMasterRepository.findOne(command.entityId());
			
			final Map<String, Object> changes = oldEvent.updateEventDetails(command);
			
			final List<MediaAssetData> mediaData = this.assetReadPlatformService.retrieveAllmediaAssetdata();
			for(MediaAssetData data : mediaData) {
				oldEvent.getEventDetails().clear();
				final JsonArray array = command.arrayOfParameterNamed("mediaData").getAsJsonArray();
				String[] media = null;
				media  = new String[array.size()];
				
				for(int i = 0; i < array.size(); i++) {
					media[i] = array.get(i).getAsString();
				}
				
				for(String mediaId : media) {
					final Long id = Long.valueOf(mediaId);
					final MediaAsset mediaAsset = this.assetRepository.findOne(id);
					final EventDetails detail = new EventDetails(mediaAsset.getId());
					oldEvent.addMediaDetails(detail);
				}
			}
	
			if(!changes.isEmpty()){
				this.eventMasterRepository.save(oldEvent);
			}
			return new CommandProcessingResultBuilder().withEntityId(command.entityId()).withCommandId(command.commandId()).build();
		} catch (DataIntegrityViolationException dve) {
			return new CommandProcessingResult(Long.valueOf(-1));
		} catch (ParseException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
	}
	
	public CommandProcessingResult deleteEventMaster(final Long eventId) {
		final List<MediaAssetData> mediaAsset = this.assetReadPlatformService.retrieveAllmediaAssetdata();
		final EventMaster event = this.eventMasterRepository.findOne(eventId);
		for(final MediaAssetData data : mediaAsset) {
			final EventDetails details = new EventDetails(data.getMediaId());
			details.delete(event);
		}
		event.delete();
		this.eventMasterRepository.save(event);
		return new CommandProcessingResultBuilder().withEntityId(eventId).build();
	}
	
}