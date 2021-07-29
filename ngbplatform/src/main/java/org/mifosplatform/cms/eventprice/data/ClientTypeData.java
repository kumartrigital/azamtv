/**
 * 
 */
package org.mifosplatform.cms.eventprice.data;

/**
 * Class for Client Type
 * 
 * @author pavani
 *
 */
public class ClientTypeData {
	
	private Long id;
	private String type;
	
	public ClientTypeData(final Long id, final String type) {
		this.id = id;
		this.type = type;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

}
