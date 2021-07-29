package org.mifosplatform.organisation.ippool.data;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.organisation.mcodevalues.data.MCodeData;

/**
 * 
 * @author ashokreddy
 *
 */
public class IpPoolData {
	
	private  Long id;
	private  String poolName;
	private  String ipAddress;
	private Collection<MCodeData> codeValueDatas;
	private List<IpPoolManagementData> ipPoolManagementData;

	public IpPoolData(final Long id, final String poolName, final String ipaddress) {
		this.id=id;
		this.poolName=poolName;
		this.ipAddress=ipaddress;
		
	}

	public IpPoolData(final Collection<MCodeData> codeValueDatas) {
		this.codeValueDatas=codeValueDatas;
	}

	public IpPoolData(final Collection<MCodeData> codeValueDatas,
			final List<IpPoolManagementData> ipPoolManagementData) {
		
		this.codeValueDatas = codeValueDatas;
		this.ipPoolManagementData = ipPoolManagementData;
	}

	public Long getId() {
		return id;
	}

	public String getPoolName() {
		return poolName;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public Collection<MCodeData> getCodeValueDatas() {
		return codeValueDatas;
	}

	public List<IpPoolManagementData> getIpPoolManagementData() {
		return ipPoolManagementData;
	}
	
	
	
}
