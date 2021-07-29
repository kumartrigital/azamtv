package org.mifosplatform.cms.inview.data;

public class Catalogue {

	private String asset_id;
	private String type;
	private String vod_type;
	private metadata metadata;

	public String getAsset_id() {
		return asset_id;
	}

	public void setAsset_id(String asset_id) {
		this.asset_id = asset_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVod_type() {
		return vod_type;
	}

	public void setVod_type(String vod_type) {
		this.vod_type = vod_type;
	}

	public metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return "Catalogue [asset_id=" + asset_id + ", type=" + type + ", vod_type=" + vod_type + ", metadata="
				+ metadata + "]";
	}

}
