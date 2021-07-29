package org.mifosplatform.cms.media.data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.cms.mediadetails.data.MediaLocationData;
import org.mifosplatform.finance.payments.data.McodeData;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

public class MediaAssetData {

	private final Long mediaId;
	private final String mediaTitle;
	private final String mediaImage;
	private final BigDecimal mediaRating;
	private final Long eventId;
	private Long noOfPages;
	private Long pageNo;
	private String assetTag;
	private List<EnumOptionData> mediaStatus;
	private List<MediaassetAttribute> mediaAttributes;
	private List<MediaassetAttribute> mediaFormat;
	private String status;
	private LocalDate releaseDate;
	private List<MediaEnumoptionData> mediaTypeData;
	private List<McodeData> mediaCategeorydata;
	private List<McodeData> mediaLanguageData;
	private MediaAssetData mediaAssetData;
	private List<MediaLocationData> mediaLocationData;
	private List<MediaassetAttributeData> mediaassetAttributes;
	private List<MediaAssetData> mediaDetails;
	private String mediatype;
	private String genre;
	private Long catageoryId;
	private String subject;
	private String overview;
	private Long contentProvider;
	private String rated;
	private BigDecimal rating;
	private String duration;
	private Long ratingCount;
	private List<McodeData> contentProviderData;
	private BigDecimal cpShareValue;
	private String quality;
	private String optType;
	private BigDecimal price;
	private Collection<MCodeData> eventCategeorydata;
	private String eventCategory;
	private String mediaCategory;
	private String contentProviderValue;
	private Double moviePrice;
	
public MediaAssetData(final Long mediaId, final String mediaTitle, final String image, final BigDecimal rating,
		final Long eventId, final String assetTag, final String quality, final String optType, final BigDecimal price){
	this.mediaId = mediaId;
	this.mediaTitle = mediaTitle;
	this.mediaImage = image;
	this.mediaRating = rating;
	this.eventId = eventId;
	this.assetTag = assetTag;
	this.quality = quality;
	this.optType = optType;
	this.price = price;
}
public MediaAssetData(final List<MediaAssetData> data, final Long noOfPages, final Long pageNo) {
	this.mediaId = null;
	this.mediaTitle = null;
	this.mediaImage = null;
	this.mediaRating = null;
	this.eventId = null;
	this.noOfPages = noOfPages;
	this.mediaDetails = data;
	this.pageNo = pageNo;
}
public MediaAssetData(final MediaAssetData mediaAssetData, final List<MediaassetAttributeData> mediaassetAttributes,
		final List<MediaLocationData> mediaLocationData, final List<EnumOptionData> status, final List<MediaassetAttribute> data, 
		final List<MediaassetAttribute> mediaFormat, final Collection<MCodeData> eventCategeorydata, final List<McodeData> mediaCategeorydata,
		final List<McodeData> mediaLangauagedata, final List<McodeData> contentProviderData, final List<MediaEnumoptionData> mediaTypeData) {

	this.mediaAssetData = mediaAssetData;
	this.mediaStatus = status;
	this.mediaAttributes = data;
	this.mediaFormat = mediaFormat;
	this.mediaId = null;
	this.mediaTitle = null;
	this.mediaImage = null;
	this.mediaRating = null;
	this.eventId = null;
	this.eventCategeorydata = eventCategeorydata;
	this.mediaCategeorydata = mediaCategeorydata;
	this.mediaLanguageData = mediaLangauagedata;
	this.mediaLocationData = mediaLocationData;
	this.mediaassetAttributes = mediaassetAttributes;
	this.contentProviderData = contentProviderData;
	this.mediaTypeData = mediaTypeData;
	
}
public MediaAssetData(final Long mediaId, final String mediaTitle, final String status, final LocalDate releaseDate, final BigDecimal share,
		final String eventCategory, final String mediaCategory, final String contentProviderValue,final Double moviePrice) {
	
          this.mediaId = mediaId;
          this.mediaTitle = mediaTitle;
          this.status = status;
          this.releaseDate = releaseDate;
          this.cpShareValue = share;
          this.eventCategory = eventCategory;
          this.mediaCategory = mediaCategory;
          this.contentProviderValue = contentProviderValue;
      	  this.mediaImage = null;
      	  this.eventId = null;
      	  this.mediaRating = null;
      	  this.moviePrice = moviePrice;
}

public MediaAssetData(final Long mediaId, final String mediatitle, final String type,final String genre, final Long catageoryId,
		final LocalDate releaseDate, final String subject, final String overview, final String image, final Long contentProvider,
		final String rated, final BigDecimal rating, final Long ratingCount, final String status, final String duration, 
		final BigDecimal cpShareValue) {
	
	 this.mediaId = mediaId;
     this.mediaTitle = mediatitle;
     this.mediatype = type;
     this.genre = genre;
     this.catageoryId = catageoryId;
     this.releaseDate = releaseDate;
     this.subject = subject;
     this.overview = overview;
     this.mediaImage = image;
     this.contentProvider = contentProvider;
     this.rated = rated;
     this.mediaRating = rating;
     this.rating = rating;
     this.ratingCount = ratingCount;
     this.duration = duration;
     this.status = status;
     this.cpShareValue = cpShareValue;
 	 this.eventId = null;
}

public Long getMediaId() {
	return mediaId;
}

public String getMediaTitle() {
	return mediaTitle;
}

public String getMediaImage() {
	return mediaImage;
}

public BigDecimal getMediaRating() {
	return mediaRating;
}

public Long getEventId() {
	return eventId;
}

public Long getNoOfPages() {
	return noOfPages;
}

public void setNoOfPages(final Long noOfPages) {
	this.noOfPages = noOfPages;
}

public Long getPageNo() {
	return pageNo;
}

public void setPageNo(final Long pageNo) {
	this.pageNo = pageNo;
}

public String getAssetTag() {
	return assetTag;
}

public void setAssetTag(final String assetTag) {
	this.assetTag = assetTag;
}

public List<EnumOptionData> getMediaStatus() {
	return mediaStatus;
}

public void setMediaStatus(final List<EnumOptionData> mediaStatus) {
	this.mediaStatus = mediaStatus;
}

public List<MediaassetAttribute> getMediaAttributes() {
	return mediaAttributes;
}

public void setMediaAttributes(final List<MediaassetAttribute> mediaAttributes) {
	this.mediaAttributes = mediaAttributes;
}

public List<MediaassetAttribute> getMediaFormat() {
	return mediaFormat;
}

public void setMediaFormat(final List<MediaassetAttribute> mediaFormat) {
	this.mediaFormat = mediaFormat;
}

public String getStatus() {
	return status;
}

public void setStatus(final String status) {
	this.status = status;
}
public LocalDate getReleaseDate() {
	return releaseDate;
}

public void setReleaseDate(final LocalDate releaseDate) {
	this.releaseDate = releaseDate;
}
public List<MediaEnumoptionData> getMediaTypeData() {
	return mediaTypeData;
}

public void setMediaTypeData(final List<MediaEnumoptionData> mediaTypeData) {
	this.mediaTypeData = mediaTypeData;
}
public List<McodeData> getMediaCategeorydata() {
	return mediaCategeorydata;
}

public void setMediaCategeorydata(final List<McodeData> mediaCategeorydata) {
	this.mediaCategeorydata = mediaCategeorydata;
}

public List<McodeData> getMediaLanguageData() {
	return mediaLanguageData;
}
public void setMediaLanguageData(final List<McodeData> mediaLanguageData) {
	this.mediaLanguageData = mediaLanguageData;
}

public MediaAssetData getMediaAssetData() {
	return mediaAssetData;
}

public void setMediaAssetData(final MediaAssetData mediaAssetData) {
	this.mediaAssetData = mediaAssetData;
}
public List<MediaLocationData> getMediaLocationData() {
	return mediaLocationData;
}

public void setMediaLocationData(final List<MediaLocationData> mediaLocationData) {
	this.mediaLocationData = mediaLocationData;
}

public List<MediaassetAttributeData> getMediaassetAttributes() {
	return mediaassetAttributes;
}
public void setMediaassetAttributes(final List<MediaassetAttributeData> mediaassetAttributes) {
	this.mediaassetAttributes = mediaassetAttributes;
}

public List<MediaAssetData> getMediaDetails() {
	return mediaDetails;
}

public void setMediaDetails(final List<MediaAssetData> mediaDetails) {
	this.mediaDetails = mediaDetails;
}

public String getMediatype() {
	return mediatype;
}

public void setMediatype(final String mediatype) {
	this.mediatype = mediatype;
}

public String getGenre() {
	return genre;
}

public void setGenre(final String genre) {
	this.genre = genre;
}

public Long getCatageoryId() {
	return catageoryId;
}
public void setCatageoryId(final Long catageoryId) {
	this.catageoryId = catageoryId;
}

public String getSubject() {
	return subject;
}

public void setSubject(final String subject) {
	this.subject = subject;
}
public String getOverview() {
	return overview;
}

public void setOverview(final String overview) {
	this.overview = overview;
}
public Long getContentProvider() {
	return contentProvider;
}

public void setContentProvider(final Long contentProvider) {
	this.contentProvider = contentProvider;
}

public String getRated() {
	return rated;
}
public void setRated(final String rated) {
	this.rated = rated;
}

public BigDecimal getRating() {
	return rating;
}

public void setRating(final BigDecimal rating) {
	this.rating = rating;
}
public String getDuration() {
	return duration;
}

public void setDuration(final String duration) {
	this.duration = duration;
}

public Long getRatingCount() {
	return ratingCount;
}
public void setRatingCount(final Long ratingCount) {
	this.ratingCount = ratingCount;
}

public List<McodeData> getContentProviderData() {
	return contentProviderData;
}

public void setContentProviderData(final List<McodeData> contentProviderData) {
	this.contentProviderData = contentProviderData;
}

public BigDecimal getCpShareValue() {
	return cpShareValue;
}

public void setCpShareValue(final BigDecimal cpShareValue) {
	this.cpShareValue = cpShareValue;
}
public String getQuality() {
	return quality;
}

public void setQuality(final String quality) {
	this.quality = quality;
}

public String getOptType() {
	return optType;
}

public void setOptType(final String optType) {
	this.optType = optType;
}
public BigDecimal getPrice() {
	return price;
}

public void setPrice(final BigDecimal price) {
	this.price = price;
}
public Collection<MCodeData> getEventCategeorydata() {
	return eventCategeorydata;
}

public void setEventCategeorydata(final Collection<MCodeData> eventCategeorydata) {
	this.eventCategeorydata = eventCategeorydata;
}

public String getEventCategory() {
	return eventCategory;
}
public void setEventCategory(final String eventCategory) {
	this.eventCategory = eventCategory;
}

public String getMediaCategory() {
	return mediaCategory;
}

public void setMediaCategory(final String mediaCategory) {
	this.mediaCategory = mediaCategory;
}

public String getContentProviderValue() {
	return contentProviderValue;
}

public void setContentProviderValue(final String contentProviderValue) {
	this.contentProviderValue = contentProviderValue;
}
	
}
