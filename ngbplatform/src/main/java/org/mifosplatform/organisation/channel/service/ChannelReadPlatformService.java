package org.mifosplatform.organisation.channel.service;

import java.util.List;


import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.organisation.channel.data.ChannelData;
import org.mifosplatform.organisation.channel.domain.LanguageEnum;


public interface ChannelReadPlatformService {

	Page<ChannelData> retrieveChannel(SearchSqlQuery searchChannel);
	
	ChannelData retrieveChannel(Long channelId);

	List<ChannelData> retrieveChannelsForDropdown();
	
	List<LanguageEnum> retrieveLanguageEnum();

	List<ChannelData> retrieveChannelName(Long broadcasterId);

}
