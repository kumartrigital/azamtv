package org.mifosplatform.organisation.groupsdetails.data;

public class GroupsDetailsData {

	private Long id;
	private String groupName;
	private String groupAddress;
	private Long countNo;
	private String attribute1;
	private String attribute2;
	private String attribute3;
	private String attribute4;
	private String isProvision;
	
	public GroupsDetailsData(final Long id,final String  groupName, final String groupAddress, final Long countNo, final String attribute1, 
			                       final String attribute2, final String attribute3, final String attribute4, final String isProvision){
		
		this.id = id;
		this.groupName = groupName;
		this.groupAddress = groupAddress;
		this.countNo = countNo;
		this.attribute1 = attribute1;
		this.attribute2	= attribute2;
		this.attribute3 = attribute3;
		this.attribute4 = attribute4;
		this.isProvision = isProvision;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(final String groupName) {
		this.groupName = groupName;
	}

	public String getGroupAddress() {
		return groupAddress;
	}

	public void setGroupAddress(final String groupAddress) {
		this.groupAddress = groupAddress;
	}

	public Long getCountNo() {
		return countNo;
	}

	public void setCountNo(final Long countNo) {
		this.countNo = countNo;
	}

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(final String attribute1) {
		this.attribute1 = attribute1;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(final String attribute2) {
		this.attribute2 = attribute2;
	}

	public String getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(final String attribute3) {
		this.attribute3 = attribute3;
	}

	public String getAttribute4() {
		return attribute4;
	}

	public void setAttribute4(final String attribute4) {
		this.attribute4 = attribute4;
	}

	public String getIsProvision() {
		return isProvision;
	}

	public void setIsProvision(final String isProvision) {
		this.isProvision = isProvision;
	}

	
	
}
