package org.mifosplatform.cms.inview.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InviewCatalogue {

	private List<Catalogue> catalogue;

	public List<Catalogue> getCatalogue() {
		return catalogue;
	}

	public void setCatalogue(List<Catalogue> catalogue) {
		this.catalogue = catalogue;
	}

	@Override
	public String toString() {
		return "InviewCatalogue [catalogue=" + catalogue + "]";
	}

	
}
