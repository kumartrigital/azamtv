package org.mifosplatform.organisation.mapping.service;

import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.broadcaster.data.BroadcasterData;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.mapping.data.ChannelMappingData;
import org.mifosplatform.portfolio.plan.data.ServiceData;

public interface ChannelMappingReadPlatformService {

	Page<ChannelMappingData> retrieveChannelMapping(SearchSqlQuery searchChannelMapping);
	
	ChannelMappingData retrieveChannelMapping(Long channelmappingId);

	List<ChannelData> retrieveSelectedChannels(Long productId);
	
	//List<ChannelData> retrieveChannelsForDropdown();

	//List<ServiceData> retrieveServicesForDropdown();

	List<ChannelMappingData> retriveproductmapping(Long productId);
}
