/**
 * 
 */
package org.mifosplatform.cms.eventmaster.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.chargecode.data.ChargesData;
import org.mifosplatform.cms.eventmaster.domain.EventMaster;
import org.mifosplatform.cms.media.data.MediaAssetData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

/**
 * POJO for {@link EventMaster}
 * 
 * @author pavani
 * @author Rakesh
 */
public class EventMasterData {
	
	private Long id;
	private String eventName;
	private String eventDescription;
	private String status;
	private Date eventStartDate;
	private Date eventEndDate;
	private LocalDate eventValidity;
	private Integer createdbyId;
	private LocalDate createdDate;
	private String allowCancellation;
	private String chargeCode;
	private String mediaTitle;
	private List<EnumOptionData> statusData;
	private List<MediaAssetData> mediaAsset;
	private List<EventDetailsData> selectedMedia;
	private List<EnumOptionData> optType;
	private List<ChargesData> chargeData;
	private EventMasterData eventMasterData;
	private List<EventDetailsData> eventDetails;
	@SuppressWarnings("unused")
	private Long statusId;
	private Collection<MCodeData> eventCategeorydata;
	@SuppressWarnings("unused")
	private String eventCategory;
	
	/** Default Constructor */
	public EventMasterData() {
		
	}
	
	/**
     * <p> The behavior of this constructor when the given @param's are called
     * 	it sets to that particular @param varibles.
     *
     * @param  mediaAsset
     * @param  statusData    
     * @param  optType 
     * @param  chargeDatas    
     * @param  eventCategeorydata 
     */
	public EventMasterData (final List<MediaAssetData> mediaAsset, final List<EnumOptionData> statusData,
							final List<EnumOptionData> optType, final List<ChargesData> chargeDatas, 
							final Collection<MCodeData> eventCategeorydata) {
		
		this.mediaAsset = mediaAsset;
		this.statusData = statusData;
		this.optType = optType;
		this.chargeData=chargeDatas;
		this.eventCategeorydata=eventCategeorydata;
	}
	
	/**
     * <p> The behavior of this constructor when the given @param's are called
     * 	it sets to that particular @param varibles.
     *
     * @param  id
     * @param  eventName    
     * @param  eventDescription 
     * @param  status    
     * @param  mediaTitle 
     * @param  createdDate
     * @param  eventCategory 
     */
	public EventMasterData(final Long id, final String eventName, final String eventDescription,
						   final String status, final String mediaTitle, final LocalDate createdDate, final String eventCategory ) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
		this.status = status;
		this.mediaTitle = mediaTitle;
		this.createdDate = createdDate;
		this.eventCategory = eventCategory;
	}
	
	/**
     * <p> The behavior of this constructor when the given @param's are called
     * 	it sets to that particular @param varibles.
     *
     * @param  id
     * @param  eventName    
     * @param  eventDescription 
     * @param  status    
     * @param  mediaTitle 
     * @param  eventStartDate
     * @param  eventEndDate 
     * @param  eventValidity
     * @param  chargeData
     * @param  eventCategory
     */
	public EventMasterData(final Long id, final String eventName, final String eventDescription, 
						   final Long status, final String mediaTitle, final Date eventStartDate,
						   final Date eventEndDate, final LocalDate eventValidity, final String chargeData, final String eventCategory) {
		this.id= id;
		this.eventName= eventName;
		this.eventDescription = eventDescription;
		this.statusId = status;
		this.mediaTitle = mediaTitle;
		this.eventStartDate = eventStartDate;
		this.eventEndDate = eventEndDate;
		this.eventValidity = eventValidity;
		this.chargeCode = chargeData;
		this.eventCategory = eventCategory;
	}
	
	/**
     * <p> The behavior of this constructor when the given @param's are called
     * 	it sets to that particular @param varibles.
     *
     * @param  id 
     * @param  eventName    
     * @param  eventDescription 
     * @param  chargeDatas    
     * @param  eventCategeorydata 
     */
	public EventMasterData(final Long id, final String eventName, final String eventDescription) {
		this.id = id;
		this.eventName = eventName;
		this.eventDescription = eventDescription;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}
	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}
	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(final String eventName) {
		this.eventName = eventName;
	}
	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}
	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(final String eventDescription) {
		this.eventDescription = eventDescription;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(final String status) {
		this.status = status;
	}
	/**
	 * @return the eventStartDate
	 */
	public Date getEventStartDate() {
		return eventStartDate;
	}
	/**
	 * @param eventStartDate the eventStartDate to set
	 */
	public void setEventStartDate(final Date eventStartDate) {
		this.eventStartDate = eventStartDate;
	}
	/**
	 * @return the eventEndDate
	 */
	public Date getEventEndDate() {
		return eventEndDate;
	}
	/**
	 * @param eventEndDate the eventEndDate to set
	 */
	public void setEventEndDate(final Date eventEndDate) {
		this.eventEndDate = eventEndDate;
	}
	/**
	 * @return the eventValidity
	 */
	public LocalDate getEventValidity() {
		return eventValidity;
	}
	/**
	 * @param eventValidity the eventValidity to set
	 */
	public void setEventValidity(final LocalDate eventValidity) {
		this.eventValidity = eventValidity;
	}
	/**
	 * @return the createdbyId
	 */
	public Integer getCreatedbyId() {
		return createdbyId;
	}
	/**
	 * @param createdbyId the createdbyId to set
	 */
	public void setCreatedbyId(final Integer createdbyId) {
		this.createdbyId = createdbyId;
	}
	/**
	 * @return the createdDate
	 */
	public LocalDate getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(final LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the mediaAsset
	 */
	public List<MediaAssetData> getMediaAsset() {
		return mediaAsset;
	}

	/**
	 * @param mediaAsset the mediaAsset to set
	 */
	public void setMediaAsset(final List<MediaAssetData> mediaAsset) {
		this.mediaAsset = mediaAsset;
	}

	/**
	 * @return the statusData
	 */
	public List<EnumOptionData> getStatusData() {
		return statusData;
	}

	/**
	 * @param statusData the statusData to set
	 */
	public void setStatusData(final List<EnumOptionData> statusData) {
		this.statusData = statusData;
	}

	/**
	 * @return the mediaTitle
	 */
	public String getMediaTitle() {
		return mediaTitle;
	}

	/**
	 * @param mediaTitle the mediaTitle to set
	 */
	public void setMediaTitle(final String mediaTitle) {
		this.mediaTitle = mediaTitle;
	}

	/**
	 * @return the optType
	 */
	public List<EnumOptionData> getOptType() {
		return optType;
	}

	/**
	 * @param optType the optType to set
	 */
	public void setOptType(final List<EnumOptionData> optType) {
		this.optType = optType;
	}

	/**
	 * @return the eventMasterData
	 */
	public EventMasterData getEventMasterData() {
		return eventMasterData;
	}

	/**
	 * @param eventMasterData the eventMasterData to set
	 */
	public void setEventMasterData(final EventMasterData eventMasterData) {
		this.eventMasterData = eventMasterData;
	}

	/**
	 * @return the eventDetails
	 */
	public List<EventDetailsData> getEventDetails() {
		return eventDetails;
	}

	/**
	 * @param eventDetails the eventDetails to set
	 */
	public void setEventDetailsData(final List<EventDetailsData> eventDetails) {
		this.eventDetails = eventDetails;
	}

	/**
	 * @return the allowCanelation
	 */
	public String isAllowCancellation() {
		return allowCancellation;
	}

	/**
	 * @param allowCanelation the allowCanelation to set
	 */
	public void setAllowCancellation(final String allowCancellation) {
		this.allowCancellation = allowCancellation;
	}

	/**
	 * @param eventDetails the eventDetails to set
	 */
	public void setEventDetails(final List<EventDetailsData> eventDetails) {
		this.eventDetails = eventDetails;
	}

	/**
	 * @return the selectedMedia
	 */
	public List<EventDetailsData> getSelectedMedia() {
		return selectedMedia;
	}

	/**
	 * @param details the selectedMedia to set
	 */
	public void setSelectedMedia(final List<EventDetailsData> eventdetails) {
		this.selectedMedia = eventdetails;
	}

	/**
	 * @return the chargeData
	 */
	public List<ChargesData> getChargeData() {
		return chargeData;
	}

	/**
	 * @param chargeData the chargeData to set
	 */
	public void setChargeData(final List<ChargesData> chargeData) {
		this.chargeData = chargeData;
	}

	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}

	/**
	 * @param chargeCode the chargeCode to set
	 */
	public void setChargeCode(final String chargeCode) {
		this.chargeCode = chargeCode;
	}

	/**
	 * @return the allowCancellation
	 */
	public String getAllowCancellation() {
		return allowCancellation;
	}

	/**
	 * @return the eventCategeorydata
	 */
	public Collection<MCodeData> getEventCategeorydata() {
		return eventCategeorydata;
	}

	/**
	 * @param eventCategeorydata the eventCategeorydata to set
	 */
	public void setEventCategeorydata(final Collection<MCodeData> eventCategeorydata) {
		this.eventCategeorydata = eventCategeorydata;
	}
	
}
