package org.mifosplatform.organisation.groupsdetails.handler;

import org.mifosplatform.commands.handler.NewCommandSourceHandler;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.organisation.groupsdetails.service.GroupsDetailsWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGroupsStatmentCommandHandler implements NewCommandSourceHandler{
	
	private final GroupsDetailsWritePlatformService groupsDetailsWritePlatformService;
	
	@Autowired
	public CreateGroupsStatmentCommandHandler(final GroupsDetailsWritePlatformService groupsDetailsWritePlatformService){
		
		this.groupsDetailsWritePlatformService = groupsDetailsWritePlatformService;
	}
	
	@Transactional
	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		
		return this.groupsDetailsWritePlatformService.generateStatment(command,command.entityId());
	}

}
