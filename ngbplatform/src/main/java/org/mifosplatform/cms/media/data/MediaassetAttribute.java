package org.mifosplatform.cms.media.data;

public class MediaassetAttribute {
	
	private final Long id;
	private final String mediaName;

	public MediaassetAttribute(final Long mediaId, final String mediaName) {
	  this.id = mediaId;
	  this.mediaName = mediaName;
	}
	
	public Long getId() {
		return id;
	}

	public String getMediaName() {
		return mediaName;
	}
}
